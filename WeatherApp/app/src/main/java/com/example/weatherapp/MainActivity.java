package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.DATA.Static_data;
import com.example.weatherapp.OtherUtil.GpsCheck;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    double latitude = 0.0;
    double longitude = 0.0;
    private LocationManager locationManager;
    SimpleDateFormat format = new SimpleDateFormat("MM월 dd일 HH시mm분");
    Date time = new Date();

    private LinearLayout weather_panel;
    private ImageView img_weather;
    private TextView
            txt_today_date,
            txt_country,
            txt_temperature,
            txt_description,
            txt_temp_max,
            txt_temp_min,
            txt_wind,
            txt_cloud,
            txt_pressure,
            txt_humidity,
            txt_sunrise,
            txt_sunset;
    private ProgressBar loading;

    private String today_date = format.format(time);
    private String country = "";
    private String temp = "";
    private String description = "";
    private String temp_max = "";
    private String temp_min = "";
    private String wind = "";
    private String cloud = "";
    private String pressure = "";
    private String humidity = "";
    private int sunrise = 0;
    private int sunset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GpsCheck.checkGPS_ON_OFF(MainActivity.this); //gps 확인
        initView();
        Weather();
    }

    private void initView() {
        weather_panel = findViewById(R.id.weather_panel);
        img_weather = findViewById(R.id.img_weather);
        txt_today_date = findViewById(R.id.txt_today_date);
        txt_country = findViewById(R.id.txt_country);
        txt_temperature = findViewById(R.id.txt_temperature);
        txt_description = findViewById(R.id.txt_description);
        txt_temp_max = findViewById(R.id.txt_temp_max);
        txt_temp_min = findViewById(R.id.txt_temp_min);
        txt_wind = findViewById(R.id.txt_wind);
        txt_cloud = findViewById(R.id.txt_cloud);
        txt_pressure = findViewById(R.id.txt_pressure);
        txt_humidity = findViewById(R.id.txt_humidity);
        txt_sunrise = findViewById(R.id.txt_sunrise);
        txt_sunset = findViewById(R.id.txt_sunset);
        loading = findViewById(R.id.loading);
    }

    private LocationListener locationListener = new LocationListener() {
        //위치값이 갱신되면 이벤트 발생
        //위치 제공자 GPS:위성 수신으로 정확도가 높다,실내사용 불가
        //위치 제공자 Network:인터넷 엑세스 수신으로 정확도가 아쉽다, 실내 사용 가능

        @Override
        public void onLocationChanged(Location location) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //여기에서 날씨 보여주기
            latitude = location.getLatitude(); //위도
            longitude = location.getLongitude(); //경도

            if (Static_data.requestQueue == null)
                Static_data.requestQueue = Volley.newRequestQueue(getApplicationContext());

            String url = "https://api.openweathermap.org/data/2.5/weather?"+"lat="+latitude+"&lon="
                    +longitude+"&units=metric&appid="+Static_data.APP_ID;
            request_weather(url);
            locationManager.removeUpdates(locationListener); //더이상 위치값 필요없는 경우 - 해제
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //disabled시
            // Toast.makeText(getContext(),"onStatusChanged provider: "+provider+" status: "+status
            //  +"Bundle: "+extras, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //enabled시
            // Toast.makeText(getContext(),"onProviderEnabled provider: "+provider, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            //변경시
            //Toast.makeText(getContext(),"onProviderDisabled provider: "+provider, Toast.LENGTH_SHORT).show();
        }
    };

    //날씨받아오기 [업데이트된 위치 값을 얻어와 수신2]
    private void Weather() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,//등록할 위치 제공자
                    100, //통지사이의 최소 시간 간격
                    1, //통지사이의 최소 변경 거리
                    locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,//등록할 위치 제공자
                    100, //통지사이의 최소 시간 간격
                    1, //통지사이의 최소 변경 거리
                    locationListener);
        } catch (SecurityException e) {
            System.err.println("security error 발생");
        }
    }

    private void request_weather(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("weather");
                            JSONObject main_obj = response.getJSONObject("main");
                            JSONObject wind_obj = response.getJSONObject("wind");
                            JSONObject cloud_obj = response.getJSONObject("clouds");
                            JSONObject sys_obj = response.getJSONObject("sys");
                            JSONObject object = array.getJSONObject(0);

                            country = String.valueOf(sys_obj.getString("country"));
                            temp = String.valueOf(main_obj.getDouble("temp"));
                            description = object.getString("description");
                            temp_max = String.valueOf(main_obj.getInt("temp_max"));
                            temp_min = String.valueOf(main_obj.getInt("temp_min"));;
                            wind = String.valueOf(wind_obj.getDouble("speed"));
                            cloud = String.valueOf(cloud_obj.getInt("all"));
                            pressure = String.valueOf(main_obj.getInt("pressure"));
                            humidity = String.valueOf(main_obj.getInt("humidity"));
                            sunrise = sys_obj.getInt("sunrise");
                            sunset = sys_obj.getInt("sunset");

                            Picasso.get().load("https://openweathermap.org/img/w/"+
                                    String.valueOf(object.getString("icon"))+".png").into(img_weather);
                            txt_today_date.setText(today_date);
                            txt_country.setText(country);
                            txt_temperature.setText(temp + "°C");
                            txt_description.setText(description);
                            txt_temp_max.setText(temp_max + "°C");
                            txt_temp_min.setText(temp_min + "°C");
                            txt_wind.setText("풍속 " + wind);
                            txt_cloud.setText(cloud + "%");
                            txt_pressure.setText(pressure + " hpa");
                            txt_humidity.setText(humidity + "%");
                            txt_sunrise.setText(Static_data.convertUnixToHour(sunrise));
                            txt_sunset.setText(Static_data.convertUnixToHour(sunset));
                            weather_panel.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e. printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        if (Static_data.requestQueue == null)
            Static_data.requestQueue = Volley.newRequestQueue(getApplicationContext());

        jsonObjectRequest.setShouldCache(false);
        Static_data.requestQueue.add(jsonObjectRequest);
    }
}