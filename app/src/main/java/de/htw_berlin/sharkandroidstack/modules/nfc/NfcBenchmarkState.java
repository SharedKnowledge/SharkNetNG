package de.htw_berlin.sharkandroidstack.modules.nfc;

import de.htw_berlin.sharkandroidstack.R;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.R.drawable.ic_media_previous;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

class NfcBenchmarkState {
    final int STATE_RESET = 1;
    final int STATE_RUNNING = 2;
    final int STATE_STOPPED = 3;

    final int STATE_RECEIVING = 4;

    private final NfcBenchmarkFragment fragment;
    private NfcMainActivity activity;

    private int currentState = 0;

    public NfcBenchmarkState(NfcBenchmarkFragment fragment, NfcMainActivity activity) {
        this.fragment = fragment;
        this.activity = activity;
    }

    public void nextStateForSending() {
        switch (currentState) {
            case STATE_RESET:
                sendState();
                return;
            case STATE_RUNNING:
                stoppedState();
                return;
            case STATE_STOPPED:
                resetState();
                return;
        }
    }

    void resetState() {
        if (STATE_RESET == currentState) {
            return;
        }
        currentState = STATE_RESET;

        fragment.startSendingButton.setVisibility(VISIBLE);
        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_start);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_play, 0);

        fragment.description.setVisibility(VISIBLE);
        fragment.msgLengthInput.setVisibility(VISIBLE);
        fragment.msgLengthOutput.setVisibility(VISIBLE);

        fragment.backFromReceivingButton.setVisibility(GONE);
        fragment.progressBar.setVisibility(GONE);
        fragment.resultList.setVisibility(GONE);

        fragment.resultAdapter.clear();
        fragment.progressBar.setProgress(0);

        activity.prepareReceiving(fragment.readerCallback);
    }

    private void sendState() {
        if (STATE_RUNNING == currentState) {
            return;
        }
        currentState = STATE_RUNNING;

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_stop);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

        fragment.progressBar.setProgress(0);

        fragment.description.setVisibility(GONE);
        fragment.msgLengthInput.setVisibility(GONE);
        fragment.msgLengthOutput.setVisibility(GONE);

        fragment.progressBar.setVisibility(VISIBLE);
        fragment.resultList.setVisibility(VISIBLE);

        fragment.timer.start();

        activity.prepareSending(fragment.onMessageSendCallback);
    }

    void stoppedState() {
        if (STATE_STOPPED == currentState) {
            return;
        }
        currentState = STATE_STOPPED;

        fragment.startSendingButton.setText(R.string.activity_nfc_benchmark_abort);
        fragment.startSendingButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_previous, 0);

        fragment.timer.cancel();
    }

    void receivingState() {
        if (STATE_RECEIVING == currentState) {
            return;
        }

        currentState = STATE_RECEIVING;

        fragment.startSendingButton.setVisibility(GONE);
        fragment.timer.cancel();

        fragment.description.setVisibility(GONE);
        fragment.msgLengthInput.setVisibility(GONE);
        fragment.msgLengthOutput.setVisibility(GONE);
        fragment.progressBar.setVisibility(GONE);

        fragment.backFromReceivingButton.setVisibility(VISIBLE);
        fragment.resultList.setVisibility(VISIBLE);
    }
}
