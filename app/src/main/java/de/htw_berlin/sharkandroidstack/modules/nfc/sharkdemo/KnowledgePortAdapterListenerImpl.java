package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.KnowledgePort;

/**
 * Created by mn-io on 25.01.2016.
 */
public class KnowledgePortAdapterListenerImpl implements KPListener {
    private Runnable callback;

    public KnowledgePortAdapterListenerImpl(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        update();
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        update();
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        update();
    }

    private void update() {
        if (callback != null) {
            callback.run();
        }
    }
}
