package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.order.Basket;
import com.telegrambot.marketplace.entity.user.State;
import com.telegrambot.marketplace.entity.user.Token;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.StateType;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.repository.BasketRepository;
import com.telegrambot.marketplace.repository.StateRepository;
import com.telegrambot.marketplace.repository.TokenRepository;
import com.telegrambot.marketplace.repository.UserRepository;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StateRepository stateRepository;
    private final BasketRepository basketRepository;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public User findUserByUpdate(final ClassifiedUpdate classifiedUpdate) {

        // Проверим, существует ли этот пользователь.
        if (userRepository.findByChatId(classifiedUpdate.getUserId()).isPresent()) {
            User user = userRepository.findByChatId(classifiedUpdate.getUserId()).get();

            // Если мы не смогли до этого записать имя пользователя, то запишем его.
            if (user.getUserName() == null && classifiedUpdate.getUserName() != null) {
                user.setUserName(classifiedUpdate.getUserName());
            }

            // Проверим менял ли пользователя имя.
            if (user.getUserName() != null && !user.getUserName().equals(classifiedUpdate.getUserName())) {
                user.setUserName(classifiedUpdate.getUserName());
            }

            if (!user.getName().equals(classifiedUpdate.getName())) {
                user.setName(classifiedUpdate.getName());
            }

            log.debug("User found: {}", user.getChatId());

            return user;
        }
        try {
            User user = new User();
            user.setName(classifiedUpdate.getName());
            user.setPassword("");
            user.setPermissions(UserType.BRONZE);
            user.setBalance(BigDecimal.valueOf(0));
            user.setDiscount(0L);
            user.setChatId(classifiedUpdate.getUserId());
            user.setUserName(classifiedUpdate.getUserName());
            log.info(user.toString());
            User savedUser = userRepository.save(user);
            log.info(savedUser.toString());

            State state = new State();
            state.setStateType(StateType.CREATE_PASSWORD);
            state.setUser(savedUser);
            log.info(state.toString());
            State savedState = stateRepository.save(state);
            log.info(savedState.toString());

            Basket basket = new Basket();
            basket.setUser(user);
            basket.setTotalSum(BigDecimal.ZERO);
            log.info(basket.toString());
            Basket savedBasket = basketRepository.save(basket);
            log.info(savedBasket.toString());

            user.setState(savedState);
            user.setBasket(savedBasket);
            User finalUser = userRepository.save(savedUser);
            log.info(finalUser.toString());

            log.info("New User: {}", finalUser.getChatId());

            return finalUser;
        } catch (Exception e) {
            log.error("USER NOT CREATED {}", e.getMessage());
        }

        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User save(final User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByChatId(final String chatId) {
        return userRepository.findByChatId(Long.valueOf(chatId)).orElse(null);
    }

    @Override
    public User getByToken(final String token) {
        return tokenRepository.getByValue(token)
                .map(Token::getUser)
                .orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUserBalance(final User user, final BigDecimal amount) {
        BigDecimal newBalance = user.getBalance().add(amount);

        // Check if the new balance is less than zero
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("User balance cannot be negative.");
        }

        user.setBalance(newBalance);
        userRepository.save(user);
        log.info("User: {}. Balance has been added. Amount: {}", user.getChatId(), amount);
    }

}
