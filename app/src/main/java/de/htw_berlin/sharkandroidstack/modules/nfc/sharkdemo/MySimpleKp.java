package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * An example KP which will send an interest to the connecting device
 * TODO: merge to KnowledgePortManager, this is basically a clone of wifidirect/MySimpleKp
 *
 * @author jgig
 */
public class MySimpleKp extends KnowledgePort {
    private SharkCS myInterest;
    private KnowledgePort kp;

    public MySimpleKp(SharkEngine se, PeerSemanticTag myIdentity) {
        super(se);
    }

    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        log("knowledge received: (" + L.knowledge2String(knowledge) + ")");
    }

    private void log(String msg) {
        System.out.println("mario: " + msg);
    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
        log("interest received " + L.contextSpace2String(interest));

        if (isAnyInterest(interest)) {
            log("any interest received " + L.contextSpace2String(interest));
            try {
                log("trying to send interest\n" + L.contextSpace2String(myInterest));
                kepConnection.expose(myInterest);
            } catch (SharkException ex) {
                log("problems:" + ex.getMessage());
            }
        }
        //TODO: if else?
        if (isPeerInterest(interest)) {
            log("Peer interest received " + L.contextSpace2String(interest));
            log("Trying to send sync interest " + L.contextSpace2String(kp.getInterest()));
            try {
                kepConnection.expose(kp.getInterest());
            } catch (SharkException ex) {
                log("problems:" + ex.getMessage());
            }
        }
    }


    private boolean isAnyInterest(SharkCS theInterest) {
        return (theInterest.isAny(SharkCS.DIM_TOPIC) && theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME));
    }

    private boolean isPeerInterest(SharkCS theInterest) {
        return (theInterest.isAny(SharkCS.DIM_TOPIC) && !theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME));
    }
}

