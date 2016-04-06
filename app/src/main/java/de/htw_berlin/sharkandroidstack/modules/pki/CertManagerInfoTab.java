package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by m on 4/6/16.
 */
public class CertManagerInfoTab extends RelativeLayout {
    public CertManagerInfoTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.findViewById(R.id.module_pki_cert_mananager_info_text);

    }
}
