package de.htw_berlin.sharkandroidstack.modules.nfc.pkidemo;

import junit.framework.TestCase;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.pki.SharkCertificate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Mario Neises (mn-io) on 24.04.16
 */
public class CertManagerTest extends TestCase {

    private final static PeerSemanticTag defaultIdentity = InMemoSharkKB.createInMemoPeerSemanticTag("dummy", "dummyID", "tcp://dummy");

    public void testSerializingCertificatesBasic() throws Exception {
        SharkKeyStorage sharkKeyStorage = CertManager.createKeys(SharkKeyPairAlgorithm.RSA, 1024);
        SharkCertificate certificate = CertManager.createNewSelfSignedCertificate(defaultIdentity, sharkKeyStorage.getPublicKey(), 10);

        byte[] serialize = certificate.serialize();
        SharkCertificate deserialize = SharkCertificate.deserialize(serialize);
        assertTrue(Arrays.equals(certificate.getFingerprint(), deserialize.getFingerprint()));
    }

    public void testSerializingCertificates() throws Exception {
        SharkKeyStorage sharkKeyStorage = CertManager.createKeys(SharkKeyPairAlgorithm.RSA, 1024);
        SharkCertificate certificate = CertManager.createNewSelfSignedCertificate(defaultIdentity, sharkKeyStorage.getPublicKey(), 10);

        HashMap<String, SharkCertificate> map = new HashMap<>();
        map.put(certificate.getSubjectPublicKey().toString(), certificate);

        byte[] bytes = CertManager.serializeCertificates(map);

        ArrayList<SharkCertificate> sharkCertificates = CertificateRawKp.deserializeBytes(bytes);

        SharkCertificate sharkCertificate = sharkCertificates.get(0);
        byte[] fingerprint = sharkCertificate.getFingerprint();

        assertTrue(Arrays.equals(certificate.getFingerprint(), fingerprint));
    }

    public void testCertificateStoring() throws Exception {
        // same logic as in CertManager
        SharkKeyStorage sharkKeyStorage = CertManager.createKeys(SharkKeyPairAlgorithm.RSA, 1024);
        SharkCertificate certificate = CertManager.createNewSelfSignedCertificate(defaultIdentity, sharkKeyStorage.getPublicKey(), 10);

        HashMap<String, SharkCertificate> map = new HashMap<>();
        String key = certificate.getSubjectPublicKey().toString();
        map.put(key, certificate);

        SharkCertificate gotCertificate = map.get(key);

        assertTrue(gotCertificate.getSubjectPublicKey().toString().equals(certificate.getSubjectPublicKey().toString()));
    }
}
