package de.htw_berlin.sharkandroidstack.modules.pki.system;

import android.content.Context;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManager {

    public static final String FILE_NAME_KEY_STORE = "sharkKeyStorage";
    private final SharkPkiStorage store;

    // temp, only for getTempInfos() so far..
    private SharkKeyStorage sharkKeyStorage;
    private PeerSemanticTag identity;
    private final SharkCertificate certificate;
    // ..end

    public CertManager(Context applicationContext, PeerSemanticTag identity) throws SharkKBException, NoSuchAlgorithmException, IOException {
        this.identity = identity;

        this.sharkKeyStorage = SharkApiHelper.restoreKeysFromFile(applicationContext, FILE_NAME_KEY_STORE);
        if (sharkKeyStorage == null) {
            sharkKeyStorage = SharkApiHelper.createKeys(SharkKeyPairAlgorithm.RSA, 1024);
            SharkApiHelper.saveKeysAsFile(applicationContext, sharkKeyStorage, FILE_NAME_KEY_STORE);
        }

        // TODO: store certs or better: serialize/restore InMemoSharkKB or SharkPkiStorage
        certificate = SharkApiHelper.createSelfSignedCertificate(identity, sharkKeyStorage.getPublicKey(), 10);

        final InMemoSharkKB kb = new InMemoSharkKB();
        store = SharkApiHelper.createStore(kb, identity, sharkKeyStorage.getPrivateKey());
        store.addSharkCertificate(certificate);
    }

    public String[] getTempInfos() {
        String[] strings = new String[3];

        strings[0] = String.format("My Identity: %s", identity);
        strings[1] = String.format("Keys: \nPublic:%s, \nPrivate: %s", sharkKeyStorage.getPrivateKey(), sharkKeyStorage.getPrivateKey());
        strings[2] = String.format("Certificate: %s", certificate);

        return strings;
    }

}
