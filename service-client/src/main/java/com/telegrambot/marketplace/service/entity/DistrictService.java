package com.telegrambot.marketplace.service.entity;

import com.telegrambot.marketplace.entity.location.City;
import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.entity.location.District;

public interface DistrictService {

    District findById(Long districtId);

    District findByCountryAndCityAndNameAndAllowedTrue(Country country, City city, String name);

    District findByCountryAndCityAndName(Country country, City city, String name);

    District save(District district);
}
