package ru.practicum.ewm.dto.request;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.enums.RequestStatus;


import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private RequestStatus status;
}
