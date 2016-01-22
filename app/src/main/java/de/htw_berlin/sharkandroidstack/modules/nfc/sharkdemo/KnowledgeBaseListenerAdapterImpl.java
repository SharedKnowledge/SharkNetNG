package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

// TODO: move to package for generic re-usage

public class KnowledgeBaseListenerAdapterImpl implements KnowledgeBaseListener {
    @Override
    public void topicAdded(SemanticTag tag) {

    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {

    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {

    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {

    }

    @Override
    public void topicRemoved(SemanticTag tag) {

    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {

    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {

    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {

    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {

    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {

    }

    @Override
    public void tagChanged(SemanticTag tag) {

    }

    @Override
    public void contextPointAdded(ContextPoint cp) {

    }

    @Override
    public void cpChanged(ContextPoint cp) {

    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {

    }
}
