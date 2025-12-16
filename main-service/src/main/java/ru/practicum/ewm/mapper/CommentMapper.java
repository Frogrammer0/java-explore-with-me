package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class CommentMapper {
    private final UserMapper userMapper;

    public Comment toComment(NewCommentDto commentDto, User author, Event event) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .author(author)
                .status(CommentStatus.PENDING)
                .createdOn(LocalDateTime.now().withNano(0))
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(comment.getEvent().getId())
                .author(userMapper.toUserShortDto(comment.getAuthor()))
                .status(comment.getStatus())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}
