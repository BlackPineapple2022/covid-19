package by.academy.it.covid19informer.service.impl;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.entity.CovidStat;
import by.academy.it.covid19informer.service.CountryService;
import by.academy.it.covid19informer.service.CovidStatService;
import by.academy.it.covid19informer.util.JSONProvider;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CovidStatServiceImpl implements CovidStatService {

    @Autowired
    private JSONProvider jsonProvider;
    @Autowired
    private CountryService countryService;

    private static final Map<String, String> HEADERS = new HashMap<>();
    private static final String specificCountryApiAddress =
            "https://coronavirus-monitor.p.rapidapi.com/coronavirus/latest_stat_by_country.php?country=";
    private static final String totalApiAddress =
            "https://coronavirus-monitor.p.rapidapi.com/coronavirus/worldstat.php";
    private static final String historyApiAddress =
            "https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_particular_country.php?country=";


    static {
        HEADERS.put("x-rapidapi-host", "coronavirus-monitor.p.rapidapi.com");
        HEADERS.put("x-rapidapi-key", "387823f027msh4e699d493de1e9dp13052djsn4db3f1b46b34");
    }

    @Override
    public Optional<CovidStat> getCovidStat(Country country) {

        String url = specificCountryApiAddress + country.getName();

        try {
            CovidStat covidStat = new CovidStat();

            covidStat.setCountry(country);

            JSONObject json = jsonProvider.getJSON(url, HEADERS);

            JSONArray latest = json.getJSONArray("latest_stat_by_country");
            JSONObject first = (JSONObject) latest.get(0);

            Long total_cases = parse(first.getString("total_cases"));
            Long total_deaths = parse(first.getString("total_deaths"));
            Long total_recovered = parse(first.getString("total_recovered"));

            covidStat.setConfirmed(total_cases);
            covidStat.setDeath(total_deaths);
            covidStat.setRecovered(total_recovered);

            String recordDateStr = first.getString("record_date");
            recordDateStr = recordDateStr.replaceAll(" ", "T");
            LocalDateTime recordDateTime = LocalDateTime.parse(recordDateStr);
            covidStat.setLastUpdate(recordDateTime);
            return Optional.of(covidStat);

        } catch (Exception e) {
            log.error("Error getting json for " + country);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<CovidStat> getTotalCovidStat() {
        try {
            String url = totalApiAddress;
            JSONObject jsonObject = jsonProvider.getJSON(url, HEADERS);
            CovidStat covidStat = new CovidStat();
            String total_cases = jsonObject.getString("total_cases");
            String total_deaths = jsonObject.getString("total_deaths");
            String total_recovered = jsonObject.getString("total_recovered");
            total_cases = total_cases.replaceAll(",", "");
            total_deaths = total_deaths.replaceAll(",", "");
            total_recovered = total_recovered.replaceAll(",", "");

            Long total = Long.parseLong(total_cases);
            Long deaths = Long.parseLong(total_deaths);
            Long recovered = Long.parseLong(total_recovered);
            covidStat.setConfirmed(total);
            covidStat.setDeath(deaths);
            covidStat.setRecovered(recovered);
            covidStat.setCountry(new Country("ALL COUNTRIES"));
            covidStat.setLastUpdate(LocalDateTime.now());
            return Optional.of(covidStat);
        } catch (Exception e) {
            log.error("Error getting world stat", e);
            return Optional.empty();
        }
    }

    @Override
    public Map<LocalDate, Optional<CovidStat>> getCovidHistoryStats(Country country) {
        log.debug("Start getting covid stat history for" + country);
        Map<LocalDate, Optional<CovidStat>> result = new HashMap<>();
        for (int i = 0; i < 14; i++) {
            result.put(LocalDate.now().minusDays(i), Optional.empty());
        }
        String url = historyApiAddress + country.getName();
        JSONObject jsonObject = jsonProvider.getJSON(url, HEADERS);


        System.out.println(jsonObject);


        JSONArray jsonArray = jsonObject.getJSONArray("stat_by_country");
        for (Object o : jsonArray) {
            JSONObject entry = (JSONObject) o;
            String record_date = entry.getString("record_date");
            record_date = record_date.replaceAll(" ", "T");
            LocalDateTime localDateTime = LocalDateTime.parse(record_date);


            System.out.println(localDateTime);


            Long total_cases = parse(entry.getString("total_cases"));
            Long total_deaths = parse(entry.getString("total_deaths"));
            Long total_recovered = parse(entry.getString("total_recovered"));
            CovidStat covidStat = new CovidStat(null, country, localDateTime, total_cases, total_deaths, total_recovered);
            Optional<CovidStat> covidStatMap = result.get(localDateTime.toLocalDate());

            if (covidStatMap == null) {
                continue;
            }
            if (!covidStatMap.isPresent() ||
                    (covidStatMap.get().getLastUpdate().isBefore(covidStat.getLastUpdate()))) {
                result.put(localDateTime.toLocalDate(), Optional.of(covidStat));
            }
        }
        System.out.println(" Return result: by date: ");
        Set<Map.Entry<LocalDate, Optional<CovidStat>>> entries = result.entrySet();
        for (Map.Entry<LocalDate, Optional<CovidStat>> entry : entries) {
            System.out.println("date: " + entry.getKey());
            System.out.println("stats: " + entry.getValue());
        }
        return result;
    }

    @Override
    public Map<LocalDate, Optional<CovidStat>> prepareCovidHistoryStats(Country country) {

        Map<LocalDate, Optional<CovidStat>> covidHistoryStats = getCovidHistoryStats(country);
        for (LocalDate localDate : covidHistoryStats.keySet()) {
            if(!covidHistoryStats.get(localDate).isPresent())
                covidHistoryStats.put(localDate,Optional.of(new CovidStat(null,country,localDate.atStartOfDay(),0L,0L,0L)));
        }
        return covidHistoryStats;
    }

    private static Long parse(String numberString) {
        numberString = numberString.replaceAll(",", "");
        Long result = 0L;
        try {
            result = Long.parseLong(numberString);
        } catch (Exception e) {
            result = 0L;
        }
        return result;
    }




}
