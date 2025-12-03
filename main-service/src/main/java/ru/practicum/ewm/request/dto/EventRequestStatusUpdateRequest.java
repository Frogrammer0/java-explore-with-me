package ru.practicum.ewm.request.dto;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.EventStatus;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    List<Long> requestsId;

    EventStatus status;
}
