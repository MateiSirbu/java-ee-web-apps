/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package services;

import data.POI;
import javafx.util.Pair;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Retrieves the POI whose coordinates are closest to the ones specified in the search query.
 */
public class ReverseGeocoder extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // if coordinates are invalid, abort and report to user
        String[] tokens = req.getParameter("query").split("[\\s,]+");
        Pair<Float, Float> queryCoords = new Pair<>(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
        if (queryCoords.getKey() < -90 || queryCoords.getKey() > 90 || queryCoords.getValue() < -180 || queryCoords.getValue() > 180) {
            req.getRequestDispatcher("/invalidcoords.html").include(req, resp);
            return;
        }

        // otherwise, identify the closest POI
        DataProvider dataProvider = (DataProvider) getServletConfig().getServletContext().getAttribute("dataProvider");
        ArrayList<POI> pointsOfInterest = dataProvider.getPointsOfInterest();
        POI closestPoi = pointsOfInterest.get(0);
        double smallestDistance = Double.MAX_VALUE;
        for (POI poi : pointsOfInterest) {
            Pair<Double, Double> poiCoords = poi.getCoordinates();
            double euclideanDistance =
                    Math.sqrt(Math.pow((poiCoords.getKey() - queryCoords.getKey()), 2)
                            + Math.pow((poiCoords.getValue() - queryCoords.getValue()), 2));
            if (euclideanDistance < smallestDistance) {
                smallestDistance = euclideanDistance;
                closestPoi = poi;
            }
        }
        req.getRequestDispatcher("/forecast?id=" + closestPoi.getId()).include(req, resp);
    }
}
