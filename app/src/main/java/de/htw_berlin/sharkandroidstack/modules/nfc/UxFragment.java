package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by m on 4/25/16.
 */
public class UxFragment extends Fragment {

    public static final String DIALOG_PROGRESS_FORMAT = "%1d/%2d bytes";
    protected ProgressDialog progressDialog;
    protected ProgressAndVibrateUxHandler uxHandler;

    final DialogInterface.OnCancelListener onProgressDialogCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            uxHandler.forceStop();
        }
    };

    ProgressDialog getProgressDialogInstance() {
        if (progressDialog == null) {
            final ProgressDialog d = new ProgressDialog(this.getActivity());
            d.setTitle(R.string.activity_nfc_sending_dialog);
            d.setIndeterminate(false);
            d.setCancelable(true);
            d.setProgress(0);
            d.setProgressNumberFormat(DIALOG_PROGRESS_FORMAT);
            d.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            d.setOnCancelListener(onProgressDialogCancelListener);
            progressDialog = d;
        }

        return progressDialog;
    }
}
