package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.nfc.Tag;

import net.sharkfw.protocols.RequestHandler;

import java.util.Arrays;

/**
 * Created by mn-io on 25.01.2016.
 */
public class NfcMessageReceivedHandler implements OnMessageReceived {
    private RequestHandler handler;
    private NfcMessageStub nfcMessageStub;
    private byte[] byteBuffer;

    public NfcMessageReceivedHandler(NfcMessageStub nfcMessageStub) {
        this.nfcMessageStub = nfcMessageStub;
    }

    public void onMessage(byte[] message) {
        System.out.println("mario receiving: " + Arrays.toString(message));
        System.out.println("mario receiving: " + new String(message));

        if (byteBuffer == null) {
            byteBuffer = message;
        } else {
            byteBuffer = concat(byteBuffer, message);
        }
    }

    public static byte[] concat(byte[] first, byte[] second) {
        final int newLength = first.length + second.length;
        byte[] result = Arrays.copyOf(first, newLength);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @Override
    public void onError(Exception exception) {
//        onMessage(exception.getMessage().getBytes());
    }

    @Override
    public void tagLost(Tag tag) {
        if (byteBuffer != null) {
            handler.handleMessage(byteBuffer, nfcMessageStub);
            byteBuffer = null;
        }
//        onMessage(tag.toString().getBytes());
    }

    @Override
    public void newTag(Tag tag) {
//        onMessage(tag.toString().getBytes());
    }


    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }


}
