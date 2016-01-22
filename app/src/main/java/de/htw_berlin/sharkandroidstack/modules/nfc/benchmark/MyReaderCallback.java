package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.IsoDepTransceiver;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyReaderCallback implements NfcAdapter.ReaderCallback {
    private OnMessageReceived onMessageReceived;
    private IsoDepTransceiver isoDepTransceiver;

    public MyReaderCallback(OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
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

        isoDepTransceiver = new IsoDepTransceiver(tag, isoDep, onMessageReceived);
    }
};