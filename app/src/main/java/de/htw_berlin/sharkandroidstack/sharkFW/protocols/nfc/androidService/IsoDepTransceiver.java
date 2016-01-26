package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.androidService;

import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;

import java.io.IOException;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

/**
 * Created by mn-io on 22.01.16.
 */
public class IsoDepTransceiver implements Runnable {

    public static final byte[] CLA_INS_P1_P2 = {0x00, (byte) 0xA4, 0x04, 0x00};
    public static final byte[] AID_ANDROID = {(byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

    private final Thread thread;

    private IsoDep isoDep;
    private OnMessageReceived onMessageReceived;
    private OnMessageSend onMessageSendCallback;

    public IsoDepTransceiver(Tag tag, IsoDep isoDep, OnMessageReceived onMessageReceived, OnMessageSend onMessageSendCall) {
        this.isoDep = isoDep;
        this.onMessageReceived = onMessageReceived;
        if (onMessageSendCall != null) {
            this.onMessageSendCallback = onMessageSendCall;
            onMessageSendCall.setMaxSize(isoDep.getMaxTransceiveLength());
        }
        onMessageReceived.newTag(tag);

        thread = new Thread(this);
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
                byte[] nextMessage = onMessageSendCallback != null ? onMessageSendCallback.getNextMessage() : "nothing".getBytes();
                if (nextMessage == null) {
                    nextMessage = "nothing".getBytes();
                }
                System.out.println("mario: working as IDT");
                System.out.println("mario: sending1: " + Arrays.toString(nextMessage) + " / " + new String(nextMessage));

                response = isoDep.transceive(nextMessage); // TODO: tag lost if null response = therefore always send data to allow bidirectional...
                System.out.println("mario: received2: " + Arrays.toString(response) + " / " + new String(response));
                if (!new String(response).equals("nothing")) {
                    onMessageReceived.onMessage(response);
                }
            }

            isoDep.close();
        } catch (TagLostException ignore) {
            System.out.println("mario: tag lost");
            onMessageReceived.tagLost();
        } catch (IOException e) {
            System.out.println("mario: error " + e.getMessage());
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

    public void interruptThread() {
        if (!thread.isInterrupted()) {
            thread.interrupt();
        }
    }
}
