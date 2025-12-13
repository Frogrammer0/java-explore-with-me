package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventConfirmedDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestsRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestsRepository requestsRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;


    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        log.info("создание события в EventServiceImpl dto = {}", eventDto);
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(eventDto.getCategory());
        checkEventDate(eventDto.getEventDate());
        Event event = eventMapper.toEvent(eventDto, initiator, category);
        eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setConfirmedRequests(0L);
        eventFullDto.setViews(0L);
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        log.info("получение события в EventServiceImpl для userId = {}", userId);
        Pageable page = PageRequest.of(from / size, size);
        getUserOrThrow(userId);
        return eventRepository.findAllByInitiatorId(userId, page).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEventById(long userId, long eventId) {
        log.info("получение события id = {} в EventServiceImpl для userId = {}", eventId, userId);
        getUserOrThrow(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("не найдено событие id = " + eventId + " для автора id = " + userId)
        );

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(UpdateEventUserRequest updateEventRequest, long userId, long eventId) {
        log.info("изменение события id = {} в EventServiceImpl для userId = {}", eventId, userId);
        Event event = getEventOrThrow(eventId);
        checkInitiator(userId, event);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("нельзя изменять опубликованные события");
        }
        if (updateEventRequest.getStateAction() == StateAction.CANCEL_REVIEW && event.getState() != EventState.PENDING) {
            throw new ConflictException("нельзя отменить событие");
        } else {
            event.setState(EventState.CANCELED);
        }

        if (updateEventRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(EventState.PENDING);
        }

        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            checkEventDate(updateEventRequest.getEventDate());
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = getCategoryOrThrow(updateEventRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(updateEventRequest.getLocation());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }


        return eventMapper.toEventFullDto(eventRepository.save(event));
    }


    @Override
    public EventFullDto cancelEventByUser(Long userId, Long eventId) {
        log.info("отмена события id = {} в EventServiceImpl для userId = {}", eventId, userId);
        Event event = getEventOrThrow(eventId);
        checkInitiator(userId, event);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("нельзя изменять опубликованные события");
        }

        event.setState(EventState.PENDING);

        UpdateEventAdminRequest adminRequest = UpdateEventAdminRequest.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory().getId())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .stateAction(StateAction.REJECT_EVENT)
                .build();

        updateEventByAdmin(eventId, adminRequest);


        return eventMapper.toEventFullDto(event);
    }


    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        Event event = getEventOrThrow(eventId);
        checkInitiator(userId, event);

        List<Request> requests = requestsRepository.findAllByIdIn(updateRequest.getRequestsId());
        requests.forEach(r -> {
            if (r.getStatus() == RequestStatus.CONFIRMED || r.getStatus() == RequestStatus.REJECTED) {
                throw new ConflictException("в списке имеются уже обработанные заявки");
            }
        });

        if (updateRequest.getAction() == RequestAction.CONFIRM) {
            AtomicLong limit = new AtomicLong(event.getParticipantLimit() -
                    requestsRepository.countConfirmedRequests(eventId, RequestStatus.CONFIRMED));

            requests.forEach(request -> {
                        if (limit.get() > 0) {
                            result.getConfirmedRequests().add(requestMapper.toParticipationRequestDto(request));
                            limit.getAndDecrement();
                        }

                        if ((result.getRejectedRequests().size() + result.getConfirmedRequests().size()) <
                                updateRequest.getRequestsId().size()) {
                            result.getRejectedRequests().add(requestMapper.toParticipationRequestDto(request));
                        }
                    }
            );
        } else if (updateRequest.getAction() == RequestAction.REJECT) {
            requests.forEach(request -> {
                result.getRejectedRequests().add(requestMapper.toParticipationRequestDto(request));
            });
        }

        return result;
    }


    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsForAdmin(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from, int size) {
        log.info("получение админом событий пользователей ids = {} в EventServiceImpl", users);

        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime start = (rangeStart != null) ? rangeStart : LocalDateTime.now();
        if ((users != null)) {
            if (users.size() <= 1 && users.getFirst() == 0) users = null;
        }

        if (states != null && states.isEmpty()) states = null;


        if ((categories != null)) {
            if (categories.size() <= 1 && categories.getFirst() == 0) categories = null;
        }

        List<EventFullDto> events = eventRepository.findAdminEvents(users, states, categories, start, rangeEnd, page)
                .stream()
                .map(eventMapper::toEventFullDto)
                .toList();

        events = appendEventFullDto(events);

        return events;

    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("изменение админом события id = {} в EventServiceImpl", eventId);
        Event event = getEventOrThrow(eventId);

        if (updateRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("не ожидает публикации событие id = " + event);
            }
            log.info("публикация админом события id = {} в EventServiceImpl", eventId);
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (updateRequest.getStateAction() == StateAction.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("нельзя отменить опубликованное событие id = " + eventId);
            }
            log.info("отклонение админом события id = {} в EventServiceImpl", eventId);
            event.setState(EventState.CANCELED);
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = getCategoryOrThrow(updateRequest.getCategory());
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            checkEventDate(updateRequest.getEventDate());
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size) {
        log.info("получение событий getPublicEvents в EventServiceImpl");
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("время начала позже времени конца");
        }
        Pageable page = PageRequest.of(from / size, size);

        LocalDateTime start = (rangeStart != null) ? rangeStart : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? rangeStart : LocalDateTime.now().plusYears(1000);
        String searchText = (text != null) ? text : "";

        List<EventFullDto> events = eventRepository.findPublicEvents(EventState.PUBLISHED, searchText,
                        categories, paid, start, end, page)
                .stream()
                .map(eventMapper::toEventFullDto)
                .toList();

        events = appendEventFullDto(events);

        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 ||
                            e.getConfirmedRequests() < e.getParticipantLimit())
                    .toList();
        }

        if (sort.equals("VIEWS")) {
            events.sort(Comparator.comparing(EventFullDto::getViews));
        }

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublishedEventById(Long eventId) {
        log.info("получение событий getPublishedEventById c eventId = {} в EventServiceImpl", eventId);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(getEventOrThrow(eventId));

        if (eventFullDto.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("не опубликовано событие с id = " + eventId);
        }


        return appendEventFullDto(eventFullDto);
    }


    private EventFullDto appendEventFullDto(EventFullDto eventDto) {
        log.info("добавление заявок и просмотров в EventFullDto id = {} в EventServiceImpl", eventDto.getId());
        Long confirmed = requestsRepository.countConfirmedRequests(eventDto.getId(), RequestStatus.CONFIRMED);
        Long views = getViewsByEvent(eventDto.getId());
        eventDto.setConfirmedRequests(confirmed);
        eventDto.setViews(views);
        return eventDto;
    }


    private EventShortDto appendEventShortDto(EventShortDto eventDto) {
        log.info("добавление заявок и просмотров в EventShortDto id = {} в EventServiceImpl", eventDto.getId());
        Long confirmed = requestsRepository.countConfirmedRequests(eventDto.getId(), RequestStatus.CONFIRMED);
        Long views = getViewsByEvent(eventDto.getId());
        eventDto.setConfirmedRequests(confirmed);
        eventDto.setViews(views);
        return eventDto;
    }


    private List<EventShortDto> appendEventShortDto(List<EventShortDto> eventDtos) {
        log.info("добавление заявок и просмотров в список EventShortDto ids = {} в EventServiceImpl", eventDtos);
        Map<Long, Long> confirmedMap = requestsRepository
                .getConfirmedRequestsByEvents(eventDtos.stream().map(EventShortDto::getId).toList())
                .stream()
                .collect(Collectors.toMap(
                        EventConfirmedDto::getEventId,
                        EventConfirmedDto::getConfirmed
                ));


        List<Long> eventIds = eventDtos.stream().map(EventShortDto::getId).toList();
        Map<Long, Long> views = getViewsByEvents(eventIds);

        return eventDtos.stream()
                .peek(
                        e -> {
                            e.setConfirmedRequests((confirmedMap.get(e.getId()) != null) ?
                                    confirmedMap.get(e.getId()) : 0);

                            e.setViews((views.get(e.getId()) != null) ? views.get(e.getId()) : 0);
                        }
                ).collect(Collectors.toList());
    }


    private List<EventFullDto> appendEventFullDto(List<EventFullDto> eventDtos) {
        log.info("добавление заявок и просмотров в список EventFullDto size = {} в EventServiceImpl", eventDtos.size());
        Map<Long, Long> confirmedMap = requestsRepository
                .getConfirmedRequestsByEvents(eventDtos.stream().map(EventFullDto::getId).toList())
                .stream()
                .collect(Collectors.toMap(
                        EventConfirmedDto::getEventId,
                        EventConfirmedDto::getConfirmed
                ));


        List<Long> eventIds = eventDtos.stream().map(EventFullDto::getId).toList();
        Map<Long, Long> views = getViewsByEvents(eventIds);

        return eventDtos.stream()
                .peek(
                        e -> {
                            e.setConfirmedRequests((confirmedMap.get(e.getId()) != null) ?
                                    confirmedMap.get(e.getId()) : 0);

                            e.setViews((views.get(e.getId()) != null) ? views.get(e.getId()) : 0);
                        }
                ).collect(Collectors.toList());
    }


    private Long getViewsByEvent(Long eventId) {
        log.info("getViewsByEvent c eventId = {} в EventServiceImpl", eventId);

        String uri = "/events/" + eventId;

        List<ViewStatsDto> stats = statsClient.getStats(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0),
                List.of(uri),
                true
        );

        if (stats.isEmpty()) {
            return 0L;
        }

        return stats.getFirst().getHits();
    }


    private Map<Long, Long> getViewsByEvents(List<Long> eventIds) {
        log.info("getViewsByEvents c eventIds = {} в EventServiceImpl", eventIds);

        if (eventIds.isEmpty()) {
            return Map.of();
        }

        List<String> uris = eventIds.stream()
                .map(e -> "events/" + e)
                .toList();

        List<ViewStatsDto> stats = statsClient.getStats(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2100, 1, 1, 0, 0),
                uris,
                true
        );


        log.info("---------------------------------------------------------------------------------------stats = {} ", stats);

        return stats.stream().collect(Collectors.toMap(
                s -> Long.valueOf(s.getUri().split("/")[2]),
                ViewStatsDto::getHits
        ));
    }


    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не найден пользователь с id = " + userId)
        );
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("не найдено событие с id = " + eventId)
        );
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("не найдена категория с id = " + categoryId)
        );
    }

    private void checkInitiator(Long userId, Event event) {
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("пользователь id =  " + userId + " не является автором события id = "
                    + event.getId());
        }
    }

    private void checkEventDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("дата события должна быть не раньше чем через 2 часа");
        }
    }


}


