package com.telegrambot.marketplace.command.admin.add;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AddCountryCommand implements AdminCommand {

    private final CountryService countryService;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_add_country_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission to add countries.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < 1) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_add_country_ <country>")
                    .build();
        }

        String countryName = args[0].toUpperCase();

        try {
            Country country = countryService.findByCountryName(CountryName.valueOf(countryName));
            if (country != null) {
                return new SendMessageBuilder()
                        .chatId(user.getChatId())
                        .message("Country exists.")
                        .build();
            }

            country = new Country();
            country.setName(CountryName.valueOf(countryName));
            country.setAllowed(true);
            Country savedCountry = countryService.save(country);

            log.info("Added country: {}", country);

            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Country " + savedCountry.getName() + " added successfully" + ".")
                    .build();
        } catch (IllegalArgumentException e) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Invalid country name: " + countryName)
                    .build();
        }
    }
}
