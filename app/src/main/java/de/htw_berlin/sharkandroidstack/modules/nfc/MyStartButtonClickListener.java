package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.view.View;
import android.widget.Button;

public class MyStartButtonClickListener implements View.OnClickListener {

    final int STATE_RESET = 1;
    final int RUNNING = 2;
    final int STATE_STOPPED = 3;

    private NfcBenchmarkFragment nfcBenchmarkFragment;

    int currentState = STATE_RESET;

    public MyStartButtonClickListener(NfcBenchmarkFragment nfcBenchmarkFragment) {
        this.nfcBenchmarkFragment = nfcBenchmarkFragment;
    }

    @Override
    public void onClick(View v) {
        switch (currentState) {
            case STATE_RESET:
                nfcBenchmarkFragment.setStateToRunning(true);
                currentState = RUNNING;
                return;
            case RUNNING:
                nfcBenchmarkFragment.setStateToStopped();
                currentState = STATE_STOPPED;
                return;
            case STATE_STOPPED:
                nfcBenchmarkFragment.setStateToReset();
                currentState = STATE_RESET;
                return;
        }
    }

    public void abortExternal(Button startButton) {
        if (currentState == RUNNING) {
            MyStartButtonClickListener.this.onClick(startButton);
        }
    }

    public void forceStart(Button startButton) {
        nfcBenchmarkFragment.setStateToRunning(false);
        currentState = RUNNING;
//        while (currentState != RUNNING) {
//            MyStartButtonClickListener.this.onClick(startButton);
//        }
    }
};