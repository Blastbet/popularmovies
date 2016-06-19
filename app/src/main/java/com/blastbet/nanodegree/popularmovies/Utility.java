package com.blastbet.nanodegree.popularmovies;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ilkka on 18.6.2016.
 */
public class Utility {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    static String dateToString(final Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    static Date parseDate(final String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.parse(dateString);
    }
}
