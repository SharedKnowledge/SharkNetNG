package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.androidService;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

/**
 * Created by mn-io on 23.01.2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcReaderCallback implements NfcAdapter.ReaderCallback {
    private OnMessageReceived onMessageReceived;
    private OnMessageSend onMessageSendCallback;
    private IsoDepTransceiver isoDepTransceiver;

    public NfcReaderCallback(OnMessageSend onMessageSendCallback, OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
        this.onMessageSendCallback = onMessageSendCallback;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            return;
        }

        if (isoDepTransceiver != null) {
            isoDepTransceiver.interruptThread();
        }

        isoDepTransceiver = new IsoDepTransceiver(tag, isoDep, onMessageReceived, onMessageSendCallback);
    }
}
