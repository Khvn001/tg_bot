package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;

import java.util.List;

public interface CityService {

    List<City> findByCountryIdAndAllowed(Long countryId);

    City findByCountryAndNameAndAllowedTrue(Country country, String name);

    City findByCountryAndName(Country country, String name);

    City findById(Long cityId);

    City save(City city);

}
