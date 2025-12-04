package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    List<Long> events;

    @Builder.Default
    Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50, message = "неверная длинна заголовка")
    String title;
}
