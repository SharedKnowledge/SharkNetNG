package de.htw_berlin.sharkandroidstack.setup;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.SyncKP;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

import de.htw_berlin.sharkandroidstack.android.KbTextViewWriter;

/**
 * An example KP which will send an interest to the connecting device
 *
 * @author jgig
 */
public class MySimpleKp extends KnowledgePort {
    private SharkCS myInterest;
    private SyncKP _kp;
    private KbTextViewWriter kbTextViewWriter;

    /**
     * @param se  the shark engine
     * @param _kp
     */
    public MySimpleKp(SharkEngine se, PeerSemanticTag myIdentity, SyncKP _kp) {
        super(se);
        this._kp = _kp;

        SharkKB kb = new InMemoSharkKB();
        PeerSemanticTag me = myIdentity;

        this.myInterest = new InMemoSharkKB().createInterest(null, myIdentity, null, null, null, null, SharkCS.DIRECTION_INOUT);
    }

    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        log("knowledge received: (" + L.knowledge2String(knowledge) + ")");
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
            log("Trying to send sync interest " + L.contextSpace2String(_kp.getInterest()));
            try {
                kepConnection.expose(_kp.getInterest());
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

    public void setTextViewWriter(KbTextViewWriter kbTextViewWriter) {
        this.kbTextViewWriter = kbTextViewWriter;
    }

    public void log(String msg) {
        if (kbTextViewWriter == null) {
            return;
        }

        kbTextViewWriter.appendToLogText(msg);
    }
}

