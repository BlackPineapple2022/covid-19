package by.academy.it.covid19informer.provider;

import by.academy.it.covid19informer.entity.CovidStat;

import java.util.List;
import java.util.Optional;

public interface CovidStatProvider {

    Optional<CovidStat> getCovidStat(String country);

    Optional<CovidStat> getAllCovidStat();

}
