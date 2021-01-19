package eu.msirbu.tw.tema3.repositories;

import eu.msirbu.tw.tema3.entities.PublicHoliday;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PublicHolidayRepository extends CrudRepository<PublicHoliday, Integer> {
    Optional<PublicHoliday> findPublicHolidayByDate(LocalDate date);
}
