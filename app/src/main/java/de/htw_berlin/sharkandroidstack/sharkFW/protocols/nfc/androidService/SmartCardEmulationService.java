package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.androidService;

import android.annotation.TargetApi;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class SmartCardEmulationService extends HostApduService {

    //TODO: set SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE to current fragment..
    //TODO: start/stop service on activity start
    //TODO: stream ...

    public static final byte[] INITIAL_TYPE_OF_SERVICE = "Hello".getBytes();

    private static OnMessageSend src;
    private static OnMessageReceived sink;


    @Override
    public void onDeactivated(int reason) {
        src.onDeactivated(reason);
        if (sink != null) {
            sink.tagLost();
        }
    }

    @Override
    public byte[] processCommandApdu(byte[] data, Bundle extras) {
        if (src == null) {
            return null;
        }

        if (selectAidApdu(data)) {
            return INITIAL_TYPE_OF_SERVICE;
        }

        if (sink != null && !new String(data).equals("nothing")) {
            sink.onMessage(data);
        }

        byte[] nextMessage = src.getNextMessage();
        if (nextMessage == null) {
            nextMessage = "nothing".getBytes();
        }

        return nextMessage;
    }


    public static void setSource(OnMessageSend src) {
        SmartCardEmulationService.src = src;
    }

    public static void setSink(OnMessageReceived sink) {
        SmartCardEmulationService.sink = sink;
    }

    private boolean selectAidApdu(byte[] apdu) {
        //TODO: how does this work?
        return apdu.length >= 2 && apdu[0] == (byte) 0 && apdu[1] == (byte) 0xa4;
    }
}