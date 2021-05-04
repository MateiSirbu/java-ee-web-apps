/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package services;

import data.Forecast;
import data.POI;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Generates the weather forecast webpage.
 */
public class ForecastPrinter extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // accessing DataProvider and retrieving POIs
        DataProvider dataProvider = (DataProvider) getServletConfig().getServletContext().getAttribute("dataProvider");
        int poiId = Integer.parseInt(req.getParameter("id"));
        POI poi = dataProvider.getPoiById(poiId);
        ArrayList<Forecast> forecasts = dataProvider.getForecastsByPoi(poi.getName());
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        generateForecastDOM(resp.getWriter(), poi, forecasts);
    }

    /**
     * Constructs the weather forecast webpage DOM.
     * @param out The PrintWriter required for displaying the webpage.
     * @param poi The POI the displayed information is about.
     * @param forecasts A list of the forecasts that will be displayed.
     */
    private void generateForecastDOM(PrintWriter out, POI poi, ArrayList<Forecast> forecasts) {
        // defining the date and time formats
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE',' dd MMMMM yyyy", new Locale("en", "US"));
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("en", "US"));
        final SimpleDateFormat timeZoneFormat = new SimpleDateFormat("'GMT'ZZZ", new Locale("en", "US"));

        dateFormat.setTimeZone(TimeZone.getTimeZone(poi.getTimeZone()));
        timeFormat.setTimeZone(TimeZone.getTimeZone(poi.getTimeZone()));
        timeZoneFormat.setTimeZone(TimeZone.getTimeZone(poi.getTimeZone()));

        // constructing the table head
        StringBuilder htmlDom =
                new StringBuilder("<html><head><title>Weather forecast for " + poi.getName() + "</title>" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/material-components-web.min.css\"/>" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\"/>" +
                        "<link rel=\"stylesheet\" href=\"./css/material-icons.css\"/>" +
                        "<link rel=\"stylesheet\" href=\"./css/roboto.css\">" +
                        "<script type=\"text/javascript\" src=\"./js/material-components-web.min.js\"></script>" +
                        "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>" +
                        "<style>.span, .text {vertical-align: middle; display: table-cell;}" +
                        "h1 {display: table; width: 100%;} .control {margin: 10px 10px 10px 10px;}" +
                        "</style></head><body class=\"mdc-typography\">" +
                        "<div class=\"control\"><h1><span style=\"width: 80px\" class=\"span\">" +
                        "<img src=\"./img/partly_cloudy.png\">" +
                        "</span><span class=\"text\">Weather forecast for " + poi.getName() + "</span>" +
                        "</h1></div><div class=\"control\"><a href=\"/\" class=\"mdc-button mdc-button--raised\">" +
                        "<i class=\"material-icons mdc-button__icon\" aria-hidden=\"true\">keyboard_backspace</i>" +
                        "<span class=\"mdc-button__label\">BACK TO SEARCH</span></a></div>" +
                        "<div class=\"control\" style=\"width: 600px\"><table class=\"mdc-data-table__table\"><thead>" +
                        "<tr class=\"mdc-data-table__header-row\">" +
                        "<th class=\"mdc-data-table__header-cell\" role=\"columnheader\" scope=\"col\">"+
                        "TIMESTAMP (" + timeZoneFormat.format(forecasts.get(0).getTimestamp()) + ")</th>" +
                        "<th class=\"mdc-data-table__header-cell mdc-data-table__header-cell--numeric\" "+
                        "role=\"columnheader\" scope=\"col\">TEMPERATURE</th>" +
                        "<th class=\"mdc-data-table__header-cell mdc-data-table__header-cell--numeric\" "+
                        "role=\"columnheader\" scope=\"col\">HUMIDITY</th>" +
                        "<th class=\"mdc-data-table__header-cell mdc-data-table__header-cell--numeric\" "+
                        "role=\"columnheader\" scope=\"col\">AIR PRESSURE</th>" +
                        "</tr></thead><tbody class=\"mdc-data-table__content\">");

        // iterate through forecasts and generate table rows
        for (int i = 0; i < forecasts.size(); i++) {
            Forecast forecast = forecasts.get(i);
            Date forecastTimestamp = forecast.getTimestamp();
            String timeZone = poi.getTimeZone();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            calendar.setTime(forecastTimestamp);
            if (i == 0 || (calendar.get(Calendar.HOUR_OF_DAY) == 0)) {
                htmlDom.append("<tr class=\"mdc-data-table__row\">")
                        .append("<th class=\"mdc-data-table__cell\" scope=\"row\"><b>")
                        .append(dateFormat.format(forecastTimestamp)).append("</b></th>")
                        .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">").append("</td>")
                        .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">").append("</td>")
                        .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">").append("</td>")
                        .append("</tr>\n");
            }
            htmlDom.append("<tr class=\"mdc-data-table__row\">")
                    .append("<th class=\"mdc-data-table__cell\" scope=\"row\">")
                    .append(timeFormat.format(forecastTimestamp)).append("</th>")
                    .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">")
                    .append(Math.round(forecast.getTemperature())).append("°C</td>")
                    .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">")
                    .append(Math.round(forecast.getHumidity())).append("%</td>")
                    .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">")
                    .append(Math.round(forecast.getPressure())).append(" mmHg</td>")
                    .append("</tr>\n");
        }
        htmlDom.append("</tbody></table></div></body>");
        out.println(htmlDom);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }
}
