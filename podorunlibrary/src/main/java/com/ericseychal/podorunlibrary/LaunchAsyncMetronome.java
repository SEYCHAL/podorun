package com.ericseychal.podorunlibrary;

import android.os.AsyncTask;

/**
 * Created by ericseychal on 07/12/2016.
 */

public class LaunchAsyncMetronome  extends AsyncTask<Void, Void, Void> {
    Metronome metronome;

    public LaunchAsyncMetronome() {
        metronome = new Metronome(1,1,1,1,2,2);
    }

    public LaunchAsyncMetronome(double bpm) {
        metronome = new Metronome(bpm,1,1,1,2,2);
    }

    @Override
    public Void doInBackground(Void... params) {
        metronome.play();
        return null;
    }

    public void setBpm(double bpm) {
        metronome.setBpm(bpm);
    }

    public void stop() {
        metronome.stop();
    }
}
