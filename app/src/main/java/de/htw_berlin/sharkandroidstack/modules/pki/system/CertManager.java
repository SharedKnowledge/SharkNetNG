package de.htw_berlin.sharkandroidstack.modules.pki.system;

import android.app.Activity;
import android.content.Context;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.KnowledgePort;
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

    // temp, only for getTempInfos() so far..
    private SharkKeyStorage sharkKeyStorage;
    private PeerSemanticTag identity;
    private final SharkCertificate certificate;
    // ..end

    public static final KPListener kpListener = new KPListener() {
        @Override
        public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
            System.out.println("mario: expose sent " + sentMutualInterest);
        }

        @Override
        public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
            System.out.println("mario: insert sent " + sentKnowledge);

        }

        @Override
        public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
            System.out.println("mario: knowledge assimilated " + newCP);

        }
    };

    public CertManager(Activity activity, PeerSemanticTag identity) throws SharkKBException, NoSuchAlgorithmException, IOException, SharkProtocolNotSupportedException {
        Context applicationContext = activity.getApplicationContext();

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

        topic = InMemoSharkKB.createInMemoContextCoordinates(
                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                identity, null, null, null, null,
                SharkCS.DIRECTION_INOUT);

        engine = new AndroidSharkEngine(activity);
        kp = new SharkPkiKP(engine, store, Certificate.TrustLevel.FULL, null);
        kp.addListener(kpListener);

        engine.stopNfc();
    }


    public void send(PeerSemanticTag to) throws SharkProtocolNotSupportedException, IOException, SharkKBException, SharkSecurityException {
        Knowledge knowledge = SharkCSAlgebra.extract(store.getSharkPkiStorageKB(), topic);

        engine.startNfc();
        engine.sendKnowledge(knowledge, to, kp);
        engine.stopNfc();
    }

    public String[] getTempInfos() {
        String[] strings = new String[4];

        strings[0] = String.format("My Identity: %s", identity);
        strings[1] = String.format("Keys: \nPublic:%s, \nPrivate: %s", sharkKeyStorage.getPrivateKey(), sharkKeyStorage.getPrivateKey());
        strings[2] = String.format("Certificate: %s", certificate);

        try {
            strings[3] = String.valueOf(getCertificates().size());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }

        return strings;
    }

    public ArrayList<SharkCertificate> getCertificates() throws SharkKBException {
        HashSet<SharkCertificate> set = store.getSharkCertificateList();
        return new ArrayList<>(set);
    }

}
