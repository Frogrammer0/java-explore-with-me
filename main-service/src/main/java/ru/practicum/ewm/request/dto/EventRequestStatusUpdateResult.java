package ru.practicum.ewm.request.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    ParticipationRequestDto confirmedRequests;

    ParticipationRequestDto rejectedRequests;
}
