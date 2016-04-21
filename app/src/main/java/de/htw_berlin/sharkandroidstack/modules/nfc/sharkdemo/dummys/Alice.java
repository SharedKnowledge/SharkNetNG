package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharksystem.android.peer.AndroidSharkEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;

/**
 * Created by mn-io on 29.01.2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class Alice extends Actor {

    public static final String TOAST_CONNECT_NOW = "Added Information to Alice.\nConnect other device with Bob now.";
    public static final String TOAST_NOTHING_TO_SEND = "Cannot sent empty information. Please add something first.";

    private EditText userInput;
    private PeerSemanticTag remotePeer;

    final View.OnClickListener sendClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            sendInformation();
        }
    };

    final View.OnClickListener userInputAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String inputText = userInput.getText().toString().trim();
            if (inputText.length() == 0) {
                return;
            }

            AndroidUtils.clearUserInput(fragment.getActivity(), userInput);
            addInformation(inputText);
        }
    };

    public Alice(String name, NfcSharkDemoFragment fragment) {
        super(name, fragment);
    }

    public void initView(View root, Runnable updater) {
        super.initView(root, updater);

        userInput = (EditText) root.findViewById(R.id.activity_nfc_sharkdemo_input);
        ImageButton sendButton = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_input_send);
        sendButton.setOnClickListener(sendClickListener);
        sendButton.setOnLongClickListener(infoLongClickListener);

        final ImageButton userInputAdd = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_input_add);
        userInputAdd.setOnClickListener(userInputAddClickListener);
        userInputAdd.setOnLongClickListener(infoLongClickListener);
    }

    public void initShark(AndroidSharkEngine engine) throws SharkKBException, SharkProtocolNotSupportedException {
        super.initShark(engine);
        initKnowledge();

//        super.initKp();
    }

    public void setRemotePeer(PeerSemanticTag remotePeer) {
        if (remotePeer == null) {
            throw new IllegalArgumentException("Remote Peer cannot be null.");
        }
        this.remotePeer = remotePeer;
    }

    private void addInformation(String msg) {
        try {
            ArrayAdapter<String> adapter = (ArrayAdapter) msgList.getAdapter();
            adapter.add(msg);
            adapter.notifyDataSetChanged();

            if (adapter.getCount() == 1) {
                showToast(String.format(TOAST_CONNECT_NOW, msg));
            }
        } catch (Exception e) {
            NfcMainActivity.handleError(fragment.getActivity(), e);
        }
    }

    private void sendInformation() {
        try {
            ArrayAdapter<String> adapter = (ArrayAdapter) msgList.getAdapter();
            if (adapter.getCount() == 0) {
                showToast(TOAST_NOTHING_TO_SEND);
                return;
            }

            byte[] bytes = serialize(adapter);

            try {
                String[] deserialize = TestKP.deserialize(bytes);
                System.out.println(Arrays.toString(deserialize));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            engine.startNfc();
            InputStream is = new ByteArrayInputStream(bytes);
            engine.sendRaw(is, remotePeer, kp);
        } catch (Exception e) {
            NfcMainActivity.handleError(fragment.getActivity(), e);
        }
    }

    public static byte[] serialize(ArrayAdapter<String> adapter) throws IOException {
        int count = adapter.getCount();
        byte[][] bytes = new byte[count][];

        for (int i = 0; i < count; i++) {
            String item = adapter.getItem(i);
            bytes[i] = item.getBytes(StandardCharsets.UTF_8);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(bytes);
        byte[] byteArray = out.toByteArray();

        return Base64.encode(byteArray, Base64.NO_WRAP);
    }

    private void showToast(String msg) {
        Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
