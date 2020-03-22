package by.academy.it.covid19informer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "COVIDSTAT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id","confirmed","death","recovered"})
@ToString(exclude = {"country"})
public class CovidStat {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COVIDSTAT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    private Country country;

    @Column(name = "DATE")
    private LocalDateTime date;

    @Column(name = "CONFIRMED")
    private Long confirmed;

    @Column(name = "DEATH")
    private Long death;

    @Column(name = "RECOVERED")
    private Long recovered;

}
