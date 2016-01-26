package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.KnowledgeBaseManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.SettingsManager;

/**
 * Created by mn-io on 22.01.16.
 */
public class NfcSharkDemoFragment extends Fragment {
    public static final String SEMANTIC_TAG_NAME = "nfcDemo";
    public static final String SEMANTIC_TAG_SI = "nfcDemoSI";
    public static final String INFORMATION_NAME = "User Input";

    SharkKB kb;
    KnowledgeBaseListenerAdapterImpl knowledgeBaseListener;
    KnowledgePortAdapterListenerImpl knowledgePortListener;
    MyKbListAdapter kbListAdapter;
//    SemanticTag tag;

    AndroidSharkEngine engine;

    EditText userInput;
    ListView kbList;

    final View.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            //TODO: dummy implementation - 1st click: engine is stopped and sending, 2nd click engine started listening
            if (engine == null) {
                try {
                    button.setText("NFC stopped / sending");
                    engine.stopNfc();


                } catch (SharkProtocolNotSupportedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    button.setText("NFC started / listening");

                    engine.startNfc();


                } catch (SharkProtocolNotSupportedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    final View.OnClickListener userInputAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            final String inputText = userInput.getText().toString().trim();
//            if (inputText.length() == 0) {
//                return;
//            }
//            clearUserInput();
//
//            try {
//                final ContextCoordinates contextCoordinates = kb.createContextCoordinates(tag, kb.getOwner(), null, null, null, null, SharkCS.DIRECTION_INOUT);
//                final ContextPoint contextPoint = kb.createContextPoint(contextCoordinates);
//                contextPoint.addInformation(inputText).setName(INFORMATION_NAME);
//                //TODO: Bug?! - cpChanged not called?!
//                //TODO: would like to print information... L.cps2String
//
//                Toast.makeText(v.getContext(), String.format("Added: %s", inputText), Toast.LENGTH_SHORT).show();
//            } catch (SharkKBException e) {
//                LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
//                Toast.makeText(v.getContext(), String.format("Error occurred: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
        }
    };

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
            clearUserInput();
            kbListAdapter.clear();
            kbListAdapter.notifyDataSetChanged();
        }
    };

    final View.OnLongClickListener infoLongClickListener = new View.OnLongClickListener() {
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

            }

            if (toastMsg != 0) {
                Toast.makeText(v.getContext(), toastMsg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

        final TextView ownerInformation = (TextView) root.findViewById(R.id.activity_nfc_sharkdemo_owner_id);
        ownerInformation.setText(String.format(root.getContext().getString(R.string.activity_nfc_sharkdemo_info), AndroidUtils.deviceId));

        root.findViewById(R.id.activity_nfc_sharkdemo_start).setOnClickListener(startClickListener);

        final ImageButton userInputAdd = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_input_add);
        userInputAdd.setOnClickListener(userInputAddClickListener);
        userInputAdd.setOnLongClickListener(infoLongClickListener);

        final ImageButton printKB = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_print_kb);
        printKB.setOnClickListener(printKBClickListener);
        printKB.setOnLongClickListener(infoLongClickListener);

        final ImageButton clearListButton = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_clear);
        clearListButton.setOnClickListener(clearClickListener);
        clearListButton.setOnLongClickListener(infoLongClickListener);

        userInput = (EditText) root.findViewById(R.id.activity_nfc_sharkdemo_input);
        kbList = (ListView) root.findViewById(R.id.activity_nfc_sharkdemo_kb_list);
        initShark(kbList);

        return root;
    }

    private void initShark(ListView kbList) {
        kbListAdapter = new MyKbListAdapter(this.getActivity());
        knowledgeBaseListener = new KnowledgeBaseListenerAdapterImpl(kbListAdapter, NfcMainActivity.LOG_ID);
        knowledgePortListener = new KnowledgePortAdapterListenerImpl(kbListAdapter);

        kbList.setAdapter(kbListAdapter);

        try {
            // uses default owner from settings!
            kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeSimple, false);

            kb.addListener(knowledgeBaseListener);

            engine = new AndroidSharkEngine(getActivity());

            String aliceOrBob = SettingsManager.getValue(SettingsManager.KEY_KB_OWNER_PREFERENCES);
            String other = "alice".equals(aliceOrBob) ? "bob" : "alice";
            Toast.makeText(NfcSharkDemoFragment.this.getActivity(), "This is: " + aliceOrBob + ", the other is " + other, Toast.LENGTH_SHORT).show();

            SemanticTag shark = kb.createSemanticTag("Shark", "http://www.sharksystem.net/");

            StandardKP kp;
            engine.stopNfc();
            if (aliceOrBob.equals("alice")) {
                //Alice
                String aliceName = aliceOrBob;
                System.out.println("mario1: I am alice " + aliceName);

                final PeerSemanticTag alice = kb.getOwner();
                ContextCoordinates cc = kb.createContextCoordinates(shark, alice, alice, null, null, null, SharkCS.DIRECTION_OUT);
                ContextPoint cp = kb.createContextPoint(cc);
                cp.addInformation("I like Shark");
                kb.addInterest(cc);
                kp = new StandardKP(engine, cc, kb);
                kp.addListener(knowledgePortListener);
                PeerSemanticTag bob = kb.createPeerSemanticTag(other, other + "Id", "tcp://localhost:1000");
                final InMemoKnowledge k = new InMemoKnowledge();
                k.addContextPoint(cp);

                engine.startNfc();

                engine.sendKnowledge(k, bob, kp);
            } else {
                //Bob
                String bobName = aliceOrBob;
                System.out.println("mario2: I am bob " + bobName);

                PeerSemanticTag bob = kb.createPeerSemanticTag(bobName, bobName + "Id", "tcp://localhost:1001");
                ContextCoordinates interest = kb.createContextCoordinates(shark, null, bob, null, null, null, SharkCS.DIRECTION_IN);
                kp = new StandardKP(engine, interest, kb);
                kp.addListener(knowledgePortListener);
            }
        } catch (SharkKBException e) {
            LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
            e.printStackTrace();
        } catch (SharkSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SharkProtocolNotSupportedException e) {
            e.printStackTrace();
        }

        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("KB in use", L.kb2String(kb)));
        kbListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void clearUserInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
        userInput.setText("");
    }
}
