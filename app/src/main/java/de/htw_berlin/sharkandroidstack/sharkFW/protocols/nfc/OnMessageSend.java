package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

/**
 * Created by mn-io on 22.01.16.
 */
public interface OnMessageSend {

    byte[] getNextMessage();

    void onDeactivated(int reason);
}
