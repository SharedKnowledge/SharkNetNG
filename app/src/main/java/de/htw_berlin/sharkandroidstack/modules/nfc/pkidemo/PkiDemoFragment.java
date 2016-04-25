package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.util.HashMap;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.UxFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard;

/**
 * Created by Mario Neises (mn-io) on 22.01.16
 */
public class PkiDemoFragment extends UxFragment {

    private final static PeerSemanticTag defaultIdentity = InMemoSharkKB.createInMemoPeerSemanticTag(AndroidUtils.deviceId, AndroidUtils.deviceId + "_Id", "tcp://" + AndroidUtils.deviceId);
    private final HashMap<String, View> tabs = new HashMap<>();

    CertManager certManager;

    final private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String selectedName = tab.getText().toString();

            certManager.stopSharing();

            View view = getView();
            if (view != null) {
                hideKeyboard(view.getContext(), view);
            }

            for (String name : tabs.keySet()) {
                int visibility = GONE;
                if (name.equals(selectedName)) {
                    visibility = VISIBLE;
                }

                tabs.get(name).setVisibility(visibility);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_pki_cert_manager_fragment, container, false);

        uxHandler = new PkiDemoUxHandler(this);

        createCertManagerIfNeeded();
        initTabs(root);

        return root;
    }

    private void createCertManagerIfNeeded() {
        if (certManager == null) {
            final Activity activity = this.getActivity();
            try {
                certManager = new CertManager(activity, defaultIdentity, (PkiDemoUxHandler) uxHandler);
            } catch (Exception e) {
                NfcMainActivity.handleError(activity, e);
            }
        }
    }

    private void initTabs(View root) {
        tabs.put("Me", root.findViewById(R.id.info));
        tabs.put("Certs", root.findViewById(R.id.certs));

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        for (View tab : tabs.values()) {
            if (tab instanceof CertManager.CertManagerAble) {
                ((CertManager.CertManagerAble) tab).setCertManager(certManager);
            }
        }

        for (String name : tabs.keySet()) {
            tabLayout.addTab(tabLayout.newTab().setText(name));
        }

        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        onTabSelectedListener.onTabSelected(tabLayout.getTabAt(0));
    }
}
