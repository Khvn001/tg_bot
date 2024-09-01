package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.repository.CityRepository;
import com.telegrambot.marketplace.service.entity.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<City> findByCountryIdAndAllowed(final Long countryId) {
        return cityRepository.findAllByCountryIdAndAllowedIsTrue(countryId);
    }

    @Override
    public City findByCountryAndNameAndAllowedTrue(final Country country, final String name) {
        return cityRepository.findByCountryAndAllowedIsTrueAndName(country, name).orElse(null);
    }

    @Override
    public City findByCountryAndName(final Country country, final String name) {
        return cityRepository.findByCountryAndName(country, name).orElse(null);
    }

    @Override
    public City findById(final Long cityId) {
        return cityRepository.findByIdAndAllowedIsTrue(cityId).orElse(null);
    }

    @Override
    @Transactional
    public City save(final City city) {
        return cityRepository.save(city);
    }

}
