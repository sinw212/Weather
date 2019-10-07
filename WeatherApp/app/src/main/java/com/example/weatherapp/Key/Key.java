package com.example.weatherapp.Key;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Key {
    public static final String APP_ID = "13f751b7bcc1be765f478c29f65dab12";
    public static Location current_location = null;

    public static String convertUnixTodate(long dt) {
            Date date = new Date(dt*1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("HH;mm EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH;mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}