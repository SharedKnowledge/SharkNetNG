package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.system.SharkException;

import java.util.ArrayList;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerListTab extends RelativeLayout implements CertManager.CertManagerAble {

    ArrayAdapter<SharkCertificate> adapter;
    CertManager certManager;

    final KnowledgeBaseListener kbListener = new KnowledgeBaseListener() {
        @Override
        public void topicAdded(SemanticTag tag) {
            System.out.println("mario: tag topicAdded");
            update();
        }

        @Override
        public void peerAdded(PeerSemanticTag tag) {
            System.out.println("mario: tag peerAdded");
            update();
        }

        @Override
        public void locationAdded(SpatialSemanticTag location) {
            System.out.println("mario: tag locationAdded");
            update();
        }

        @Override
        public void timespanAdded(TimeSemanticTag time) {
            System.out.println("mario: tag timespanAdded");
            update();
        }

        @Override
        public void topicRemoved(SemanticTag tag) {
            System.out.println("tag topicRemoved");
            update();
        }

        @Override
        public void peerRemoved(PeerSemanticTag tag) {
            System.out.println("mario: tag peerRemoved");
            update();
        }

        @Override
        public void locationRemoved(SpatialSemanticTag tag) {
            System.out.println("mario: tag locationRemoved");
            update();
        }

        @Override
        public void timespanRemoved(TimeSemanticTag tag) {
            System.out.println("mario: tag timespanRemoved");
            update();
        }

        @Override
        public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {
            System.out.println("mario: tag predicateCreated");
            update();
        }

        @Override
        public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {
            System.out.println("mario: tag predicateRemoved");
            update();
        }

        @Override
        public void tagChanged(SemanticTag tag) {
            System.out.println("mario: tag tagChanged");
            update();
        }

        @Override
        public void contextPointAdded(ContextPoint cp) {
            System.out.println("mario: tag contextPointAdded");
            update();
        }

        @Override
        public void cpChanged(ContextPoint cp) {
            System.out.println("mario: tag cpChanged");
            update();
        }

        @Override
        public void contextPointRemoved(ContextPoint cp) {
            System.out.println("mario: tag contextPointRemoved");
            update();
        }
    };

    final static OnClickListener headerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parent = (View) v.getParent();
            View content = parent.findViewById(R.id.module_pki_cert_list_entry_content);
            int visibility = content.getVisibility() == GONE ? VISIBLE : GONE;
            content.setVisibility(visibility);
        }
    };

    final Runnable updateHandler = new Runnable() {
        @Override
        public void run() {
            try {
                final ArrayList<SharkCertificate> certificates = certManager.getCertificates();
                adapter.clear();
                adapter.addAll(certificates);
                adapter.notifyDataSetChanged();
            } catch (SharkKBException e) {
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

        ListView certList = (ListView) this.findViewById(R.id.module_pki_cert_mananager_cert_list);
        adapter = initAdapter();
        certList.setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        update();
//        PkiMainActivity.certManager.addKBListener(kbListener);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
//        PkiMainActivity.certManager.removeKBListener(kbListener);
        super.onDetachedFromWindow();
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

                final TextView header = (TextView) view.findViewById(android.R.id.text1);
                header.setOnClickListener(headerClickListener);
                if (certManager.getIdentity().equals(item.getIssuer())) {
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

    @Override
    public void setCertManager(CertManager certManager) {
        this.certManager = certManager;
    }
}
