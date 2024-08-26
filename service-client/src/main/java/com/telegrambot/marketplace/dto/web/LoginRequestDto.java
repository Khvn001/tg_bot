package com.telegrambot.marketplace.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Tag(name = "LoginRequestDto", description = "Объект для авторизации")
@Data
public class LoginRequestDto {

    @NotBlank
    @Schema(description = "ID чата юзера", example = "1308288532", required = true)
    private String chatId;

    @NotBlank
    @Schema(description = "Пароль", example = "qwerty123", required = true)
    private String password;

}
