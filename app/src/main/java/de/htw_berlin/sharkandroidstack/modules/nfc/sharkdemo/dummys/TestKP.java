package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

import org.json.JSONException;

/**
 * Created by msc on 21.03.16.
 */
public class TestKP extends KnowledgePort {

    public TestKP(SharkEngine se) {
        super(se);
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {
        // UNUSED
    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {
        // UNUSED
    }

    @Override
    protected void handleExpose(ASIPInterest interest, ASIPConnection asipConnection) throws SharkKBException {
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
        try {
            String str = ASIPSerializer.serializeKnowledge(asipKnowledge).toString(2);
            System.out.println("mario: " + str);
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        kb.addInformation()
        //TODO: mario: got message here
    }
}
