package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;

import java.io.IOException;
import java.lang.ref.WeakReference;

import de.htw_berlin.sharkandroidstack.AndroidUtils;

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcStreamStub implements StreamStub {

    private final NfcAdapter nfcAdapter;
    private final WeakReference<Activity> activity;
    private final NfcReaderCallback nfcReaderCallback;
    private boolean isStarted = false;

    RequestHandler _handler;

    final RequestHandler _internHandler = new RequestHandler() {
        public void handleMessage(byte[] msg, MessageStub stub) {
            System.out.println("mario handleMessage");
            NfcStreamStub.this._handler.handleMessage(msg, stub);
        }

        public void handleStream(StreamConnection con) {
//            NfcStreamStub.this._connectionStr = "tcp://" + con.getReceiverAddressString() + ":"+PORT;
            System.out.println("mario handleStream");
            NfcStreamStub.this._handler.handleStream(con);

        }

        @Override
        public void handleNewConnectionStream(StreamConnection con) {
//            NfcStreamStub.this._connectionStr = "tcp://" + con.getReceiverAddressString() + ":"+PORT;
            System.out.println("mario handleNewConnectionStream");
            NfcStreamStub.this._handler.handleNewConnectionStream(con);
        }
    };

    final static OnMessageSend onMessageSend = new OnMessageSend() {
        @Override
        public byte[] getNextMessage() {
            final String s = AndroidUtils.generateRandomString(512);
            System.out.println("mario: sending " + s);
            return s.getBytes();
        }

        @Override
        public void onDeactivated(int reason) {

        }
    };

    final static OnMessageReceived onMessageReceived = new OnMessageReceived() {
        @Override
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
    };

    public NfcStreamStub(Context context, WeakReference<Activity> activity) throws SharkProtocolNotSupportedException {
        this.activity = activity;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (this.nfcAdapter == null) {
            throw new SharkProtocolNotSupportedException("NFC is not supported");
        }

        nfcReaderCallback = new NfcReaderCallback(onMessageReceived);
    }

    @Override
    public StreamConnection createStreamConnection(String addressString) throws IOException {
        return null;
    }

    @Override
    public String getLocalAddress() {
        return null;
    }

    @Override
    public void setHandler(RequestHandler handler) {
        this._handler = handler;
    }

    @Override
    public void stop() {
        NfcAdapterHelper.prepareReceiving(activity.get(), nfcReaderCallback);
        isStarted = false;
    }

    @Override
    public void start() throws IOException {
        //TODO: state for start... ?!
        NfcAdapterHelper.prepareSending(activity.get(), onMessageSend);
        isStarted = true;
    }

    @Override
    public boolean started() {
        return isStarted;
    }

}
