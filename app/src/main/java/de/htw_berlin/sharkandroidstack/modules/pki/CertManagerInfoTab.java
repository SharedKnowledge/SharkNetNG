package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManagerInfoTab extends RelativeLayout {
    private TextView view;

    public CertManagerInfoTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        view = (TextView) this.findViewById(R.id.module_pki_cert_mananager_info_text);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == VISIBLE) {
            StringBuilder builder = new StringBuilder();
            for (String info : PkiMainActivity.certManager.getTempInfos()) {
                builder.append(info + "\n\n");
            }
            view.setText(builder);
        }
    }
}
