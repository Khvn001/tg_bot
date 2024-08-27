package com.telegrambot.marketplace.controller;

import com.telegrambot.marketplace.dto.web.LoginRequestDto;
import com.telegrambot.marketplace.dto.web.TwoTokenResponseDto;
import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.service.auth.TokenService;
import com.telegrambot.marketplace.service.entity.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Операции с авторизацией")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @Operation(summary = "Авторизация в приложение.")
    @ApiResponse(responseCode = "200", description = "ok")
    @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content = @Content(
                    schema = @Schema(implementation = UnifiedResponseDto.class),
                    examples = {
                            @ExampleObject(name = "ErrorExample", value =
                                    "{\n" +
                                            "  \"data\": null,\n" +
                                            "  \"status\": 412,\n" +
                                            "  \"message\": \"Error message\",\n" +
                                            "  \"errors\": [\"error detail\"],\n" +
                                            "  \"timestamp\": \"2024-02-04T20:46:06.808Z\"\n" +
                                            "}"
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "422",
            description = "User Not Found",
            content = @Content(
                    schema = @Schema(implementation = UnifiedResponseDto.class),
                    examples = {
                            @ExampleObject(name = "ErrorExample", value =
                                    "{\n" +
                                            "  \"data\": null,\n" +
                                            "  \"status\": 429,\n" +
                                            "  \"message\": \"Error message\",\n" +
                                            "  \"errors\": [\"error detail\"],\n" +
                                            "  \"timestamp\": \"2024-02-04T20:46:06.808Z\"\n" +
                                            "}"
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка сервера",
            content = @Content(
                    schema = @Schema(implementation = UnifiedResponseDto.class),
                    examples = {
                            @ExampleObject(name = "ErrorExample", value =
                                    "{\n" +
                                            "  \"data\": null,\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"message\": \"Error message\",\n" +
                                            "  \"errors\": [\"error detail\"],\n" +
                                            "  \"timestamp\": \"2024-02-04T20:46:06.808Z\"\n" +
                                            "}"
                            )
                    }
            )
    )
    @SecurityRequirements()
    @PostMapping()
    public UnifiedResponseDto<TwoTokenResponseDto> authenticateUser(
            @RequestBody @Valid final LoginRequestDto loginRequestDto) {
        User user = userService.findByChatId(String.valueOf(loginRequestDto.getChatId()));

        if (user == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!user.getPassword().equals(loginRequestDto.getPassword())) {
            throw new AccessDeniedException("Wrong password.");
        }

        return new UnifiedResponseDto<>(tokenService.createTokens(user));
    }
}
