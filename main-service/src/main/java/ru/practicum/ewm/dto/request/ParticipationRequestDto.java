package ru.practicum.ewm.dto.request;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.RequestStatus;

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

    Long requester;

    Long event;

    RequestStatus status;

    LocalDateTime created;

}
