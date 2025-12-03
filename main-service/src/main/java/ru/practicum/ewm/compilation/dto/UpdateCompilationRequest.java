package ru.practicum.ewm.compilation.dto;

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
public class UpdateCompilationRequest {

    List<Long> events;

    Boolean pinned;

    @Size(min = 1, max = 50, message = "неверная длина заголовка")
    String title;
}
