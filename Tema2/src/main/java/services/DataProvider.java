/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package services;

import data.Forecast;
import data.POI;
import javafx.util.Pair;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provides and manipulates mock weather data.
 */
public class DataProvider {
    // hardcoded points of interest
    ArrayList<POI> pointsOfInterest = new ArrayList<>(Arrays.asList(
            new POI(1, "București", new Pair<>(44.422, 26.102), 66, "GMT+2"),
            new POI(2, "Brașov", new Pair<>(45.651, 25.610), 586, "GMT+2"),
            new POI(3, "Tărlungeni", new Pair<>(45.648, 25.755), 603, "GMT+2"),
            new POI(4, "Zizin", new Pair<>(45.634, 25.776), 611, "GMT+2"),
            new POI(5, "Sânpetru", new Pair<>(45.704, 25.631), 532, "GMT+2"),
            new POI(6, "Săcele", new Pair<>(45.618, 25.701), 636, "GMT+2"),
            new POI(7, "Predeal", new Pair<>(45.504, 25.575), 1084, "GMT+2"),
            new POI(8, "Bod", new Pair<>(45.761, 25.642), 499, "GMT+2"),
            new POI(9, "Colonia Bod", new Pair<>(45.758, 25.602), 500, "GMT+2"),
            new POI(10, "Constanța", new Pair<>(44.116, 28.636), 15, "GMT+2"),
            new POI(11, "Suceava", new Pair<>(47.659, 26.224), 287, "GMT+2"),
            new POI(12, "Viena", new Pair<>(48.333, 16.019), 173, "GMT+1"),
            new POI(13, "Paris", new Pair<>(48.860, 2.352), 62, "GMT+1"),
            new POI(14, "London", new Pair<>(51.508, 0.127), 23, "GMT"),
            new POI(15, "Istanbul", new Pair<>(41.034, 28.991), 9, "GMT+3"),
            new POI(16, "Bratislava", new Pair<>(48.136, 17.125), 147, "GMT+1"),
            new POI(17, "Cape Town", new Pair<>(-33.962, 18.415), 17, "GMT+2"),
            new POI(18, "New Delhi", new Pair<>(28.612, 77.207), 228, "GMT+5:30"),
            new POI(19, "Kuala Lumpur", new Pair<>(3.106, 102.059), 45, "GMT+8"),
            new POI(20, "Oslo", new Pair<>(59.915, 10.769), 5, "GMT+1"),
            new POI(21, "Kinshasa", new Pair<>(-4.440, 15.266), 423, "GMT+1"),
            new POI(22, "McMurdo Station", new Pair<>(-77.846, 166.69), 176, "GMT+13"),
            new POI(23, "Cairo", new Pair<>(30.044, 31.235), 23, "GMT+2"),
            new POI(24, "Mount Everest", new Pair<>(27.988, 86.924), 8848, "GMT+5:45"),
            new POI(25, "Tokyo", new Pair<>(35.681, 139.76), 7, "GMT+9")
    ));
    CopyOnWriteArrayList<Forecast> forecasts = new CopyOnWriteArrayList<>();
    final int UPDATE_OVERHEAD;

    public DataProvider(int UPDATE_OVERHEAD) {
        this.UPDATE_OVERHEAD = UPDATE_OVERHEAD;
        pointsOfInterest.sort(Comparator.comparing(POI::getName));
        generateForecasts(false);
    }

    public ArrayList<POI> getPointsOfInterest() {
        return pointsOfInterest;
    }

    /**
     * Gets the POI that has a specified ID.
     *
     * @param poiId The ID itself.
     * @return The POI itself.
     */
    public POI getPoiById(int poiId) {
        for (POI poi : pointsOfInterest) {
            if (poi.getId() == poiId)
                return poi;
        }
        return null;
    }

    /**
     * Gets the weather forecasts of a specified point of interest.
     *
     * @param poiName The name of the POI.
     * @return The list of forecasts.
     */
    public ArrayList<Forecast> getForecastsByPoi(String poiName) {
        ArrayList<Forecast> forecasts = new ArrayList<>();
        int poiID = 0;

        for (POI poi : pointsOfInterest) {
            if (poi.getName().equals(poiName))
                poiID = poi.getId();
        }

        if (poiID == 0)
            return forecasts;

        for (Forecast forecast : this.forecasts) {
            if (forecast.getIdPoi() == poiID) {
                forecasts.add(forecast);
            }
        }

        return forecasts;
    }

    /**
     * Deletes forecasts of a POI with a specific ID.
     *
     * @param forecasts The list of forecasts to be modified.
     * @param poiId     The ID of the POI whose forecasts will be deleted.
     */
    public CopyOnWriteArrayList<Forecast> deleteForecastsByPoiId(CopyOnWriteArrayList<Forecast> forecasts, int poiId) {
        forecasts.removeIf(forecast -> forecast.getIdPoi() == poiId);
        return forecasts;
    }

    /**
     * An algorithm that generates (kinda) accurate weather forecasts,
     * based on the latitude of the POI and the time of the day.
     */
    public void generateForecasts(boolean willLog) {
        int spinningRodAnimationFrame = 0;
        for (POI poi : pointsOfInterest) {
            forecasts = deleteForecastsByPoiId(forecasts, poi.getId());
            SecureRandom rand = new SecureRandom();
            double latitude = poi.getCoordinates().getKey();
            double altitude = poi.getAltitude();

            // weather trends should be symmetric to the equator
            if (latitude < 0)
                latitude = -latitude;

            /* Rough approximation of temperature variance during a day,
             * with respect to the latitude coordinate of the POI.
             */

            final int LAT_AMPL_MAX = 45;
            final int AMPL_MAX = 5;
            final int AMPL_STD_DEV = 15;

            /* Variance (2 * amplitude) is described by a bell curve, where:
             *    at +/-15° latitude,    the amplitude is ~   1°C
             *    at +/-30° latitude,    the amplitude is ~ 2.5°C
             *    at +/-45° latitude,    the amplitude is     5°C
             *    at +/-60° latitude,    the amplitude is ~ 2.5°C
             *    at +/-75° latitude,    the amplitude is ~   1°C
             *
             * This is the amplitude of the sine wave that will describe
             * the temperature in a day at a POI. */

            double amplitude = AMPL_MAX * Math.exp(-(Math.pow((latitude - LAT_AMPL_MAX), 2))
                    / (2 * Math.pow(AMPL_STD_DEV, 2)));

            /* Rough approximation of the average temperature during a day,
             * with respect to the latitude coordinate of the POI. */

            final int TEMP_AT_EQUATOR = 35;
            final double TEMP_FUNCTION_SLOPE = -0.6;

            /* The average temperature at sea level is described by a linear function, where:
             *    at the equator,       the avg. temp. is    35°C
             *    at +/-45° latitude,   the avg. temp. is ~  10°C
             *    at the poles,         the avg. temp. is ~ -15°C */

            double averageTemp = TEMP_FUNCTION_SLOPE * latitude + TEMP_AT_EQUATOR;

            /* The humidity is described by a bell curve, where:
             *    at +/-15° latitude,    the humidity is ~   30%
             *    at +/-30° latitude,    the humidity is ~   55%
             *    at +/-45° latitude,    the humidity is ~   80%
             *    at +/-60° latitude,    the humidity is     90%
             *    at +/-75° latitude,    the humidity is ~   80% */

            final int LAT_HUMIDITY_MAX = 60;
            final int HUMIDITY_MAX = 90;
            final int HUMIDITY_STD_DEV = 30;

            double humidity = HUMIDITY_MAX * Math.exp(-(Math.pow((latitude - LAT_HUMIDITY_MAX), 2))
                    / (2 * Math.pow(HUMIDITY_STD_DEV, 2)));

            /* The air pressure is described by a linear function, where:
             *    at 0 m (sea level),        the pressure is   760 mmHg
             *    at 8848 m (Everest peak),  the pressure is ~ 253 mmHg */

            final int PRESSURE_AT_SEA_LVL = 760;
            final double PRESSURE_FUNCTION_SLOPE = -0.057;

            double pressure = PRESSURE_FUNCTION_SLOPE * altitude + PRESSURE_AT_SEA_LVL;

            /* The offset of the temperature with respect to the altitude is a linear function, where:
             *    at 0 m (sea level),        the offset is   0°C
             *    at 8848 m (Everest peak),  the offset is ~ 35°C */

            double tempOffset = 0.004 * altitude;

            // preparing timestamp for the first forecast
            Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(poi.getTimeZone()));
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // iterate through each forecast and set values
            for (int hrOffset = 0; hrOffset <= 24; hrOffset++) {
                double temp = (amplitude
                        * Math.sin((calendar.get(Calendar.HOUR_OF_DAY) - 6) / (24 / (2 * Math.PI)))
                        + averageTemp) - tempOffset;

                // simulating entropy: randomly modify the temperature, humidity & pressure, but just a bit
                final double TEMP_MAX_DEV = 2, TEMP_MIN_DEV = -2;
                temp = temp + (rand.nextDouble() * (TEMP_MAX_DEV - TEMP_MIN_DEV)) + TEMP_MIN_DEV;

                final double HUMIDITY_MAX_DEV = 15, HUMIDITY_MIN_DEV = -15;
                humidity = humidity + (rand.nextDouble() * (HUMIDITY_MAX_DEV - HUMIDITY_MIN_DEV)) + HUMIDITY_MIN_DEV;
                if (humidity > 100) humidity = 100;
                if (humidity < 0) humidity = 0;

                final double PRESSURE_MAX_DEV = 4, PRESSURE_MIN_DEV = -4;
                pressure = pressure + (rand.nextDouble() * (PRESSURE_MAX_DEV - PRESSURE_MIN_DEV)) + PRESSURE_MIN_DEV;

                // construct forecast and move on to the next one
                Forecast forecast = new Forecast(poi.getId(), calendar.getTime(), temp, humidity, pressure);
                forecasts.add(forecast);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }
            if (willLog) {
                try {
                    Thread.sleep(UPDATE_OVERHEAD);
                    String updateStatus = "\rInaccuWeather is updating forecasts ";
                    switch (spinningRodAnimationFrame % 4) {
                        case 0:
                            updateStatus += "\\";
                            break;
                        case 1:
                            updateStatus += "|";
                            break;
                        case 2:
                            updateStatus += "/";
                            break;
                        case 3:
                            updateStatus += "─";
                            break;
                    }
                    System.out.print(updateStatus);
                    spinningRodAnimationFrame++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
