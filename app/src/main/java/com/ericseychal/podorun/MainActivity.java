package com.ericseychal.podorun;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWheel();
    }

    private void initWheel() {
        bpmWheel = BPM_WHEEL;
        wheelView = (WheelView) findViewById(R.id.wheelview);
        counter = (TextView) findViewById(R.id.counter);
        fab = (FloatingActionButton) findViewById(R.id.button_io);
        circle = (ImageView) findViewById(R.id.cicle);

        counter.setTextSize(30);
        counter.setText("0.00\n Km/H");
        reserveAngle = 0;

        wheelView.setOnWheelAngleChangeListener(new WheelView.OnWheelAngleChangeListener() {
            @Override
            public void onWheelAngleChange(float angle) {
                if (!buttonIo) {
                    bpmWheel = calculBpmWheel(angle);
                    reserveAngle = angle;
                    launchAsyncMetronome.setBpm(bpmWheel);
                    counter.setText( String.format("%.2f", bpmWheel) + "\n Km/H");
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
        return (angle/360) * BPM_WHEEL;
    }

    @AccessCoarseLocation
    @AccessFineLocation
    private void location() {

    }

    

}
