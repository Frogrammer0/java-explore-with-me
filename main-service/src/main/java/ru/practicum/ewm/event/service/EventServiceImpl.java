package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestsRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

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


    public EventFullDto create(NewEventDto eventDto, Long userId) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(eventDto.getCategory());
        checkEventDate(eventDto.getEventDate());
        Event event = eventMapper.toEvent(eventDto, initiator, category);

        return eventMapper.toEventFullDto(event);
    }

    public List<EventShortDto> getEventsByUser(long userId, int from, int size) {
        //Pageable page = PageRequest.of(from/size, size);
        /// тут писать
        return null;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не найден пользователь с id = " + userId)
        );
    }

    private Event getEventOrThrow(Long eventId) {
        return null;
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("не найдена категория с id = " + categoryId)
        );
    }

    private void checkInitiator(Long userId, Event event) {

    }

    private void checkEventDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("дата события должна быть не раньше чем через 2 часа");
        }
    }
}
