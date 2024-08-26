package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Tag(name = "UnifiedResponse", description = "Универсальная обертка ответа")
@Getter
@ToString
public class UnifiedResponseDto<T> {


    @Schema(description = "Данные ответа", nullable = true)
    private T data = null;

    @Schema(description = "Статус ответа")
    private final int status;

    @Schema(description = "Сообщение о ошибке", nullable = true)
    private final String message;

    @Schema(description = "Список ошибок")
    private final List<String> errors;

    @Schema(description = "Время ответа")
    private final ZonedDateTime timestamp = ZonedDateTime.now();

    public UnifiedResponseDto(final int status, final String message, final List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public UnifiedResponseDto(final int status, final String message, final String error) {
        this.status = status;
        this.message = message;
        this.errors = Collections.singletonList(error);
    }

    public UnifiedResponseDto(final T data, final int status, final String message, final List<String> errors) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public UnifiedResponseDto(final T data, final int status, final String message, final String error) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.errors = Collections.singletonList(error);
    }

    public UnifiedResponseDto(final T data) {
        this.data = data;
        this.status = 0;
        this.message = null;
        this.errors = Collections.emptyList();
    }

    public UnifiedResponseDto(final ExceptionMessage exceptionMessage) {
        this.data = null;
        this.status = exceptionMessage.getStatus();
        this.message = exceptionMessage.getMessage();
        this.errors = exceptionMessage.getErrors();
    }

    public UnifiedResponseDto() {
        this.data = null;
        this.status = 0;
        this.message = null;
        this.errors = Collections.emptyList();
    }

}
