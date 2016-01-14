package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.nfc.Tag;

public interface OnMessageReceived {

    void onMessage(byte[] message);

    void onError(Exception exception);

    void tagLost(Tag tag);

    void newTag(Tag tag);
}
