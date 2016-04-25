package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import net.sharksystem.android.protocols.nfc.ux.TdmaNfcUxHandler;

import java.lang.ref.WeakReference;

/**
 * Created by m on 4/22/16.
 */
public class ProgressAndVibrateUxHandler extends TdmaNfcUxHandler {

    public final static int VIBRATION_DURATION_SHORT = 250;

    public static final String DIALOG_INIT = "Please connect now. NFC is negotiating which device is acting as reader or smartCard. Touch to abort.";
    public static final String DIALOG_TAG_GONE = "Tag is gone. Touch to dismiss.";
    public static final String DIALOG_DONE = "Done. Touch to dismiss.";
    public static final String DIALOG_INTERRUPTED = "Sending was interrupted. Touch to dismiss.";
    public static final String DIALOG_IN_PROGRESS = "Sending in progress... Touch to abort.";

    final Runnable vibrateShort;
    final WeakReference<UxFragment> fragment;
    final Handler handler = new Handler(Looper.getMainLooper());

    int totalDataLength;
    boolean canVibrateForReceiving = false;
    boolean canVibrateForSending = false;
    boolean hasVibrated = false;

    boolean isShowingDoneMessage = false;

    final Runnable sendingDoneUpdateProgress = new Runnable() {
        @Override
        public void run() {
            final ProgressDialog d = fragment.get().getProgressDialogInstance();
            d.setProgress(d.getMax());
            d.setMessage(DIALOG_DONE);
        }
    };

    final Runnable sendingTagGoneUpdateProgress = new Runnable() {
        @Override
        public void run() {
            final ProgressDialog d = fragment.get().getProgressDialogInstance();
            d.setMessage(DIALOG_TAG_GONE);
        }
    };

    final Runnable sendingNotDoneProgressUpdate = new Runnable() {
        @Override
        public void run() {
            final ProgressDialog d = fragment.get().getProgressDialogInstance();
            d.setMessage(DIALOG_INTERRUPTED);
        }
    };

    public ProgressAndVibrateUxHandler(UxFragment fragment) {
        this.fragment = new WeakReference(fragment);

        final Vibrator vibrator = ((Vibrator) this.fragment.get().getActivity().getSystemService(Activity.VIBRATOR_SERVICE));
        this.vibrateShort = new Runnable() {

            @Override
            public void run() {
                vibrator.vibrate(VIBRATION_DURATION_SHORT);
            }
        };
    }

    public void showProgressDialog() {
        final Runnable prepareSendingProgressUpdate = new Runnable() {
            @Override
            public void run() {
                final ProgressDialog d = fragment.get().getProgressDialogInstance();
                d.dismiss();
                d.setMessage(DIALOG_INIT);
                d.show();
                d.setProgress(0);
                d.setMax(totalDataLength);
            }
        };
        isShowingDoneMessage = false;
        fragment.get().getActivity().runOnUiThread(prepareSendingProgressUpdate);
    }

    @Override
    public void preparedSending(final int totalDataLength) {
        this.totalDataLength = totalDataLength;
        resetVibrateFlags();
        super.preparedSending(totalDataLength);
    }

    @Override
    public void preparedSendingFailed() {
        super.preparedSendingFailed();
        isShowingDoneMessage = false;
    }

    @Override
    public void sending(final int currentDataLength, int leftDataLength) {
        super.sending(currentDataLength, leftDataLength);

        if (leftDataLength == 0) {
            canVibrateForSending = true;
            vibrateIfReady();
        }

        if (isShowingDoneMessage) {
            return;
        }

        if (leftDataLength == 0) {
            isShowingDoneMessage = true;
            fragment.get().getActivity().runOnUiThread(sendingDoneUpdateProgress);
            return;
        }

        isShowingDoneMessage = false;
        final Runnable sendingProgressUpdate = new Runnable() {
            @Override
            public void run() {
                final ProgressDialog d = fragment.get().getProgressDialogInstance();
                d.incrementProgressBy(currentDataLength);
                d.setMessage(DIALOG_IN_PROGRESS);
            }
        };
        fragment.get().getActivity().runOnUiThread(sendingProgressUpdate);
    }

    @Override
    public void tagGoneOnSender() {
        super.tagGoneOnSender();
        if (isShowingDoneMessage) {
            return;
        }
        resetVibrateFlags();
        isShowingDoneMessage = true;
        fragment.get().getActivity().runOnUiThread(sendingTagGoneUpdateProgress);
    }

    @Override
    public void sendingNotDoneCompletely(byte[] byteBuffer) {
        super.sendingNotDoneCompletely(byteBuffer);
        if (isShowingDoneMessage) {
            return;
        }
        isShowingDoneMessage = true;
        fragment.get().getActivity().runOnUiThread(sendingNotDoneProgressUpdate);
    }

    @Override
    public void receiving(int currentDataLength, int newTotalDataLength) {
        super.receiving(currentDataLength, newTotalDataLength);
        if (newTotalDataLength > 0 && currentDataLength == 0) {
            canVibrateForReceiving = true;
            vibrateIfReady();
        }
    }

    private void vibrateIfReady() {
        if (!canVibrateForReceiving || !canVibrateForSending || hasVibrated) {
            return;
        }

        hasVibrated = true;
        handler.post(vibrateShort);
        handler.postDelayed(vibrateShort, VIBRATION_DURATION_SHORT + 100);
    }

    @Override
    public void handleErrorOnReceiving(Exception exception) {
        resetVibrateFlags();
        NfcMainActivity.handleError(fragment.get().getActivity(), exception);
        super.handleErrorOnReceiving(exception);
    }

    @Override
    public void tagGoneOnReceiver() {
        resetVibrateFlags();
        super.tagGoneOnReceiver();
    }

    private void resetVibrateFlags() {
        hasVibrated = false;
        canVibrateForReceiving = false;
        canVibrateForSending = false;
    }
}
