package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    public static final int TIMER_END = 30000;
    public static final int TICK_INTERVAL = 1000;

    public static final int DEFAULT_MESSAGE_LENGTH = 512;

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
            resultAdapter.notifyDataSetChanged();
            resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);
        }
    };

    final Runnable updateListSending = new Runnable() {
        @Override
        public void run() {
            resultAdapter.notifyDataSetChanged();
            resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);
        }
    };

    final CountDownTimer timer = new CountDownTimer(TIMER_END, TICK_INTERVAL) {

        public void onTick(long millisUntilFinished) {
            long soFar = (TIMER_END - millisUntilFinished) / TICK_INTERVAL;
            progressBar.setProgress((int) soFar);
        }

        public void onFinish() {
            progressBar.setProgress(progressBar.getMax());
            benchmarkState.stoppedState();
        }
    };

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        benchmarkState = new NfcBenchmarkState(this, (NfcMainActivity) getActivity());

        final View root = inflater.inflate(R.layout.module_nfc_benchmark_fragment, container, false);

        msgLengthOutput = (TextView) root.findViewById(R.id.activity_nfc_benchmark_msg_length_output);
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
        msgLengthInput.setOnSeekBarChangeListener(seekBarChangeListener);
        msgLengthInput.setProgress(DEFAULT_MESSAGE_LENGTH);

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
