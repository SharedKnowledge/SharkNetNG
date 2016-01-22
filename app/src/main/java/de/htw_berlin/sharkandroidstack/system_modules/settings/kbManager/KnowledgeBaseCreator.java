package de.htw_berlin.sharkandroidstack.system_modules.settings.kbManager;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.system.L;

import java.util.UUID;

/**
 * Created by simon on 18.03.15.
 * This is a helper class that creates KnowledgeBases with random data.
 */
public class KnowledgeBaseCreator {

    public SyncKB getKb(String owner) throws SharkKBException {
        return prepareKb(InMemoSharkKB.createInMemoPeerSemanticTag(owner, owner + "Id", "tcp://localhost:5555"));
    }

    private SyncKB prepareKb(PeerSemanticTag owner) throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        try {
            final SemanticTag tag1 = kb.createSemanticTag(owner.getName() + " Semantic Tag 1", owner.getName() + " Subject Identifier 1");
            final SemanticTag tag2 = kb.createSemanticTag(owner.getName() + " Semantic Tag 2", owner.getName() + " Subject Identifier 2");
            final ContextCoordinates contextCoordinates1 = kb.createContextCoordinates(tag1, owner, null, null, null, null, SharkCS.DIRECTION_INOUT);
            final ContextCoordinates contextCoordinates2 = kb.createContextCoordinates(tag2, owner, null, null, null, null, SharkCS.DIRECTION_INOUT);

            kb.createContextPoint(contextCoordinates1).addInformation(UUID.randomUUID().toString());
            kb.createContextPoint(contextCoordinates2).addInformation(UUID.randomUUID().toString());
        } catch (SharkKBException e) {
            L.e("Knowledge Base Factory", "Could not create semantic tags for " + owner.getName() + " knowledge base");
            throw e;
        }

        kb.setOwner(owner);

        try {
            return new SyncKB(kb);
        } catch (SharkKBException e) {
            L.e("Knowledge Base Factory", "Could not create a sync KB from " + owner.getName() + "s InMemoKb.");
            throw e;
        }
    }
}
