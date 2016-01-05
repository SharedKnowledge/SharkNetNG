package de.htw_berlin.sharkandroidstack.sharkFW.protocols.bluetooth;

import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;

import java.io.IOException;

public class BluetoothStreamStub implements StreamStub {
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
