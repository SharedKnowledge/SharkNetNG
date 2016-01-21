package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.os.CountDownTimer;
import android.view.View;

import de.htw_berlin.sharkandroidstack.R;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.R.drawable.ic_media_previous;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

class NfcBenchmarkState {
    static final int STATE_RESET = 1;
    static final int STATE_PREPARED = 2;
    static final int STATE_RUNNING = 3;
    static final int STATE_STOPPED = 4;
    static final int STATE_RECEIVING = 4;

    static final int TICK_INTERVAL = 1000;

    private final NfcBenchmarkFragment fragment;
    private final NfcMainActivity activity;

    private int currentState = 0;
    private boolean needReset = false;
    private CountDownTimer timer;

    public NfcBenchmarkState(NfcBenchmarkFragment fragment, NfcMainActivity activity) {
        this.fragment = fragment;
        this.activity = activity;
    }

    public void nextStateForSending() {
        switch (currentState) {
            case STATE_RESET:
                preparedState();
                return;
            case STATE_RUNNING:
                stoppedState();
                return;
            case STATE_PREPARED:
                resetState();
                return;
            case STATE_STOPPED:
                resetState();
                return;
        }
    }

    void resetState() {
        if (!updateStateIfPossible(STATE_RESET)) {
            return;
        }
        needReset = false;

        fragment.startSendingButton.setVisibility(VISIBLE);
        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_start);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_play, 0);
        fragment.backFromReceivingButton.setVisibility(GONE);

        fragment.setResultVisibility(View.GONE, GONE);

        fragment.resultAdapter.clear();

        activity.prepareReceiving(fragment.readerCallback);
    }

    void preparedState() {
        if (!updateStateIfPossible(STATE_PREPARED)) {
            return;
        }

        fragment.startSendingButton.setText("ready");

        activity.prepareSending(fragment.onMessageSendCallback);
    }

    void sendState() {
        if (needReset || !updateStateIfPossible(STATE_RUNNING)) {
            return;
        }
        needReset = true;

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_stop);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

        int durationInSec = fragment.getDurationInSec();
        final int durationInMS = durationInSec * TICK_INTERVAL;
        fragment.progressBar.setMax(durationInSec);
        fragment.progressBar.setProgress(0);

        fragment.setResultVisibility(VISIBLE, VISIBLE);

        timer = new CountDownTimer(durationInMS, TICK_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                final long soFar = (durationInMS - millisUntilFinished) / TICK_INTERVAL;
                final long timeLeft = (millisUntilFinished / TICK_INTERVAL) + 1;
                fragment.progressDescription.setText(timeLeft + "");
                fragment.progressBar.setProgress((int) soFar);
            }

            public void onFinish() {
                fragment.progressBar.setProgress(fragment.progressBar.getMax());
                fragment.progressDescription.setText(0 + "");
                stoppedState();
            }
        };
        timer.start();
    }

    void stoppedState() {
        if (!updateStateIfPossible(STATE_STOPPED)) {
            return;
        }

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_abort);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_previous, 0);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        activity.prepareReceiving(fragment.readerCallback);
    }

    void receivingState() {
        if (!updateStateIfPossible(STATE_RECEIVING)) {
            return;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        fragment.startSendingButton.setVisibility(GONE);
        fragment.setResultVisibility(View.VISIBLE, GONE);
        fragment.backFromReceivingButton.setVisibility(VISIBLE);
    }

    private boolean updateStateIfPossible(final int state) {
        if (state == currentState) {
            return false;
        }
        currentState = state;
        return true;
    }
}
