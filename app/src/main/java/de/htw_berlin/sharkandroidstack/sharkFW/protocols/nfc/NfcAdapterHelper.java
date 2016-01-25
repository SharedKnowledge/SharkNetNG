package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Build;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.androidService.SmartCardEmulationService;

/**
 * Created by mn-io on 23.01.2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcAdapterHelper {

    public static final int NFC_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;

    /*
     * NFC is waiting for other NFC device to connect to.
     * Technically this device is actively trying to detect devices by electromagnetic induction,
     * which means it is "sending" energy in order to activate passive devices.
     */
    public static void prepareReceiving(Activity activity, NfcAdapter.ReaderCallback readerCallback) {
        if (activity.isDestroyed()) {
            return;
        }
        getAdapter(activity).enableReaderMode(activity, readerCallback, NFC_FLAGS, null);
    }

    /*
     * NFC acts as a passive SmartCard, which contains data to send.
     * Technically this device is waiting to receive energy by electromagnetic induction.
     */
    public static void prepareSending(Activity activity, OnMessageSend src) {
        if (activity.isDestroyed()) {
            return;
        }
        SmartCardEmulationService.setSource(src);
        getAdapter(activity).disableReaderMode(activity);
    }

    public static NfcAdapter getAdapter(Activity activity) {
        return NfcAdapter.getDefaultAdapter(activity.getApplicationContext());
    }
}
