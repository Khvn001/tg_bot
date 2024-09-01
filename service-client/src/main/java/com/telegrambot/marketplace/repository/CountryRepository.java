package com.telegrambot.marketplace.repository;

import com.telegrambot.marketplace.entity.location.Country;
import com.telegrambot.marketplace.enums.CountryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByNameAndAllowedIsTrue(CountryName name);

    Optional<Country> findByName(CountryName name);

    List<Country> findAllByAllowedIsTrue();

    List<Country> findByAllowedIsFalse();

    List<Country> findByAllowedIsTrue();
}
