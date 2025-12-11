package ru.practicum.ewm.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @Email(message = "Некорректный email")
    @NotBlank
    @Size(min = 6, max = 254, message = "неверная длина адреса электронной почты")
    String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "неверная длина имени")
    String name;
}
