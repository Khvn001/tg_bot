package com.telegrambot.marketplace.command.courier;

import com.fasterxml.jackson.databind.JsonNode;
import com.telegrambot.marketplace.command.Command;
import com.telegrambot.marketplace.config.BotConfig;
import com.telegrambot.marketplace.config.typehandlers.PhotoHandler;
import com.telegrambot.marketplace.dto.bot.Answer;
import com.telegrambot.marketplace.dto.bot.ClassifiedUpdate;
import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.StateType;
import com.telegrambot.marketplace.service.s3.S3Service;
import com.telegrambot.marketplace.dto.bot.SendMessageBuilder;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.DistrictService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductPortionService;
import com.telegrambot.marketplace.service.entity.ProductService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.service.entity.StateService;
import com.telegrambot.marketplace.service.entity.UserService;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.product.description.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PhotoCommand implements Command {

    private final UserService userService;
    private final StateService stateService;
    private final CountryService countryService;
    private final CityService cityService;
    private final DistrictService districtService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;
    private final ProductService productService;
    private final ProductPortionService productPortionService;
    private final S3Service s3Service;
    private final BotConfig botConfig;

    private static final int ZERO_NUMBER = 0;
    private static final int ONE_NUMBER = 1;
    private static final int TWO_NUMBER = 2;
    private static final int THREE_NUMBER = 3;
    private static final int FOUR_NUMBER = 4;
    private static final int FIVE_NUMBER = 5;
    private static final int SIX_NUMBER = 6;
    private static final int SEVEN_NUMBER = 7;
    private static final int EIGHT_NUMBER = 8;

    @Override
    public Class<?> handler() {
        return PhotoHandler.class;
    }

    @Override
    public Object getFindBy() {
        return "PHOTO";
    }

    @Override
    @Transactional
    public Answer getAnswer(final ClassifiedUpdate update, final User user) {
        if (StateType.PRODUCT_PORTION_PHOTO.equals(user.getState().getStateType())) {

            String stateValue = user.getState().getValue();
            String[] parts = stateValue.split(" ");

            Long countryId = Long.valueOf(parts[ZERO_NUMBER]);
            Long cityId = Long.valueOf(parts[ONE_NUMBER]);
            Long districtId = Long.valueOf(parts[TWO_NUMBER]);
            Long categoryId = Long.valueOf(parts[THREE_NUMBER]);
            Long subcategoryId = Long.valueOf(parts[FOUR_NUMBER]);
            Long productId = Long.valueOf(parts[FIVE_NUMBER]);
            BigDecimal latitude = new BigDecimal(parts[SIX_NUMBER]);
            BigDecimal longitude = new BigDecimal(parts[SEVEN_NUMBER]);
            BigDecimal amount = new BigDecimal(parts[EIGHT_NUMBER]);

            Country country = countryService.findById(countryId);
            City city = cityService.findById(cityId);
            District district = districtService.findById(districtId);
            ProductCategory category = productCategoryService.findById(categoryId);
            ProductSubcategory subcategory = productSubcategoryService.findById(subcategoryId);
            Product product = productService.findById(productId);

            List<PhotoSize> photos = update.getUpdate().getMessage().getPhoto();
            PhotoSize largestPhoto = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null);
            if (largestPhoto != null) {
                String fileId = largestPhoto.getFileId();
                String photoName = "COURIER:" + user.getChatId() + "COUNTRY:" + country.getName().name()
                        + "CITY:" + city.getName() + "DISTRICT:" + district.getName()
                        + "CATEGORY:" + category.getName().name() + "SUBCATEGORY:" + subcategory.getName().name()
                        + "PRODUCT:" + product.getName() + "LATITUDE:" + latitude + "LONGITUDE:" + longitude
                        + "AMOUNT:" + amount + "FILEID:" + fileId + ".jpg";
                String photoUrl = downloadPhotoFromTelegram(fileId, botConfig.getToken(), photoName);
                if (photoUrl != null) {
                    log.info(photoUrl);
                    ProductPortion savedProductPortion = productPortionService.saveProductPortion(
                            user, country, city, district, category, subcategory,
                            product, latitude, longitude, amount, photoUrl);
                    log.info(savedProductPortion.toString());

                    // Finish the form
                    user.getState().setStateType(StateType.NONE);
                    user.getState().setValue(null);
                    stateService.save(user.getState());
                    userService.save(user);

                    List<InlineKeyboardButton> buttons = new ArrayList<>();
                    buttons.add(InlineKeyboardButton.builder()
                            .text("Start New ProductPortion Form")
                            .callbackData("/start_productportion_form")
                            .build());

                    return new SendMessageBuilder()
                            .chatId(user.getChatId())
                            .message("ProductPortion has been created successfully.")
                            .buttons(buttons)
                            .build();
                }
            }
            return new SendMessageBuilder()
                    .chatId(user.getChatId())
                    .message("Please upload a photo.")
                    .build();
        }
        return new SendMessageBuilder()
                .chatId(user.getChatId())
                .message("Photo is not requested.")
                .build();
    }

    private String downloadPhotoFromTelegram(final String fileId, final String botToken, final String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        String filePathUrl = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(filePathUrl, JsonNode.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String filePath = response.getBody().get("result").get("file_path").asText();
            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;

            // Download the file from the fileUrl
            byte[] fileBytes = restTemplate.getForObject(fileUrl, byte[].class);
            if (fileBytes != null) {
                // Upload to S3
                return s3Service.uploadFile(fileName, fileBytes);
            }
        }
        return null;
    }
}
