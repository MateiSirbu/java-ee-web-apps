/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package services;

import data.POI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;

/**
 * Provides support for search query disambiguation.
 */
public class StringDisambiguator extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // accessing DataProvider and retrieving POIs
        DataProvider dataProvider = (DataProvider) getServletConfig().getServletContext().getAttribute("dataProvider");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String query = Normalizer.normalize(req.getParameter("query").trim().toUpperCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        ArrayList<POI> pointsOfInterest = dataProvider.getPointsOfInterest();
        ArrayList<POI> matchingPois = new ArrayList<>();

        // identifying the POIs that match regex
        for (POI poi : pointsOfInterest) {
            String poiName = Normalizer.normalize(poi.getName().toUpperCase(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            if (poiName.contains(query.toUpperCase()))
                matchingPois.add(poi);
        }

        // if no POI matches, report to user
        if (matchingPois.size() == 0)
            req.getRequestDispatcher("/notfound.html").include(req, resp);

        // if 1 POI matches, show forecast
        else if (matchingPois.size() == 1)
            req.getRequestDispatcher("/forecast?id=" + matchingPois.get(0).getId()).include(req, resp);

        // if 2 or more POI match, disambiguate
        else
            generateDisambiguationDOM(resp.getWriter(), matchingPois);
    }

    /**
     * Constructs the disambiguation webpage DOM.
     * @param out The PrintWriter required for displaying the webpage.
     * @param candidatePois The list of POIs the user needs to choose from.
     */
    public void generateDisambiguationDOM(PrintWriter out, ArrayList<POI> candidatePois) {
        // construct table head
        StringBuilder htmlDom =
                new StringBuilder("<html><head><title>Disambiguation</title>" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/material-components-web.min.css\"/>" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\"/>" +
                        "<link rel=\"stylesheet\" href=\"./css/material-icons.css\"/>" +
                        "<link rel=\"stylesheet\" href=\"./css/roboto.css\">" +
                        "<script type=\"text/javascript\" src=\"./js/material-components-web.min.js\"></script>" +
                        "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>" +
                        "<style>.span, .text {vertical-align: middle; display: table-cell;}" +
                        "h1 {display: table; width: 100%;}.control {margin: 10px 10px 10px 10px;}" +
                        "a { font-weight: bold; text-decoration: none; color: #2a2a2a; }" +
                        "</style></head><body class=\"mdc-typography\">" +
                        "<div class=\"control\"><h1><span style=\"width: 80px\" class=\"span\">" +
                        "<img src=\"./img/partly_cloudy.png\">" +
                        "</span><span class=\"text\">Your query matches multiple POIs.</span>" +
                        "</h1></div><div class=\"control\"><a href=\"/\" class=\"mdc-button mdc-button--raised\">" +
                        "<i class=\"material-icons mdc-button__icon\" aria-hidden=\"true\">keyboard_backspace</i>" +
                        "<span class=\"mdc-button__label\">BACK TO SEARCH</span></a></div>" +
                        "<div class=\"control\" style=\"width: 400px\">" +
                        "<table class=\"mdc-data-table__table\"<thead><tr class=\"mdc-data-table__header-row\">\n" +
                        "<th class=\"mdc-data-table__header-cell\" role=\"columnheader\" scope=\"col\">POINT OF INTEREST"+
                        "</th><th class=\"mdc-data-table__header-cell mdc-data-table__header-cell--numeric\" role="+
                        "\"columnheader\" scope=\"col\">COORDINATES</th></tr></thead><tbody class=\"mdc-data-table__content\">");

        // iterate through POIs and generate clickable table rows
        for (POI poi: candidatePois) {
            htmlDom.append("<tr class=\"mdc-data-table__row\">")
                    .append("<th class=\"mdc-data-table__cell\" scope=\"row\">")
                    .append("<a href=\"/forecast?id=").append(poi.getId()).append("\"><b>")
                    .append(poi.getName()).append("</b></a>").append("</th>")
                    .append("<td class=\"mdc-data-table__cell mdc-data-table__cell--numeric\">")
                    .append(poi.getCoordinates().getKey()).append(", ").append(poi.getCoordinates().getValue())
                    .append("</tr>\n");
        }
        htmlDom.append("</tbody></table></div></body>");

        // finalize and show disambiguation webpage
        out.println(htmlDom);
    }
}
