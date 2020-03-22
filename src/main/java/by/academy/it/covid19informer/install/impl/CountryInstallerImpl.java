package by.academy.it.covid19informer.install.impl;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.install.CountryInstaller;
import by.academy.it.covid19informer.provider.impl.JohnsHopkinsUniversityCovidStatProviderImpl;
import by.academy.it.covid19informer.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

@Service
@Slf4j
public class CountryInstallerImpl implements CountryInstaller {

    @Autowired
    CountryRepository countryRepository;

    @Override
    public void install() {

        JohnsHopkinsUniversityCovidStatProviderImpl csp = new JohnsHopkinsUniversityCovidStatProviderImpl();
        try {
            Class<JohnsHopkinsUniversityCovidStatProviderImpl> covidStatProviderClass = JohnsHopkinsUniversityCovidStatProviderImpl.class;
            Method getJSON = covidStatProviderClass.getDeclaredMethod("getJSON", String.class);
            getJSON.setAccessible(true);
            JSONObject jsonObject = (JSONObject) getJSON.invoke(csp, "");
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray covid19stats = data.getJSONArray("covid19Stats");
            Set<String> countries = new TreeSet<>();
            for (Object covid19stat : covid19stats) {
                JSONObject covid19statJson = (JSONObject) covid19stat;
                String country = covid19statJson.getString("country");
                countries.add(country);
            }
            for (String country : countries) {
                countryRepository.save(new Country(null, country, null, null));
            }
        } catch (Exception e) {
            log.error("Error while installing data",e);
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        CountryInstaller bean = context.getBean(CountryInstaller.class);
        bean.install();
    }
}
