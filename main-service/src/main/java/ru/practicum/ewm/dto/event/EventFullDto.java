package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    String annotation;

    @NotBlank
    String title;

    String description;

    CategoryDto categoryDto;

    Long confirmedRequests;

    @NotBlank
    Boolean paid;

    Integer participantLimit;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotBlank
    LocalDateTime eventDate;


    @NotBlank
    UserShortDto initiator;

    @NotBlank
    Location location;


    @Builder.Default
    Boolean requestModeration = true;

    EventState state;

    Long views;
}
