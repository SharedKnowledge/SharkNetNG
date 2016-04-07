package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.system.SharkException;

import java.util.ArrayList;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerListTab extends RelativeLayout {

    final static OnClickListener headerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parent = (View) v.getParent();
            View content = parent.findViewById(R.id.module_pki_cert_list_entry_content);
            int visibility = content.getVisibility() == GONE ? VISIBLE : GONE;
            content.setVisibility(visibility);
        }
    };

    public CertManagerListTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ListView certList = (ListView) this.findViewById(R.id.module_pki_cert_mananager_cert_list);

        try {
            final ArrayList<SharkCertificate> certificates = PkiMainActivity.certManager.getCertificates();
            final ArrayAdapter<SharkCertificate> adapter = initAdapter();
            certList.setAdapter(adapter);
            adapter.addAll(certificates);
            adapter.notifyDataSetChanged();

        } catch (SharkKBException e) {
            String text = "An error occurred: " + e.getMessage();
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            LogManager.addThrowable(PkiMainActivity.LOG_ID, e);
        }


        Button createCertButton = (Button) this.findViewById(R.id.module_pki_cert_mananager_create_cert);
        createCertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show();
                PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("112663172666e296", "112663172666e296_Id", "tcp://112663172666e296");

                String text;
                try {
                    PkiMainActivity.certManager.send(bob);
                    text = "done";
                } catch (Exception e) {
                    text = "An error occurred: " + e.getMessage();
                    LogManager.addThrowable(PkiMainActivity.LOG_ID, e);
                }

                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private ArrayAdapter<SharkCertificate> initAdapter() {
        return new ArrayAdapter<SharkCertificate>(getContext(), R.layout.module_pki_cert_list_entry, android.R.id.text1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final SharkCertificate item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);

                final TextView header = (TextView) view.findViewById(android.R.id.text1);
                header.setOnClickListener(headerClickListener);
                if (PkiMainActivity.myIdentity.equals(item.getIssuer())) {
                    header.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    header.setText(String.format("(me) Issuer Name: %s", item.getIssuer().getName()));
                } else {
                    header.setTextColor(getResources().getColor(android.R.color.black));
                    header.setText(String.format("Issuer Name: %s", item.getIssuer().getName()));
                }

                final View content = view.findViewById(R.id.module_pki_cert_list_entry_content);
                content.setVisibility(GONE);

                final TextView issuer = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_issuer);
                issuer.setText(item.getIssuer().toString());

                final TextView subject = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_subject);
                subject.setText(item.getSubject().toString());

                final TextView subjectPK = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_subject_pk);
                subjectPK.setText(item.getSubjectPublicKey().toString());

                final TextView trustLevel = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_trust_level);
                trustLevel.setText(item.getTrustLevel().toString());

                final TextView validity = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_validity);
                validity.setText(item.getValidity().toString());

                final TextView fingerprint = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_fingerprint);
                String itemFingerprint;
                try {
                    itemFingerprint = Arrays.toString(item.getFingerprint());
                } catch (SharkException e) {
                    itemFingerprint = "N/A, error: " + e.toString();
                }
                fingerprint.setText(itemFingerprint);

                StringBuilder itemTransmitters = new StringBuilder();
                for (PeerSemanticTag transmitter : item.getTransmitterList()) {
                    itemTransmitters.append(transmitter).append(", ");
                }
                itemTransmitters.deleteCharAt(itemTransmitters.length() - 1);
                itemTransmitters.deleteCharAt(itemTransmitters.length() - 1);

                final TextView transmitters = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_transmitters);
                transmitters.setText(itemTransmitters);

                return view;

            }
        };
    }
}
