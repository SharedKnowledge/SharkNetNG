package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.support.annotation.NonNull;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.system.L;

import java.io.InputStream;
import java.util.ArrayList;

import de.htw_berlin.sharkandroidstack.modules.nfc.RawKp;

/**
 * Created by Mario Neises (mn-io) on 25.04.16
 */
public class CertificateRawKp extends RawKp {

    private final CertManager certManager;

    public CertificateRawKp(SharkEngine se, CertManager certManager) {
        super(se);
        this.certManager = certManager;
    }

    @Override
    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
        ASIPInMessage inMessage = (ASIPInMessage) asipConnection;
        InputStream is2 = inMessage.getRaw();

        try {
            byte[] buffer = new byte[is2.available()];
            is2.read(buffer);
            ArrayList<SharkCertificate> certificates = deserializeBytes(buffer);
            certManager.updateCertificates(certificates);
        } catch (Exception e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }

        super.handleRaw(is, asipConnection);
    }

    @NonNull
    static ArrayList<SharkCertificate> deserializeBytes(byte[] buffer) {
        byte[][] receivedData;
        try {
            receivedData = deserializeAsBytes(buffer);
        } catch (Exception e) {
            L.d(e.getMessage());
            e.printStackTrace();
            return null;
        }

        ArrayList<SharkCertificate> certificates = new ArrayList<>();
        for (byte[] d : receivedData) {
            try {
                SharkCertificate deserialize = SharkCertificate.deserialize(d);
                certificates.add(deserialize);
            } catch (Exception e) {
                L.d(e.getMessage());
                e.printStackTrace();
            }
        }
        return certificates;
    }
}
