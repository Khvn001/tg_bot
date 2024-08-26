package com.telegrambot.marketplace.command.admin.availability;

import com.telegrambot.marketplace.command.admin.AdminCommand;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.DistrictService;
import com.telegrambot.marketplace.config.typehandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ToggleDistrictAvailabilityCommand implements AdminCommand {

    private final DistrictService districtService;
    private final CityService cityService;
    private final CountryService countryService;

    @Override
    public Class handler() {
        return CommandHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "/admin_toggle_district_availability_";
    }

    @Override
    @SneakyThrows
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (!UserType.ADMIN.equals(user.getPermissions())) {
            return new SendMessageBuilder().chatId(user.getChatId())
                    .message("You do not have permission to toggle district availability.")
                    .build();
        }

        String[] args = update.getArgs().toArray(new String[0]);
        if (args.length < 2) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Usage: /admin_toggle_district_availability_ <country> <city> <district>")
                    .build();
        }

        String countryName = args[0];
        String cityName = args[1];
        String districtName = args[2];

        Country country = countryService.findByCountryName(CountryName.valueOf(countryName));
        if (country == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Country not found.")
                    .build();
        }

        City city = cityService.findByCountryAndName(country, cityName);
        if (city == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("City not found.")
                    .build();
        }

        District district = districtService.findByCountryAndCityAndName(country, city, districtName);
        if (district == null) {
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("District not found.")
                    .build();
        }

        district.setAllowed(!district.isAllowed());
        districtService.save(district);

        String status = district.isAllowed() ? "available" : "unavailable";

        log.info("District: {}. New Status: {}", district, status);

        return new SendMessageBuilder()
                .chatId(user.getChatId()).message("District " + district.getName() + " is now " + status + ".")
                .build();
    }
}
