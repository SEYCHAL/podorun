package com.ericseychal.podorun;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;
import com.mindandgo.locationdroid.LocationDroid;
import com.xavierbauquet.theo.Theo;
import com.xavierbauquet.theo.annotations.location.AccessCoarseLocation;
import com.xavierbauquet.theo.annotations.location.AccessFineLocation;

public class MainActivity extends AppCompatActivity {
    final double BPM_WHEEL = 40;

    private double bpmWheel;
    private TextView counter;
    private WheelView wheelView;
    private ImageView circle;
    private LaunchAsyncMetronome launchAsyncMetronome;
    private FloatingActionButton fab;
    private boolean buttonIo = true;
    private float reserveAngle;
    private float speed;
    LocationDroid locationDroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWheel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Theo.isPermissionGranted(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
            location();
        } else {
            autorisation();
        }

    }

    @Override
    protected void onStop() {
        locationDroid.stop();
        super.onStop();
    }

    private void initWheel() {
        bpmWheel = BPM_WHEEL;
        wheelView = (WheelView) findViewById(R.id.wheelview);
        counter = (TextView) findViewById(R.id.counter);
        fab = (FloatingActionButton) findViewById(R.id.button_io);
        circle = (ImageView) findViewById(R.id.cicle);

        counter.setTextSize(30);
        reserveAngle = 0;
        speed = 0;
//        circle.setBackgroundColor(getColor());

        wheelView.setOnWheelAngleChangeListener(new WheelView.OnWheelAngleChangeListener() {
            @Override
            public void onWheelAngleChange(float angle) {
                if (!buttonIo) {
                    bpmWheel = calculBpmWheel(angle);
                    reserveAngle = angle;
                    launchAsyncMetronome.setBpm(bpmWheel);
                    showCounter();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonIo) {
                    wheelView.setAngle(reserveAngle);
                    launchAsyncMetronome = new LaunchAsyncMetronome(bpmWheel);
                    launchAsyncMetronome.execute();
                } else {
                    launchAsyncMetronome.stop();
                    launchAsyncMetronome = null;
                }
                buttonIo = !buttonIo;
            }
        });
    }

    private double calculBpmWheel(float angle) {
        return (angle / 360) * BPM_WHEEL;
    }

    private void showCounter(){
        counter.setText(String.format("%.2f", speed) + "\n Km/H\n" + String.format("%.1f", bpmWheel));
    }

    @AccessCoarseLocation
    @AccessFineLocation
    @SuppressWarnings("MissingPermission")
    private void location() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            counter.setText(getResources().getString(R.string.error_permission));
//            return;
//        LocationManager
//                GPS
//        }

            locationDroid = new LocationDroid(this) {
                @Override
                public void onNewLocation(Location location) {
                    speed = location.getSpeed();
                    showCounter();
                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }

                @Override
                public void serviceProviderStatusListener(String s, int i, Bundle bundle) {

                }
            };

//        locationDroid.setMaxTimeBetweenUpdates(10f);
//        locationDroid.setDistanceBetweenUpdates(5f);

            try {
                locationDroid.start();
            } catch (SecurityException s) {
                Log.e("Permissions Error", s.toString());
            }
    }

    @AccessCoarseLocation
    @AccessFineLocation
    @SuppressWarnings("MissingPermission")
    private void autorisation() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Theo.REQUEST_CODE) {
            if (Theo.isPermissionGranted(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                location();
            } else {
                counter.setText(getResources().getString(R.string.error_permission));
            }
        }
    }
}
