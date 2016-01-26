package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
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
public class Alice extends SharkDemoIdentity {
    public static String name = "Alice";

    private SharkKB kb;
    private PeerSemanticTag peer;
    private ContextCoordinates cc;
    private StandardKP kp;
    private SharkEngine engine;
    private ContextPoint cp;

    public Alice(KnowledgeBaseListenerAdapterImpl knowledgeBaseListener, SharkEngine engine, KPListener knowledgePortListener) throws SharkKBException {
        this.engine = engine;
        kb = KnowledgeBaseManager.getInMemoKb(KnowledgeBaseManager.implementationTypeSimple, false);
        kb.addListener(knowledgeBaseListener);

        //TODO: Address is absolutely not needed here, but framework requires something
        peer = kb.createPeerSemanticTag(name, name + "Id", "tcp://localhost:123");
        kb.setOwner(peer);

        SemanticTag topic = kb.createSemanticTag("Shark", "http://www.sharksystem.net/");
        cc = kb.createContextCoordinates(topic, peer, peer, null, null, null, SharkCS.DIRECTION_OUT);

        kp = new StandardKP(engine, cc, kb);
        kp.addListener(knowledgePortListener);
    }

    public void addInformation(String msg) throws SharkKBException, SharkProtocolNotSupportedException, IOException, SharkSecurityException {
        cp = kb.createContextPoint(cc);
        cp.addInformation(msg);
        kb.addInterest(cc);
        //TODO: Bug?! - cpChanged not called?!
    }

    @Override
    public void sendInformation(PeerSemanticTag otherPeer) throws SharkProtocolNotSupportedException, IOException, SharkSecurityException, SharkKBException {
        if (cp == null) {
            throw new IllegalStateException("Context Point cannot be null, call addInformation() first.");
        }
        final InMemoKnowledge k = new InMemoKnowledge();
        k.addContextPoint(cp);
        engine.startNfc();
        engine.sendKnowledge(k, otherPeer, kp);
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
        return "Alice - I can send information to Bob ONCE.";
    }

    @Override
    public PeerSemanticTag getPeer() {
        return peer;
    }
}
