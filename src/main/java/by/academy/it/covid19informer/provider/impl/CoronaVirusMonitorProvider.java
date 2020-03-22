package by.academy.it.covid19informer.provider.impl;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.entity.CovidStat;
import by.academy.it.covid19informer.provider.CovidStatProvider;
import by.academy.it.covid19informer.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
@Service
public class CoronaVirusMonitorProvider implements CovidStatProvider {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Optional<CovidStat> getCovidStat(String country) {

        try {
            CovidStat cs = new CovidStat();

            Optional<Country> foundCountry = countryRepository.findByName(country);

            if (!foundCountry.isPresent()){
                return Optional.empty();
            }

            cs.setCountry(foundCountry.get());

            JSONObject json = getJSON(country);
            JSONArray latest = json.getJSONArray("latest_stat_by_country");
            JSONObject latestFirst = (JSONObject)latest.get(0);

            String total_cases = latestFirst.getString("total_cases");
            String total_deaths = latestFirst.getString("total_deaths");
            String total_recovered = latestFirst.getString("total_recovered");

            total_cases = total_cases.replaceAll(",", "");
            total_deaths = total_deaths.replaceAll(",", "");
            total_recovered = total_recovered.replaceAll(",", "");

            Long total = 0L;
            Long deaths = 0L;
            Long recovered = 0L;

            try {
                total = Long.parseLong(total_cases);
            }catch (Exception e){
                 total = 0L;
            }

            try {
                deaths = Long.parseLong(total_deaths);
            }catch (Exception e){
                deaths = 0L;
            }

            try {
                recovered = Long.parseLong(total_recovered);
            }catch (Exception e){
                recovered = 0L;
            }

            cs.setConfirmed(total);
            cs.setDeath(deaths);
            cs.setRecovered(recovered);

            String recordDateStr = latestFirst.getString("record_date");
            recordDateStr = recordDateStr.replaceAll(" ", "T");
            LocalDateTime recordDateTime = LocalDateTime.parse(recordDateStr);
            cs.setDate(recordDateTime);

            return Optional.of(cs);

        } catch (Exception e) {
            log.error("Error getting json for " + country);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<CovidStat> getAllCovidStat() {
        try {
            CovidStat cs = new CovidStat();
            JSONObject jsonObject = getJSON();
            String total_cases = jsonObject.getString("total_cases");
            String total_deaths = jsonObject.getString("total_deaths");
            String total_recovered = jsonObject.getString("total_recovered");
            total_cases = total_cases.replaceAll(",", "");
            total_deaths = total_deaths.replaceAll(",", "");
            total_recovered = total_recovered.replaceAll(",", "");

            Long total = Long.parseLong(total_cases);
            Long deaths = Long.parseLong(total_deaths);
            Long recovered = Long.parseLong(total_recovered);
            cs.setConfirmed(total);
            cs.setDeath(deaths);
            cs.setRecovered(recovered);
            cs.setCountry(new Country("All"));
            cs.setDate(LocalDateTime.now());
            return Optional.of(cs);
        } catch (Exception e) {
            log.error("Error while getting all stats", e);
            return Optional.empty();
        }

    }

    private JSONObject getJSON(String country) {
        try {
            String url = ResourceBundle.getBundle("coronavirusmonitor").getString("country.api.address") + country;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("x-rapidapi-host", ResourceBundle.getBundle("coronavirusmonitor").getString("x-rapidapi-host"));
            httpGet.setHeader("x-rapidapi-key", ResourceBundle.getBundle("coronavirusmonitor").getString("x-rapidapi-key"));
            CloseableHttpResponse response = null;
            synchronized (JohnsHopkinsUniversityCovidStatProviderImpl.class) {
                Thread.sleep(300);
                response = httpClient.execute(httpGet);
            }

            HttpEntity responseEntity = response.getEntity();
            String responseBodyString = convertInputStreamToString(responseEntity.getContent());
            JSONObject jsonObject = new JSONObject(responseBodyString);
            return jsonObject;
        } catch (Exception e) {
            log.error("Error getting json", e);
            return null;
        }
    }

    private JSONObject getJSON() {
        try {
            String url = ResourceBundle.getBundle("coronavirusmonitor").getString("all.api.address");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("x-rapidapi-host", ResourceBundle.getBundle("coronavirusmonitor").getString("x-rapidapi-host"));
            httpGet.setHeader("x-rapidapi-key", ResourceBundle.getBundle("coronavirusmonitor").getString("x-rapidapi-key"));
            CloseableHttpResponse response = null;
            synchronized (JohnsHopkinsUniversityCovidStatProviderImpl.class) {
                Thread.sleep(300);
                response = httpClient.execute(httpGet);
            }

            HttpEntity responseEntity = response.getEntity();
            String responseBodyString = convertInputStreamToString(responseEntity.getContent());
            JSONObject jsonObject = new JSONObject(responseBodyString);
            return jsonObject;
        } catch (Exception e) {
            log.error("Error getting json", e);
            return null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        CoronaVirusMonitorProvider provider = context.getBean(CoronaVirusMonitorProvider.class);
        Optional<CovidStat> worldStats = provider.getAllCovidStat();
        System.out.println(worldStats);

    }


}
