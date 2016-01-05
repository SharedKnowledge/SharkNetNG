package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.annotation.TargetApi;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SmartCardEmulationService extends HostApduService {

    public static final int DEFAULT_MAX_LENGTH = 200;
    public static final byte[] WELCOME_MESSAGE = "Hello".getBytes();

    private static EditText input;
    byte[] byteBuffer;

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (selectAidApdu(apdu)) {
            return WELCOME_MESSAGE;
        }

        int maxLength = getMaxLength(apdu);
        return getNextMessage(maxLength);
    }

    private int getMaxLength(byte[] apdu) {
        final String payload = new String(apdu);
        if (payload.startsWith(IsoDepTransceiver.ISO_DEP_MAX_LENGTH)) {
            final String substring = payload.substring(IsoDepTransceiver.ISO_DEP_MAX_LENGTH.length(), payload.length());
            return new Integer(substring);
        }

        return DEFAULT_MAX_LENGTH;
    }

    @Override
    public void onDeactivated(int reason) {
    }

    byte[] getNextMessage(int maxLength) {
        if (null == byteBuffer) {
            byteBuffer = input.getText().toString().getBytes();
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

    public static void setInput(EditText input) {
        SmartCardEmulationService.input = input;
    }

    private boolean selectAidApdu(byte[] apdu) {
        //TODO: how does this work?
        return apdu.length >= 2 && apdu[0] == (byte) 0 && apdu[1] == (byte) 0xa4;
    }
}