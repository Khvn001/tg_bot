package com.telegrambot.marketplace.repository;

import com.telegrambot.marketplace.entity.user.Token;
import com.telegrambot.marketplace.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> getByValue(String value);

    void deleteAllByUser(User user);

    List<Token> findAllByUserId(Long clientId);

}
