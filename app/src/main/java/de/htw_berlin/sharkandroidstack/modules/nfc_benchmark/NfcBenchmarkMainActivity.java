package de.htw_berlin.sharkandroidstack.modules.nfc_benchmark;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.sharkfw.system.Util;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.android.KbTextViewWriter;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.setup.SharkStack;


public class NfcBenchmarkMainActivity extends ParentActivity {

    static SharkStack sharkStack;
    KbTextViewWriter kbTextViewWriter;

//    EditText inputEditText;
//    Button sendButton;

    TextView outputHeader;
    boolean isShowingLogOutput = false;
    View.OnClickListener toggleClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            isShowingLogOutput = !isShowingLogOutput;

            Button button = (Button) view;

            if (!isShowingLogOutput) {
                button.setText("show Log");
                outputHeader.setText("KB");
                kbTextViewWriter.showKbText();
            } else {
                button.setText("show KB");
                outputHeader.setText("Log:");
                kbTextViewWriter.showLogText();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResource(R.layout.module_nfc_benchmark_activity);

        TextView inputHeader = (TextView) findViewById(R.id.inputHeader);
        inputHeader.setText("Your Name: " + Utils.deviceId + ", Input:");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (outputHeader == null) {
            outputHeader = (TextView) findViewById(R.id.outputHeader);
//            inputEditText= (EditText) findViewById(R.id.inputEditText);
//            sendButton= (Button) findViewById(R.id.send);
//            sendButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String input = inputEditText.getText().toString();
//                    //TODO: connect to Basic chat module?
//                    //TODO: wrap as Interest and add to KB, sync KB, ....
//                }
//            });
        }

        if (sharkStack == null) {
            kbTextViewWriter = KbTextViewWriter.getInstance();

            sharkStack = new SharkStack(this, Utils.deviceId).setTextViewWriter(kbTextViewWriter).start();

            kbTextViewWriter.setOutputTextView((TextView) findViewById(R.id.outputTextView));
            View toggleLogView = findViewById(R.id.toogleLog);
            toggleLogView.setOnClickListener(toggleClickListener);
            toggleClickListener.onClick(toggleLogView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sharkStack != null) {
            sharkStack.stop();
            sharkStack = null;
        }
    }


}
