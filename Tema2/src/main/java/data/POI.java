package data;

import javafx.util.Pair;

import java.util.Objects;

public class POI {
    int id;
    String name;
    Pair<Double, Double> coordinates;
    int altitude;
    String timeZone;

    public POI(int id, String name, Pair<Double, Double> coordinates, int altitude, String timeZone) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.altitude = altitude;
        this.timeZone = timeZone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Pair<Double, Double> getCoordinates() {
        return coordinates;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public int getAltitude() {
        return altitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        POI poi = (POI) o;
        return id == poi.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
