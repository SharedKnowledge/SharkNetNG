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
    private MyKbListAdapter kbListAdapter;

    public KnowledgeBaseListenerAdapterImpl(MyKbListAdapter kbListAdapter) {
        this.kbListAdapter = kbListAdapter;
    }

    @Override
    public void topicAdded(SemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Topic added", tag.toString()));
    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Peer added", tag.toString()));
    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Location added", location.toString()));
    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Timespan added", time.toString()));
    }

    @Override
    public void topicRemoved(SemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Topic removed", tag.toString()));
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Peer removed", tag.toString()));
    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Location removed", tag.toString()));
    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Timespan removed", tag.toString()));
    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {
        String data = subject.toString() + " / " + type + " / " + object.toString();
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Predicate created", data));
    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {
        String data = subject.toString() + " / " + type + " / " + object.toString();
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Predicate removed", data));
    }

    @Override
    public void tagChanged(SemanticTag tag) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Tag changed", tag.toString()));
    }

    @Override
    public void contextPointAdded(ContextPoint cp) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Context point added", cp.toString()));
    }

    @Override
    public void cpChanged(ContextPoint cp) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Context point changed", cp.toString()));
    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Context point removed", cp.toString()));
    }
}
