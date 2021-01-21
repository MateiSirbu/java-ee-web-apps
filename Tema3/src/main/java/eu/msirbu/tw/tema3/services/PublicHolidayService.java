/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.PublicHoliday;
import eu.msirbu.tw.tema3.repositories.PublicHolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PublicHolidayService {
    private final PublicHolidayRepository publicHolidayRepository;

    @Autowired
    public PublicHolidayService(PublicHolidayRepository publicHolidayRepository) {
        this.publicHolidayRepository = publicHolidayRepository;
    }

    public List<PublicHoliday> getAllPublicHolidays() {
        List<PublicHoliday> holidayList = new ArrayList<>();
        this.publicHolidayRepository.findAll().forEach(holidayList::add);
        return holidayList;
    }

    public Optional<PublicHoliday> getPublicHolidayByDate(LocalDate date) {
        return publicHolidayRepository.findPublicHolidayByDate(date);
    }

}
