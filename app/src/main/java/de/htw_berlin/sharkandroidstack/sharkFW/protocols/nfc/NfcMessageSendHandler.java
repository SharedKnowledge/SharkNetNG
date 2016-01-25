package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import net.sharkfw.protocols.RequestHandler;

import de.htw_berlin.sharkandroidstack.AndroidUtils;

/**
 * Created by mn-io on 25.01.2016.
 */
public class NfcMessageSendHandler implements OnMessageSend {
    private RequestHandler handler;

    @Override
    public byte[] getNextMessage() {
        final String s = AndroidUtils.generateRandomString(512);
        System.out.println("mario: sending " + s);
        return s.getBytes();
    }

    @Override
    public void onDeactivated(int reason) {

    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
}
