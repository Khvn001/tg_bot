package com.telegrambot.marketplace.repository;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByCountryAndCityAndNameAndAllowedIsTrue(Country country, City city, String name);

    Optional<District> findByCountryAndCityAndName(Country country, City city, String name);

    List<District> findByAllowedIsFalse();

    List<District> findByAllowedIsTrue();
}
