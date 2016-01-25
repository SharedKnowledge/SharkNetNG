package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.nfc.Tag;

import net.sharkfw.protocols.RequestHandler;

/**
 * Created by mn-io on 25.01.2016.
 */
public class NfcMessageReceivedHandler implements OnMessageReceived {
    private RequestHandler handler;

    public void onMessage(byte[] message) {
        System.out.println("mario receiving: " + new String(message));
    }

    @Override
    public void onError(Exception exception) {
        onMessage(exception.getMessage().getBytes());
    }

    @Override
    public void tagLost(Tag tag) {
        onMessage(tag.toString().getBytes());
    }

    @Override
    public void newTag(Tag tag) {
        onMessage(tag.toString().getBytes());
    }


    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }


}
