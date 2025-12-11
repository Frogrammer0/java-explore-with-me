package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        return ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Требуемый объект не найден")
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        return ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Конфликт полей или объектов")
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException e) {
        return ApiError.builder()
                .errors(List.of())
                .message(e.getMessage() + e.getLocalizedMessage())
                .reason("Неверный запрос")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleBadRequest(final ForbiddenException e) {
        return ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Ошибка доступа")
                .status(HttpStatus.FORBIDDEN)
                .timestamp(LocalDateTime.now())
                .build();
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingRequestParam(final MissingServletRequestParameterException e) {
        return ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Отсутствует требуемый параметр")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(HttpMessageNotReadableException e) {
        return ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Некорректный формат данных")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationExceptions(MethodArgumentNotValidException e) {

        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Некорректный формат данных")
                .message(e.getMessage())
                .errors(List.of())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Некорректный формат данных")
                .message(e.getMessage())
                .errors(List.of())
                .timestamp(LocalDateTime.now())
                .build();
    }

}
