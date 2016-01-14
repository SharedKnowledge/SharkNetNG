package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.R.drawable.ic_media_previous;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {

    public static final int TIMER_END = 30000;
    public static final int TICK_INTERVAL = 1000;

    public static final int DEFAULT_MESSAGE_LENGTH = 512;

    //TODO: set SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE to current fragment..
    //TODO: change MyStartButtonClickListener state on other device + clarify description/button
    //TODO: stats more expressive + final stats
    //TODO: migrate chat

    TextView msgLengthOutput;
    Button startButton;
    ProgressBar progressBar;
    TextView description;
    ListView resultList;
    SeekBar msgLengthInput;

    MyReaderCallback readerCallback;
    MyStartButtonClickListener buttonClickListener;
    MyResultAdapter resultAdapter;


    final CountDownTimer timer = new CountDownTimer(TIMER_END, TICK_INTERVAL) {

        public void onTick(long millisUntilFinished) {
            long soFar = (TIMER_END - millisUntilFinished) / TICK_INTERVAL;
            progressBar.setProgress((int) soFar);
        }

        public void onFinish() {
            progressBar.setProgress(progressBar.getMax());
            buttonClickListener.abortExternal(startButton);

        }
    };

    final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            msgLengthOutput.setText(progress + "");
            resultAdapter.setMsgLength(progress);
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
        final View root = inflater.inflate(R.layout.module_nfc_benchmark_fragment, container, false);

        msgLengthOutput = (TextView) root.findViewById(R.id.activity_nfc_benchmark_msg_length_output);
        progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);

        description = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description);
        description.setText(Html.fromHtml(getString(R.string.activity_nfc_benchmark_description)));

        resultList = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);
        resultAdapter = new MyResultAdapter(this.getActivity(), resultList);
        resultList.setAdapter(resultAdapter);

        buttonClickListener = new MyStartButtonClickListener(this);
        startButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);
        startButton.setOnClickListener(buttonClickListener);

        msgLengthInput = (SeekBar) root.findViewById(R.id.activity_nfc_benchmark_msg_length_input);
        msgLengthInput.setOnSeekBarChangeListener(seekBarChangeListener);
        msgLengthInput.setProgress(DEFAULT_MESSAGE_LENGTH);

        setStateToReset();

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
            readerCallback = new MyReaderCallback(resultAdapter, resultAdapter);
        }
        ((NfcMainActivity) getActivity()).prepareReceiving(readerCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setStateToRunning() {
        startButton.setText(R.string.activity_nfc_benchmark_stop);
        startButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

        progressBar.setProgress(0);

        description.setVisibility(GONE);
        msgLengthInput.setVisibility(GONE);
        msgLengthOutput.setVisibility(GONE);

        progressBar.setVisibility(VISIBLE);
        resultList.setVisibility(VISIBLE);

        timer.start();

        ((NfcMainActivity) getActivity()).prepareSending(resultAdapter);
    }

    public void setStateToStopped() {
        startButton.setText(R.string.activity_nfc_benchmark_abort);
        startButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_previous, 0);

        timer.cancel();
    }

    public void setStateToReset() {
        startButton.setText(R.string.activity_nfc_benchmark_start);
        startButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_play, 0);

        description.setVisibility(VISIBLE);
        msgLengthInput.setVisibility(VISIBLE);
        msgLengthOutput.setVisibility(VISIBLE);

        progressBar.setVisibility(GONE);
        resultList.setVisibility(GONE);

        resultAdapter.clear();
        progressBar.setProgress(0);
    }
}
