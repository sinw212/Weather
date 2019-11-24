package com.example.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherapp.OtherUtil.GpsCheck;

public class SplashActivity extends Activity {
    private final int PERMISSIONREQUEST_RESULT = 100; // 콜백 호출시 requestcode로 넘어가는 구분자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        GpsCheck.checkGPS_ON_OFF(SplashActivity.this); //gps 확인
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    Thread.sleep(2000);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                else
                    CheckPermission();
            }
            else { // 마시멜로우 미만
                Thread.sleep(2000);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //퍼미션 권한 진행 함수
    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            //사용자의 최초 퍼미션 허용을 확인         -true: 사용자 퍼미션 거부 , -false: 사용자 동의 미 필요
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                Toast.makeText(getApplicationContext(), "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,},
                        PERMISSIONREQUEST_RESULT);
            }
            else {
                Toast.makeText(getApplicationContext(), "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,},
                        PERMISSIONREQUEST_RESULT);
            }
        }else{
            // 퍼미션을 다 동의 했을 경우 다음으로 진행
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (requestCode == PERMISSIONREQUEST_RESULT) {
            if (grantResult.length > 0) {
                for (int aGrantResult : grantResult) {
                    if (aGrantResult == PackageManager.PERMISSION_DENIED) {
                        //권한이 하나라도 거부 될 시
                        finish();
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    { // 여러개의 권한 확인 후 메인으로 넘기기 위함
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }
            }
        }
    }
}