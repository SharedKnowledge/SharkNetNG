package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.R.drawable.ic_media_previous;

public class MyStartButtonClickListener implements View.OnClickListener {

    public static final int TIMER_END = 30000;
    public static final int TICK_INTERVAL = 1000;

    final int STATE_RESET = 1;
    final int RUNNING = 2;
    final int STATE_STOPPED = 3;

    private final ProgressBar progressBar;
    private final ListView results;
    private final View description;
    private final OnMessageSend benchmarkSource;
    private NfcMainActivity activity;
    private Button button;

    int currentState = STATE_RESET;

    public MyStartButtonClickListener(ProgressBar progressBar, ListView results, View description, OnMessageSend benchmarkSource, NfcMainActivity activity, Button startButton) {
        this.progressBar = progressBar;
        this.results = results;
        this.description = description;
        this.benchmarkSource = benchmarkSource;
        this.activity = activity;
        button = startButton;
    }

    final CountDownTimer timer = new CountDownTimer(TIMER_END, TICK_INTERVAL) {

        public void onTick(long millisUntilFinished) {
            long soFar = (TIMER_END - millisUntilFinished) / TICK_INTERVAL;
            progressBar.setProgress((int) soFar);
        }

        public void onFinish() {
            progressBar.setProgress(progressBar.getMax());
            if (currentState == RUNNING) {
                MyStartButtonClickListener.this.onClick(button);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (currentState) {
            case STATE_RESET:
                setStateToRunning();
                return;
            case RUNNING:
                setStateToStopped();
                return;
            case STATE_STOPPED:
                setStateToReset();
                return;
        }

    }

    public void setStateToRunning() {
        button.setText(R.string.activity_nfc_benchmark_stop);
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

        progressBar.setProgress(0);
        timer.start();

        description.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        results.setVisibility(View.VISIBLE);

        activity.prepareSending(benchmarkSource);

        currentState = RUNNING;
    }

    public void setStateToStopped() {
        button.setText(R.string.activity_nfc_benchmark_abort);
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_previous, 0);

        timer.cancel();

        currentState = STATE_STOPPED;
    }

    public void setStateToReset() {
        button.setText(R.string.activity_nfc_benchmark_start);
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_play, 0);

        description.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        results.setVisibility(View.GONE);
        currentState = STATE_RESET;
    }
};