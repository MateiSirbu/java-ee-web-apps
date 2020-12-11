package data;

import java.util.Date;

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
}