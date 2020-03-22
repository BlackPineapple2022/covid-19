package by.academy.it.covid19informer.controller.api;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.entity.CovidStat;
import by.academy.it.covid19informer.provider.CovidStatProvider;
import by.academy.it.covid19informer.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
public class MainController {
    @Autowired
    private CovidStatProvider covidStatProvider;
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("api/all")
    public CovidStat getAllStat() {
        Optional<CovidStat> allCovidStat = covidStatProvider.getAllCovidStat();

        if (!allCovidStat.isPresent()){
            log.error("Service currently unavailable");
            return null;
        }
        return allCovidStat.get();
    }

    @GetMapping("/api/{country}")
    public CovidStat getCovidStat(@PathVariable String country) {
        Optional<CovidStat> covidStat = covidStatProvider.getCovidStat(country);

        if (!covidStat.isPresent()){
            log.error("Sorry, no information for country" + country);
            return null;
        }

        Optional<Country> byName = countryRepository.findByName(country);

        if(!byName.isPresent()){
            covidStat.get().setCountry(new Country(country));
        }else{
            covidStat.get().setCountry(byName.get());
        }
        return covidStat.get();
    }
}
