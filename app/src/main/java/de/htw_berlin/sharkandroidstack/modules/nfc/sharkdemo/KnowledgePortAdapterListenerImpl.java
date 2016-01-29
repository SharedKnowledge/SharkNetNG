package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.system.L;

/**
 * Created by mn-io on 25.01.2016.
 */
public class KnowledgePortAdapterListenerImpl implements KPListener {
    private final MyKbListAdapter kbListAdapter;
    private Runnable callback;

    public KnowledgePortAdapterListenerImpl(MyKbListAdapter kbListAdapter, Runnable callback) {
        this.kbListAdapter = kbListAdapter;
        this.callback = callback;
    }

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Expose sent", L.contextSpace2String(sentMutualInterest), MyKbListAdapter.SRC_KP_LISTENER));
        update();
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Insert sent", L.knowledge2String(sentKnowledge), MyKbListAdapter.SRC_KP_LISTENER));
        update();
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Knowledge assimilated", L.cp2String(newCP), MyKbListAdapter.SRC_KP_LISTENER));
        update();
    }

    private void update() {
        if (callback != null) {
            callback.run();
        }
    }
}
