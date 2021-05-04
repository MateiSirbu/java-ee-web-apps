/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Analyzes and routes the search query to specialized servlets.
 */
public class Dispatcher extends HttpServlet {

    private void handleCoordinates(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // found coordinates; resolving coordinates and showing forecast
        req.getRequestDispatcher("/gpsresolve").include(req, resp);
    }

    private void handleStringQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // found search query; disambiguating string (if necessary) and showing forecast
        req.getRequestDispatcher("/disambiguate").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String query = req.getParameter("query");
        if (query.trim().equals("")) {
            req.getRequestDispatcher("/notfound.html").include(req, resp);
            return;
        }
        String[] tokens = query.split("[\\s,]+");
        if (tokens.length == 2) {
            try {
                Float.parseFloat(tokens[0]);
                Float.parseFloat(tokens[1]);
                handleCoordinates(req, resp);
            }
            catch (NumberFormatException e) {
                handleStringQuery(req, resp);
            }
        }
        else {
            handleStringQuery(req, resp);
        }

    }
}
