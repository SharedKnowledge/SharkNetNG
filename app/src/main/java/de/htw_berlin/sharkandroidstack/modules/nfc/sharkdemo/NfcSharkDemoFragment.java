package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.SharkKBException;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys.Alice;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys.Bob;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by mn-io on 22.01.16.
 */
public class NfcSharkDemoFragment extends Fragment {

    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";

    private Alice alice;
    private Bob bob;

    AndroidSharkEngine engine;

    final TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(Tab tab) {
            String name = tab.getText().toString();

            View view = getView();
            if (view != null && view.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            switch (name) {
                case ALICE:
                    alice.show();
                    bob.hide();
                    break;
                case BOB:
                    bob.show();
                    alice.hide();
                    break;
            }
        }

        @Override
        public void onTabUnselected(Tab tab) {
        }

        @Override
        public void onTabReselected(Tab tab) {
        }
    };

    public final Runnable updater = new Runnable() {
        @Override
        public void run() {
            ((Vibrator) getActivity().getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(500);
            Toast.makeText(getActivity(), "Done.", Toast.LENGTH_LONG).show();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

        createDialogPrompt();

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);

        alice = new Alice(ALICE, this);
        bob = new Bob(BOB, this);
        alice.initView(root.findViewById(R.id.alice), updater);
        bob.initView(root.findViewById(R.id.bob), updater);
        alice.createTab(tabLayout);
        bob.createTab(tabLayout);

        engine = new AndroidSharkEngine(getActivity());
        try {
            alice.initShark(engine);
            bob.initShark(engine);
            alice.setRemotePeer(bob.getPeer());
            tabLayout.setOnTabSelectedListener(onTabSelectedListener);
            onTabSelectedListener.onTabSelected(tabLayout.getTabAt(0));

        } catch (SharkKBException | SharkProtocolNotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Exception on init: '" + e.getMessage() + "'. Check Log for details.", Toast.LENGTH_LONG).show();
            LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
        }

        return root;
    }

    private void createDialogPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.activity_nfc_sharkdemo_intro_title)
                .setMessage(R.string.activity_nfc_sharkdemo_intro_body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }
}
