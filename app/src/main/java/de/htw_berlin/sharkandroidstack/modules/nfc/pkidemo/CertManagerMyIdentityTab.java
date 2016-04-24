package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerMyIdentityTab extends ScrollView implements CertManager.CertManagerAble {

    EditText peerName;

    ListView peerSiList;
    EditText peerSiNew;
    ImageButton peerSiAddButton;

    ListView peerAddressList;
    EditText peerAddressNew;
    ImageButton peerAddressAddButton;

    View myCertView;

    CertManager certManager;

    final TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            certManager.getIdentity().setName(s.toString());
        }
    };

    final OnClickListener addSiClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String si = peerSiNew.getText().toString();
            if (si.isEmpty()) {
                return;
            }

            try {
                certManager.getIdentity().addSI(si);
                ArrayAdapter adapter = (ArrayAdapter) peerSiList.getAdapter();
                adapter.add(si);
                adapter.notifyDataSetChanged();
                peerSiNew.setText("");
            } catch (Exception e) {
                NfcMainActivity.handleError(getContext(), e);
            }
        }
    };

    final OnClickListener addAddressClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String address = peerAddressNew.getText().toString();
            if (address.isEmpty()) {
                return;
            }

            certManager.getIdentity().addAddress(address);

            ArrayAdapter adapter = (ArrayAdapter) peerAddressList.getAdapter();
            adapter.add(address);
            adapter.notifyDataSetChanged();

            peerAddressNew.setText("");
        }
    };

    final OnClickListener createCertClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                certManager.createOrOverwriteSelfSignedCertificate();
                certManager.fillCertView(myCertView);
            } catch (SharkKBException e) {
                NfcMainActivity.handleError(getContext(), e);
            }
        }
    };

    public CertManagerMyIdentityTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View container = this.findViewById(R.id.module_pki_cert_manager_peer_info_container);
        peerName = (EditText) container.findViewById(R.id.module_pki_cert_manager_peer_name_edit);
        peerName.addTextChangedListener(onTextChangedListener);

        peerSiList = (ListView) container.findViewById(R.id.module_pki_cert_manager_peer_si_list);
        ArrayAdapter<String> sisAdapter = initAdapterForSis();
        peerSiList.setAdapter(sisAdapter);
        peerSiNew = (EditText) container.findViewById(R.id.module_pki_cert_manager_peer_si_add_new);
        peerSiAddButton = (ImageButton) container.findViewById(R.id.module_pki_cert_manager_peer_si_add_button);
        peerSiAddButton.setOnClickListener(addSiClickListener);

        peerAddressList = (ListView) container.findViewById(R.id.module_pki_cert_manager_peer_address_list);
        ArrayAdapter<String> addressAdapter = initAdapterForAddresses();
        peerAddressList.setAdapter(addressAdapter);
        peerAddressNew = (EditText) container.findViewById(R.id.module_pki_cert_manager_peer_address_add_new);
        peerAddressAddButton = (ImageButton) container.findViewById(R.id.module_pki_cert_manager_peer_address_add_button);
        peerAddressAddButton.setOnClickListener(addAddressClickListener);

        final View createCertButton = container.findViewById(R.id.module_pki_cert_manager_create_cert_button);
        createCertButton.setOnClickListener(createCertClickListener);

        myCertView = container.findViewById(R.id.module_pki_cert_list_entry);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        update();
    }

    public void update() {
        PeerSemanticTag identity = certManager.getIdentity();
        peerName.setText(identity.getName());

        ArrayAdapter addressAdapter = (ArrayAdapter) peerAddressList.getAdapter();
        addressAdapter.addAll(identity.getAddresses());
        addressAdapter.notifyDataSetChanged();

        ArrayAdapter siAdapter = (ArrayAdapter) peerSiList.getAdapter();
        siAdapter.addAll(identity.getSI());
        siAdapter.notifyDataSetChanged();

        certManager.fillCertView(myCertView);
    }

    private ArrayAdapter<String> initAdapterForSis() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_with_delete, android.R.id.text1) {
            final OnClickListener deleteItemClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) v.getTag();
                    try {
                        certManager.getIdentity().removeSI(text);
                        remove(text);
                        notifyDataSetChanged();
                    } catch (SharkKBException e) {
                        NfcMainActivity.handleError(getContext(), e);
                    }
                }
            };

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(item);

                View deleteButton = view.findViewById(R.id.delete_item_button);
                deleteButton.setTag(item);
                deleteButton.setOnClickListener(deleteItemClickListener);
                return view;
            }
        };


        return adapter;
    }

    @NonNull
    private ArrayAdapter<String> initAdapterForAddresses() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_with_delete, android.R.id.text1) {
            final OnClickListener deleteItemClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) v.getTag();
                    certManager.getIdentity().removeAddress(text);

                    remove(text);
                    notifyDataSetChanged();
                }
            };

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String item = this.getItem(position);
                final View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(item);

                View deleteButton = view.findViewById(R.id.delete_item_button);
                deleteButton.setTag(item);
                deleteButton.setOnClickListener(deleteItemClickListener);
                return view;
            }
        };

        return adapter;
    }

    @Override
    public void setCertManager(CertManager certManager) {
        this.certManager = certManager;
    }
}
