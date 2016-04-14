package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.os.CountDownTimer;
import android.view.View;

import net.sharksystem.android.protocols.nfc.NfcAdapterHelper;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;

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
    static final int STATE_RECEIVING = 5;
    public static final String SMART_CARD_IDENTIFIER = "NFC_BENCHMARK";

    private final NfcBenchmarkFragment fragment;
    private final NfcMainActivity activity;

    private int currentState = 0;
    private CountDownTimer timer;

    public NfcBenchmarkState(NfcBenchmarkFragment fragment, NfcMainActivity activity) {
        this.fragment = fragment;
        this.activity = activity;
    }

    public boolean nextStateForSending() {
        switch (currentState) {
            case STATE_RESET:
                return preparedState();
            case STATE_RUNNING:
                return stoppedState();
            case STATE_PREPARED:
                return resetState();
            case STATE_STOPPED:
                return resetState();
        }
        return false;
    }

    boolean resetState() {
        if (!updateStateIfPossible(STATE_RESET)) {
            return false;
        }

        fragment.startSendingButton.setVisibility(VISIBLE);
        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_start);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_play, 0);
        fragment.backFromReceivingButton.setVisibility(GONE);

        fragment.setResultVisibility(View.GONE, GONE);

        fragment.resultAdapter.clear();

        NfcAdapterHelper.actAsNfcReaderWriter(SMART_CARD_IDENTIFIER, activity, fragment.onMessageSendCallback, fragment.onMessageReceivedCallback);
        return true;
    }

    boolean preparedState() {
        if (!updateStateIfPossible(STATE_PREPARED)) {
            return false;
        }

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_ready);
        NfcAdapterHelper.actAsSmartCard(SMART_CARD_IDENTIFIER, activity, fragment.onMessageSendCallback, fragment.onMessageReceivedCallback);
        return true;
    }

    boolean sendState() {
        if (!updateStateIfPossible(STATE_RUNNING)) {
            return false;
        }

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_stop);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

        fragment.setResultVisibility(VISIBLE, VISIBLE);

        timer = fragment.prepareTimer();
        timer.start();

        return true;
    }

    boolean stoppedState() {
        if (!updateStateIfPossible(STATE_STOPPED)) {
            return false;
        }

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_abort);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_previous, 0);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        NfcAdapterHelper.actAsNfcReaderWriter(SMART_CARD_IDENTIFIER, activity, fragment.onMessageSendCallback, fragment.onMessageReceivedCallback);
        return true;
    }

    boolean receivingState() {
        if (!updateStateIfPossible(STATE_RECEIVING)) {
            return false;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        fragment.startSendingButton.setVisibility(GONE);
        fragment.setResultVisibility(View.VISIBLE, GONE);
        fragment.backFromReceivingButton.setVisibility(VISIBLE);
        return true;
    }

    boolean isState(int state) {
        return state == currentState;
    }

    private boolean updateStateIfPossible(final int state) {
        if (STATE_STOPPED == currentState && STATE_RESET != state) {
            return false;
        }

        if ((STATE_RUNNING == currentState || STATE_PREPARED == currentState) && STATE_RECEIVING == state) {
            return false;
        }

        if (STATE_RECEIVING == currentState && (STATE_RUNNING == state || STATE_PREPARED == state)) {
            return false;
        }
        if (state == currentState) {
            return false;
        }
        currentState = state;
        return true;
    }
}
