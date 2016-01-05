package de.htw_berlin.sharkandroidstack.modules.wifidirect;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.KbTextViewWriter;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.setup.SharkStack;

public class WifiDirectMainActivity extends ParentActivity {

    static SharkStack sharkStack;
    KbTextViewWriter kbTextViewWriter;

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

        setLayoutResource(R.layout.module_wifi_direct_activity);

        TextView deviceName = (TextView) findViewById(R.id.deviceName);
        deviceName.setText("Your Name: " + getMACAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (outputHeader == null) {
            outputHeader = (TextView) findViewById(R.id.outputHeader);
        }
        if (sharkStack == null) {
            kbTextViewWriter = KbTextViewWriter.getInstance();

            sharkStack = new SharkStack(this, getDeviceId()).setTextViewWriter(kbTextViewWriter).start();

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

    private String getMACAddress(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    private String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
