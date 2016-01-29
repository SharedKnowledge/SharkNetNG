package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.KnowledgeBaseListenerAdapterImpl;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.KnowledgePortAdapterListenerImpl;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.MyKbListAdapter;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;
import de.htw_berlin.sharkandroidstack.system_modules.settings.KnowledgeBaseManager;

/**
 * Created by mn-io on 29.01.2016.
 */
public class Actor {

    String name;
    View root;
    NfcSharkDemoFragment fragment;
    ListView kbList;

    AndroidSharkEngine engine;
    SharkKB kb;
    ContextCoordinates cc;

    PeerSemanticTag peer;
    SemanticTag topic;
    StandardKP kp;

    KnowledgeBaseListenerAdapterImpl knowledgeBaseListener;
    KnowledgePortAdapterListenerImpl knowledgePortListener;
    MyKbListAdapter kbListAdapter;

    final View.OnClickListener printKBClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String data = L.kb2String(kb);
            L.l(data);
            kbListAdapter.add(new MyKbListAdapter.MyDataHolder("KB in use", data));
            kbListAdapter.notifyDataSetChanged();
        }
    };

    final View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            kbListAdapter.clear();
            kbListAdapter.notifyDataSetChanged();
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
        final ImageButton printKB = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_print_kb);
        printKB.setOnClickListener(printKBClickListener);
        printKB.setOnLongClickListener(infoLongClickListener);

        final ImageButton clearListButton = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_clear);
        clearListButton.setOnClickListener(clearClickListener);
        clearListButton.setOnLongClickListener(infoLongClickListener);

        kbList = (ListView) root.findViewById(R.id.activity_nfc_sharkdemo_kb_list);

        kbListAdapter = new MyKbListAdapter(fragment.getActivity());
        knowledgeBaseListener = new KnowledgeBaseListenerAdapterImpl(kbListAdapter, NfcMainActivity.LOG_ID);
        knowledgePortListener = new KnowledgePortAdapterListenerImpl(kbListAdapter, updater);
        kbList.setAdapter(kbListAdapter);
    }

    public void initShark(AndroidSharkEngine engine) throws SharkKBException, SharkProtocolNotSupportedException {
        this.engine = engine;

        kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeSimple, false);
        kb.addListener(knowledgeBaseListener);

        //TODO: Address is absolutely not needed here, but framework requires something
        peer = kb.createPeerSemanticTag(name, name + "Id", "tcp://localhost:124");
        kb.setOwner(peer);

        topic = kb.createSemanticTag("Shark", "http://www.sharksystem.net/");
    }

    public void initKp(ContextCoordinates cc) {
        this.cc = cc;

        kp = new StandardKP(engine, cc, kb);
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
        }
    }
}
