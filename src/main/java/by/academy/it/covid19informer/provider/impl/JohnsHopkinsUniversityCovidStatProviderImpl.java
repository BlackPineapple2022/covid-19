package by.academy.it.covid19informer.provider.impl;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.entity.CovidStat;
import by.academy.it.covid19informer.provider.CovidStatProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;


@Slf4j
public class JohnsHopkinsUniversityCovidStatProviderImpl implements CovidStatProvider {

    @Override
    public Optional<CovidStat> getCovidStat(String countryStr) {
        try {
            JSONObject json = getJSON(countryStr);

            CovidStat cs = new CovidStat();
            Country c = new Country();

            c.setName(countryStr);

            cs.setCountry(c);

            cs.setConfirmed(0L);
            cs.setDeath(0L);
            cs.setRecovered(0L);

            JSONObject data = json.getJSONObject("data");
            JSONArray covid19Stats = data.getJSONArray("covid19Stats");
            for (Object covid19Stat : covid19Stats) {
                JSONObject jsonObject = (JSONObject) covid19Stat;
                if (jsonObject.getString("country").equals(countryStr)) {
                    cs.setConfirmed(cs.getConfirmed() + jsonObject.getLong("confirmed"));
                    cs.setDeath(cs.getDeath() + jsonObject.getLong("deaths"));
                    cs.setRecovered(cs.getRecovered() + jsonObject.getLong("recovered"));
                }
            }

            JSONObject firstJsonInArray = (JSONObject) covid19Stats.get(0);
            if (!firstJsonInArray.getString("country").equals(countryStr)) {
                return Optional.empty();
            }
            LocalDateTime localDateTime = LocalDateTime.parse(firstJsonInArray.getString("lastUpdate"));
            cs.setDate(localDateTime);
            return Optional.of(cs);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CovidStat> getAllCovidStat() {

        try {
            CovidStat cs = new CovidStat();

            cs.setConfirmed(0L);
            cs.setDeath(0L);
            cs.setRecovered(0L);

            JSONObject json = getJSON("");
            JSONObject data = json.getJSONObject("data");
            JSONArray covid19Stats = data.getJSONArray("covid19Stats");
            for (Object covid19Stat : covid19Stats) {
                JSONObject jso = (JSONObject) covid19Stat;
                cs.setConfirmed(cs.getConfirmed() + jso.getLong("confirmed"));
                cs.setDeath(cs.getDeath() + jso.getLong("deaths"));
                cs.setRecovered(cs.getRecovered() + jso.getLong("recovered"));
            }
            JSONObject firstJsonInArray = (JSONObject) covid19Stats.get(0);
            String lastUpdate = firstJsonInArray.getString("lastUpdate");
            LocalDateTime parse = LocalDateTime.parse(lastUpdate);
            cs.setDate(parse);
            return Optional.of(cs);
        } catch (Exception e) {
            log.error("Error, while getting all stats", e);
            return Optional.empty();
        }
    }

    private JSONObject getJSON(String country) {
        try {
            String url = ResourceBundle.getBundle("rapidapi").getString("apiaddress") + country;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("x-rapidapi-host", ResourceBundle.getBundle("rapidapi").getString("x-rapidapi-host"));
            httpGet.setHeader("x-rapidapi-key", ResourceBundle.getBundle("rapidapi").getString("x-rapidapi-key"));
            CloseableHttpResponse response = null;
            synchronized (JohnsHopkinsUniversityCovidStatProviderImpl.class) {
                Thread.sleep(300);
                response = httpClient.execute(httpGet);
            }

            HttpEntity responseEntity = response.getEntity();
            String responseBodyString = convertInputStreamToString(responseEntity.getContent());
            JSONObject jsonObject = new JSONObject(responseBodyString);
            if (!jsonObject.getBoolean("error")) {
                return jsonObject;
            }
        } catch (Exception e) {
            log.error("Error getting json", e);
            return null;
        }
        return null;
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

}
