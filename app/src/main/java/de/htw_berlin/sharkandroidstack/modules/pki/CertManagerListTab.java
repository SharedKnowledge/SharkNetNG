package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by m on 4/6/16.
 */
public class CertManagerListTab extends RelativeLayout {
    public CertManagerListTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ListView certList = (ListView) this.findViewById(R.id.module_pki_cert_mananager_cert_list);
        Button createCertButton = (Button) this.findViewById(R.id.module_pki_cert_mananager_create_cert);
        createCertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show();
            }
        });
    }
}
