package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.enums.CountryName;
import com.telegrambot.marketplace.repository.CountryRepository;
import com.telegrambot.marketplace.service.entity.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public Country findByCountryNameAndAllowedTrue(final CountryName countryName) {
        return countryRepository.findByNameAndAllowedIsTrue(countryName).orElse(null);
    }

    @Override
    public Country findByCountryName(final CountryName countryName) {
        return countryRepository.findByName(countryName).orElse(null);
    }

    @Override
    public List<Country> findAllByAllowedIsTrue() {
        return countryRepository.findAllByAllowedIsTrue();
    }

    @Override
    @Transactional
    public Country save(final Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country findById(final Long countryId) {
        return countryRepository.findById(countryId).orElse(null);
    }
}

