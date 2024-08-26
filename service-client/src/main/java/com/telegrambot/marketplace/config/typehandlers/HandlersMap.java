package com.telegrambot.marketplace.config.typehandlers;

import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.TelegramType;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Component
@Slf4j
public class HandlersMap {
    private final EnumMap<TelegramType, List<Handler>> hashMap = new EnumMap<>(TelegramType.class);
    private final List<Handler> handlers;

    // Тут точно также находим все обработчики, просто в первом случае я использовал
    // @Autowired. Это немного лучше.
    public HandlersMap(final List<Handler> handlers) {
        this.handlers = handlers;
    }

    @PostConstruct
    private void init() {
        for (Handler handler : handlers) {
            if (!hashMap.containsKey(handler.getHandleType())) {
                hashMap.put(handler.getHandleType(), new ArrayList<>());
            }
            hashMap.get(handler.getHandleType()).add(handler);
        }
        log.info(hashMap.toString());

        hashMap.values().forEach(h -> h.sort((o1, o2) -> o2.priority() - o1.priority()));
    }

    public Answer execute(final ClassifiedUpdate classifiedUpdate, final User user) {
        log.info(classifiedUpdate.getLog());
        log.info(hashMap.keySet().toString());
        log.info(hashMap.values().toString());
        if (!hashMap.containsKey(classifiedUpdate.getTelegramType())) {
            return new Answer();
        }

        for (Handler handler : hashMap.get(classifiedUpdate.getTelegramType())) {
            if (handler.condition(user, classifiedUpdate)) {
                return handler.getAnswer(user, classifiedUpdate);
            }
        }
        return null;
    }
}
