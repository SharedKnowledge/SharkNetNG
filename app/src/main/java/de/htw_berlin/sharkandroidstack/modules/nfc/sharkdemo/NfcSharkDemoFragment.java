package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharksystem.android.peer.AndroidSharkEngine;
import net.sharksystem.android.protocols.nfc.NfcMessageStub;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcSharkDemoFragment extends Fragment {

    public static final String TOAST_CONNECT_NOW = "Added Information to Alice.\nConnect other device with Bob now.";
    public static final String TOAST_NOTHING_TO_SEND = "Cannot sent empty information. Please add something first.";
    public static final String EXCEPTION_DATA_NOT_WELL_SERIALIZED = "Data was not serialized / deserialized well.\nInput was: %s\nOutput was: %s";
    public static final PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("dummy", "dummySi", "tcp://localhost");

    AndroidSharkEngine engine;
    SimpleRawKp kp;

    EditText inputText;
    ListView sendList;
    ListView receivedList;

    boolean hasShownSendNowHint = false;

    private Runnable kpUpdater = new Runnable() {
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
                showToast(TOAST_CONNECT_NOW);
                hasShownSendNowHint = true;
            }
        }
    };

    final View.OnClickListener inputSendButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                final ArrayAdapter<String> adapter = (ArrayAdapter) sendList.getAdapter();
                final int count = adapter.getCount();

                if (count == 0) {
                    showToast(TOAST_NOTHING_TO_SEND);
                    return;
                }

                final String[] stringArray = new String[count];
                for (int i = 0; i < count; i++) {
                    stringArray[i] = adapter.getItem(i);
                }

                final byte[] serialized = SimpleRawKp.serialize(stringArray);
                final String[] deserialized = SimpleRawKp.deserialize(serialized);
                if (!Arrays.equals(stringArray, deserialized)) {
                    throw new AssertionError(String.format(EXCEPTION_DATA_NOT_WELL_SERIALIZED, Arrays.toString(stringArray), Arrays.toString(deserialized)));
                }

                engine.startNfc();
                final InputStream is = new ByteArrayInputStream(serialized);
                engine.sendRaw(is, peerSemanticTag, null);
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

        final NfcDemoUxHandler uxHandler = new NfcDemoUxHandler(getActivity());

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

    private ArrayAdapter<String> prepareAdapter() {
        final Activity a = getActivity();
        final int l = R.layout.module_nfc_sharkdemo_list_entry;
        final int t = android.R.id.text1;

        return new ArrayAdapter<String>(a, l, t) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final String item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);

                final TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(item);
                return view;
            }
        };
    }

    private void createDialogPrompt() {
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

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
