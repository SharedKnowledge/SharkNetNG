package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
    private KnowledgeBaseListenerAdapterImpl knowledgeBaseListener;
    private MyKbListAdapter kbListAdapter;

    //TODO: shark log > android LogManager...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.module_nfc_sharkdemo_fragment, container, false);

        final TextView ownerInformation = (TextView) root.findViewById(R.id.activity_nfc_sharkdemo_owner_id);
        ownerInformation.setText("Your device id, used as KB owner: " + AndroidUtils.deviceId + "\n\nAdd Context information below:");


        kbListAdapter = new MyKbListAdapter(this.getActivity());
        knowledgeBaseListener = new KnowledgeBaseListenerAdapterImpl(kbListAdapter);

        try {
            kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeDummy, false, AndroidUtils.deviceId);
            kb.addListener(knowledgeBaseListener);
            final ListView kbList = (ListView) root.findViewById(R.id.activity_nfc_sharkdemo_kb_list);
            kbList.setAdapter(kbListAdapter);

//            final PeerSemanticTag ownerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("asdcId", "adcasdc", "tcp://localhost:5555");
//            final SemanticTag tag1 = kb.createSemanticTag("test", "testSi");
//            final ContextCoordinates contextCoordinates1 = kb.createContextCoordinates(tag1, ownerSemanticTag, null, null, null, null, SharkCS.DIRECTION_INOUT);
//            kb.createContextPoint(contextCoordinates1).addInformation(UUID.randomUUID().toString());
        } catch (SharkKBException e) {
            LogManager.addThrowable(NfcMainActivity.LOG_ID, e);
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
