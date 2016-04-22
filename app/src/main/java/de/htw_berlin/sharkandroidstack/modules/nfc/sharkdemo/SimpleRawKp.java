package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.annotation.TargetApi;
import android.os.Build;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by mn-io on 21.04.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class SimpleRawKp extends KnowledgePort {

    private final Runnable updater;
    private String[] receivedData;
    private Handler handler = new Handler();

    public SimpleRawKp(SharkEngine se, Runnable updater) {
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
            is2.read(buffer);
            receivedData = deserialize(buffer);
        } catch (Exception e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }

        handler.post(updater);

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

    public static byte[] serialize(String[] stringArray) throws IOException {
        final byte[][] bytes = new byte[stringArray.length][];
        for (int i = 0; i < stringArray.length; i++) {
            bytes[i] = stringArray[i].getBytes(StandardCharsets.UTF_8);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(bytes);
        byte[] byteArray = out.toByteArray();

        return Base64.encode(byteArray, Base64.NO_WRAP);
    }

    public String[] getReceivedData() {
        return receivedData;
    }
}
