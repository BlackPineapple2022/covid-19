package by.academy.it.covid19informer.service.impl;

import by.academy.it.covid19informer.service.CountryService;
import by.academy.it.covid19informer.service.InstallService;
import by.academy.it.covid19informer.util.JSONProvider;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class InstallServiceImpl implements InstallService {

    @Autowired
    JSONProvider jsonProvider;

    @Autowired
    CountryService countryService;

    private static final Map<String, String> HEADERS = new HashMap<>();
    private static final String casesByCountry =
            "https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_country.php";

    static {
        HEADERS.put("x-rapidapi-host", "coronavirus-monitor.p.rapidapi.com");
        HEADERS.put("x-rapidapi-key", "387823f027msh4e699d493de1e9dp13052djsn4db3f1b46b34");
    }

    @Override
    public void install() {
        log.info("Start installing data ");
        Set<String> countriesStr = new TreeSet<>();
        String url = casesByCountry;
        JSONObject json = jsonProvider.getJSON(url, HEADERS);
        JSONArray countries_stat = json.getJSONArray("countries_stat");
        for (Object o : countries_stat) {
            String countryName = ((JSONObject) o).getString("country_name");
            countriesStr.add(countryName);
        }
        for (String str : countriesStr) {
            countryService.create(str);
        }
    }
}
