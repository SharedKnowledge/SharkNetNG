package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by msc on 21.03.16.
 */
public class TestKP extends KnowledgePort {

    public TestKP(SharkEngine se) {
        super(se);
    }

    @Override
    protected void handleInsert(Knowledge knowledge, KEPConnection kepConnection) {
    }

    @Override
    protected void handleExpose(SharkCS interest, KEPConnection kepConnection) {
    }

    @Override
    protected void handleExpose(ASIPInterest interest, ASIPConnection asipConnection) throws SharkKBException {
    }

    @Override
    protected void handleInsert(ASIPKnowledge asipKnowledge, ASIPConnection asipConnection) {
    }

    @Override
    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
        ASIPInMessage inMessage = (ASIPInMessage) asipConnection;
        InputStream is2 = inMessage.getRaw();
        try {
            byte[] buffer = new byte[is2.available()];
            int result = is2.read(buffer);
            String rawContent = new String(buffer);
            System.out.println("mario: raw " + rawContent + " | " + result);
        } catch (IOException e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }

        super.handleRaw(is, asipConnection);
    }

}
