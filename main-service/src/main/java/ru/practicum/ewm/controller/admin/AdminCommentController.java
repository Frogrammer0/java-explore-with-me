package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.model.enums.CommentStatus;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentForModeration(
            @RequestParam CommentStatus status,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
            ) {
        log.info("getCommentForModeration in AdminCommentController");
        return commentService.getCommentByStatus(status, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto moderateComment(
            @PathVariable Long commentId,
            @RequestParam CommentStatus status
    ) {
        log.info("moderateComment in AdminCommentController");
        return commentService.moderateComment(commentId, status);
    }
}
