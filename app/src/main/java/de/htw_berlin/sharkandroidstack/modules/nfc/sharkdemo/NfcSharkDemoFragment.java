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

import net.sharkfw.system.L;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys.Alice;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys.Bob;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys.SharkDemoIdentity;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by mn-io on 22.01.16.
 */
public class NfcSharkDemoFragment extends Fragment {

    SharkDemoIdentity currentID;
    Alice alice;
    Bob bob;

    KnowledgeBaseListenerAdapterImpl knowledgeBaseListener;
    KnowledgePortAdapterListenerImpl knowledgePortListener;
    MyKbListAdapter kbListAdapter;

    TextView ownerInformation;

    AndroidSharkEngine engine;

    EditText userInput;
    ListView kbList;

    final View.OnClickListener userInputAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String inputText = userInput.getText().toString().trim();
            if (inputText.length() == 0) {
                return;
            }
            clearUserInput();

            try {
                alice.addInformation(inputText);
                Toast.makeText(v.getContext(), String.format("Added Information to Alice:\n%s", inputText), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
                Toast.makeText(v.getContext(), String.format("Error occurred: %s", e.getMessage()), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    final View.OnClickListener printKBClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String data = L.kb2String(currentID.getKB());
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

    private View.OnClickListener startClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (currentID == null || alice.name.equals(currentID.getName())) {
                    currentID = bob;
                } else {
                    alice.sendInformation(bob.getPeer());
                    currentID = alice;
                }

                ownerInformation.setText(currentID.getLongName());
                clearClickListener.onClick(null);
            } catch (IllegalStateException e) {
                Toast.makeText(getActivity(), "Cannot sent empty information. Please add something first.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Exception occurred: '" + e.getMessage() + "'. Check Log for details.", Toast.LENGTH_LONG).show();
                LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

        final ImageButton userInputAdd = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_input_add);
        userInputAdd.setOnClickListener(userInputAddClickListener);
        userInputAdd.setOnLongClickListener(infoLongClickListener);

        final ImageButton printKB = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_print_kb);
        printKB.setOnClickListener(printKBClickListener);
        printKB.setOnLongClickListener(infoLongClickListener);

        final ImageButton clearListButton = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_clear);
        clearListButton.setOnClickListener(clearClickListener);
        clearListButton.setOnLongClickListener(infoLongClickListener);


        ownerInformation = (TextView) root.findViewById(R.id.activity_nfc_sharkdemo_owner_id);
        userInput = (EditText) root.findViewById(R.id.activity_nfc_sharkdemo_input);
        kbList = (ListView) root.findViewById(R.id.activity_nfc_sharkdemo_kb_list);


        kbListAdapter = new MyKbListAdapter(this.getActivity());
        knowledgeBaseListener = new KnowledgeBaseListenerAdapterImpl(kbListAdapter, NfcMainActivity.LOG_ID);
        knowledgePortListener = new KnowledgePortAdapterListenerImpl(kbListAdapter);
        kbList.setAdapter(kbListAdapter);

        initShark();

        Button startButton = (Button) root.findViewById(R.id.activity_nfc_sharkdemo_start);
        startButton.setOnClickListener(startClickListener);
        startButton.performClick();

        return root;
    }

    private void initShark() {
        try {
            engine = new AndroidSharkEngine(getActivity());

            alice = new Alice(knowledgeBaseListener, engine, knowledgePortListener);
            bob = new Bob(knowledgeBaseListener, engine, knowledgePortListener);

            engine.stopNfc();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Exception on init: '" + e.getMessage() + "'. Check Log for details.", Toast.LENGTH_LONG).show();
            LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
        }
    }

    private void clearUserInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
        userInput.setText("");
    }
}
