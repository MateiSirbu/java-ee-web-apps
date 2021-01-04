/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package data;

import java.util.Date;
import java.util.Objects;

public class Forecast {
    int idPoi;
    Date timestamp;
    double temperature;
    double humidity;
    double pressure;

    public Forecast(int idPoi, Date timestamp, double temperature, double humidity, double pressure) {
        this.idPoi = idPoi;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public int getIdPoi() {
        return idPoi;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forecast forecast = (Forecast) o;
        return idPoi == forecast.idPoi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPoi);
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "idPoi=" + idPoi +
                ", timestamp=" + timestamp +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}