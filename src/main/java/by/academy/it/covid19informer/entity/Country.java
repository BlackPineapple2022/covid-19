package by.academy.it.covid19informer.entity;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "countryDetail", "covidStats"})
@ToString
public class Country {

    private Long id;

    private String name;

    private transient CountryDetail countryDetail;
    private transient List<CovidStat> covidStats;

    public Country(String name) {
        this.name = name;
    }
}
