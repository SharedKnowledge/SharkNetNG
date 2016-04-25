package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.security.pki.SharkCertificate;

import java.util.Collection;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerListTab extends RelativeLayout implements CertManager.CertManagerAble {

    ArrayAdapter<SharkCertificate> adapter;
    CertManager certManager;

    final Runnable updateHandler = new Runnable() {
        @Override
        public void run() {
            try {
                final Collection<SharkCertificate> certificates = certManager.getCertificates();
                adapter.clear();
                adapter.addAll(certificates);
                adapter.notifyDataSetChanged();
            } catch (SharkKBException e) {
                NfcMainActivity.handleError(getContext(), e);
            }
        }
    };

    final OnClickListener shareCertsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                certManager.startSharing();
            } catch (Exception e) {
                NfcMainActivity.handleError(getContext(), e);
            }
        }
    };

    public CertManagerListTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Button shareCertsButton = (Button) findViewById(R.id.module_pki_cert_manager_share_cert);
        shareCertsButton.setOnClickListener(shareCertsClickListener);

        ListView certList = (ListView) findViewById(R.id.module_pki_cert_manager_cert_list);
        adapter = initAdapter();
        certList.setAdapter(adapter);
    }

    void update() {
        getHandler().postDelayed(updateHandler, 100);
    }

    @NonNull
    private ArrayAdapter<SharkCertificate> initAdapter() {
        return new ArrayAdapter<SharkCertificate>(getContext(), R.layout.module_pki_cert_list_entry, android.R.id.text1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final SharkCertificate item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);
                certManager.fillCertView(view, item);
                return view;
            }
        };
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE) {
            update();
        }
        super.setVisibility(visibility);
    }

    @Override
    public void setCertManager(CertManager certManager) {
        this.certManager = certManager;
        certManager.addUpdateCallback(updateHandler);
    }
}
