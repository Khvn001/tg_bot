package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.product.description.ProductCategory;

public interface ProductCategoryService {

    ProductCategory findByNameAndAllowedTrue(String productCategoryName);

    ProductCategory findByName(String productCategoryName);

    ProductCategory save(ProductCategory category);

    ProductCategory findById(Long categoryId);
}
