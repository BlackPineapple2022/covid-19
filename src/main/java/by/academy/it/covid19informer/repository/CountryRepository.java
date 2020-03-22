package by.academy.it.covid19informer.repository;

import by.academy.it.covid19informer.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface CountryRepository extends JpaRepository<Country,Long> {

    Optional<Country> findByName(String name);

}
