package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import java.util.Arrays;

/**
 * Created by mn-io on 25.01.2016.
 */
public class NfcMessageSendHandler implements OnMessageSend {
    private byte[] byteBuffer = new byte[0];
    private int size;

    @Override
    public byte[] getNextMessage() {
        final byte[] data = getBytesFromBuffer(size);
        System.out.println("mario: sending " + Arrays.toString(data));
        System.out.println("mario: sending " + new String(data));
        //TODO: clear after sending
        return data;
    }

    @Override
    public void onDeactivated(int reason) {

    }

    @Override
    public void setMaxSize(int size) {
        this.size = size;
    }

    public void setData(byte[] data) {
        //TODO: check if data is not overwritten
        if (byteBuffer != null && byteBuffer.length > 0) {
            throw new IllegalStateException("Buffer not empty. Data loss on attempt to overwrite existing data");
        }
        this.byteBuffer = data;
    }

    byte[] getBytesFromBuffer(int maxLength) {
        if (byteBuffer == null || 0 == byteBuffer.length) {
            byteBuffer = null;
            return null;
        }

        int length = Math.min(byteBuffer.length, maxLength);
        final byte[] currentBuffer = Arrays.copyOfRange(byteBuffer, 0, length);

        byteBuffer = Arrays.copyOfRange(byteBuffer, length, byteBuffer.length);
        return currentBuffer;
    }
}
