package ru.practicum.ewm.dto.request;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EventConfirmedDto {

    Long eventId;

    Long confirmed;
}
