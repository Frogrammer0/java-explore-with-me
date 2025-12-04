package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "неверная длина аннотации")
    String annotation;

     Long category;

    @Size(min = 20, max = 7000, message = "неверная длина описания")
    String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    EventState state;

    @Size(min = 3, max = 120, message = "неверная длина заголовка")
    String title;
}
