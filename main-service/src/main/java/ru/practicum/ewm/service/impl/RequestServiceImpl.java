package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestsRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        getUserOrThrow(userId);
        return requestsRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.info("создание заявки в RequestServiceImpl от пользователя id = {} на событие id = {}", userId, eventId);
        User user = getUserOrThrow(userId);
        Request request;

        if (eventId == 0) {

            request = Request.builder()
                    .requester(user)
                    .event(null)
                    .status(RequestStatus.CONFIRMED)
                    .created(LocalDateTime.now().withNano(0))
                    .build();

            return requestMapper.toParticipationRequestDto(requestsRepository.save(request));

        }

        Event event = getEventOrThrow(eventId);
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("нельзя подавать заявку на свое событие");
        }
        if (!eventRepository.existsByIdAndState(eventId, EventState.PUBLISHED)) {
            throw new ConflictException("событие не опубликовано");
        }

        if (requestsRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("такая заявка уже существует");
        }

        if (requestsRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                == event.getParticipantLimit()) {
            throw new ConflictException("мест на данное событие больше нет");
        }

        request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now().withNano(0))
                .build();

        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        log.info("отмена заявки id = {}  от пользователя id = {} в RequestServiceImpl", requestId, userId);
        getUserOrThrow(userId);
        Request request = requestsRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("заявка с id = " + requestId + "не найдена")
        );

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ForbiddenException("пользователь не является владельцем заявки");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId) {
        log.info("получение заявок события id = {}  от пользователя id = {} в RequestServiceImpl", eventId, userId);

        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("событие с id = " + eventId + " и инициатором " + userId + " не найдено");
        }
        return requestsRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatuses(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateRequests) {
        log.info("изменение статусов заявок события id = {}  от пользователя id = {} в RequestServiceImpl",
                eventId, userId);

        if (updateRequests == null || updateRequests.getRequestIds() == null) {
            throw new ConflictException("лимит участников события исчерпан");
        }

        Event event = getEventOrThrow(eventId);

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("пользователь id = " + userId +
                    " не является создателем события id = " + eventId);
        }

        if (updateRequests.getStatus() == RequestStatus.CONFIRMED &&
                event.getParticipantLimit() != 0 &&
                requestsRepository.countByEventIdAndStatus(eventId, RequestStatus.PENDING)
                        + requestsRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                        > event.getParticipantLimit()) {

            throw new ConflictException("лимит участников события исчерпан");
        }

        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        long confirmedCount = requestsRepository
                .countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);


        if (updateRequests.getStatus() == RequestStatus.CONFIRMED &&
                event.getParticipantLimit() != 0) {

            long pendingCount = requestsRepository
                    .countByEventIdAndStatus(eventId, RequestStatus.PENDING);

            if (confirmedCount + pendingCount > event.getParticipantLimit()) {
                throw new ConflictException("лимит участников события исчерпан");
            }
        }

        List<Request> requests =
                requestsRepository.findAllByIdIn(updateRequests.getRequestIds());

        if (updateRequests.getStatus() == RequestStatus.CONFIRMED) {

            for (Request request : requests) {
                if (request.getStatus() == RequestStatus.PENDING) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    updateResult.getConfirmedRequests()
                            .add(requestMapper.toParticipationRequestDto(request));
                } else {
                    updateResult.getRejectedRequests()
                            .add(requestMapper.toParticipationRequestDto(request));
                }
            }

        } else if (updateRequests.getStatus() == RequestStatus.REJECTED) {

            for (Request request : requests) {
                if (request.getStatus() == RequestStatus.CONFIRMED) {
                    throw new ConflictException("нельзя отменить уже принятую заявку");
                }
                request.setStatus(RequestStatus.REJECTED);
                updateResult.getRejectedRequests()
                        .add(requestMapper.toParticipationRequestDto(request));
            }
        }

        requestsRepository.saveAll(requests);
        return updateResult;
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("не найдено событие с id = " + eventId));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не найден пользователь с id = " + userId)
        );
    }

}
