package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {

    public static final int DEFAULT_MESSAGE_LENGTH = 512;
    public static final int DEFAULT_DURATION_IN_SEC = 30;

    // TODO: on received: show progress with stats afterwards + reset on new receiving data

    //TODO: set SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE to current fragment..
    //TODO: change MyStartButtonClickListener state on other device + clarify description/button
    //TODO: stats more expressive + final stats

    TextView msgLengthOutput;
    Button startSendingButton;
    Button backFromReceivingButton;
    ProgressBar progressBar;
    TextView description;
    ListView resultList;
    SeekBar msgLengthInput;

    MyReaderCallback readerCallback;
    MyResultAdapter resultAdapter;

    NfcBenchmarkState benchmarkState;
    OnMessageReceivedImpl onMessageReceivedCallback;
    OnMessageSendImpl onMessageSendCallback;

    final OnClickListener startButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            benchmarkState.nextStateForSending();
        }
    };

    OnClickListener backButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            benchmarkState.resetState();
        }
    };

    final Runnable updateListReceiving = new Runnable() {
        @Override
        public void run() {
            benchmarkState.receivingState();
            resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);
        }
    };

    final Runnable updateListSending = new Runnable() {
        @Override
        public void run() {
            benchmarkState.sendState();
            resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);
        }
    };

    final SeekBar.OnSeekBarChangeListener msgLengthChangeListener = new SeekBar.OnSeekBarChangeListener() {
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

    final SeekBar.OnSeekBarChangeListener durationChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
            durationOutput.setText(progress + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    TextView durationOutput;
    SeekBar durationInput;

    int getDurationInSec() {
        return new Integer(durationOutput.getText().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        benchmarkState = new NfcBenchmarkState(this, (NfcMainActivity) getActivity());

        final View root = inflater.inflate(R.layout.module_nfc_benchmark_fragment, container, false);

        msgLengthOutput = (TextView) root.findViewById(R.id.activity_nfc_benchmark_msg_length_output);
        durationOutput = (TextView) root.findViewById(R.id.activity_nfc_benchmark_duration_output);
        progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);

        description = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description);
        description.setText(Html.fromHtml(getString(R.string.activity_nfc_benchmark_description)));

        resultAdapter = new MyResultAdapter(getActivity());
        onMessageReceivedCallback = new OnMessageReceivedImpl(resultAdapter, updateListReceiving, getActivity());
        onMessageSendCallback = new OnMessageSendImpl(resultAdapter, updateListSending, getActivity());

        resultList = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);
        resultList.setAdapter(resultAdapter);

        startSendingButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);
        startSendingButton.setOnClickListener(startButtonClickListener);

        backFromReceivingButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_back);
        backFromReceivingButton.setOnClickListener(backButtonClickListener);

        msgLengthInput = (SeekBar) root.findViewById(R.id.activity_nfc_benchmark_msg_length_input);
        msgLengthInput.setOnSeekBarChangeListener(msgLengthChangeListener);
        msgLengthInput.setProgress(DEFAULT_MESSAGE_LENGTH);

        durationInput = (SeekBar) root.findViewById(R.id.activity_nfc_benchmark_duration_input);
        durationInput.setOnSeekBarChangeListener(durationChangeListener);
        durationInput.setProgress(DEFAULT_DURATION_IN_SEC);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (readerCallback == null) {
            readerCallback = new MyReaderCallback(onMessageReceivedCallback);
        }

        benchmarkState.resetState();
    }
}
