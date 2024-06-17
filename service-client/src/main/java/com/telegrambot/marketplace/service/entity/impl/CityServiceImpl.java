package com.telegrambot.marketplace.service.entity.impl;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.repository.CityRepository;
import com.telegrambot.marketplace.service.entity.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<City> findByCountryIdAndAllowed(Long countryId){
        return cityRepository.findAllByCountryIdAndAllowedIsTrue(countryId);
    }

    @Override
    public City findById(Long countryId){
        return cityRepository.findByIdAndAllowedIsTrue(countryId).orElse(null);
    }
}
