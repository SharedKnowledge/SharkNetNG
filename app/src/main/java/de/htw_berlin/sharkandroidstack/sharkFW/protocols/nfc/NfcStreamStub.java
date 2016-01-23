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

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcStreamStub implements StreamStub {

    public final int NFC_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;

    private final NfcAdapter nfcAdapter;
    private final WeakReference<Activity> activity;
    private final NfcReaderCallback nfcReaderCallback;
    private boolean isStarted = false;

    RequestHandler _handler;

    final RequestHandler _internHandler = new RequestHandler() {
        public void handleMessage(byte[] msg, MessageStub stub) {
            NfcStreamStub.this._handler.handleMessage(msg, stub);
        }

        public void handleStream(StreamConnection con) {
//            NfcStreamStub.this._connectionStr = "tcp://" + con.getReceiverAddressString() + ":"+PORT;
            NfcStreamStub.this._handler.handleStream(con);
        }

        @Override
        public void handleNewConnectionStream(StreamConnection con) {
//            NfcStreamStub.this._connectionStr = "tcp://" + con.getReceiverAddressString() + ":"+PORT;
            NfcStreamStub.this._handler.handleNewConnectionStream(con);
        }
    };

    public NfcStreamStub(Context context, WeakReference<Activity> activity) throws SharkProtocolNotSupportedException {
        this.activity = activity;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (this.nfcAdapter == null) {
            throw new SharkProtocolNotSupportedException("NFC is not supported");
        }

        nfcReaderCallback = new NfcReaderCallback(new OnMessageReceived() {
            @Override
            public void onMessage(byte[] message) {

            }

            @Override
            public void onError(Exception exception) {

            }

            @Override
            public void tagLost(Tag tag) {

            }

            @Override
            public void newTag(Tag tag) {

            }
        });
        stop(); // stop means: reading actively and notify callback when NFC detected
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
        nfcAdapter.enableReaderMode(activity.get(), nfcReaderCallback, NFC_FLAGS, null);
        isStarted = false;
    }

    @Override
    public void start() throws IOException {
        nfcAdapter.disableReaderMode(activity.get());
        isStarted = true;
    }

    @Override
    public boolean started() {
        return isStarted;
    }

}
