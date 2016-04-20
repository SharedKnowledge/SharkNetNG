package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Assert;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoASIPKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharksystem.android.peer.AndroidSharkEngine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;

/**
 * Created by mn-io on 29.01.2016.
 */
public class Alice extends Actor {

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(fragment.getActivity(), R.layout.module_nfc_sharkdemo_list_entry, android.R.id.text1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final String item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(item);
                return view;
            }
        };
        msgList.setAdapter(adapter);
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
            knowledge.addInformation(msg, asipSpace);
            ArrayAdapter<String> adapter = (ArrayAdapter) msgList.getAdapter();
            adapter.add(msg);
            adapter.notifyDataSetChanged();

            showToast(String.format("Added Information to Alice.\nConnect other device with Bob now.", msg));
        } catch (Exception e) {
            NfcMainActivity.handleError(fragment.getActivity(), e);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendInformation() {
        try {
//            Iterator<ASIPInformationSpace> information = knowledge.informationSpaces();
//            boolean hasOne = information.hasNext();
//            if (!hasOne) {
//                showToast("Cannot sent empty information. Please add something first.");
//                return;
//            }

            engine.startNfc();
//            engine.sendASIPKnowledge(knowledge, remotePeer, null);
            InputStream is = new ByteArrayInputStream("Hello".getBytes(StandardCharsets.UTF_8));
            engine.sendRaw(is, remotePeer, kp);
        } catch (Exception e) {
            NfcMainActivity.handleError(fragment.getActivity(), e);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
