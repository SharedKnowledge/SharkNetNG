package de.htw_berlin.sharkandroidstack.modules.nfc2;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyReaderCallback;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter;
import de.htw_berlin.sharkandroidstack.modules.nfc.OnMessageReceivedImpl;
import de.htw_berlin.sharkandroidstack.modules.nfc.OnMessageSendImpl;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.SmartCardEmulationService;

public class NfcActivity extends ParentActivity {

    private final View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(NfcActivity.this);
            view.setEnabled(false);
            Button button = (Button) view;
            button.setTextColor(Color.DKGRAY);
            button.setText(button.getText() + "...");
            prepareSending(nfcAdapter);
        }
    };

    private Button sendButton;
    private ListView resultList;
    private MyResultAdapter resultAdapter;
    private OnMessageReceivedImpl onMessageReceivedCallback;
    private OnMessageSendImpl onMessageSendCallback;
    private MyReaderCallback readerCallback;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc2_main);

        resultList = (ListView) findViewById(R.id.activity_nfc_benchmark_results);

        resultAdapter = new MyResultAdapter(this);
        onMessageReceivedCallback = new OnMessageReceivedImpl(resultAdapter, updateList, this);
        onMessageSendCallback = new OnMessageSendImpl(resultAdapter, updateList, this);
        resultList.setAdapter(resultAdapter);
    }

    final Runnable updateList = new Runnable() {
        @Override
        public void run() {
//            buttonClickListener.forceStart(startButton);
            resultAdapter.notifyDataSetChanged();
            resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (sendButton == null) {
            sendButton = (Button) findViewById(R.id.activity_nfc_benchmark_button_start);
            sendButton.setOnClickListener(sendOnClickListener);
        }

        if (readerCallback == null) {
            readerCallback = new MyReaderCallback(onMessageReceivedCallback);
        }

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        prepareReceiving(nfcAdapter, readerCallback);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareSending(NfcAdapter nfcAdapter) {
        SmartCardEmulationService.setSource(new OnMessageSendImpl(null, null, null));
        nfcAdapter.disableReaderMode(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onPause() {
        super.onPause();

        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) nfcAdapter.disableReaderMode(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareReceiving(NfcAdapter nfcAdapter, MyReaderCallback readerCallback) {

        final int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        nfcAdapter.enableReaderMode(this, readerCallback, flags, null);
    }
}
