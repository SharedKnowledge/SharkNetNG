package de.htw_berlin.sharkandroidstack.modules.pki.system;

import android.content.Context;
import android.support.annotation.NonNull;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.key.storage.filesystem.FSSharkKeyStorage;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class SharkApiHelper {

    public static SharkKeyStorage createKeys(SharkKeyPairAlgorithm algorithm, int keySize) {
        final SharkKeyGenerator keyGenerator = new SharkKeyGenerator(algorithm, keySize);

        SharkKeyStorage sharkKeyStorage = new SharkKeyStorage();
        sharkKeyStorage.setPrivateKey(keyGenerator.getPrivateKey());
        sharkKeyStorage.setPublicKey(keyGenerator.getPublicKey());
        sharkKeyStorage.setSharkKeyPairAlgorithm(algorithm);

        return sharkKeyStorage;
    }

    public static SharkKeyStorage restoreKeysFromFile(Context context,String fileName) {
        String filePath = getStorageFilePath(context, fileName);
        return new FSSharkKeyStorage(filePath).load();
    }

    public static void saveKeysAsFile(Context context, SharkKeyStorage sharkKeyStorage, String fileName) throws IOException {
        String filePath = getStorageFilePath(context, fileName);
        new FSSharkKeyStorage(filePath).saveAndThrowExceptions(sharkKeyStorage);
    }

    @NonNull
    private static String getStorageFilePath(Context context, String name) {
        return context.getFilesDir().toString() + File.pathSeparator + name;
    }

    public static SharkCertificate createSelfSignedCertificate(PeerSemanticTag identity, PublicKey publicKey, int validForYears) {
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
}
