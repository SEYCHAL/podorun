package com.ericseychal.podorunwear;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ericseychal.podorunlibrary.LaunchAsyncMetronome;
import com.lukedeighton.wheelview.WheelView;
import com.mindandgo.locationdroid.LocationDroid;
import com.xavierbauquet.theo.Theo;
import com.xavierbauquet.theo.annotations.location.AccessCoarseLocation;
import com.xavierbauquet.theo.annotations.location.AccessFineLocation;

public class MainActivity extends Activity {

    final double BPM_WHEEL = 40;

    private double bpmWheel;
    private TextView counter;
    private TextView bpm;
    private WheelView wheelView;
    private ImageView circle;
    private LaunchAsyncMetronome launchAsyncMetronome;
    private Button fab;
    private boolean buttonIo = true;
    private float reserveAngle;
    private float speed;
    LocationDroid locationDroid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                initWheel(stub);
            }
        });
    }

    private void initWheel(View view) {
        bpmWheel = BPM_WHEEL;
        wheelView = (WheelView) findViewById(R.id.wheelview);
        counter = (TextView) findViewById(R.id.counter);
        fab = (Button) findViewById(R.id.button_io);
        circle = (ImageView) findViewById(R.id.cicle);
        bpm = (TextView) findViewById(R.id.bpm);

        counter.setTextSize(30);
        reserveAngle = 0;
        speed = 0;
//        circle.setBackgroundColor(getColor());
        showCounter();

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
                    fab.setText("Stop");
                } else {
                    launchAsyncMetronome.stop();
                    launchAsyncMetronome = null;
                    fab.setText("Start");
                }
                buttonIo = !buttonIo;
            }
        });
    }

    private double calculBpmWheel(float angle) {
        return (angle / 360) * BPM_WHEEL;
    }

    private void showCounter(){
        bpm.setText("\n\n\n\n\n" + String.format("%.1f", bpmWheel) + " bpm");
        counter.setText(String.format("%.2f", speed) + "\n Km/H");
    }

    @AccessCoarseLocation
    @AccessFineLocation
    @SuppressWarnings("MissingPermission")
    private void location() {
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
            if (Theo.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                location();
            } else {
                counter.setText(getResources().getString(R.string.error_permission));
            }
        }
    }

}
