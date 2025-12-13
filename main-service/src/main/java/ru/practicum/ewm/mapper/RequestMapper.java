package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.User;

@Component
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id((request.getEvent() == null) ? 0 : request.getId())
                .event((request.getEvent() == null) ? 0 : request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public Request toRequest(ParticipationRequestDto requestDto, User requester, Event event) {
        return Request.builder()
                .id(requestDto.getId())
                .requester(requester)
                .event(event)
                .status(requestDto.getStatus())
                .created(requestDto.getCreated())
                .build();
    }
}
