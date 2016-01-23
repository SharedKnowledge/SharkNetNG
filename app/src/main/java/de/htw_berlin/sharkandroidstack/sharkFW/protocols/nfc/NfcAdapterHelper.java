package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Build;

/**
 * Created by mn-io on 23.01.2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcAdapterHelper {

    public static final int NFC_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;

    public static void prepareReceiving(Activity activity, NfcAdapter.ReaderCallback readerCallback) {
        if (activity.isDestroyed()) {
            return;
        }
        getAdapter(activity).enableReaderMode(activity, readerCallback, NFC_FLAGS, null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void prepareSending(Activity activity, OnMessageSend src) {
        SmartCardEmulationService.setSource(src);
        getAdapter(activity).disableReaderMode(activity);
    }

    public static NfcAdapter getAdapter(Activity activity) {
        return NfcAdapter.getDefaultAdapter(activity.getApplicationContext());
    }
}
