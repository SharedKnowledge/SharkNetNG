package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.key.storage.filesystem.FSSharkKeyStorage;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.system.SharkException;
import net.sharksystem.android.peer.AndroidSharkEngine;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import de.htw_berlin.sharkandroidstack.R;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

/**
 * Created by m on 4/24/16.
 */
public class CertManager {

    public static final String FILE_NAME_KEY_STORE = "sharkKeyStorage";
    private final PeerSemanticTag identity;

    private final AndroidSharkEngine engine;

    private SharkKeyStorage sharkKeyStorage;

    private ArrayList<SharkCertificate> certificates = new ArrayList<>();
    private SharkCertificate myCertificate;
    //    private final SharkPkiKP kp;
    //    private final SharkPkiStorage store;
    //    private final ContextCoordinates topic;

    final static OnClickListener headerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parent = (View) v.getParent();
            View content = parent.findViewById(R.id.module_pki_cert_list_entry_content);
            int visibility = content.getVisibility() == GONE ? VISIBLE : GONE;
            content.setVisibility(visibility);
        }
    };

    public CertManager(Activity activity, PeerSemanticTag identityPeer) throws SharkKBException, NoSuchAlgorithmException, IOException {
        Context applicationContext = activity.getApplicationContext();

        identity = identityPeer;
        engine = new AndroidSharkEngine(activity);

        sharkKeyStorage = restoreKeysFromFile(applicationContext, FILE_NAME_KEY_STORE);
        if (sharkKeyStorage == null) {
            sharkKeyStorage = createKeys(SharkKeyPairAlgorithm.RSA, 1024);
            saveKeysAsFile(applicationContext, sharkKeyStorage, FILE_NAME_KEY_STORE);
        }

//        topic = InMemoSharkKB.createInMemoContextCoordinates(
//                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
//                identity, null, null, null, null,
//                SharkCS.DIRECTION_INOUT);
//        final InMemoSharkKB kb = new InMemoSharkKB(); //new FSSharkKB(applicationContext.getFilesDir().toString());
//        store = new SharkPkiStorage(kb, identity, sharkKeyStorage.getPrivateKey());
//        kp = new SharkPkiKP(engine, store, Certificate.TrustLevel.FULL, null);
    }

    public SharkCertificate createOrOverwriteSelfSignedCertificate() throws SharkKBException {
        // TODO: java.lang.NullPointerException: Attempt to invoke interface method 'java.util.Enumeration net.sharkfw.knowledgeBase.Knowledge.contextPoints()' on a null object reference
        //   at net.sharkfw.security.pki.storage.SharkPkiStorage.getSharkCertificate(SharkPkiStorage.java:246)

//        SharkCertificate previousCertificate = store.getSharkCertificate(identity);
//        if (previousCertificate != null) {
//            store.deleteSharkCertificate(previousCertificate);
//        }

        certificates.remove(myCertificate);
        SharkCertificate certificate = createNewSelfSignedCertificate(identity, sharkKeyStorage.getPublicKey(), 10);
        myCertificate = certificate;
        certificates.add(certificate);

//        store.addSharkCertificate(certificate);

        return certificate;
    }

    public PeerSemanticTag getIdentity() {
        return identity;
    }

    public ArrayList<SharkCertificate> getCertificates() throws SharkKBException {
//        HashSet<SharkCertificate> set = store.getSharkCertificateList();
        return certificates;
    }

    private static SharkCertificate createNewSelfSignedCertificate(PeerSemanticTag identity, PublicKey publicKey, int validForYears) {
        final LinkedList<PeerSemanticTag> peerList = new LinkedList<>();
        peerList.addFirst(identity);
        Date date = getDate(validForYears);
        return new SharkCertificate(identity, identity, peerList, Certificate.TrustLevel.FULL, publicKey, date);
    }


    private static Date getDate(int yearsInFuture) throws IllegalArgumentException {
        if (yearsInFuture <= 0) {
            throw new IllegalArgumentException("Has to be a positive number");
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, yearsInFuture);
        return cal.getTime();
    }

    private static SharkKeyStorage createKeys(SharkKeyPairAlgorithm algorithm, int keySize) {
        final SharkKeyGenerator keyGenerator = new SharkKeyGenerator(algorithm, keySize);

        SharkKeyStorage sharkKeyStorage = new SharkKeyStorage();
        sharkKeyStorage.setPrivateKey(keyGenerator.getPrivateKey());
        sharkKeyStorage.setPublicKey(keyGenerator.getPublicKey());
        sharkKeyStorage.setSharkKeyPairAlgorithm(algorithm);

        return sharkKeyStorage;
    }

    private static SharkKeyStorage restoreKeysFromFile(Context context, String fileName) {
        String filePath = getStorageFilePath(context, fileName);
        return new FSSharkKeyStorage(filePath).load();
    }

    private static void saveKeysAsFile(Context context, SharkKeyStorage sharkKeyStorage, String fileName) throws IOException {
        String filePath = getStorageFilePath(context, fileName);
        final boolean save = new FSSharkKeyStorage(filePath).save(sharkKeyStorage);
        if (!save) {
            throw new IOException("Could not save keys");
        }
    }

    @NonNull
    private static String getStorageFilePath(Context context, String name) {
        return context.getFilesDir().toString() + File.pathSeparator + name;
    }

    public void fillCertView(View myCertView) {
        if (myCertificate == null) {
            myCertView.setVisibility(GONE);
        } else {
            fillCertView(myCertView, myCertificate);
            myCertView.setVisibility(VISIBLE);
        }
    }

    public void fillCertView(View view, SharkCertificate cert) {
        final TextView header = (TextView) view.findViewById(android.R.id.text1);
        header.setOnClickListener(headerClickListener);
        if (getIdentity().equals(cert.getIssuer())) {
            header.setTextColor(view.getResources().getColor(android.R.color.holo_blue_dark));
            header.setText(String.format("(me) Issuer Name: %s", cert.getIssuer().getName()));
        } else {
            header.setTextColor(view.getResources().getColor(android.R.color.black));
            header.setText(String.format("Issuer Name: %s", cert.getIssuer().getName()));
        }

        final View content = view.findViewById(R.id.module_pki_cert_list_entry_content);
        content.setVisibility(GONE);

        final TextView issuer = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_issuer);
        issuer.setText(cert.getIssuer().toString());

        final TextView subject = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_subject);
        subject.setText(cert.getSubject().toString());

        final TextView subjectPK = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_subject_pk);
        subjectPK.setText(cert.getSubjectPublicKey().toString());

        final TextView trustLevel = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_trust_level);
        trustLevel.setText(cert.getTrustLevel().toString());

        final TextView validity = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_validity);
        validity.setText(cert.getValidity().toString());

        final TextView fingerprint = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_fingerprint);
        String itemFingerprint;
        try {
            itemFingerprint = Arrays.toString(cert.getFingerprint());
        } catch (SharkException e) {
            itemFingerprint = "N/A, error: " + e.toString();
        }
        fingerprint.setText(itemFingerprint);

        StringBuilder itemTransmitters = new StringBuilder();
        for (PeerSemanticTag transmitter : cert.getTransmitterList()) {
            itemTransmitters.append(transmitter).append(", ");
        }
        itemTransmitters.deleteCharAt(itemTransmitters.length() - 1);
        itemTransmitters.deleteCharAt(itemTransmitters.length() - 1);

        final TextView transmitters = (TextView) content.findViewById(R.id.module_pki_cert_list_entry_transmitters);
        transmitters.setText(itemTransmitters);
    }

    public interface CertManagerAble {

        void setCertManager(CertManager certManager);
    }
}
