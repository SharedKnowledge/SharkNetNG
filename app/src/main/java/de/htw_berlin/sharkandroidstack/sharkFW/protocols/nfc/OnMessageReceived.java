package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

public interface OnMessageReceived {

    void onMessage(byte[] message);

    void onError(Exception exception);
}
