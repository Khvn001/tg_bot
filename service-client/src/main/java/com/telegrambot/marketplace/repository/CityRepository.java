package com.telegrambot.marketplace.repository;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByCountryIdAndAllowedIsTrue(Long countryId);

    Optional<City> findByIdAndAllowedIsTrue(Long cityId);

    Optional<City> findByCountryAndAllowedIsTrueAndName(Country country, String name);

    Optional<City> findByCountryAndName(Country country, String name);

    Optional<City> findByName(String cityName);

    List<City> findByAllowedIsFalse();

    List<City> findByAllowedIsTrue();
}
