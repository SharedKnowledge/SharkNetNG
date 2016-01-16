package de.htw_berlin.sharkandroidstack.modules.nfc2;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyReaderCallback;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyStartButtonClickListener;
import de.htw_berlin.sharkandroidstack.modules.nfc.OnMessageReceivedImpl;
import de.htw_berlin.sharkandroidstack.modules.nfc.OnMessageSendImpl;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.SmartCardEmulationService;

public class NfcActivity extends ParentActivity {

    public static final int DEFAULT_MESSAGE_LENGTH = 512;
    public static final int TIMER_END = 30000;
    public static final int TICK_INTERVAL = 1000;

    // TODO: static?
    public static NfcAdapter nfcAdapter;

    TextView msgLengthOutput;
    Button startButton;
    ProgressBar progressBar;
    TextView description;
    ListView resultList;
    SeekBar msgLengthInput;

    MyReaderCallback readerCallback;
    MyStartButtonClickListener buttonClickListener;
    MyResultAdapter resultAdapter;
    OnMessageReceivedImpl onMessageReceivedCallback;
    OnMessageSendImpl onMessageSendCallback;

    final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            msgLengthOutput.setText(progress + "");
            onMessageSendCallback.setMsgLength(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_nfc_benchmark_fragment);

        NfcActivity root = this;

        msgLengthOutput = (TextView) root.findViewById(R.id.activity_nfc_benchmark_msg_length_output);
        progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);

        description = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description);
        description.setText(Html.fromHtml(getString(R.string.activity_nfc_benchmark_description)));

        resultList = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);

        resultAdapter = new MyResultAdapter(this);
        onMessageReceivedCallback = new OnMessageReceivedImpl(resultAdapter, updateList, this);
        onMessageSendCallback = new OnMessageSendImpl(resultAdapter, updateList, this);
        resultList.setAdapter(resultAdapter);

        // buttonClickListener = new MyStartButtonClickListener(this);
        startButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);
        startButton.setOnClickListener(sendOnClickListener);

        msgLengthInput = (SeekBar) root.findViewById(R.id.activity_nfc_benchmark_msg_length_input);
        msgLengthInput.setOnSeekBarChangeListener(seekBarChangeListener);
        msgLengthInput.setProgress(DEFAULT_MESSAGE_LENGTH);

//        setStateToReset();
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

        if (readerCallback == null) {
            readerCallback = new MyReaderCallback(onMessageReceivedCallback);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        prepareReceiving(readerCallback);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareSending(OnMessageSend src) {
        SmartCardEmulationService.setSource(src);
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
    void prepareReceiving(MyReaderCallback readerCallback) {
        final int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        nfcAdapter.enableReaderMode(this, readerCallback, flags, null);
    }

    private final View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            view.setEnabled(false);
            Button button = (Button) view;
            button.setTextColor(Color.DKGRAY);
            button.setText(button.getText() + "...");
            prepareSending(onMessageSendCallback);
        }
    };
}
