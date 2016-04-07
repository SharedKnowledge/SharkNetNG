package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by mn-io on 06.04.16.
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
}
