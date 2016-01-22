package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.KnowledgeBaseManager;

public class NfcSharkDemoFragment extends Fragment {

    //TODO: shark log > android LogManager...

    private SharkKB kb;
    private KnowledgeBaseListenerAdapterImpl knowledgeBaseListener;
    private MyKbListAdapter kbListAdapter;

    EditText userInput;
    ListView kbList;

    final View.OnClickListener userInputAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String inputText = userInput.getText().toString().trim();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
            userInput.setText("");

            try {
                final SemanticTag tag1 = kb.createSemanticTag("test", "testSi");
                final ContextCoordinates contextCoordinates1 = kb.createContextCoordinates(tag1, kb.getOwner(), null, null, null, null, SharkCS.DIRECTION_INOUT);
                kb.createContextPoint(contextCoordinates1).addInformation(inputText);
                Toast.makeText(v.getContext(), "Added: " + inputText, Toast.LENGTH_LONG).show();
            } catch (SharkKBException e) {
                LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
                Toast.makeText(v.getContext(), "Error occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    };

    final View.OnClickListener printKBClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            kbListAdapter.add(new MyKbListAdapter.MyDataHolder("KB in use", L.kb2String(kb)));
            kbListAdapter.notifyDataSetChanged();
        }
    };

    final View.OnLongClickListener printKBLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(v.getContext(), R.string.activity_nfc_sharkdemo_hint_print_kb, Toast.LENGTH_LONG).show();
            return true;
        }
    };

    final View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
            userInput.setText("");

            kbListAdapter.clear();
            kbListAdapter.notifyDataSetChanged();
        }
    };

    final View.OnLongClickListener clearLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(v.getContext(), R.string.activity_nfc_sharkdemo_hint_clear, Toast.LENGTH_LONG).show();
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

        final TextView ownerInformation = (TextView) root.findViewById(R.id.activity_nfc_sharkdemo_owner_id);
        ownerInformation.setText(String.format(root.getContext().getString(R.string.activity_nfc_sharkdemo_info), AndroidUtils.deviceId));

        userInput = (EditText) root.findViewById(R.id.activity_nfc_sharkdemo_input);
        final ImageButton userInputAdd = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_input_add);
        userInputAdd.setOnClickListener(userInputAddClickListener);

        final ImageButton printKB = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_print_kb);
        printKB.setOnClickListener(printKBClickListener);
        printKB.setOnLongClickListener(printKBLongClickListener);

        final ImageButton clearList = (ImageButton) root.findViewById(R.id.activity_nfc_sharkdemo_clear);

        clearList.setOnClickListener(clearClickListener);
        clearList.setOnLongClickListener(clearLongClickListener);

        kbList = (ListView) root.findViewById(R.id.activity_nfc_sharkdemo_kb_list);

        kbListAdapter = new MyKbListAdapter(this.getActivity());
        knowledgeBaseListener = new KnowledgeBaseListenerAdapterImpl(kbListAdapter, NfcMainActivity.LOG_ID);

        try {
            kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeDummy, false, AndroidUtils.deviceId);
            kb.addListener(knowledgeBaseListener);
            kbList.setAdapter(kbListAdapter);

            kbListAdapter.add(new MyKbListAdapter.MyDataHolder("KB in use", L.kb2String(kb)));
            kbListAdapter.notifyDataSetChanged();
        } catch (SharkKBException e) {
            LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
            e.printStackTrace();
        }

        return root;
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
}
