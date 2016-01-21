package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

import static de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter.MyDataHolder;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {
    public static final int TICK_INTERVAL = 1000;
    public static final int DEFAULT_MESSAGE_LENGTH = 512;
    public static final int DEFAULT_DURATION_IN_SEC = 30;
    public static final int TIMEOUT_RECEIVING_ADD_RESULT = 2000;

    // TODO: on received: show progress with stats afterwards + reset on new receiving data

    //TODO: set SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE to current fragment..
    //TODO: change MyStartButtonClickListener state on other device + clarify description/button
    //TODO: stats more expressive + final stats

    Button startSendingButton;
    Button backFromReceivingButton;
    ProgressBar progressBar;
    TextView progressDescription;
    private TextView description;
    private TextView description2;
    private ListView resultList;
    private SeekBar msgLengthInput;
    private TextView msgLengthOutput;
    private SeekBar durationInput;
    private TextView durationOutput;

    MyReaderCallback readerCallback;
    MyResultAdapter resultAdapter;

    NfcBenchmarkState benchmarkState;
    OnMessageReceivedImpl onMessageReceivedCallback;
    OnMessageSendImpl onMessageSendCallback;

    final Runnable addResultsAsync = new Runnable() {
        @Override
        public void run() {
            addResult();
        }
    };

    final OnClickListener startButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            benchmarkState.nextStateForSending();
            if (benchmarkState.isStopped()) {
                addResult();
            }
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

            final Handler handler = resultList.getHandler();
            handler.removeCallbacks(addResultsAsync);
            handler.postDelayed(addResultsAsync, TIMEOUT_RECEIVING_ADD_RESULT);
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
        progressDescription = (TextView) root.findViewById(R.id.activity_nfc_benchmark_progress_description);

        description = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description);
        description2 = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description2);

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

    protected void setResultVisibility(final int visibility, final int progressVisibility) {
        int invertedVisibility = View.VISIBLE == visibility ? View.GONE : View.VISIBLE;
        description.setVisibility(invertedVisibility);
        description2.setVisibility(invertedVisibility);
        msgLengthInput.setVisibility(invertedVisibility);
        msgLengthOutput.setVisibility(invertedVisibility);
        durationInput.setVisibility(invertedVisibility);
        durationOutput.setVisibility(invertedVisibility);

        resultList.setVisibility(visibility);

        progressBar.setVisibility(progressVisibility);
        progressDescription.setVisibility(progressVisibility);
    }

    public CountDownTimer prepareTimer() {
        final int durationInSec = getDurationInSec();
        final int durationInMS = durationInSec * TICK_INTERVAL;
        progressBar.setMax(durationInSec);
        progressBar.setProgress(0);

        return new CountDownTimer(durationInMS, TICK_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                final long soFar = (durationInMS - millisUntilFinished) / TICK_INTERVAL;
                final long timeLeft = (millisUntilFinished / TICK_INTERVAL) + 1;
                progressDescription.setText(timeLeft + "");
                progressBar.setProgress((int) soFar);
            }

            public void onFinish() {
                addResult();
                progressBar.setProgress(durationInSec);
                progressDescription.setText(0 + "");
                benchmarkState.stoppedState();
            }
        };
    }

    public void addResult() {
        final long fixedTimeoutTimer = onMessageReceivedCallback.readAndResetTimer() - TIMEOUT_RECEIVING_ADD_RESULT;
        long measuredTime = Math.min(onMessageSendCallback.readAndResetTimer(), fixedTimeoutTimer);

        final long byteCount1 = onMessageReceivedCallback.readAndResetCount();
        long byteCount2 = onMessageSendCallback.readAndResetCount();
        long byteCount = Math.max(byteCount1, byteCount2);

        final BigDecimal asSeconds = BigDecimal.valueOf(measuredTime).setScale(4).divide(BigDecimal.valueOf(1000), RoundingMode.HALF_DOWN);
        final BigDecimal bytePerSecond = BigDecimal.valueOf(byteCount).setScale(4).divide(asSeconds, RoundingMode.HALF_DOWN);
        final BigDecimal bitsPerSecond = BigDecimal.valueOf(byteCount * 8).setScale(4).divide(asSeconds, RoundingMode.HALF_DOWN);

        final int tagCount2 = onMessageSendCallback.readAndResetTagCount();
        final int tagCount = Math.max(onMessageReceivedCallback.readAndResetTagCount(), tagCount2);

        String msg = "Payload received: " + byteCount1 + " Bytes\n";
        msg += "Payload sent: " + byteCount2 + " Bytes\n";
        msg += "Tags detected: " + tagCount + "\n";
        if (View.VISIBLE == progressBar.getVisibility()) {
            msg += "Time elapsed: " + progressBar.getProgress() + " seconds\n";
        }
        msg += "Time measured*: " + asSeconds + " seconds\n";
        msg += "Throughput: " + bytePerSecond + " byte/s, " + bitsPerSecond + " bit/s, \n";

        msg += "\n* Time is started on first message sent/tag discovered, stopped with system overhead on result collecting.";

        final MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_NONE, MyDataHolder.TYPE_RESULT, msg);
        resultAdapter.add(dataHolder);
        resultAdapter.notifyDataSetChanged();
        resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);

        LogManager.addEntry(NfcMainActivity.LOG_ID, msg, 2);
    }
}
