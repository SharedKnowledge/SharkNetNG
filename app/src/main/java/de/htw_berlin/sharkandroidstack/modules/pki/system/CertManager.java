package de.htw_berlin.sharkandroidstack.modules.pki.system;

import android.content.Context;
import android.support.annotation.NonNull;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.key.storage.filesystem.FSSharkKeyStorage;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManager {

    public static final String FILE_NAME_KEY_STORE = "sharkKeyStorage";

    private CertManager() {
    }

    public static SharkKeyStorage restoreKeys(Context context) {
        String filePath = getStorageFilePath(context);

        return new FSSharkKeyStorage(filePath).load();
    }

    public static SharkKeyStorage createAndStoreKeys(Context context, SharkKeyPairAlgorithm algorithm, int keySize) {
        String filePath = getStorageFilePath(context);

        final SharkKeyGenerator keyGenerator = new SharkKeyGenerator(algorithm, keySize);

        SharkKeyStorage sharkKeyStorage = new SharkKeyStorage();
        sharkKeyStorage.setPrivateKey(keyGenerator.getPrivateKey());
        sharkKeyStorage.setPublicKey(keyGenerator.getPublicKey());
        sharkKeyStorage.setSharkKeyPairAlgorithm(algorithm);

        new FSSharkKeyStorage(filePath).save(sharkKeyStorage);
        return sharkKeyStorage;
    }

    @NonNull
    private static String getStorageFilePath(Context context) {
        return context.getFilesDir().toString() + File.pathSeparator + FILE_NAME_KEY_STORE;
    }

    public static SharkCertificate createSelfSignedCertificate(PeerSemanticTag identity, PublicKey publicKey, int validForYears) {
        LinkedList<PeerSemanticTag> peerList = new LinkedList<>();
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

    public static SharkPkiStorage createStore(InMemoSharkKB kb, PeerSemanticTag owner, PrivateKey privateKey) throws SharkKBException, NoSuchAlgorithmException {
        return new SharkPkiStorage(kb, owner, privateKey);
    }
}
