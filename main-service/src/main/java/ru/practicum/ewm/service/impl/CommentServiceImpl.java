package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.CommentStatus;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto create(Long userId, Long eventId, NewCommentDto commentDto) {
        log.info("создание коммента от пользователя id = {}, на событие id = {}, с содержанием dto = {}", userId,
                eventId, commentDto);

        User author = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("нельзя комментировать не опубликованные события");
        }

        Comment comment = commentMapper.toComment(commentDto, author, event);

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getPublishedCommentsByEvent(Long eventId, int from, int size) {
        log.info("получение опубликованных комментариев на событие id = {}", eventId);
        getEventOrThrow(eventId);
        PageRequest page = PageRequest.of(from / size, size);
        return commentRepository.findByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, page)
                .stream().map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public List<CommentDto> getCommentByStatus(CommentStatus status, int from, int size) {
        log.info("получение комментариев со статусом status = {}", status);
        PageRequest page = PageRequest.of(from / size, size);
        return commentRepository.findByStatus(status, page).stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto moderateComment(Long commentId, CommentStatus status) {
        Comment comment = getCommentOrThrow(commentId);

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("комментарий уже опубликован");
        }
        if (status == CommentStatus.PENDING) {
            throw new ConflictException("нельзя установить статус PENDING");
        }

        comment.setStatus(status);

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не найден пользователь с id = " + userId)
        );
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("не найдено событие с id = " + eventId)
        );
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("не найден комментарий с id = " + commentId)
        );
    }
}
