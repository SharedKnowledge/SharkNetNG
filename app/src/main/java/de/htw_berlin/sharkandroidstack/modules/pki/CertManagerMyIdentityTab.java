package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.sharkfw.knowledgeBase.SharkKBException;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerMyIdentityTab extends ScrollView {

    EditText peerName;

    ListView peerSiList;
    EditText peerSiNew;
    ImageButton peerSiAddButton;

    ListView peerAddressList;
    EditText peerAddressNew;
    ImageButton peerAddressAddButton;

    final OnClickListener addSiClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String si = peerSiNew.getText().toString();
            if (si.isEmpty()) {
                return;
            }

            try {
                PkiMainActivity.myIdentity.addSI(si);
                ArrayAdapter adapter = (ArrayAdapter) peerSiList.getAdapter();
                adapter.add(si);
                adapter.notifyDataSetChanged();
                peerSiNew.setText("");
            } catch (SharkKBException e) {
                PkiMainActivity.handleError(getContext(), e);
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

            PkiMainActivity.myIdentity.addAddress(address);

            ArrayAdapter adapter = (ArrayAdapter) peerAddressList.getAdapter();
            adapter.add(address);
            adapter.notifyDataSetChanged();

            peerAddressNew.setText("");
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

        peerSiList = (ListView) container.findViewById(R.id.module_pki_cert_manager_peer_si_list);
        peerSiNew = (EditText) container.findViewById(R.id.module_pki_cert_manager_peer_si_add_new);
        peerSiAddButton = (ImageButton) container.findViewById(R.id.module_pki_cert_manager_peer_si_add_button);

        peerAddressList = (ListView) container.findViewById(R.id.module_pki_cert_manager_peer_address_list);
        peerAddressNew = (EditText) container.findViewById(R.id.module_pki_cert_manager_peer_address_add_new);
        peerAddressAddButton = (ImageButton) container.findViewById(R.id.module_pki_cert_manager_peer_address_add_button);

        peerName.setText(PkiMainActivity.myIdentity.getName());

        ArrayAdapter<String> sisAdapter = initAdapterForSis();
        peerSiList.setAdapter(sisAdapter);
        peerSiAddButton.setOnClickListener(addSiClickListener);

        ArrayAdapter<String> addressAdapter = initAdapterForAddresses();
        peerAddressList.setAdapter(addressAdapter);
        peerAddressAddButton.setOnClickListener(addAddressClickListener);
    }

    private ArrayAdapter<String> initAdapterForSis() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_with_delete, android.R.id.text1) {
            final OnClickListener deleteItemClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) v.getTag();
                    try {
                        PkiMainActivity.myIdentity.removeSI(text);
                        remove(text);
                        notifyDataSetChanged();
                    } catch (SharkKBException e) {
                        PkiMainActivity.handleError(getContext(), e);
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

        adapter.addAll(PkiMainActivity.myIdentity.getSI());
        adapter.notifyDataSetChanged();
        return adapter;
    }

    @NonNull
    private ArrayAdapter<String> initAdapterForAddresses() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_with_delete, android.R.id.text1) {
            final OnClickListener deleteItemClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) v.getTag();
                    PkiMainActivity.myIdentity.removeAddress(text);

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

        adapter.addAll(PkiMainActivity.myIdentity.getAddresses());
        adapter.notifyDataSetChanged();
        return adapter;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == VISIBLE) {
//            StringBuilder builder = new StringBuilder();
//            view.setText(builder);
        }
    }
}
