package com.telegrambot.marketplace.controller.courier;

import com.telegrambot.marketplace.dto.web.AddProductPortionRequestDto;
import com.telegrambot.marketplace.dto.web.UnifiedResponseDto;
import com.telegrambot.marketplace.entity.inventory.ProductPortion;
import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.entity.product.description.Product;
import com.telegrambot.marketplace.entity.product.description.ProductCategory;
import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.entity.user.User;
import com.telegrambot.marketplace.enums.UserType;
import com.telegrambot.marketplace.exception.NotFoundException;
import com.telegrambot.marketplace.service.entity.CityService;
import com.telegrambot.marketplace.service.entity.CountryService;
import com.telegrambot.marketplace.service.entity.DistrictService;
import com.telegrambot.marketplace.service.entity.ProductCategoryService;
import com.telegrambot.marketplace.service.entity.ProductPortionService;
import com.telegrambot.marketplace.service.entity.ProductService;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import com.telegrambot.marketplace.service.entity.UserService;
import com.telegrambot.marketplace.service.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courier")
@AllArgsConstructor
@Tag(name = "Product Portion Management",
        description = "Endpoints for managing product portions by couriers and admins")
@Slf4j
public class ProductPortionController {

    private final UserService userService;
    private final CountryService countryService;
    private final CityService cityService;
    private final DistrictService districtService;
    private final ProductCategoryService productCategoryService;
    private final ProductSubcategoryService productSubcategoryService;
    private final ProductService productService;
    private final ProductPortionService productPortionService;
    private final S3Service s3Service;

    @Operation(summary = "Add a new product portion",
            description = "Allows a courier to add a new product portion with images",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portion created successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })
    @PostMapping("/add/product_portion")
    public UnifiedResponseDto<String> addProductPortion(
            @RequestBody @Valid final AddProductPortionRequestDto requestDto,
            @RequestParam("photos") @NotNull final List<MultipartFile> photos) {

        final User courier = getUser(requestDto.getCourierId());
        final Country country = countryService.findById(requestDto.getCountryId());
        final City city = cityService.findById(requestDto.getCityId());
        final District district = districtService.findById(requestDto.getDistrictId());
        final ProductCategory category = productCategoryService.findById(requestDto.getCategoryId());
        final ProductSubcategory subcategory = productSubcategoryService.findById(requestDto.getSubcategoryId());
        final Product product = productService.findById(requestDto.getProductId());

        List<String> photoNames = new ArrayList<>();

        // Generate photo names
        for (MultipartFile photo : photos) {
            String photoName = "COURIER:" + courier.getId() +
                    "COUNTRY:" + country.getName().name() +
                    "CITY:" + city.getName() +
                    "DISTRICT:" + district.getName() +
                    "CATEGORY:" + category.getName().name() +
                    "SUBCATEGORY:" + subcategory.getName().name() +
                    "PRODUCT:" + product.getName() +
                    "LATITUDE:" + requestDto.getLatitude() +
                    "LONGITUDE:" + requestDto.getLongitude() +
                    "AMOUNT:" + requestDto.getAmount() +
                    "FILEID:" + photo.getOriginalFilename() + ".jpg";

            photoNames.add(photoName);
        }

        // Upload files to S3 with custom names
        List<String> photoUrls = s3Service.uploadMultipartFilesWithCustomNames(photoNames, photos);

        ProductPortion savedProductPortion = productPortionService.saveProductPortion(
                courier, country, city, district, category, subcategory,
                product, requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getAmount(), photoUrls);

        return new UnifiedResponseDto<>("ProductPortion created successfully: " + savedProductPortion.toString());
    }

    private @NotNull User getUser(final String courierId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!(UserType.COURIER.equals(currentUser.getPermissions())
                || UserType.ADMIN.equals(currentUser.getPermissions()))) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }

        if (UserType.ADMIN.equals(currentUser.getPermissions())) {
            User courier = userService.findByChatId(courierId);
            if (courier == null) {
                throw new NotFoundException("Courier not found");
            }
            if (UserType.COURIER.equals(courier.getPermissions())
                    || UserType.ADMIN.equals(courier.getPermissions())) {
                return courier;
            }
        }

        return currentUser;
    }

    @Operation(summary = "Get product portions by courier",
            description = "Allows an admin to fetch product portions for a specific courier or for themselves if " +
                    "the courier ID is not provided. A courier can only see their own product portions.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Courier not found",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })
    @GetMapping("/product_portions")
    public UnifiedResponseDto<List<ProductPortion>> getProductPortionsByCourier(
            @RequestParam(value = "courierId", required = false) final String courierId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (UserType.ADMIN.equals(currentUser.getPermissions())) {
            if (courierId != null) {
                User courier = userService.findByChatId(courierId);
                if (courier == null) {
                    throw new NotFoundException("Courier not found");
                }
                List<ProductPortion> productPortions = productPortionService.getProductPortionsByCourier(courier);
                return new UnifiedResponseDto<>(productPortions);
            } else {
                List<ProductPortion> productPortions = productPortionService.getProductPortionsByCourier(currentUser);
                return new UnifiedResponseDto<>(productPortions);  // Or fetch all product portions if needed
            }
        }

        if (UserType.COURIER.equals(currentUser.getPermissions())) {
            List<ProductPortion> productPortions = productPortionService.getProductPortionsByCourier(currentUser);
            return new UnifiedResponseDto<>(productPortions);
        }

        throw new AccessDeniedException("You do not have permission to view product portions.");
    }

    @Operation(summary = "Get product portions by city",
            description = "Allows an admin to fetch product portions for a specific city",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "City not found",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })

    @GetMapping("/city/{cityId}/product_portions")
    public UnifiedResponseDto<List<ProductPortion>> getProductPortionsByCity(@PathVariable final Long cityId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }
        City city = cityService.findById(cityId);
        List<ProductPortion> productPortions = productPortionService.getProductPortionsByCity(city);
        return new UnifiedResponseDto<>(productPortions);
    }

    @Operation(summary = "Get product portions by district",
            description = "Allows an admin to fetch product portions for a specific district",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "District not found",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })
    @GetMapping("/district/{districtId}/product_portions")
    public UnifiedResponseDto<List<ProductPortion>> getProductPortionsByDistrict(@PathVariable final Long districtId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }
        District district = districtService.findById(districtId);
        List<ProductPortion> productPortions = productPortionService.getProductPortionsByDistrict(district);
        return new UnifiedResponseDto<>(productPortions);
    }

    @Operation(summary = "Get product portions by subcategory",
            description = "Allows an admin to fetch product portions for a specific product subcategory",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Subcategory not found",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })
    @GetMapping("/subcategory/{subcategoryId}/product_portions")
    public UnifiedResponseDto<List<ProductPortion>> getProductPortionsBySubcategory(
            @PathVariable final Long subcategoryId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }
        ProductSubcategory subcategory = productSubcategoryService.findById(subcategoryId);
        List<ProductPortion> productPortions = productPortionService.getProductPortionsBySubcategory(subcategory);
        return new UnifiedResponseDto<>(productPortions);
    }

    @Operation(summary = "Get product portions by product",
            description = "Allows an admin to fetch product portions for a specific product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })
    @GetMapping("/product/{productId}/product_portions")
    public UnifiedResponseDto<List<ProductPortion>> getProductPortionsByProduct(@PathVariable final Long productId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        if (!UserType.ADMIN.equals(currentUser.getPermissions())) {
            throw new AccessDeniedException("You do not have permission to add entities.");
        }
        Product product = productService.findById(productId);
        List<ProductPortion> productPortions = productPortionService.getProductPortionsByProduct(product);
        return new UnifiedResponseDto<>(productPortions);
    }

    @Operation(summary = "Delete a product portion",
            description = "Allows an admin or the courier who created the product portion to delete it.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product portion deleted successfully",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to " +
                            "delete this product portion",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Product portion not found",
                            content = @Content(schema = @Schema(implementation = UnifiedResponseDto.class)))
            })
    @DeleteMapping("/delete/product_portion/{productPortionId}")
    public UnifiedResponseDto<String> deleteProductPortion(@PathVariable final Long productPortionId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser == null) {
            throw new NotFoundException("User Not Found");
        }

        ProductPortion productPortion = productPortionService.findById(productPortionId);
        if (productPortion == null) {
            throw new NotFoundException("Product Portion Not Found");
        }

        // Check if the current user is either an admin or the owner of the product portion (courier)
        if (!(UserType.ADMIN.equals(currentUser.getPermissions()) ||
                productPortion.getCourier().getId().equals(currentUser.getId()))) {
            throw new AccessDeniedException("You do not have permission to delete this product portion.");
        }

        // Delete the product portion
        productPortionService.deleteProductPortion(productPortionId);

        return new UnifiedResponseDto<>("Product portion deleted successfully.");
    }

}
