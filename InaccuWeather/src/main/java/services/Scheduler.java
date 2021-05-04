/*
 * InaccuWeather
 * (c) 2020 Matei Sîrbu.
 */
package services;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules the forecast update event.
 */
public class Scheduler extends HttpServlet {

    ScheduledExecutorService scheduler;

    @Override
    public void init() throws ServletException {
        super.init();

        // The simulated overhead per updated POI (0.4 secs).
        final int UPDATE_OVERHEAD = 400;

        // The context the mock data resides in.
        DataProvider dataProvider = new DataProvider(UPDATE_OVERHEAD);
        getServletConfig().getServletContext().setAttribute("dataProvider", dataProvider);

        /* Retrieves current instant in time. Used for determining first execution time. Since in testing mode the
         * waiting time for the first execution is 5 secs (hardcoded value), the Calendar is considered unused.
         * In production, it is used to determine the time remaining until the top of the hour.               */
        @SuppressWarnings("unused") Calendar calendar = Calendar.getInstance();

        /* For testing purposes, forecasts will initially be updated after 10 seconds, then after every 20
         * seconds. Taking the overhead and the number of POIs into account, the updating process should
         * take around 10 seconds (0.4 secs overhead per POI).                                                */
        final int UPDATE_PERIOD = 20000;    /*                  3600000 millisecs, a.k.a. 1 hr.               */
        final int MINS_TO_NEXT_HR = 0;      /* use these        25 - calendar.get(Calendar.MINUTE);           */
        final int SECS_TO_NEXT_HR = 10;     /* in production:   60 - calendar.get(Calendar.SECOND);           */
        final int MILLISECS_TO_NEXT_HR = 0; /*                  1000 - calendar.get(Calendar.MILLISECOND);    */

        /* Time remaining until first execution, i.e. the start of the next hour. For testing
         * purposes, MINS_TO_NEXT_HR AND SECS_TO_NEXT_HR are 0, hence the suppressed warning below.           */
        @SuppressWarnings("PointlessArithmeticExpression")
        final int MILLISECS_TO_FIRST_EXEC = 60000 * MINS_TO_NEXT_HR + 1000 * SECS_TO_NEXT_HR + MILLISECS_TO_NEXT_HR;

        // Service that executes the asynchronous task.
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.print("\nInaccuWeather is updating forecasts ─");
                dataProvider.generateForecasts(true);
                System.out.print("\b\b...\nUpdate completed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, MILLISECS_TO_FIRST_EXEC, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * Used for manually triggering the asynchronous update event.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        DataProvider dataProvider = (DataProvider) getServletConfig().getServletContext().getAttribute("dataProvider");
        final AsyncContext asyncContext = req.startAsync();
        final long TERMINATION_TIMEOUT = 60;

        asyncContext.start(() -> {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
                System.out.println("\n\n[!] The scheduler is being shut down.\n" +
                        "[!] Please wait until pending updates, if any, have completed execution...");
                try {
                    if (!scheduler.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("\n[!] Scheduler has been shut down. Updating manually:");
                }
            }
            System.out.print("\nInaccuWeather is updating forecasts ─");
            dataProvider.generateForecasts(true);
            System.out.print("\b\b...\nUpdate completed.");
            asyncContext.complete();
        });
    }
}
