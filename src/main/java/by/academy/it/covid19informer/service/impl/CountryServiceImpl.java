package by.academy.it.covid19informer.service.impl;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.service.CountryService;
import by.academy.it.covid19informer.util.JSONProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CountryServiceImpl implements CountryService {

    @Autowired
    JSONProvider jsonProvider;

    private static final List<Country> countries = new ArrayList<>();

    @Override
    public Optional<Country> findByName(String countryName) {
        log.debug("Finding country by name "+ countryName);
        for (Country country : countries) {
            if(country.getName().equals(countryName)){
                return Optional.of(country);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Country> findAll() {
        return countries;
    }

    @Override
    public Country create(String countryName) {
        Country country = new Country(countryName);
        countries.add(new Country(countryName));
        return country;
    }


}
