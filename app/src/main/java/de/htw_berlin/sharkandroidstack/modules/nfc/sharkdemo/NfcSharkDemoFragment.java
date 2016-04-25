package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.SharkSecurityException;
import net.sharksystem.android.peer.AndroidSharkEngine;
import net.sharksystem.android.protocols.nfc.NfcMessageStub;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.ProgressAndVibrateUxHandler;
import de.htw_berlin.sharkandroidstack.modules.nfc.RawKp;
import de.htw_berlin.sharkandroidstack.modules.nfc.UxFragment;

/**
 * Created by Mario Neises (mn-io) on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcSharkDemoFragment extends UxFragment {

    public static final PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("dummy", "dummySi", "tcp://localhost");

    AndroidSharkEngine engine;
    SimpleRawKp kp;

    EditText inputText;
    ListView sendList;
    ListView receivedList;

    boolean hasShownSendNowHint = false;

    final Runnable kpUpdater = new Runnable() {
        @Override
        public void run() {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) receivedList.getAdapter();
            adapter.clear();
            adapter.addAll(kp.getReceivedData());
            adapter.notifyDataSetChanged();
        }
    };

    final View.OnClickListener inputAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (inputText.length() == 0) {
                return;
            }

            final String data = inputText.getText().toString().trim();
//            AndroidUtils.clearUserInput(getActivity(), inputText);

            final ArrayAdapter<String> adapter = (ArrayAdapter) sendList.getAdapter();
            adapter.add(data);
            adapter.notifyDataSetChanged();

            inputText.setText(null);
            if (sendList.getCount() == 1 && !hasShownSendNowHint) {
                showToast(getString(R.string.activity_nfc_toast_connect_now));
                hasShownSendNowHint = true;
            }

            try {
                prepareSending();
            } catch (Exception e) {
                NfcMainActivity.handleError(getActivity(), e);
            }
        }
    };

    final View.OnClickListener inputSendButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                final int count = sendList.getCount();
                if (count == 0) {
                    showToast(getString(R.string.activity_nfc_toast_nothing_to_send));
                    return;
                }

                uxHandler.startReaderModeNegotiation();
                uxHandler.showProgressDialog();
            } catch (Exception e) {
                NfcMainActivity.handleError(getActivity(), e);
            }
        }
    };

    public final View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListView currentList;

            switch (v.getId()) {
                case R.id.activity_nfc_sharkdemo_send_clear:
                    currentList = sendList;
                    break;
                case R.id.activity_nfc_sharkdemo_received_clear:
                    currentList = receivedList;
                    break;
                default:
                    return;
            }

            final ArrayAdapter<String> adapter = (ArrayAdapter<String>) currentList.getAdapter();
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
    };

    final public static View.OnLongClickListener infoLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int toastMsg = 0;
            switch (v.getId()) {
//                case R.id.activity_nfc_sharkdemo_clear:
//                    toastMsg = R.string.activity_nfc_sharkdemo_hint_clear;
//                    break;
//                case R.id.activity_nfc_sharkdemo_print_kb:
//                    toastMsg = R.string.activity_nfc_sharkdemo_hint_print_kb;
//                    break;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

//        createDialogPrompt();

        final View inputContainer = root.findViewById(R.id.activity_nfc_sharkdemo_input_container);
        inputText = (EditText) inputContainer.findViewById(R.id.activity_nfc_sharkdemo_input);
        final ImageButton inputAddButton = (ImageButton) inputContainer.findViewById(R.id.activity_nfc_sharkdemo_input_add);
        inputAddButton.setOnClickListener(inputAddButtonClickListener);
        inputAddButton.setOnLongClickListener(infoLongClickListener);
        final View inputSendButton = inputContainer.findViewById(R.id.activity_nfc_sharkdemo_input_send);
        inputSendButton.setOnClickListener(inputSendButtonClickListener);
        inputSendButton.setOnLongClickListener(infoLongClickListener);

        View sendContainer = root.findViewById(R.id.activity_nfc_sharkdemo_send_container);
        sendList = (ListView) sendContainer.findViewById(R.id.activity_nfc_sharkdemo_send_list);
        sendList.setAdapter(prepareAdapter());
        final View sendClearButton = sendContainer.findViewById(R.id.activity_nfc_sharkdemo_send_clear);
        sendClearButton.setOnClickListener(clearClickListener);
        sendClearButton.setOnLongClickListener(infoLongClickListener);

        final View receivedContainer = root.findViewById(R.id.activity_nfc_sharkdemo_received_container);
        receivedList = (ListView) receivedContainer.findViewById(R.id.activity_nfc_sharkdemo_received_list);
        receivedList.setAdapter(prepareAdapter());
        final View receivedClearButton = receivedContainer.findViewById(R.id.activity_nfc_sharkdemo_received_clear);
        receivedClearButton.setOnClickListener(clearClickListener);
        receivedClearButton.setOnLongClickListener(infoLongClickListener);

        uxHandler = new ProgressAndVibrateUxHandler(this);

        engine = new AndroidSharkEngine(getActivity());
        engine.activateASIP();
        kp = new SimpleRawKp(engine, kpUpdater);

        try {
            engine.stopNfc();
            final NfcMessageStub protocolStub = (NfcMessageStub) engine.getProtocolStub(0);
            protocolStub.setUxHandler(uxHandler);
        } catch (SharkProtocolNotSupportedException e) {
            NfcMainActivity.handleError(getActivity(), e);
        }

        return root;
    }

    ArrayAdapter<String> prepareAdapter() {
        final Activity a = getActivity();
        final int l = R.layout.simple_list_item_with_delete;
        final int t = android.R.id.text1;

        return new ArrayAdapter<String>(a, l, t) {
            final View.OnClickListener deleteItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) v.getTag();
                    remove(text);
                    notifyDataSetChanged();
                }
            };

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final String item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);

                final TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(item);

                View deleteButton = view.findViewById(R.id.delete_item_button);
                deleteButton.setTag(item);
                deleteButton.setOnClickListener(deleteItemClickListener);
                return view;
            }
        };
    }

    void prepareSending() throws IOException, ClassNotFoundException, SharkSecurityException, SharkKBException {
        final ArrayAdapter<String> adapter = (ArrayAdapter) sendList.getAdapter();
        final int count = adapter.getCount();

        final String[] stringArray = new String[count];
        for (int i = 0; i < count; i++) {
            stringArray[i] = adapter.getItem(i);
        }

        final byte[] serialized = RawKp.serialize(stringArray);
        final String[] deserialized = RawKp.deserializeAsStrings(serialized);
        if (!Arrays.equals(stringArray, deserialized)) {
            final String msg = String.format(
                    getString(R.string.activity_nfc_exception_data_not_well_serialized),
                    Arrays.toString(stringArray),
                    Arrays.toString(deserialized));
            throw new AssertionError(msg);
        }

        final InputStream is = new ByteArrayInputStream(serialized);
        engine.sendRaw(is, peerSemanticTag, null);
    }

    void createDialogPrompt() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final DialogInterface.OnClickListener closeDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };

        builder.setTitle(R.string.activity_nfc_sharkdemo_intro_title)
                .setMessage(R.string.activity_nfc_sharkdemo_intro_body)
                .setPositiveButton(android.R.string.ok, closeDialogListener)
                .create()
                .show();
    }

    void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
