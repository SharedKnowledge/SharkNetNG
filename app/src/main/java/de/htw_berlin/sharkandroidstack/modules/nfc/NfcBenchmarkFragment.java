package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
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

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {
    public static final int DEFAULT_MESSAGE_LENGTH = 512;

    //TODO: set SmartCardEmulationService.INITIAL_TYPE_OF_SERVICE to current fragment..
    //TODO: change MyStartButtonClickListener state on other device + clarify description/button
    //TODO: stats more expressive + final stats
    //TODO: migrate chat

    NfcMainActivity activity;
    TextView msgLengthOutput;

    MyReaderCallback readerCallback;
    MyStartButtonClickListener buttonClickListener;
    MyResultAdapter resultAdapter;

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

        activity = (NfcMainActivity) NfcBenchmarkFragment.this.getActivity();
        msgLengthOutput = (TextView) root.findViewById(R.id.activity_nfc_benchmark_msg_length_output);
        resultAdapter = new MyResultAdapter(this.getActivity());

        final SeekBar msgLengthInput = (SeekBar) root.findViewById(R.id.activity_nfc_benchmark_msg_length_input);
        msgLengthInput.setOnSeekBarChangeListener(seekBarChangeListener);
        msgLengthInput.setProgress(DEFAULT_MESSAGE_LENGTH);

        final ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);
        final ListView results = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);

        final TextView description = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description);
        final Button startButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);

        results.setAdapter(resultAdapter);
        description.setText(Html.fromHtml(getString(R.string.activity_nfc_benchmark_description)));

        buttonClickListener = new MyStartButtonClickListener(progressBar, results, description, activity, startButton, resultAdapter);
        startButton.setOnClickListener(buttonClickListener);

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
        activity.prepareReceiving(readerCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
