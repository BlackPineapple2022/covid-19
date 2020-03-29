package by.academy.it.covid19informer.controller;

import by.academy.it.covid19informer.entity.Country;
import by.academy.it.covid19informer.entity.CovidStat;
import by.academy.it.covid19informer.service.CountryService;
import by.academy.it.covid19informer.service.CovidStatService;
import by.academy.it.covid19informer.service.InstallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class WelcomeController {

    @Autowired
    private CovidStatService covidStatService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private InstallService installService;

    @GetMapping("/")
    public String main(Model model) {
        Optional<CovidStat> totalStat = covidStatService.getTotalCovidStat();

        if (totalStat.isPresent()) {
            model.addAttribute("stats",totalStat.get());
        }

        List<Country> allCountries = countryService.findAll();
        model.addAttribute("countries", allCountries);

        return "main";
    }

    @PostMapping("/")
    public String main(Model model, @RequestParam String countryName) {
        System.out.println(countryName);
        Optional<Country> country = countryService.findByName(countryName);
        if (!country.isPresent()) {
            return main(model);
        }

        List<Country> all = countryService.findAll();
        model.addAttribute("countries", all);

        Optional<CovidStat> covidStat = covidStatService.getCovidStat(country.get());
        // TODO: checking

        List<String> dates = new ArrayList<>();

        List<Long> confirmed = new ArrayList<>();
        List<Long> deaths = new ArrayList<>();
        List<Long> recovered = new ArrayList<>();

        Map<LocalDate, Optional<CovidStat>> historyStats = covidStatService.prepareCovidHistoryStats(country.get());

        for (LocalDate localDate : historyStats.keySet()) {
            dates.add(""+localDate.getDayOfMonth()+"/"+localDate.getMonthValue());
            confirmed.add(historyStats.get(localDate).get().getConfirmed());
            deaths.add(historyStats.get(localDate).get().getDeath());
            recovered.add(historyStats.get(localDate).get().getRecovered());
        }


        model.addAttribute("stats", covidStat.get());


        model.addAttribute("dates",dates);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("deaths", deaths);
        model.addAttribute("recovered", recovered);
        return "welcome";
    }


    
    @GetMapping("/install")
    public void install() {
        installService.install();
    }




}
