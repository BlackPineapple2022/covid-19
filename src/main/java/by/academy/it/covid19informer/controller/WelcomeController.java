package by.academy.it.covid19informer.controller;

import by.academy.it.covid19informer.entity.CovidStat;
import by.academy.it.covid19informer.provider.CovidStatProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class WelcomeController {
    @Autowired
    CovidStatProvider covidStatProvider;

    @GetMapping("/")
    public String main(Model model) {

        Optional<CovidStat> allCovidStat = covidStatProvider.getAllCovidStat();
        if (allCovidStat.isPresent()){
            model.addAttribute("all",allCovidStat);
        }

        return "welcome";
    }

    @GetMapping("/{country}")
    public String main(@PathVariable String country, Model model) {

        Optional<CovidStat> covidStat = covidStatProvider.getCovidStat(country);
        if (covidStat.isPresent()){
            model.addAttribute("all",covidStat);
        }

        return "welcome";
    }


}
