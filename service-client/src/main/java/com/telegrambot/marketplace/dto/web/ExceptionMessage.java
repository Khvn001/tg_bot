package com.telegrambot.marketplace.dto.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class ExceptionMessage {

    private final int status;

    private final String message;

    private final List<String> errors;

    private final ZonedDateTime timestamp = ZonedDateTime.now();

    public ExceptionMessage(final int status, final String message, final String error) {
        this.status = status;
        this.message = message;
        this.errors = Collections.singletonList(error);
    }
}
