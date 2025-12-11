package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000, message = "неверная длина аннотации")
    String annotation;

    @NotBlank
    Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "неверная длина описания")
    String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank
    LocalDateTime eventDate;

    @NotBlank
    Location location;

    @Builder.Default
    Boolean paid = false;

    @Builder.Default
    Integer participantLimit = 0;

    @Builder.Default
    Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120, message = "неверная длина названия")
    String title;
}
