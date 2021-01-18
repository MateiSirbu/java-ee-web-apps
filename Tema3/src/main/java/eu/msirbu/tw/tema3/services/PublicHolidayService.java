package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.PublicHoliday;
import eu.msirbu.tw.tema3.repositories.PublicHolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

}
