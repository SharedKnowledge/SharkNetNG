package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.os.CountDownTimer;
import android.view.View;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.NfcAdapterHelper;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.R.drawable.ic_media_previous;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by mn-io on 22.01.16.
 */
class NfcBenchmarkState {
    static final int STATE_RESET = 1;
    static final int STATE_PREPARED = 2;
    static final int STATE_RUNNING = 3;
    static final int STATE_STOPPED = 4;
    static final int STATE_RECEIVING = 4;

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

        NfcAdapterHelper.prepareReceiving(activity, fragment.readerCallback);
    }

    void preparedState() {
        if (!updateStateIfPossible(STATE_PREPARED)) {
            return;
        }

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_ready);
        NfcAdapterHelper.prepareSending(activity, fragment.onMessageSendCallback, fragment.onMessageReceivedCallback);
    }

    void sendState() {
        if (needReset || !updateStateIfPossible(STATE_RUNNING)) {
            return;
        }
        needReset = true;

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_stop);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

        fragment.setResultVisibility(VISIBLE, VISIBLE);

        timer = fragment.prepareTimer();
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

        NfcAdapterHelper.prepareReceiving(activity, fragment.readerCallback);
    }

    void receivingState() {
        if (STATE_RUNNING == currentState) {
            return;
        }
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

    boolean isState(int state) {
        return state == currentState;
    }

    private boolean updateStateIfPossible(final int state) {
        if (state == currentState) {
            return false;
        }
        currentState = state;
        return true;
    }

    public boolean isStopped() {
        return currentState == STATE_STOPPED;
    }
}
