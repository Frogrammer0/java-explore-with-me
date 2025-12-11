package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Component
@NoArgsConstructor
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
