package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;

import java.io.IOException;
import java.util.Arrays;

public class IsoDepTransceiver implements Runnable {

    public static final String ISO_DEP_MAX_LENGTH = "Iso-Dep-Max-Length: ";
    public static final byte[] CLA_INS_P1_P2 = {0x00, (byte) 0xA4, 0x04, 0x00};
    public static final byte[] AID_ANDROID = {(byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

    private final Thread thread;

    private Tag tag;
    private IsoDep isoDep;
    private OnMessageReceived onMessageReceived;

    public IsoDepTransceiver(Tag tag, IsoDep isoDep, OnMessageReceived onMessageReceived) {
        this.tag = tag;
        this.isoDep = isoDep;
        this.onMessageReceived = onMessageReceived;

        onMessageReceived.newTag(tag);

        //TODO: stop thread before new one? investigate death
        thread = new Thread(this);
        thread.setName(IsoDepTransceiver.class.getSimpleName() + "-Thread");
        thread.start();
    }

    @Override
    public void run() {
        try {
            isoDep.connect();
            final byte[] selectAidApdu = createSelectAidApdu(AID_ANDROID);
            byte[] response = isoDep.transceive(selectAidApdu);
            if (!Arrays.equals(response, SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE)) {
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
            onMessageReceived.tagLost(tag);
        } catch (IOException e) {
            onMessageReceived.onError(e);
        }
    }

    private byte[] createSelectAidApdu(byte[] aid) {
        byte[] result = new byte[6 + aid.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte) aid.length;
        System.arraycopy(aid, 0, result, 5, aid.length);
        result[result.length - 1] = 0;
        return result;
    }

    public void stop() {
//        if (!thread.isInterrupted()) {
//            thread.interrupt();
//        }
    }
}
