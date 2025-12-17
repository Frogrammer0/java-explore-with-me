package ru.practicum.ewm.dto.comment;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private Long event;
    private UserShortDto author;
    private CommentStatus status;
    private LocalDateTime createdOn;
}
