package com.telegrambot.marketplace.repository;


import com.telegrambot.marketplace.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findAllById(Long id);

    Optional<User> findByChatId(Long chatId);

    @Query("SELECT SUM(u.balance) FROM User u")
    BigDecimal sumOfBalances();
}
