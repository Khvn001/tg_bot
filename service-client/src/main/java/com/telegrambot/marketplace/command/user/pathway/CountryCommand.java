package com.telegrambot.marketplace.command.user.pathway;

import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.typehandlers.CallbackHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.ProductInventoryCityService;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.service.entity.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CountryCommand implements Command {

    private final CountryService countryService;
    private final CityService cityService;
    private final ProductInventoryCityService productInventoryCityService;
    private final UserService userService;

    @Override
    public Class handler() {
        return CallbackHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/country_";
    }

    @SneakyThrows
    @Override
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (UserType.ADMIN.equals(user.getPermissions())
                || UserType.COURIER.equals(user.getPermissions())
                || UserType.MODERATOR.equals(user.getPermissions())) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("You do not have permission.")
                    .build();
        }

        CountryName countryName = CountryName.valueOf(update.getArgs().getFirst().toUpperCase());
        log.info(countryName.getCountry());
        user.setCountry(countryService.findByCountryNameAndAllowedTrue(countryName));
        userService.save(user);
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Please select a City or go back:")
                .buttons(getCityButtons(countryName))
                .build();
    }

    private List<InlineKeyboardButton> getCityButtons(final CountryName countryName) {
        // Fetch cities from the database with boolean field is_allowed = true
        Country country = countryService.findByCountryNameAndAllowedTrue(countryName);
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Change Country")
                .callbackData("/start")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("View Basket")
                .callbackData("/basket_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("View Profile")
                .callbackData("/profile_")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("Add Balance")
                .callbackData("/addBalance_")
                .build());
        if (country != null) {
            List<City> cities = cityService.findByCountryIdAndAllowed(country.getId());
            log.info("{} cities found", cities.size());
            for (City city : cities) {
                if (productInventoryCityService.findAvailableProducts(city) == null) {
                    continue;
                }
                buttons.add(InlineKeyboardButton.builder()
                        .text(city.getName())
                        .callbackData("/city_" + city.getId() + "_" + countryName)
                        .build());
            }
        }
        return buttons;
    }
}
