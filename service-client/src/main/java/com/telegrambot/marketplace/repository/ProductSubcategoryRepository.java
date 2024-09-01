package com.telegrambot.marketplace.repository;

import com.telegrambot.marketplace.entity.product.description.ProductSubcategory;
import com.telegrambot.marketplace.enums.ProductSubcategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSubcategoryRepository extends JpaRepository<ProductSubcategory, Long> {

    Optional<ProductSubcategory> findByAllowedIsTrueAndName(ProductSubcategoryName name);

    Optional<ProductSubcategory> findByName(ProductSubcategoryName name);

    List<ProductSubcategory> findByAllowedIsFalse();

    List<ProductSubcategory> findByAllowedIsTrue();
}
