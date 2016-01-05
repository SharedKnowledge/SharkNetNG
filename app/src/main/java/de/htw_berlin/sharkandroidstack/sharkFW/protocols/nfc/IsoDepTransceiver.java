package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;

import java.io.IOException;
import java.util.Arrays;

public class IsoDepTransceiver implements Runnable {

    public static final String ISO_DEP_MAX_LENGTH = "Iso-Dep-Max-Length: ";

    private IsoDep isoDep;
    private OnMessageReceived onMessageReceived;

    public IsoDepTransceiver(IsoDep isoDep, OnMessageReceived onMessageReceived) {
        this.isoDep = isoDep;
        this.onMessageReceived = onMessageReceived;
    }

    private static final byte[] CLA_INS_P1_P2 = {0x00, (byte) 0xA4, 0x04, 0x00};
    private static final byte[] AID_ANDROID = {(byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

    private byte[] createSelectAidApdu(byte[] aid) {
        byte[] result = new byte[6 + aid.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte) aid.length;
        System.arraycopy(aid, 0, result, 5, aid.length);
        result[result.length - 1] = 0;
        return result;
    }

    @Override
    public void run() {
        try {
            isoDep.connect();
            final byte[] selectAidApdu = createSelectAidApdu(AID_ANDROID);
            byte[] response = isoDep.transceive(selectAidApdu);
            if (!Arrays.equals(response, SmartCardEmulationService.WELCOME_MESSAGE)) {
                return;
            }

            while (isoDep.isConnected() && !Thread.interrupted()) {
                final int maxTransceiveLength = isoDep.getMaxTransceiveLength();
                final byte[] bytes = (ISO_DEP_MAX_LENGTH + maxTransceiveLength).getBytes();
                response = isoDep.transceive(bytes);
                onMessageReceived.onMessage(response);
            }

            isoDep.close();
        } catch (TagLostException ignore) {
        } catch (IOException e) {
            onMessageReceived.onError(e);
        }
    }
}
