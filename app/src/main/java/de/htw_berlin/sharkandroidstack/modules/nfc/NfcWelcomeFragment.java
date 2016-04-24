package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Fragment;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.benchmark.NfcBenchmarkFragment;
import de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo.PkiDemoFragment;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by m on 4/20/16.
 */
public class NfcWelcomeFragment extends Fragment {

    private Button enableNfcButton;
    private TextView enableNfcHint;
    private Button startBenchmarkButton;
    private TextView startBenchmarkHint;
    private Button startSharkDemoButton;
    private TextView startSharkDemoHint;
    private Button startPkiDemoButton;
    private TextView startPkiDemoHint;
    private final View.OnClickListener enableNfcClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NfcMainActivity.SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
        }
    };
    private View.OnClickListener startButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final NfcMainActivity activity = (NfcMainActivity) NfcWelcomeFragment.this.getActivity();
            switch (v.getId()) {
                case R.id.activity_nfc_benchmark_button_start:
                    activity.changeFragment(new NfcBenchmarkFragment());
                    break;
                case R.id.activity_nfc_sharkdemo_button_start:
                    activity.changeFragment(new NfcSharkDemoFragment());
                case R.id.activity_nfc_pkidemo_button_start:
                    activity.changeFragment(new PkiDemoFragment());
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_welcome_fragment, container, false);

        enableNfcButton = (Button) root.findViewById(R.id.activity_nfc_enable);
        enableNfcHint = (TextView) root.findViewById(R.id.activity_nfc_enable_hint);

        startBenchmarkButton = (Button) root.findViewById(R.id.activity_nfc_benchmark_button_start);
        startBenchmarkHint = (TextView) root.findViewById(R.id.activity_nfc_benchmark_hint);

        startSharkDemoButton = (Button) root.findViewById(R.id.activity_nfc_sharkdemo_button_start);
        startSharkDemoHint = (TextView) root.findViewById(R.id.activity_nfc_sharkdemo_hint);

        startPkiDemoButton = (Button) root.findViewById(R.id.activity_nfc_pkidemo_button_start);
        startPkiDemoHint = (TextView) root.findViewById(R.id.activity_nfc_pkidemo_hint);


        startBenchmarkButton.setOnClickListener(startButtonClickListener);
        startSharkDemoButton.setOnClickListener(startButtonClickListener);
        startPkiDemoButton.setOnClickListener(startButtonClickListener);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNfcSupport();
    }

    private void checkNfcSupport() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        String reason = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            reason = String.format(getString(R.string.activity_nfc_no_nfc_too_old), Build.VERSION.SDK_INT, Build.VERSION_CODES.KITKAT);
        } else if (nfcAdapter == null) {
            reason = getString(R.string.activity_nfc_no_nfc_no_adapter);
        }

        if (null != reason) {
            reason = String.format(getString(R.string.activity_nfc_no_nfc), reason);
            LogManager.addEntry(NfcMainActivity.LOG_ID, reason, 1);
            enableNfcHint.setText(reason);
            enableNfcHint.setVisibility(VISIBLE);
            setButtonVisibility(GONE);
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            enableNfcButton.setOnClickListener(enableNfcClickListener);
            enableNfcButton.setVisibility(VISIBLE);
            setButtonVisibility(GONE);
            return;
        }

        enableNfcButton.setVisibility(GONE);
        enableNfcHint.setVisibility(GONE);
        setButtonVisibility(VISIBLE);
    }

    private void setButtonVisibility(int visibility) {
        startBenchmarkButton.setVisibility(visibility);
        startBenchmarkHint.setVisibility(visibility);

        startSharkDemoButton.setVisibility(visibility);
        startSharkDemoHint.setVisibility(visibility);

        startPkiDemoButton.setVisibility(visibility);
        startPkiDemoHint.setVisibility(visibility);
    }
}
