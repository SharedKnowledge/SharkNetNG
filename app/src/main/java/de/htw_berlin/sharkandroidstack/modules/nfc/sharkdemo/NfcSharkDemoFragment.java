package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.KnowledgeBaseManager;

public class NfcSharkDemoFragment extends Fragment {

    private SharkKB kb;

    //TODO: shark log > android LogManager...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

        final TextView ownerInformation = (TextView) root.findViewById(R.id.activity_nfc_sharkdemo_owner_id);
        ownerInformation.setText("Your device id, used as KB owner: " + AndroidUtils.deviceId + "\n\nAdd Context information below:");

        try {
            kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeDummy, false, AndroidUtils.deviceId);
            kb.addListener(new KnowledgeBaseListenerAdapterImpl());
        } catch (SharkKBException e) {
            LogManager.addEntry(NfcMainActivity.LOG_ID, e.getCause(), 3);
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
