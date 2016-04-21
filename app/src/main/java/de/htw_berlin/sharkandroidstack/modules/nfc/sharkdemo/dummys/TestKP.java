package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.os.Handler;
import android.util.Base64;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by msc on 21.03.16.
 */
public class TestKP extends KnowledgePort {

    private final Runnable updater;
    private String[] receivedData;

    public TestKP(SharkEngine se, Runnable updater) {
        super(se);
        this.updater = updater;
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {
    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {
    }

    @Override
    protected void handleExpose(ASIPInterest interest, ASIPConnection asipConnection) throws SharkKBException {
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
    }

    @Override
    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
        ASIPInMessage inMessage = (ASIPInMessage) asipConnection;
        InputStream is2 = inMessage.getRaw();
        try {
            byte[] buffer = new byte[is2.available()];
            int result = is2.read(buffer);
            receivedData = deserialize(buffer);
//            String rawContent = new String(buffer);
//            System.out.println("mario: raw " + rawContent + " | " + result);
//            System.out.println("mario: back " + Arrays.toString(deserialize));
        } catch (Exception e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }

        new Handler().post(updater);

        super.handleRaw(is, asipConnection);
    }

    public static String[] deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
        byte[] decode = Base64.decode(buffer, Base64.NO_WRAP);
        ByteArrayInputStream in = new ByteArrayInputStream(decode);
        byte[][] object = (byte[][]) new ObjectInputStream(in).readObject();

        String[] stringArray = new String[object.length];

        for (int i = 0; i < object.length; i++) {
            stringArray[i] = new String(object[i]);
        }

        return stringArray;
    }

    public String[] getReceivedData() {
        return receivedData;
    }
}
