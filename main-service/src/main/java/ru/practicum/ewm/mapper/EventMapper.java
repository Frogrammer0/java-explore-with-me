package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.model.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EventMapper {

    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event toEvent(NewEventDto eventDto, User initiator, Category category) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .title(eventDto.getTitle())
                .eventDate(eventDto.getEventDate())
                .createdOn(LocalDateTime.now().withNano(0))
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(EventState.PENDING)
                .initiator(initiator)
                .category(category)
                .location(eventDto.getLocation())
                .build();
    }

    public UpdateEventUserRequest toUpdateEventUserRequest(Event event) {
        return UpdateEventUserRequest.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory().getId())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .requestModeration(event.getRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toEventShortDto(EventFullDto eventFullDto) {
        return EventShortDto.builder()
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategory())
                .eventDate(eventFullDto.getEventDate())
                .initiator(eventFullDto.getInitiator())
                .paid(eventFullDto.getPaid())
                .title(eventFullDto.getTitle())
                .build();
    }

}
