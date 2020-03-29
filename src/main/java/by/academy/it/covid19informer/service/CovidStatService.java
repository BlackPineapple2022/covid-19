package by.academy.it.covid19informer.service;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.entity.CovidStat;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface CovidStatService {

    Optional<CovidStat> getCovidStat(Country country);

    Optional<CovidStat> getTotalCovidStat();

    Map<LocalDate,Optional<CovidStat>> getCovidHistoryStats(Country country);

    Map<LocalDate,Optional<CovidStat>> prepareCovidHistoryStats(Country country);



}
