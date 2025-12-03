package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
public class CompilationDto {

    @NotBlank
    Long id;

    @NotBlank
    Boolean pinned;

    @NotBlank
    String title;

    List<EventShortDto> events;

}
