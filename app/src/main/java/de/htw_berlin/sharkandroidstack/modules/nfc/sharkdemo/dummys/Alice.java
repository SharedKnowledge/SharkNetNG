package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharksystem.android.peer.AndroidSharkEngine;

import java.util.Iterator;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by mn-io on 29.01.2016.
 */
public class Alice extends Actor {

    private EditText userInput;
    private PeerSemanticTag remotePeer;

    private ASIPSpace asipSpace;
    private InMemoKnowledge knowledge;

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
        STSet topics = InMemoSharkKB.createInMemoSTSet();
        topics.merge(topic);

        PeerSTSet peers = InMemoSharkKB.createInMemoPeerSTSet();
        peers.merge(peer);

        asipSpace = kb.createASIPSpace(topics, null, peers, peer, null, null, null, ASIPSpace.DIRECTION_OUT);
        knowledge = new InMemoKnowledge(kb.getVocabulary());

        super.initKp();
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
            showToast(String.format("Added Information to Alice.\nConnect other device with Bob now.", msg));
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void sendInformation() {
        try {
            Iterator<ASIPInformation> information = knowledge.getInformation(asipSpace);
            boolean hasOne = information.hasNext();
            if (!hasOne) {
                showToast("Cannot sent empty information. Please add something first.");
                return;
            }

            engine.startNfc();
            engine.sendASIPKnowledge(knowledge, remotePeer, kp);
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable t) {
        t.printStackTrace();
        LogManager.addThrowable(NfcMainActivity.LOG_ID, t);
        String msg = String.format("Error occurred: %s\nCheck Log for details.", t.getMessage());
        showToast(msg);
    }
}
