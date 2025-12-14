package ru.practicum.ewm.dto.request;

import lombok.*;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
