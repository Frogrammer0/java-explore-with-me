package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    Long id;

    Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50, message = "неверная длинна заголовка")
    String title;

    List<EventShortDto> events;

}
