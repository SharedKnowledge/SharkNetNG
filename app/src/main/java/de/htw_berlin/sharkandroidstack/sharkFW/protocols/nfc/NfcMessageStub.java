package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.Build;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcMessageStub implements MessageStub {

    public static final String SMART_CARD_IDENTIFIER = "SHARK NFC";

    private final NfcAdapter nfcAdapter;
    private final WeakReference<Activity> activity;
    private final NfcMessageReceivedHandler receivedRequestHandler;
    private final NfcMessageSendHandler sendRequestHandler;
    private boolean isStarted = false;

    public NfcMessageStub(Context context, WeakReference<Activity> activity) throws SharkProtocolNotSupportedException {
        this.activity = activity;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (this.nfcAdapter == null) {
            throw new SharkProtocolNotSupportedException("NFC is not supported");
        }

        receivedRequestHandler = new NfcMessageReceivedHandler(this);
        sendRequestHandler = new NfcMessageSendHandler();
    }

    @Override
    public void setHandler(RequestHandler handler) {
        receivedRequestHandler.setHandler(handler);
    }

    @Override
    public void stop() {
        NfcAdapterHelper.prepareSending(SMART_CARD_IDENTIFIER, activity.get(), sendRequestHandler, receivedRequestHandler);
        isStarted = false;
    }

    @Override
    public void start() {
        NfcAdapterHelper.prepareReceiving(SMART_CARD_IDENTIFIER, activity.get(), sendRequestHandler, receivedRequestHandler);
        isStarted = true;
    }

    @Override
    public boolean started() {
        return isStarted;
    }

    @Override
    public void setReplyAddressString(String addr) {

    }

    //TODO: ignore address..
    @Override
    public void sendMessage(byte[] msg, String recAddress) throws IOException {
        sendRequestHandler.setData(msg);
    }

    @Override
    public String getReplyAddressString() {
        return null;
    }
}
