package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.enums.CommentStatus;

import java.util.List;

public interface CommentService {

    CommentDto create(Long userId, Long eventId, NewCommentDto commentDto);

    List<CommentDto> getPublishedCommentsByEvent(Long eventId, int from, int size);

    List<CommentDto> getCommentByStatus(CommentStatus status, int from, int size);

    CommentDto moderateComment(Long commentId, CommentStatus status);
}
