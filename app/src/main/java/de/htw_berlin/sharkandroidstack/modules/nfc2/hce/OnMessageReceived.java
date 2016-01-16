package de.htw_berlin.sharkandroidstack.modules.nfc2.hce;

public interface OnMessageReceived {

    void onMessage(byte[] message);

    void onError(Exception exception);
}
