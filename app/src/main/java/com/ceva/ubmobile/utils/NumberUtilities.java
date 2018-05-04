package com.ceva.ubmobile.utils;

import com.ceva.ubmobile.core.constants.Constants;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by brian on 20/03/2017.
 */

public class NumberUtilities {
    public static String getWithDecimal(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        String formattedString = formatter.format(number);

        return formattedString;
    }

    public static String getWithDecimalPlusCurrency(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        String formattedString = formatter.format(number);

        return Constants.KEY_NAIRA + formattedString;
    }

    public static String getNumbersOnly(String text) {
        text = text.replaceAll("[^0-9.]", "");
        return text;
    }

    public static String getNumbersOnlyNoDecimal(String text) {
        text = text.replaceAll("[^0-9]", "");
        return text;
    }

    /**
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return
     */
    public static float distance(float lat_a, float lng_a, float lat_b, float lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (float) (distance * meterConversion);
    }

    /**
     * @param timeStamp
     * @return
     */
    public static String getDate(long timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getAccountNumberFromSpinner(String string) {
        String[] fromsplit = string.split("-");
        int max = fromsplit.length;
        max = max - 1;

        return fromsplit[max].trim();
    }

}
