package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.StateAction;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "неверная длина аннотации")
    String annotation;

    Long category;

    @Size(min = 20, max = 7000, message = "неверная длина описания")
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    @Positive
    Integer participantLimit;

    Boolean requestModeration;

    StateAction stateAction;

    @Size(min = 3, max = 120, message = "неверная длина названия")
    String title;
}
