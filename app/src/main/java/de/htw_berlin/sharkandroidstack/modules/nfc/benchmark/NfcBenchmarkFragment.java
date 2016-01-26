package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.androidService.NfcReaderCallback;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

import static de.htw_berlin.sharkandroidstack.modules.nfc.benchmark.MyResultAdapter.MyDataHolder;

/**
 * Created by mn-io on 22.01.16.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {
    public static final int TICK_INTERVAL = 1000;
    public static final int DEFAULT_DURATION_IN_SEC = 30;
    public static final int TIMEOUT_RECEIVING_ADD_RESULT = 5000;
    public static final int MSG_LENGTH_SCALE_FACTOR = 8;
    public static final int DEFAULT_MESSAGE_LENGTH = 1024 / MSG_LENGTH_SCALE_FACTOR;

    public static final String MSG_PAYLOAD_RECEIVED = "Payload handled: ";
    public static final String MSG_PAYLOAD_SENT = "Payload sent: ";
    public static final String MSG_TIME_ELAPSED = "Time elapsed: ";
    public static final String MSG_TIME_MEASURED = "Time measured*: ";
    public static final String MSG_THROUGHPUT = "Throughput: ";
    public static final String MSG_MSG_DETECTED = "\nMessage packages (chunks) detected: ";
    public static final String MSG_TAGS_DETECTED = "\nTags detected: ";
    public static final String MSG_TAGS_PER_SECOND = "Tags per second: ";
    public static final String MSG_BYTES_PER_TAG = "Bytes per tag: ";
    public static final String MSG_TIME_HINT = "\n* Time is started on first message sent/tag discovered, stopped with system overhead on result collecting.";

    public static final String MSG_BYTES = " Bytes\n";
    public static final String MSG_SECONDS = " seconds\n";
    public static final String MSG_BYTE_S = " byte/s, ";
    public static final String MSG_BIT_S = " bit/s \n";
    public static final String MSG_NEW_LINE = "\n";

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;

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

    NfcReaderCallback readerCallback;
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
            if (benchmarkState.isState(NfcBenchmarkState.STATE_STOPPED)) {
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
            if (benchmarkState.isState(NfcBenchmarkState.STATE_RECEIVING)) {
                final Handler handler = resultList.getHandler();
                handler.removeCallbacks(addResultsAsync);
                handler.postDelayed(addResultsAsync, TIMEOUT_RECEIVING_ADD_RESULT);
                return;
            }

            if (!benchmarkState.receivingState()) {
                return;
            }
        }
    };
    //TODO: remove this updater?
    final Runnable updateListSending = new Runnable() {
        @Override
        public void run() {
            if (!benchmarkState.sendState()) {
                return;
            }
        }
    };

    final SeekBar.OnSeekBarChangeListener msgLengthChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progress *= MSG_LENGTH_SCALE_FACTOR;
            msgLengthOutput.setText(progress + "");
            onMessageSendCallback.setMaxSize(progress);
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

        resultList = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);
        initAdapterAndCallbacks(resultList);

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

    private void initAdapterAndCallbacks(ListView resultList) {
        resultAdapter = new MyResultAdapter(getActivity());
        onMessageReceivedCallback = new OnMessageReceivedImpl(resultAdapter, updateListReceiving, getActivity());
        onMessageSendCallback = new OnMessageSendImpl(resultAdapter, updateListSending, getActivity());
        resultList.setAdapter(resultAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (readerCallback == null) {
            readerCallback = new NfcReaderCallback(onMessageSendCallback, onMessageReceivedCallback);
        }

        benchmarkState.resetState();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private int getDurationInSec() {
        return Integer.valueOf(durationOutput.getText().toString());
    }

    private void addResult() {
        String msg = calcStats(onMessageSendCallback) + MSG_NEW_LINE + MSG_NEW_LINE + calcStats(onMessageReceivedCallback) + MSG_TIME_HINT;

        final MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_NONE, MyDataHolder.TYPE_RESULT, msg);
        resultAdapter.add(dataHolder);
        resultAdapter.notifyDataSetChanged();
        resultList.smoothScrollToPosition(resultAdapter.getCount() - 1);

        LogManager.addEntry(NfcMainActivity.LOG_ID, msg, 2);
    }

    private String calcStats(OnAdapterUpdate adapter) {
        //TODO: check timer.
        final long byteCount = adapter.readAndResetByteCount();
        final int msgCount = adapter.readAndResetMsgCount();
        final int tagCount = adapter.readAndResetTagCount();
        long timer = adapter.readAndResetTimer();
        int maxMsgSize = 0;

        String src;
        if (adapter instanceof OnMessageSendImpl) {
            src = "Sent";
        } else {
            src = "Received";
            timer -= TIMEOUT_RECEIVING_ADD_RESULT;
            maxMsgSize = ((OnMessageReceivedImpl) adapter).readAndResetMsgSize();
        }

        final BigDecimal timerBigDecimal = BigDecimal.valueOf(timer);
        final BigDecimal timerAsSeconds = timerBigDecimal.setScale(4, ROUNDING_MODE)
                .divide(BigDecimal.valueOf(1000), ROUNDING_MODE);

        final BigDecimal bytePerSecond;
        final BigDecimal bitsPerSecond;
        if (timerAsSeconds.equals(BigDecimal.ZERO)) {
            bytePerSecond = BigDecimal.ZERO;
            bitsPerSecond = BigDecimal.ZERO;
        } else {
            bytePerSecond = BigDecimal.valueOf(byteCount).setScale(4, ROUNDING_MODE)
                    .divide(timerAsSeconds, ROUNDING_MODE);
            bitsPerSecond = BigDecimal.valueOf(byteCount * 8).setScale(4, ROUNDING_MODE)
                    .divide(timerAsSeconds, ROUNDING_MODE);
        }
        final BigDecimal tagsPerSecond = BigDecimal.valueOf(tagCount).setScale(2, ROUNDING_MODE).divide(timerAsSeconds, ROUNDING_MODE);
        final BigDecimal bytesPerTag = tagCount == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(byteCount)
                .setScale(2, ROUNDING_MODE).divide(BigDecimal.valueOf(tagCount), ROUNDING_MODE);

        final StringBuilder msg = new StringBuilder(src).append(":" + MSG_NEW_LINE);

        if (View.VISIBLE == progressBar.getVisibility()) {
            final int p = progressBar.getProgress();
            msg.append(MSG_TIME_ELAPSED).append(p).append(MSG_SECONDS);
        }
        if (maxMsgSize > 0) {
            msg.append("Max payload per response: ").append(maxMsgSize).append(MSG_BYTES);
        }

        msg.append(MSG_PAYLOAD_RECEIVED).append(byteCount).append(MSG_BYTES);
        msg.append(MSG_TIME_MEASURED).append(timerAsSeconds).append(MSG_SECONDS);
        msg.append(MSG_THROUGHPUT).append(bytePerSecond).append(MSG_BYTE_S).append(bitsPerSecond).append(MSG_BIT_S);

        msg.append(MSG_MSG_DETECTED).append(msgCount).append(MSG_NEW_LINE);
        msg.append(MSG_TAGS_DETECTED).append(tagCount).append(MSG_NEW_LINE);
        msg.append(MSG_TAGS_PER_SECOND).append(tagsPerSecond).append(MSG_NEW_LINE);
        msg.append(MSG_BYTES_PER_TAG).append(bytesPerTag).append(MSG_NEW_LINE);
        return msg.toString();
    }

    @NonNull
    private String calcStats() {
        //TODO: fix/enhance stats
        final long fixedTimeoutTimer = onMessageReceivedCallback.readAndResetTimer() - TIMEOUT_RECEIVING_ADD_RESULT;
        final long measuredTime = Math.min(onMessageSendCallback.readAndResetTimer(), fixedTimeoutTimer);

        final long byteCount1 = onMessageReceivedCallback.readAndResetByteCount();
        final long byteCount2 = onMessageSendCallback.readAndResetByteCount();
        final long byteCount = Math.max(byteCount1, byteCount2);

        final int tagCount = Math.max(onMessageReceivedCallback.readAndResetTagCount(), onMessageSendCallback.readAndResetTagCount());
        final int msgCount = Math.max(onMessageReceivedCallback.readAndResetMsgCount(), onMessageSendCallback.readAndResetMsgCount());

        final BigDecimal asSeconds = BigDecimal.valueOf(measuredTime).setScale(4, ROUNDING_MODE)
                .divide(BigDecimal.valueOf(1000), ROUNDING_MODE);
        final BigDecimal bytePerSecond = asSeconds.equals(BigDecimal.ZERO) ? BigDecimal.ZERO :
                BigDecimal.valueOf(byteCount).setScale(4, ROUNDING_MODE).divide(asSeconds, ROUNDING_MODE);
        final BigDecimal bitsPerSecond = asSeconds.equals(BigDecimal.ZERO) ? BigDecimal.ZERO :
                BigDecimal.valueOf(byteCount * 8).setScale(4, ROUNDING_MODE).divide(asSeconds, ROUNDING_MODE);

        final BigDecimal tagsPerSecond = asSeconds.equals(BigDecimal.ZERO) ? BigDecimal.ZERO :
                BigDecimal.valueOf(tagCount).setScale(2, ROUNDING_MODE).divide(asSeconds, ROUNDING_MODE);
        final BigDecimal bytesPerTag = tagCount == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(byteCount)
                .setScale(2, ROUNDING_MODE).divide(BigDecimal.valueOf(tagCount), ROUNDING_MODE);


        final StringBuilder msg = new StringBuilder();

        msg.append(MSG_PAYLOAD_RECEIVED).append(byteCount1).append(MSG_BYTES);
        msg.append(MSG_PAYLOAD_SENT).append(byteCount2).append(MSG_BYTES);

        if (View.VISIBLE == progressBar.getVisibility()) {
            final int p = progressBar.getProgress();
            msg.append(MSG_TIME_ELAPSED).append(p).append(MSG_SECONDS);
        }

        msg.append(MSG_TIME_MEASURED).append(asSeconds).append(MSG_SECONDS);
        msg.append(MSG_THROUGHPUT).append(bytePerSecond).append(MSG_BYTE_S).append(bitsPerSecond).append(MSG_BIT_S);

        msg.append(MSG_MSG_DETECTED).append(msgCount).append(MSG_NEW_LINE);
        msg.append(MSG_TAGS_DETECTED).append(tagCount).append(MSG_NEW_LINE);
        msg.append(MSG_TAGS_PER_SECOND).append(tagsPerSecond).append(MSG_NEW_LINE);
        msg.append(MSG_BYTES_PER_TAG).append(bytesPerTag).append(MSG_NEW_LINE);
        msg.append(MSG_TIME_HINT);
        return msg.toString();
    }
}
