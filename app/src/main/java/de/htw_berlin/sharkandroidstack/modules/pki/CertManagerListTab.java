package de.htw_berlin.sharkandroidstack.modules.pki;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
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

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.SharkException;

import java.util.ArrayList;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerListTab extends RelativeLayout {

    ArrayAdapter<SharkCertificate> adapter;
    Vibrator vibrator;

    final static OnClickListener headerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parent = (View) v.getParent();
            View content = parent.findViewById(R.id.module_pki_cert_list_entry_content);
            int visibility = content.getVisibility() == GONE ? VISIBLE : GONE;
            content.setVisibility(visibility);
        }
    };

    final static OnClickListener shareClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Click", Toast.LENGTH_LONG).show();
            PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("112663172666e296", "112663172666e296_Id", "tcp://112663172666e296");

            try {
                PkiMainActivity.certManager.send(bob);
                String text = "Done.";
                Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                PkiMainActivity.handleError(v.getContext(), e);
            }
        }
    };

    final OnClickListener createClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                SharkCertificate certificate = PkiMainActivity.certManager.createSelfSignedCertificate();
                String text = "Cert created with fingerprint: " + Arrays.toString(certificate.getFingerprint());
                update();
                Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                PkiMainActivity.handleError(v.getContext(), e);
            }

        }
    };

    final KPListener kpListener = new KPListener() {
        @Override
        public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
//            Toast.makeText(getContext(), "expose sent", Toast.LENGTH_SHORT).show();
//            System.out.println("mario: es " + sentMutualInterest);
        }

        @Override
        public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
//            Toast.makeText(getContext(), "insert sent", Toast.LENGTH_SHORT).show();
//            System.out.println("mario: is " + sentKnowledge);
        }

        @Override
        public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
            if (!newCP.getContextCoordinates().getTopic().identical(SharkPkiStorage.PKI_CONTEXT_COORDINATE)) {
                return;
            }

            update();
            vibrator.vibrate(500);
            String text = "Certificate(s) received.";
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
        }
    };

    public CertManagerListTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        vibrator = ((Vibrator) getContext().getSystemService(Activity.VIBRATOR_SERVICE));

        ListView certList = (ListView) this.findViewById(R.id.module_pki_cert_mananager_cert_list);
        adapter = initAdapter();
        certList.setAdapter(adapter);

        Button shareCertButton = (Button) this.findViewById(R.id.module_pki_cert_mananager_share_cert);
        shareCertButton.setOnClickListener(shareClickListener);

        Button createCertButton = (Button) this.findViewById(R.id.module_pki_cert_mananager_create_cert);
        createCertButton.setOnClickListener(createClickListener);
    }

    @Override
    protected void onAttachedToWindow() {
        update();
        PkiMainActivity.certManager.addKPListener(kpListener);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        PkiMainActivity.certManager.removeKPListener(kpListener);
        super.onDetachedFromWindow();
    }

    void update() {
        try {
            final ArrayList<SharkCertificate> certificates = PkiMainActivity.certManager.getCertificates();
            adapter.clear();
            adapter.addAll(certificates);
            adapter.notifyDataSetChanged();
        } catch (SharkKBException e) {
            PkiMainActivity.handleError(getContext(), e);
        }
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
