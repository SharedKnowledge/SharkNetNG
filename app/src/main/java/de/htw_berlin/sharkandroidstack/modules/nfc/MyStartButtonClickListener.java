package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.R.drawable.ic_media_previous;

public class MyStartButtonClickListener implements View.OnClickListener {

    public static final int TIMER_END = 30000;
    final int STATE_STARTABLE = 1;
    final int STATE_ABORTABLE = 2;
    final int STATE_RESETABLE = 3;

    private final ProgressBar progressBar;
    private final ListView results;
    private final View description;
    private final OnMessageSend benchmarkSource;
    private NfcMainActivity activity;

    int currentState = STATE_STARTABLE;

    public MyStartButtonClickListener(ProgressBar progressBar, ListView results, View description, OnMessageSend benchmarkSource, NfcMainActivity activity) {
        this.progressBar = progressBar;
        this.results = results;
        this.description = description;
        this.benchmarkSource = benchmarkSource;
        this.activity = activity;
    }

    final CountDownTimer timer = new CountDownTimer(TIMER_END, 1000) {

        public void onTick(long millisUntilFinished) {
            long soFar = (TIMER_END - millisUntilFinished) / 1000;
            int total = (int) soFar;
            progressBar.setProgress(total);
        }

        public void onFinish() {
            progressBar.setProgress(progressBar.getMax());
        }
    };

    @Override
    public void onClick(View v) {
        TextView view = (TextView) v;

        switch (currentState) {
            case STATE_STARTABLE:
                view.setText(R.string.activity_nfc_benchmark_stop);
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

                progressBar.setProgress(0);
                timer.start();

                description.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                results.setVisibility(View.VISIBLE);

                activity.prepareSending(benchmarkSource);

                currentState = STATE_ABORTABLE;
                return;
            case STATE_ABORTABLE:
                view.setText(R.string.activity_nfc_benchmark_abort);
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_previous, 0);

                timer.cancel();

                currentState = STATE_RESETABLE;
                return;
            case STATE_RESETABLE:
                view.setText(R.string.activity_nfc_benchmark_start);
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_play, 0);

                description.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                results.setVisibility(View.GONE);
                currentState = STATE_STARTABLE;
                return;
        }

    }
};