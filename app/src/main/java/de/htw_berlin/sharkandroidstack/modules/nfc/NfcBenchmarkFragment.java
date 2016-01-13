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
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {

    NfcMainActivity activity;

    MyReaderCallback readerCallback;
    MyStartButtonClickListener buttonClickListener;
    MyResultAdapter resultAdapter;

    final OnMessageSend benchmarkSource = new OnMessageSend() {
        @Override
        public byte[] getNextMessage() {
            byte[] message = Utils.generateRandomString(30).getBytes();
            resultAdapter.addMessageOut(message);
            return message;
        }
    };

    final OnMessageReceived onMessageReceived = new OnMessageReceived() {
        @Override
        public void onMessage(final byte[] message) {
            resultAdapter.addMessageIn(message);
        }

        @Override
        public void onError(Exception exception) {
            exception.printStackTrace();
            resultAdapter.addMessageError(exception.getMessage());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resultAdapter = new MyResultAdapter(this.getActivity());

        final View root = inflater.inflate(R.layout.module_nfc_benchmark_fragment, container, false);
        final ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);
        final ListView results = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);
        results.setAdapter(resultAdapter);

        final TextView description = (TextView) root.findViewById(R.id.activity_nfc_benchmark_description);
        description.setText(Html.fromHtml(getString(R.string.activity_nfc_benchmark_description)));
        final Button startButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);

        activity = (NfcMainActivity) NfcBenchmarkFragment.this.getActivity();

        buttonClickListener = new MyStartButtonClickListener(progressBar, results, description, benchmarkSource, activity, startButton);
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
            readerCallback = new MyReaderCallback(onMessageReceived, resultAdapter);
        }
        activity.prepareReceiving(readerCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
