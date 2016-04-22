package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
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
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final WeakReference<Activity> activity;

    private boolean hasVibratedForReceiving = false;

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
        if (newTotalDataLength > 0 && currentDataLength == 0 && !hasVibratedForReceiving) {
            hasVibratedForReceiving = true;
            handler.post(vibrateShort);
            handler.postDelayed(vibrateShort, VIBRATION_DURATION_SHORT + 100);
        }
    }

    @Override
    public void sending(int currentDataLength, int leftDataLength) {
        super.sending(currentDataLength, leftDataLength);
    }

    @Override
    public void sendingNotDoneCompletely() {
        super.sendingNotDoneCompletely();
    }

    @Override
    public void tagGoneOnReceiver() {
        super.tagGoneOnReceiver();
        hasVibratedForReceiving = false;
    }

    @Override
    public void tagGoneOnSender() {
        super.tagGoneOnSender();
    }
}
