package ru.practicum.ewm.user.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserShortDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotBlank
    private Long id;

    @NotBlank
    String name;
}
