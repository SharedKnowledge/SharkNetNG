package de.htw_berlin.sharkandroidstack.modules.pki;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by m on 4/6/16.
 */
public class CertManagerFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_pki_cert_manager_fragment, container, false);

        ListView certList = (ListView) root.findViewById(R.id.module_pki_cert_mananager_cert_list);
        Button createCertButton = (Button) root.findViewById(R.id.module_pki_cert_mananager_create_cert);
        createCertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show();
            }
        });


        return root;
    }
}
