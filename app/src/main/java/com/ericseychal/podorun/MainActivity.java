package com.ericseychal.podorun;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;

public class MainActivity extends AppCompatActivity {
    TextView counter;
    WheelView wheelView;
    Metronome metronome;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWheel();
    }

    private void initWheel() {
        wheelView = (WheelView) findViewById(R.id.wheelview);
        counter = (TextView) findViewById(R.id.counter);
        fab = (FloatingActionButton) findViewById(R.id.button_io);
        final boolean buttonIo = false;
        metronome = new Metronome(80,1,1,1,2,2);

        counter.setTextSize(30);
        counter.setText("0.00\n Km/H");


        wheelView.setOnWheelAngleChangeListener(new WheelView.OnWheelAngleChangeListener() {
            @Override
            public void onWheelAngleChange(float angle) {
                counter.setText("Speed \n"+ String.format("%.2f", angle) +"\n Km/H");
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonIo) {
                    metronome.stop();
                } else {
                    metronome.play();
                }

            }
        });
    }

}
