package de.htw_berlin.sharkandroidstack.modules.nfc2;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.OnMessageSendImpl;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.IsoDepTransceiver;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.SmartCardEmulationService;

public class NfcActivity extends ParentActivity {

    private final View.OnClickListener exitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private final View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(NfcActivity.this);
            if (nfcAdapter == null) {
                Toast.makeText(NfcActivity.this, "Send preparation failed", Toast.LENGTH_LONG).show();
            } else {
                view.setEnabled(false);
                Button button = (Button) view;
                button.setTextColor(Color.DKGRAY);
                button.setText(button.getText() + "...");
                prepareSending(nfcAdapter);
            }
        }
    };

    private TextView output;

    private Button sendButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc2_main);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (output == null) {
            output = (TextView) findViewById(R.id.outputTextView);
        }

        if (sendButton == null) {
            sendButton = (Button) findViewById(R.id.sendButton);
            sendButton.setOnClickListener(sendOnClickListener);
        }

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(NfcActivity.this, "Receive preparation failed", Toast.LENGTH_LONG).show();
        } else {
            prepareReceiving(output, nfcAdapter);
        }
    }

    StringBuilder outputStringBuilder = new StringBuilder();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareSending(NfcAdapter nfcAdapter) {
        SmartCardEmulationService.setSource(new OnMessageSendImpl(null, null, null));
        nfcAdapter.disableReaderMode(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onPause() {
        super.onPause();

        final NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) nfcAdapter.disableReaderMode(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareReceiving(final TextView output, NfcAdapter nfcAdapter) {
        final OnMessageReceived onMessageReceived = new OnMessageReceived() {
            @Override
            public void onMessage(final byte[] message) {
                System.out.println("mario: in " + new String(message));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputStringBuilder.append(new String(message));
                        output.setText(outputStringBuilder.toString());
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
                onMessage(("Finished with error: " + exception.getMessage()).getBytes());
            }

            @Override
            public void tagLost(Tag tag) {
                System.out.println("mario: tag lost");
            }

            @Override
            public void newTag(Tag tag) {
                System.out.println("mario: new tag");
            }
        };

        final NfcAdapter.ReaderCallback readerCallback = new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                System.out.println("mario new tag");
                IsoDep isoDep = IsoDep.get(tag);
                if (isoDep == null) {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputStringBuilder = new StringBuilder();
                        output.setText(outputStringBuilder.toString());
                    }
                });

                IsoDepTransceiver transceiver = new IsoDepTransceiver(tag, isoDep, onMessageReceived);
                Thread thread = new Thread(transceiver);
                thread.start();
            }
        };

        final int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        nfcAdapter.enableReaderMode(this, readerCallback, flags, null);
    }
}
