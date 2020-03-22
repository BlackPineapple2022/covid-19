package by.academy.it.covid19informer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "COUNTRY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id","countryDetail","covidStats"})
@ToString(exclude ={"countryDetail","covidStats"} )
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COUNTRY_ID")
    @JsonIgnore
    private Long id;
    @Column(name = "COUNTRY_NAME")
    private String name;
    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "country")
    private CountryDetail countryDetail;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "country")
    List<CovidStat> covidStats;

    public Country(String name) {
        this.name = name;
    }
}
