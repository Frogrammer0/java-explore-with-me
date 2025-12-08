package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.ForbiddenException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.RequestsRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestsRepository requestsRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;


    @Override
    public EventFullDto create(NewEventDto eventDto, Long userId) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(eventDto.getCategory());
        checkEventDate(eventDto.getEventDate());
        Event event = eventMapper.toEvent(eventDto, initiator, category);

        return eventMapper.toEventFullDto(event);
    }

    public List<EventShortDto> getEventsForUser(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        getUserOrThrow(userId);
        return eventRepository.findAllByInitiatorId(userId, page).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventByIdAndInitiatorId(long userId, long eventId) {
        getUserOrThrow(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("не найдено событие id = " + eventId + " для автора id = " + userId)
        );

        return eventMapper.toEventFullDto(event);
    }

    public EventFullDto editEventForUser(UpdateEventUserRequest updateEventRequest, long userId, long eventId) {
        Event event = getEventOrThrow(eventId);
        checkInitiator(userId, event);
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("нельзя изменять опубликованные события");
        }
        if (updateEventRequest.getState() == EventState.CANCELED && event.getState() != EventState.PENDING) {
            throw new ConflictException("нельзя отменить событие");
        } else {
            event.setState(updateEventRequest.getState());
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


    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsForAdmin(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime startRange,
                                                LocalDateTime endRange,
                                                int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        if (users != null && users.isEmpty()) users = null;
        if (states != null && states.isEmpty()) states = null;
        if (categories != null && categories.isEmpty()) categories = null;

        return eventRepository.findAdminEvents(users, states, categories, startRange, endRange, page).stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

    }

    public EventFullDto editForAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = getEventOrThrow(eventId);
        if (updateRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("не ожидает публикации событие id = " + event);
            }
            event.setState(EventState.PUBLISHED);
            checkEventDate(updateRequest.getEventDate());
            event.setPublishedOn(LocalDateTime.now());
        } else if (updateRequest.getStateAction() == StateAction.REJECT_EVENT) {
            event.setState(EventState.CANCELED);
        }

        if (updateRequest.getAnnotation() != null) event.setAnnotation(updateRequest.getAnnotation());
        if (updateRequest.getCategory() != null) {
            Category category = getCategoryOrThrow(updateRequest.getCategory());
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) event.setDescription(updateRequest.getDescription());

        return null;
    }
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
    if (userId != event.getInitiator().getId()) {
        throw new ForbiddenException("пользователь id =  " + userId + " не является автором события id = "
                + event.getId());
    }
}

private void checkEventDate(LocalDateTime date) {
    if (date.isBefore(LocalDateTime.now().plusHours(2))) {
        throw new ConflictException("дата события должна быть не раньше чем через 2 часа");
    }
}
}
