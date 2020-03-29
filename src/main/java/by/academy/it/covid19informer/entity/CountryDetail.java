package by.academy.it.covid19informer.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "population"})
@ToString
public class CountryDetail {

    private Long id;

    private Country country;

    private Long population;


}
