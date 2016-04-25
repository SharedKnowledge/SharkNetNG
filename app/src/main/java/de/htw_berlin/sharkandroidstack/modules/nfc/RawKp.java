package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by mn-io on 21.04.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RawKp extends KnowledgePort {

    public RawKp(SharkEngine se) {
        super(se);
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {

    }

    @Override
    protected void handleExpose(SharkCS sharkCS, KEPConnection kepConnection) {

    }

    public static String[] deserializeAsStrings(byte[] buffer) throws IOException, ClassNotFoundException {
        byte[][] object = deserializeAsBytes(buffer);
        String[] stringArray = new String[object.length];

        for (int i = 0; i < object.length; i++) {
            stringArray[i] = new String(object[i]);
        }

        return stringArray;
    }

    public static byte[][] deserializeAsBytes(byte[] buffer) throws IOException, ClassNotFoundException {
        byte[] decode = Base64.decode(buffer, Base64.NO_WRAP);
        ByteArrayInputStream in = new ByteArrayInputStream(decode);
        byte[][] object = (byte[][]) new ObjectInputStream(in).readObject();
        return object;
    }

    public static byte[] serialize(String[] stringArray) throws IOException {
        final byte[][] bytes = new byte[stringArray.length][];
        for (int i = 0; i < stringArray.length; i++) {
            bytes[i] = stringArray[i].getBytes(StandardCharsets.UTF_8);
        }

        byte[] asBytes = serialize(bytes);
        return asBytes;
    }

    public static byte[] serialize(byte[][] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(bytes);
        byte[] byteArray = out.toByteArray();

        byte[] asBytes = Base64.encode(byteArray, Base64.NO_WRAP);
        return asBytes;
    }
}
