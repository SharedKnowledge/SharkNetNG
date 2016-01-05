package de.htw_berlin.sharkandroidstack.setup;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class SharkSecurityStack {

    private static SharkKeyGenerator keyGenerator = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, 1024);

    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) {
        return InMemoSharkKB.createInMemoPeerSemanticTag(name, si, address);
    }

    public static Date getDate(String dateFormattedLikeDdMmYyyy) throws ParseException {
        Date validUntil = new Date();
        validUntil.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(dateFormattedLikeDdMmYyyy).getTime());
        return validUntil;
    }

    public static Date getDate(int yearsInFuture) throws IllegalArgumentException {
        if (yearsInFuture <= 0) {
            throw new IllegalArgumentException("Has to be a number larger 0");
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, yearsInFuture);
        return cal.getTime();
    }

    public static SharkCertificate createCertificate(PeerSemanticTag me, Date validUntil, SharkKeyGenerator keyGenerator) throws NoSuchAlgorithmException, InvalidKeySpecException {
        LinkedList<PeerSemanticTag> peerList = new LinkedList<>();
        peerList.addFirst(me);

        PublicKey publicKey = keyGenerator.getPublicKey();
//        TODO: TEST
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyGenerator.getPublicKey().getEncoded()));

        SharkCertificate sharkCertificate = new SharkCertificate(me, me, peerList, Certificate.TrustLevel.UNKNOWN, publicKey, validUntil);
        return sharkCertificate;
    }

    public static SharkPkiStorage createStoreForCertificate(SharkKeyGenerator keyGenerator, PeerSemanticTag me) throws NoSuchAlgorithmException, InvalidKeySpecException, SharkKBException {
        PrivateKey privateKey = keyGenerator.getPrivateKey();
//        TODO: test
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));

        SharkPkiStorage sharkPkiStorage = new SharkPkiStorage(new InMemoSharkKB(), me, privateKey);
        //TODO: extract public key from cert, extract PeerSemanticTag from cert, check whether its me if needed
        return sharkPkiStorage;
    }

//    public static void addCertificate(SharkPkiStorage storage, SharkCertificate certificate) throws SharkKBException {
//        storage.addSharkCertificate(certificate);
//    }

//    public static SharkCertificate getCertificate(SharkPkiStorage storage, PeerSemanticTag me) throws SharkKBException {
//        return storage.getSharkCertificate(me);
//    }

    public static Knowledge extractCertificateAsKnowledge(SharkPkiStorage storage, PeerSemanticTag me) throws SharkKBException, SharkSecurityException, IOException {
        ContextCoordinates ccs = InMemoSharkKB.createInMemoContextCoordinates(
                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                me,
                null, null,
                null, null,
                SharkCS.DIRECTION_INOUT);

        return SharkCSAlgebra.extract(storage.getSharkPkiStorageKB(), ccs);
    }

//    public static void sendKnowledgeTo(SharkEngine engine, Knowledge knowledge, PeerSemanticTag destiny, KnowledgePort knowledgePort) throws SharkSecurityException, IOException, SharkKBException {
//        engine.sendKnowledge(knowledge, destiny, knowledgePort);
//    }

    public static Interest createCertificateInterest(PeerSemanticTag me, PeerSemanticTag other) throws SharkKBException {
        STSet stSetInterest = InMemoSharkKB.createInMemoSTSet();
        //request Cert
        stSetInterest.createSemanticTag(Certificate.CERTIFICATE_SEMANTIC_TAG_NAME, Certificate.CERTIFICATE_SEMANTIC_TAG_SI);

        // request Fingerprint
        //stSetInterest.createSemanticTag(SharkPkiKP.KP_CERTIFICATE_VALIDATION_TAG_NAME, SharkPkiKP.KP_CERTIFICATE_VALIDATION_TAG_SI);

        PeerSTSet peerStSetIssuer = InMemoSharkKB.createInMemoPeerSTSet();
        peerStSetIssuer.merge(me);

        PeerSTSet peerStSetSubject = InMemoSharkKB.createInMemoPeerSTSet();
        peerStSetSubject.merge(other);

        Interest interest = InMemoSharkKB.createInMemoInterest(
                stSetInterest,
                me,
                peerStSetSubject,
                peerStSetIssuer,
                null,
                null,
                SharkCS.DIRECTION_OUT
        );

        return interest;
    }

//    public static void sendInterestTo(SharkEngine engine, Interest interest, PeerSemanticTag other, KnowledgePort knowledgePort) throws SharkSecurityException, IOException, SharkKBException {
//        engine.sendInterest(interest, other, knowledgePort);
//    }
}
