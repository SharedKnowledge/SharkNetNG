package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Activity;
import android.os.Handler;
import android.os.Vibrator;

import net.sharksystem.android.protocols.nfc.NfcUXHandler;

import java.lang.ref.WeakReference;

import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;

/**
 * Created by m on 4/22/16.
 */
public class NfcDemoUxHandler extends NfcUXHandler {

    public final static int VIBRATION_DURATION = 500;
    public final static int VIBRATION_DURATION_SHORT = 250;

    private final Runnable vibrateShort;
    private WeakReference<Activity> activity;
    private Handler handler = new Handler();

    public NfcDemoUxHandler(Activity activity) {
        this.activity = new WeakReference<>(activity);

        final Vibrator vibrator = ((Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE));
        this.vibrateShort = new Runnable() {

            @Override
            public void run() {
                vibrator.vibrate(VIBRATION_DURATION_SHORT);
            }
        };
    }

    @Override
    public void handleErrorOnReceiving(Exception exception) {
        NfcMainActivity.handleError(activity.get(), exception);
        super.handleErrorOnReceiving(exception);
    }

    @Override
    public void preparedSending(int totalDataLength) {
        super.preparedSending(totalDataLength);
    }

    @Override
    public void preparedSendingFailed() {
        super.preparedSendingFailed();
    }

    @Override
    public void receiving(int currentDataLength, int newTotalDataLength) {
        super.receiving(currentDataLength, newTotalDataLength);
    }

    @Override
    public void sending(int currentDataLength, int leftDataLength) {
        if (leftDataLength == 0) {
            handler.post(vibrateShort);
            handler.postDelayed(vibrateShort, VIBRATION_DURATION_SHORT + 100);
        }
        super.sending(currentDataLength, leftDataLength);
    }

    @Override
    public void sendingNotDoneCompletely() {
        super.sendingNotDoneCompletely();
    }

    @Override
    public void tagGoneOnReceiver() {
        super.tagGoneOnReceiver();
    }

    @Override
    public void tagGoneOnSender() {
        super.tagGoneOnSender();
    }
}
