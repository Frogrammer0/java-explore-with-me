package ru.practicum.ewm.request.dto;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    Long id;

    Long event;

    Long requester;

    LocalDateTime created;

    RequestStatus status;
}
