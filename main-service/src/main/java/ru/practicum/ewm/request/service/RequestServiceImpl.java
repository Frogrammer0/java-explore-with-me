package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.ForbiddenException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.RequestsRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return List.of();
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.info("создание заявки в RequestServiceImpl от пользователя id = {} на событие id = {}", userId, eventId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не найден пользователь с id = " + userId)
        );
        if (eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("событие не найдено")
        ).getInitiator().getId() == userId) {
            throw new ConflictException("нельзя подавать заявку на свое событие");
        }

        if (!eventRepository.existsByIdAndState(eventId, EventState.PUBLISHED)) {
            throw new NotFoundException("событие не опубликовано");
        }
        if (requestsRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("такая заявка уже существует");
        }
        Event event = eventRepository.findById(eventId).get();
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ConflictException("мест на данное событие больше нет");
        }
        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
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
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не найден пользователь с id = " + userId)
        );
        Request request = requestsRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("заявка с id = " + requestId + "не найдена")
        );

        if (request.getRequester().getId() != userId) {
            throw new ForbiddenException("пользователь не является владельцем заявки");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toParticipationRequestDto(requestsRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(userId, eventId)) {
            throw new NotFoundException("событие с id = " + eventId + " и инициатором " + userId + " не найдено");
        }


        return requestsRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatuses(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateRequests) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("не найдено событие с id = " + eventId)
        );

        if (event.getInitiator().getId() != userId) {
            throw new ForbiddenException("пользователь id = " + userId +
                    " не является создателем события id = " + eventId);
        }

        Long alreadyConfirmed = requestsRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int newConfirmed = updateRequests.getRequestsId().size();

        if (alreadyConfirmed + newConfirmed > event.getParticipantLimit()) {
            throw new ConflictException("число заявок больше лимита участников");
        }

        List<Request> requests = requestsRepository.findAllByIdIn(updateRequests.getRequestsId());
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();

        if (updateRequests.getStatus() == RequestStatus.CONFIRMED) {
            requests.forEach(request -> {
                if (request.getStatus() == RequestStatus.PENDING) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    updateResult.getConfirmedRequests().add(requestMapper.toParticipationRequestDto(request));
                } else {
                    updateResult.getRejectedRequests().add(requestMapper.toParticipationRequestDto(request));
                }
            });
        } else if (updateRequests.getStatus() == RequestStatus.REJECTED) {
            requests.forEach(request -> {
                if (request.getStatus() != RequestStatus.REJECTED) {
                    request.setStatus(RequestStatus.REJECTED);
                }
                updateResult.getRejectedRequests().add(requestMapper.toParticipationRequestDto(request));
            });
        }

        return updateResult;
    }
}
