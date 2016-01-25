package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.androidService;

import android.annotation.TargetApi;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class SmartCardEmulationService extends HostApduService {

    //TODO: set SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE to current fragment..
    //TODO: start/stop service on activity start

    public static int DEFAULT_MAX_LENGTH = 200;
    public static final byte[] INITIAL_TYPE_OF_SERVICE = "Hello".getBytes();

    private static OnMessageSend src;
    byte[] byteBuffer;

    @Override
    public void onDeactivated(int reason) {
        src.onDeactivated(reason);
    }

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (src == null) {
            return null;
        }

        if (selectAidApdu(apdu)) {
            return INITIAL_TYPE_OF_SERVICE;
        }

        int maxLength = getMaxLength(apdu);
        return getNextMessage(maxLength);
    }

    private int getMaxLength(byte[] apdu) {
        final String payload = new String(apdu);
        if (payload.startsWith(IsoDepTransceiver.ISO_DEP_MAX_LENGTH)) {
            final String substring = payload.substring(IsoDepTransceiver.ISO_DEP_MAX_LENGTH.length(), payload.length());
            return Integer.valueOf(substring);
        }

        return DEFAULT_MAX_LENGTH;
    }

    byte[] getNextMessage(int maxLength) {
        if (null == byteBuffer) {
            byteBuffer = src.getNextMessage();
        }

        return getBytesFromBuffer(maxLength);
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

    public static void setSource(OnMessageSend src) {
        SmartCardEmulationService.src = src;
    }

    private boolean selectAidApdu(byte[] apdu) {
        //TODO: how does this work?
        return apdu.length >= 2 && apdu[0] == (byte) 0 && apdu[1] == (byte) 0xa4;
    }
}