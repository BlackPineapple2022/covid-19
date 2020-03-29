package by.academy.it.covid19informer.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "confirmed", "death", "recovered", "lastUpdate"})
@ToString
public class CovidStat {

    private Long id;

    private Country country;

    private LocalDateTime lastUpdate;

    private Long confirmed;

    private Long death;

    private Long recovered;

}
