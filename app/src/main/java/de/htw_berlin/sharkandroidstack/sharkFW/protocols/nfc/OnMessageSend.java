package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

public interface OnMessageSend {

    byte[] getNextMessage();

    void onDeactivated(int reason);
}
