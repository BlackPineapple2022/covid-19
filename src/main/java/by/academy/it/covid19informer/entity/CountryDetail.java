package by.academy.it.covid19informer.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="COUNTRY_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id","population"})
@ToString(exclude = {"country"})
public class CountryDetail {
    @Id
    @GenericGenerator(name = "one-one", strategy = "foreign",
            parameters = @Parameter(name = "property", value = "country"))
    @GeneratedValue(generator = "one-one")
    @Column(name="COUNTRY_DETAIL_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Country country;

    @Column(name="POPULATION")
    private Long population;



}
