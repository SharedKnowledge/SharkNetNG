package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.key.storage.filesystem.FSSharkKeyStorage;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.SharkPkiKP;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharksystem.android.peer.AndroidSharkEngine;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by m on 4/24/16.
 */
public class CertManager {

    public static final String FILE_NAME_KEY_STORE = "sharkKeyStorage";

    private final PeerSemanticTag identity;

    private final ContextCoordinates topic;
    private final SharkPkiStorage store;
    private final AndroidSharkEngine engine;
    private final SharkPkiKP kp;
    private SharkKeyStorage sharkKeyStorage;
    private ArrayList<SharkCertificate> certificates;

    public CertManager(Activity activity, PeerSemanticTag identityPeer) throws SharkKBException, NoSuchAlgorithmException, IOException {
        Context applicationContext = activity.getApplicationContext();

        identity = identityPeer;
        engine = new AndroidSharkEngine(activity);

        topic = InMemoSharkKB.createInMemoContextCoordinates(
                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                identity, null, null, null, null,
                SharkCS.DIRECTION_INOUT);

        final InMemoSharkKB kb = new InMemoSharkKB(); //new FSSharkKB(applicationContext.getFilesDir().toString());

        sharkKeyStorage = restoreKeysFromFile(applicationContext, FILE_NAME_KEY_STORE);
        if (sharkKeyStorage == null) {
            sharkKeyStorage = createKeys(SharkKeyPairAlgorithm.RSA, 1024);
            saveKeysAsFile(applicationContext, sharkKeyStorage, FILE_NAME_KEY_STORE);
        }

        store = new SharkPkiStorage(kb, identity, sharkKeyStorage.getPrivateKey());
        kp = new SharkPkiKP(engine, store, Certificate.TrustLevel.FULL, null);
    }

    public SharkCertificate createOrOverwriteSelfSignedCertificate() throws SharkKBException {
        SharkCertificate previousCertificate = store.getSharkCertificate(identity);
        if (previousCertificate != null) {
            store.deleteSharkCertificate(previousCertificate);
        }

        SharkCertificate certificate = createNewSelfSignedCertificate(identity, sharkKeyStorage.getPublicKey(), 10);
        store.addSharkCertificate(certificate);
        return certificate;
    }

    public PeerSemanticTag getIdentity() {
        return identity;
    }

    public ArrayList<SharkCertificate> getCertificates() throws SharkKBException {
        HashSet<SharkCertificate> set = store.getSharkCertificateList();
        if (set == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(set);
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

    public interface CertManagerAble {
        void setCertManager(CertManager certManager);
    }
}
