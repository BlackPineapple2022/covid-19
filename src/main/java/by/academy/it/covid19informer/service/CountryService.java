package by.academy.it.covid19informer.service;

import by.academy.it.covid19informer.entity.Country;

import java.util.List;
import java.util.Optional;

public interface CountryService {

    Optional<Country> findByName(String countryName);

    List<Country> findAll();

    Country create(String countryName);

}
