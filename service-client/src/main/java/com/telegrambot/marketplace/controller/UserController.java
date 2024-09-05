package com.telegrambot.marketplace.controller;

import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.service.entity.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/add")
@Tag(name = "Команды администратора для работы с юзером")
@Slf4j
public class UserController {

    private final UserService userService;

    // Get user balance by user ID
    @GetMapping("/{chatId}/balance")
    public UnifiedResponseDto<BigDecimal> getUserBalance(@PathVariable final String chatId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        User user = userService.findByChatId(chatId);
        if (user != null) {
            return new UnifiedResponseDto<>(user.getBalance());
        } else {
            throw new NotFoundException("User not found.");
        }
    }

    // Add balance to a user by user ID
    @PostMapping("/{chatId}/balance/add")
    public UnifiedResponseDto<String> addUserBalance(@PathVariable final String chatId,
                                                     @RequestBody @Valid final BigDecimal amount) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        User user = userService.findByChatId(chatId);
        if (user != null) {
            userService.addUserBalance(user, amount);
            return new UnifiedResponseDto<>("Balance added successfully.");
        } else {
            throw new NotFoundException("User not found.");
        }
    }

    // Deduct balance from a user by user ID
    @PostMapping("/{chatId}/balance/deduct")
    public UnifiedResponseDto<String> deductUserBalance(@PathVariable final String chatId,
                                                        @RequestBody @Valid final BigDecimal amount) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        User user = userService.findByChatId(chatId);
        if (user != null) {
            userService.addUserBalance(user, amount.negate());
            return new UnifiedResponseDto<>("Balance deducted successfully.");
        } else {
            throw new NotFoundException("User not found.");
        }
    }

}
