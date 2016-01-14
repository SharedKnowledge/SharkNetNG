package de.htw_berlin.sharkandroidstack.modules.nfc;

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
    private MyResultAdapter adapter;

    public MyReaderCallback(OnMessageReceived onMessageReceived, MyResultAdapter adapter) {
        this.onMessageReceived = onMessageReceived;
        this.adapter = adapter;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            return;
        }

        adapter.addTagChanged(tag.toString());

        IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, onMessageReceived);
        Thread thread = new Thread(transceiver);
        thread.start();
    }
};