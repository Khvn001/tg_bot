package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import com.telegrambot.marketplace.repository.ProductSubcategoryRepository;
import com.telegrambot.marketplace.service.entity.ProductSubcategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSubcategoryServiceImpl implements ProductSubcategoryService {

    private final ProductSubcategoryRepository productSubcategoryRepository;

    public ProductSubcategory findByName(final String productCategoryName) {
        return productSubcategoryRepository
                .findByAllowedIsTrueAndName(ProductSubcategoryName.valueOf(productCategoryName))
                .orElse(null);
    }

    @Override
    @Transactional
    public ProductSubcategory save(final ProductSubcategory subcategory) {
        return productSubcategoryRepository.save(subcategory);
    }
}
