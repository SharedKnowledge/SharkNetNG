package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.IsoDepTransceiver;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;

import static android.R.drawable.ic_media_pause;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcBenchmarkFragment extends Fragment {

    NfcMainActivity activity;
    Button startButton;
    ProgressBar progressBar;
    ListView results;

    final View.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView view = (TextView) v;
            view.setText(R.string.activity_nfc_benchmark_stop);
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, ic_media_pause, 0);

            activity.prepareSending();
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

    final NfcAdapter.ReaderCallback readerCallback = new NfcAdapter.ReaderCallback() {
        @Override
        public void onTagDiscovered(Tag tag) {
            IsoDep isoDep = IsoDep.get(tag);
            if (isoDep == null) {
                return;
            }

            System.out.println("Mario: Tag discovered " + tag);
//            NfcBenchmarkFragment.this.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    outputStringBuilder = new StringBuilder();
//                    output.setText(outputStringBuilder.toString());
//                }
//            });

            IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, onMessageReceived);
            Thread thread = new Thread(transceiver);
            thread.start();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.module_nfc_benchmark_activity, container, false);
        startButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);
        startButton.setOnClickListener(startClickListener);

        progressBar = (ProgressBar) root.findViewById(R.id.activity_nfc_benchmark_progress);
        results = (ListView) root.findViewById(R.id.activity_nfc_benchmark_results);

        activity = (NfcMainActivity) NfcBenchmarkFragment.this.getActivity();

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.prepareReceiving(readerCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
