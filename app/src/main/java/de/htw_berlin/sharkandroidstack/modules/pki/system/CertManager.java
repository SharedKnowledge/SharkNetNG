package de.htw_berlin.sharkandroidstack.modules.pki.system;

import android.app.Activity;
import android.content.Context;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.SharkPkiKP;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;

/**
 * Created by mn-io on 06.04.16.
 */
public class CertManager {

    public static final String FILE_NAME_KEY_STORE = "sharkKeyStorage";
    private final SharkPkiStorage store;
    private final ContextCoordinates topic;
    private final AndroidSharkEngine engine;
    private final SharkPkiKP kp;

    private SharkKeyStorage sharkKeyStorage;
    private final PeerSemanticTag identity;

    public CertManager(Activity activity, PeerSemanticTag identityPeer) throws SharkKBException, NoSuchAlgorithmException, IOException, SharkProtocolNotSupportedException {
        Context applicationContext = activity.getApplicationContext();

        identity = identityPeer;
        engine = new AndroidSharkEngine(activity);

        topic = InMemoSharkKB.createInMemoContextCoordinates(
                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                identity, null, null, null, null,
                SharkCS.DIRECTION_INOUT);

        final InMemoSharkKB kb = new InMemoSharkKB(); //new FSSharkKB(applicationContext.getFilesDir().toString());

        sharkKeyStorage = SharkApiHelper.restoreKeysFromFile(applicationContext, FILE_NAME_KEY_STORE);
        if (sharkKeyStorage == null) {
            sharkKeyStorage = SharkApiHelper.createKeys(SharkKeyPairAlgorithm.RSA, 1024);
            SharkApiHelper.saveKeysAsFile(applicationContext, sharkKeyStorage, FILE_NAME_KEY_STORE);
        }

        store = new SharkPkiStorage(kb, identity, sharkKeyStorage.getPrivateKey());
        kp = new SharkPkiKP(engine, store, Certificate.TrustLevel.FULL, null);

        engine.stopNfc();
    }

    public SharkCertificate createSelfSignedCertificate() throws SharkKBException {
        SharkCertificate previousCertificate = store.getSharkCertificate(identity);
        if (previousCertificate != null) {
            store.deleteSharkCertificate(previousCertificate);
        }

        SharkCertificate certificate = SharkApiHelper.createSelfSignedCertificate(identity, sharkKeyStorage.getPublicKey(), 10);
        store.addSharkCertificate(certificate);
        return certificate;
    }

    public void addKPListener(KPListener kpListener) {
        kp.addListener(kpListener);
    }

    public void removeKPListener(KPListener kpListener) {
        kp.removeListener(kpListener);
    }

    public void addKBListener(KnowledgeBaseListener listener) {
        store.getSharkPkiStorageKB().addListener(listener);
    }

    public void removeKBListener(KnowledgeBaseListener listener) {
        store.getSharkPkiStorageKB().removeListener(listener);
    }

    public void send(PeerSemanticTag to) throws SharkProtocolNotSupportedException, IOException, SharkKBException, SharkSecurityException {
        Knowledge knowledge = SharkCSAlgebra.extract(store.getSharkPkiStorageKB(), topic);

        engine.startNfc();
        engine.sendKnowledge(knowledge, to, kp);
//        engine.stopNfc();
    }

    public ArrayList<SharkCertificate> getCertificates() throws SharkKBException {
        HashSet<SharkCertificate> set = store.getSharkCertificateList();
        if (set == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(set);
    }

}
