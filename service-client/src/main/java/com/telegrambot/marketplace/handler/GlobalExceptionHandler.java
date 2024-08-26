package com.telegrambot.marketplace.handler;

import com.telegrambot.marketplace.dto.web.ExceptionMessage;
import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.exception.CustomAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
    })
    public ResponseEntity<Object> handleBadArgumentsExceptions(final RuntimeException ex) {
        final ExceptionMessage exceptionMessage = new ExceptionMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex.getLocalizedMessage(),
                ex.getClass().getSimpleName()
        );

        log.error(exceptionMessage.toString(), ex);

        var resp = new UnifiedResponseDto<>(exceptionMessage);

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
    })
    public ResponseEntity<Object> handleValidationException(final RuntimeException ex) {
        final ExceptionMessage exceptionMessage = new ExceptionMessage(
                HttpStatus.PRECONDITION_FAILED.value(),
                ex.getLocalizedMessage(),
                ex.getClass().getSimpleName()
        );

        log.error(exceptionMessage.toString(), ex);

        var resp = new UnifiedResponseDto<>(exceptionMessage);

        return new ResponseEntity<>(resp, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler({
    })
    public ResponseEntity<Object> handleNotFoundException(final RuntimeException ex) {
        final ExceptionMessage exceptionMessage = new ExceptionMessage(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getLocalizedMessage(),
                ex.getClass().getSimpleName()
        );

        log.error(exceptionMessage.toString(), ex);

        var resp = new UnifiedResponseDto<>(exceptionMessage);

        return new ResponseEntity<>(resp, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException ex) {
        final ExceptionMessage exceptionMessage = new ExceptionMessage(
                HttpStatus.FORBIDDEN.value(),
                ex.getLocalizedMessage(),
                ex.getClass().getSimpleName()
        );

        log.error(exceptionMessage.toString(), ex);

        var resp = new UnifiedResponseDto<>(exceptionMessage);

        return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({CustomAuthenticationException.class, AuthenticationException.class})
    public ResponseEntity<Object> handleCustomAuthenticationException(final RuntimeException ex) {
        final ExceptionMessage exceptionMessage = new ExceptionMessage(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getLocalizedMessage(),
                ex.getClass().getSimpleName()
        );

        log.error(exceptionMessage.toString(), ex);

        var resp = new UnifiedResponseDto<>(exceptionMessage);

        return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    }

    @NotNull
    private static ResponseEntity<Object> getFieldErrorsResponse(@NotNull final BindException ex) {
        final List<String> errors = new ArrayList<>();

        ex.getFieldErrors().forEach(fieldError -> errors.add(
                        "Error: "
                                + fieldError.getDefaultMessage()
                                + ", "
                                + fieldError.getObjectName()
                                + ": "
                                + fieldError.getField()
                                + " = ["
                                + fieldError.getRejectedValue()
                                + "]"
                )
        );

        final ExceptionMessage exceptionMessage = new ExceptionMessage(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );

        log.error(exceptionMessage.toString(), ex);

        var resp = new UnifiedResponseDto<>(exceptionMessage);

        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
