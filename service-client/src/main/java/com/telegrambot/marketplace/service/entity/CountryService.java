package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.enums.CountryName;

import java.util.List;

public interface CountryService {

    Country findByCountryName(CountryName countryName);

    List<Country> findAllByAllowedIsTrue();

    Country save(Country country);
}
