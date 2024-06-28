package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;

import java.util.List;

public interface CityService {

    List<City> findByCountryIdAndAllowed(Long countryId);

    City findByCountryAndName(Country country, String name);

    City findById(Long countryId);
}
