package com.example.weatherapp.DATA;


import com.android.volley.RequestQueue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Static_data {
    //OpenWeatherMap API 키값
    public static final String APP_ID = "13f751b7bcc1be765f478c29f65dab12";
    public static RequestQueue requestQueue;;

    public static String convertUnixToHour(long dt) {
        //일출/일몰 시간 변환
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH;mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}