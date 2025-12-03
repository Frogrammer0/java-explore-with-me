package ru.practicum.ewm.request.dto;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.EventStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
public class ParticipationRequestDto {

    Long id;

    Long event;

    Long requester;

    LocalDateTime created;

    EventStatus status;
}
