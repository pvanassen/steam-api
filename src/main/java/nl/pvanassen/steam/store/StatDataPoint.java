package nl.pvanassen.steam.store;

import java.util.Date;

/**
 * Object defining data points
 * 
 * @author Paul van Assen
 */
public class StatDataPoint {

    private final Date date;
    private final int sales;

    private final double average;

    StatDataPoint( Date date, int sales, double average ) {
        super();
        this.date = date;
        this.sales = sales;
        this.average = average;
    }

    /**
     * @return the average
     */
    public double getAverage() {
        return average;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the sales
     */
    public int getSales() {
        return sales;
    }
}
