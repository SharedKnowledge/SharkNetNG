package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;

/**
 * Created by Mario Neises (mn-io) on 25.04.16
 */
public class UxFragment extends Fragment {

    public static final String DIALOG_PROGRESS_FORMAT = "%1d/%2d bytes";
    public static final String DIALOG_SENDING_PROGRESS = "Sending progress";

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
            d.setTitle(DIALOG_SENDING_PROGRESS);
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
