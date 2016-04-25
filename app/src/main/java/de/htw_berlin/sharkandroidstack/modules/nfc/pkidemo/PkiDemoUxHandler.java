package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.os.Handler;

import java.util.ArrayList;

import de.htw_berlin.sharkandroidstack.modules.nfc.ProgressAndVibrateUxHandler;
import de.htw_berlin.sharkandroidstack.modules.nfc.UxFragment;

/**
 * Created by m on 4/25/16.
 */
public class PkiDemoUxHandler extends ProgressAndVibrateUxHandler {
    private final Handler handler = new Handler();
    private ArrayList<Runnable> updateHandlers = new ArrayList<>();

    public PkiDemoUxHandler(UxFragment fragment) {
        super(fragment);
    }

    public void addUpdateCallback(Runnable updateHandler) {
        this.updateHandlers.add(updateHandler);
    }

    public void removeUpdateCallback(Runnable updateHandler) {
        this.updateHandlers.remove(updateHandler);
    }

    public void fireCertificatesUpdateCallback() {
        for (Runnable updateHandler : updateHandlers) {
            handler.post(updateHandler);
        }
    }
}
