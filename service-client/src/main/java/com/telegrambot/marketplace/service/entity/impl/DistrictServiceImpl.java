package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;
import com.telegrambot.marketplace.repository.DistrictRepository;
import com.telegrambot.marketplace.service.entity.DistrictService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;

    @Override
    public District findById(final Long districtId) {
        return districtRepository.findById(districtId).orElse(null);
    }

    @Override
    public District findByCountryAndCityAndNameAndAllowedTrue(
            final Country country, final City city, final String name) {
        return districtRepository.findByCountryAndCityAndNameAndAllowedIsTrue(country, city, name).orElse(null);
    }

    @Override
    public District findByCountryAndCityAndName(final Country country, final City city, final String name) {
        return districtRepository.findByCountryAndCityAndName(country, city, name).orElse(null);
    }

    @Override
    @Transactional
    public District save(final District district) {
        return districtRepository.save(district);
    }
}
