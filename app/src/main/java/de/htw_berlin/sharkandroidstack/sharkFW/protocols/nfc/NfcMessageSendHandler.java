package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import java.util.Arrays;

/**
 * Created by mn-io on 25.01.2016.
 */
public class NfcMessageSendHandler implements OnMessageSend {
    private byte[] data = new byte[0];

    @Override
    public byte[] getNextMessage() {
        System.out.println("mario: sending " + Arrays.toString(data));
        System.out.println("mario: sending " + new String(data));
        //TODO: clear after sending
        return data;
    }

    @Override
    public void onDeactivated(int reason) {

    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
