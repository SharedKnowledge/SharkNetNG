package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.content.Context;
import android.nfc.NfcAdapter;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;

import java.io.IOException;

public class NfcStreamStub implements StreamStub {

    RequestHandler _handler;

    //TODO: implement receiver here

    public NfcStreamStub(Context context) throws SharkProtocolNotSupportedException {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null) {
            throw new SharkProtocolNotSupportedException("NFC is not supported");
        }
    }

    private RequestHandler _internHandler = new RequestHandler() {
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

    }

    @Override
    public void start() throws IOException {
    }

    @Override
    public boolean started() {
        return false;
    }
}
