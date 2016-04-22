package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Activity;
import android.app.ProgressDialog;
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
    public static final String DIALOG_INIT = "Please connect now.";

    private final Runnable vibrateShort;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final WeakReference<Activity> activity;
    private final WeakReference<ProgressDialog> progressDialog;

    private boolean hasVibratedForReceiving = false;
    final Runnable sendingDoneUpdateProgress = new Runnable() {
        @Override
        public void run() {
            final ProgressDialog d = progressDialog.get();
            d.setCancelable(true);
            d.setProgress(d.getMax());
            d.setMessage("Done. Touch to dismiss.");
        }
    };
    final Runnable sendingNotDoneProgressUpdate = new Runnable() {
        @Override
        public void run() {
            final ProgressDialog d = progressDialog.get();
            d.setCancelable(true);
            d.setMessage("Sending was interrupted. Touch to dismiss.");
        }
    };

    public NfcDemoUxHandler(Activity activity, ProgressDialog progressDialog) {
        this.activity = new WeakReference<>(activity);
        this.progressDialog = new WeakReference<>(progressDialog);

        final Vibrator vibrator = ((Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE));
        this.vibrateShort = new Runnable() {

            @Override
            public void run() {
                vibrator.vibrate(VIBRATION_DURATION_SHORT);
            }
        };
    }

    @Override
    public void preparedSending(final int totalDataLength) {
        super.preparedSending(totalDataLength);
        final Runnable prepareSendingProgressUpdate = new Runnable() {
            @Override
            public void run() {
                final ProgressDialog d = progressDialog.get();
                d.setMax(totalDataLength);
                d.setMessage(DIALOG_INIT);
                d.setCancelable(false);
                d.show();
            }
        };
        activity.get().runOnUiThread(prepareSendingProgressUpdate);
    }

    @Override
    public void preparedSendingFailed() {
        super.preparedSendingFailed();
    }

    @Override
    public void sending(final int currentDataLength, int leftDataLength) {
        super.sending(currentDataLength, leftDataLength);

        if (leftDataLength == 0) {
            activity.get().runOnUiThread(sendingDoneUpdateProgress);
            return;
        }

        final Runnable sendingProgressUpdate = new Runnable() {
            @Override
            public void run() {
                final ProgressDialog d = progressDialog.get();
                d.incrementProgressBy(currentDataLength);
                d.setMessage("Sending in progress...");
            }
        };
        activity.get().runOnUiThread(sendingProgressUpdate);
    }

    @Override
    public void tagGoneOnSender() {
        super.tagGoneOnSender();
    }

    @Override
    public void sendingNotDoneCompletely() {
        super.sendingNotDoneCompletely();
        activity.get().runOnUiThread(sendingNotDoneProgressUpdate);
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
    public void handleErrorOnReceiving(Exception exception) {
        NfcMainActivity.handleError(activity.get(), exception);
        super.handleErrorOnReceiving(exception);
    }

    @Override
    public void tagGoneOnReceiver() {
        super.tagGoneOnReceiver();
        hasVibratedForReceiving = false;
    }
}
