package ru.practicum.ewm.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
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
