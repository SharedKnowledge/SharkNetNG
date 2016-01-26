package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;

import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.KnowledgeBaseListenerAdapterImpl;
import de.htw_berlin.sharkandroidstack.system_modules.settings.KnowledgeBaseManager;

/**
 * Created by mn-io on 26.01.2016.
 */
public class Bob extends SharkDemoIdentity {

    static final String name = "Bob";
    final SharkKB kb;
    final PeerSemanticTag peer;
    final ContextCoordinates cc;
    final StandardKP kp;

    public Bob(KnowledgeBaseListenerAdapterImpl knowledgeBaseListener, SharkEngine engine, KPListener knowledgePortListener) throws SharkKBException {
        kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeSimple, false);
        kb.addListener(knowledgeBaseListener);
        peer = kb.createPeerSemanticTag(name, name + "Id", "tcp://localhost:124");
        kb.setOwner(peer);

        SemanticTag topic = kb.createSemanticTag("Shark", "http://www.sharksystem.net/");
        cc = kb.createContextCoordinates(topic, null, peer, null, null, null, SharkCS.DIRECTION_IN);

        kp = new StandardKP(engine, cc, kb);
        kp.addListener(knowledgePortListener);
    }

    @Override
    public SharkKB getKB() {
        return kb;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLongName() {
        return "Bob - I can receive information from Alice";
    }

    @Override
    public PeerSemanticTag getPeer() {
        return peer;
    }

    @Override
    public void sendInformation(PeerSemanticTag peer) {

    }

    @Override
    public void addInformation(String msg) throws SharkKBException, SharkProtocolNotSupportedException, IOException, SharkSecurityException {

    }
}

