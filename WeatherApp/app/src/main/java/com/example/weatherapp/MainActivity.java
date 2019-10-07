package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.example.weatherapp.Adapter.ViewPagerAdapter;
import com.example.weatherapp.Key.Key;
import com.example.weatherapp.Model.WeatherResult;
import com.example.weatherapp.Retrofit.OpenWeatherMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private ImageView img_weather;
    private TextView today_weather, txt_temperature, txt_description,
            txt_date_time, txt_wind, txt_pressure,
            txt_humidity, txt_sunrise, txt_sunset, txt_geo_coords;

    private LinearLayout weather_panel;

    private CompositeDisposable compositeDisposable;
    private OpenWeatherMap mService;


    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("진입T", "DD");
            Key.current_location = locationResult.getLastLocation();
            Log.i("진입:", String.valueOf(Key.current_location.getLatitude()));
        }
     };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Dexter.withActivity(this)
                .withPermissions(permission.ACCESS_COARSE_LOCATION,
                        permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                        }
                        else{
                            if (ContextCompat.checkSelfPermission(MainActivity.this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                                    ContextCompat.checkSelfPermission(MainActivity.this, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
                            {
                                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission.ACCESS_FINE_LOCATION ) ||
                                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission.ACCESS_COARSE_LOCATION )){

                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{permission.ACCESS_FINE_LOCATION,
                                                    permission.ACCESS_COARSE_LOCATION},101);
                                }
                                else{
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{permission.ACCESS_FINE_LOCATION,
                                                    permission.ACCESS_COARSE_LOCATION},101);
                                }
                            }
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        Snackbar.make(getApplicationContext(), "Permission Denied", Snackbar.LENGTH_LONG).show();
                    }
                }).check();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.i("진입1", "gg");
                    locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
//                            .setInterval(5000)
//                            .setFastestInterval(3000)
                            .setSmallestDisplacement(10.0f);

                    LocationSettingsRequest.Builder builder =
                            new LocationSettingsRequest.Builder();
                    builder.addLocationRequest(locationRequest);
                    Log.i("진입2", "gg");
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback , Looper.myLooper());
                    Log.i("진입3", "gg");
                    Thread.sleep(500);
                    getWeatherInformation();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        //Request permission
    }

    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions, @NonNull int[]grantResult ){
        super.onRequestPermissionsResult(requestCode,permissions,grantResult);

        if(requestCode == 101){
            if(grantResult.length > 0){
                for(int aGrant : grantResult){
                    if(aGrant == PackageManager.PERMISSION_DENIED){
                        new AlertDialog.Builder(MainActivity.this)
                                .setCancelable(false)
                                .setMessage("권한 확인")
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APN_SETTINGS)
                                                .setData(Uri.parse("package:"+getPackageName()));
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
                        return;
                    }
                }
            }
        }
    }

    private void getWeatherInformation() {
        Log.i("진입4", "gg");
        compositeDisposable
                .add(mService.getWeatherByLating(String.valueOf(Key.current_location.getLatitude()),String.valueOf(Key.current_location.getLongitude()),
                Key.APP_ID.trim(),
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        //Load image
                        Picasso.get().load("https://openweathermap.org/img/w/" +
                                weatherResult.getWeather().get(0).getIcon() +
                                ".png").into(img_weather);

                        //Load information
                        today_weather.setText(weatherResult.getName());
                        txt_date_time.setText(new StringBuilder("Weather in ").append(weatherResult.getName()).toString());
                        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("C").toString());
                        txt_date_time.setText(Key.convertUnixTodate(weatherResult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        txt_sunrise.setText(Key.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(Key.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txt_geo_coords.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());

                        //Display panel
                        weather_panel.setVisibility(View.VISIBLE);
                        Log.i("진입5", "gg");
                    }
                }));
    }
    private void initView(){
        img_weather = (ImageView)findViewById(R.id.img_weather);
        today_weather = (TextView)findViewById(R.id.today_weather);
        txt_temperature = (TextView)findViewById(R.id.txt_temperature);
        txt_description = (TextView)findViewById(R.id.txt_description);
        txt_date_time = (TextView)findViewById(R.id.txt_date_time);
        txt_wind = (TextView)findViewById(R.id.txt_wind);
        txt_pressure = (TextView)findViewById(R.id.txt_pressure);
        txt_humidity = (TextView)findViewById(R.id.txt_humidity);
        txt_sunrise = (TextView)findViewById(R.id.txt_sunrise);
        txt_sunset = (TextView)findViewById(R.id.txt_sunset);
        txt_geo_coords = (TextView)findViewById(R.id.txt_geo_coords);
        weather_panel = (LinearLayout)findViewById(R.id.weather_panel);
    }
}