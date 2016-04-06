package de.htw_berlin.sharkandroidstack.modules.pki;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

import de.htw_berlin.sharkandroidstack.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by m on 4/6/16.
 */
public class CertManagerFragment extends Fragment {

    final private HashMap<String, View> tabs = new HashMap<>();

    final private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String selectedName = tab.getText().toString();

            hideKeyboard();

            for (String name : tabs.keySet()) {
                int visibility = GONE;
                if (name.equals(selectedName)) {
                    visibility = VISIBLE;
                }

                tabs.get(name).setVisibility(visibility);
            }
        }

        private void hideKeyboard() {
            View view = getView();
            if (view != null && view.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

        tabs.put("Info", root.findViewById(R.id.info));
        tabs.put("Certs", root.findViewById(R.id.certs));

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        for (String name : tabs.keySet()) {
            tabLayout.addTab(tabLayout.newTab().setText(name));
        }

        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        onTabSelectedListener.onTabSelected(tabLayout.getTabAt(0));

        return root;
    }
}
