package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {

    NfcMainActivity activity;

    MyReaderCallback readerCallback;
    MyStartButtonClickListener buttonClickListener;

    final static OnMessageSend benchmarkSource = new OnMessageSend() {
        @Override
        public byte[] getNextMessage() {
            byte[] message = Utils.generateRandomString(30).getBytes();
            System.out.println("Mario: out > " + new String(message) + " | " + Arrays.toString(message));
            return message;
        }
    };

    final OnMessageReceived onMessageReceived = new OnMessageReceived() {
        @Override
        public void onMessage(final byte[] message) {
            System.out.println("Mario: in > " + new String(message) + " | " + Arrays.toString(message));
//            NfcBenchmarkFragment.this.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    outputStringBuilder.append(new String(message));
//                    output.setText(outputStringBuilder.toString());
//                }
//            });
        }

        @Override
        public void onError(Exception exception) {
            exception.printStackTrace();
            onMessage(("Finished with error: " + exception.getMessage()).getBytes());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.module_nfc_benchmark_activity, container, false);
        final ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);
        final ListView results = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);

        final View description = root.findViewById(R.id.activity_nfc_benchmark_description);
        final Button startButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);

        activity = (NfcMainActivity) NfcBenchmarkFragment.this.getActivity();

        buttonClickListener = new MyStartButtonClickListener(progressBar, results, description, benchmarkSource, activity);
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
            readerCallback = new MyReaderCallback(onMessageReceived);
        }
        activity.prepareReceiving(readerCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
