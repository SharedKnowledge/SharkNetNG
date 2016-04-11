package de.htw_berlin.sharkandroidstack.modules.pki;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import de.htw_berlin.sharkandroidstack.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerFragment extends Fragment {

    final private HashMap<String, View> tabs = new HashMap<>();

    final private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String selectedName = tab.getText().toString();

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

        tabs.put("Me", root.findViewById(R.id.info));
        tabs.put("Certs", root.findViewById(R.id.certs));

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        for (String name : tabs.keySet()) {
            tabLayout.addTab(tabLayout.newTab().setText(name));
        }

        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        onTabSelectedListener.onTabSelected(tabLayout.getTabAt(0));

        hideKeyboard(root.getContext(), root);

        return root;
    }
}
