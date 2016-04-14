package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharksystem.android.peer.AndroidSharkEngine;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.KnowledgePortAdapterListenerImpl;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;

/**
 * Created by mn-io on 29.01.2016.
 */
public class Actor {

    String name;
    View root;
    NfcSharkDemoFragment fragment;
    ListView msgList;

    AndroidSharkEngine engine;

    PeerSemanticTag peer;
    TestKP kp;

    ASIPSpace asipSpace;
    InMemoKnowledge knowledge;

    KnowledgePortAdapterListenerImpl knowledgePortListener;

    final View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayAdapter<String> adapter = (ArrayAdapter) msgList.getAdapter();
            adapter.clear();
            try {
                initKnowledge();
            } catch (SharkKBException e) {
                NfcMainActivity.handleError(v.getContext(), e);
            }
            adapter.notifyDataSetChanged();
        }
    };

    final public static View.OnLongClickListener infoLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int toastMsg = 0;
            switch (v.getId()) {
                case R.id.activity_nfc_sharkdemo_clear:
                    toastMsg = R.string.activity_nfc_sharkdemo_hint_clear;
                    break;
                case R.id.activity_nfc_sharkdemo_print_kb:
                    toastMsg = R.string.activity_nfc_sharkdemo_hint_print_kb;
                    break;
                case R.id.activity_nfc_sharkdemo_input_add:
                    toastMsg = R.string.activity_nfc_sharkdemo_hint_add;
                    break;
                case R.id.activity_nfc_sharkdemo_input_send:
                    toastMsg = R.string.activity_nfc_sharkdemo_hint_send;
                    break;
            }

            if (toastMsg != 0) {
                Toast.makeText(v.getContext(), toastMsg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    public Actor(String name, NfcSharkDemoFragment fragment) {
        this.name = name;
        this.fragment = fragment;
    }

    public void initView(View root, Runnable updater) {
        this.root = root;
        final ImageButton clearListButton = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_clear);
        clearListButton.setOnClickListener(clearClickListener);
        clearListButton.setOnLongClickListener(infoLongClickListener);

        msgList = (ListView) root.findViewById(R.id.activity_nfc_sharkdemo_msg_list);

//        kbListAdapter = new MyKbListAdapter(fragment.getActivity());
//        knowledgePortListener = new KnowledgePortAdapterListenerImpl(updater);
//        msgList.setAdapter(kbListAdapter);
    }

    public void initShark(AndroidSharkEngine engine) throws SharkKBException, SharkProtocolNotSupportedException {
        this.engine = engine;
        peer = InMemoSharkKB.createInMemoPeerSemanticTag(name, name + "Id", "tcp://");
    }

    public void initKnowledge() throws SharkKBException {
        STSet topics = InMemoSharkKB.createInMemoSTSet();
        SemanticTag topic = InMemoSharkKB.createInMemoSemanticTag("Shark", "http://www.sharksystem.net/");
        topics.merge(topic);

        PeerSTSet peers = InMemoSharkKB.createInMemoPeerSTSet();
        peers.merge(peer);
        InMemoSharkKB kb = new InMemoSharkKB();
        asipSpace = kb.createASIPSpace(topics, null, peers, peer, null, null, null, ASIPSpace.DIRECTION_OUT);
        knowledge = new InMemoKnowledge(kb.getVocabulary());
    }

    public void initKp() {
        kp = new TestKP(engine);
        kp.addListener(knowledgePortListener);
    }

    public void createTab(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText(name));
    }

    public void show() {
        root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        root.setVisibility(View.GONE);
        try {
            engine.stopNfc();
        } catch (SharkProtocolNotSupportedException e) {
            NfcMainActivity.handleError(fragment.getActivity(), e);
        }
    }
}
