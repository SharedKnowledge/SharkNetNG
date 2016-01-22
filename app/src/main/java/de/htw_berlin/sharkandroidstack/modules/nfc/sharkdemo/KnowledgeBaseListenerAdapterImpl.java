package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.system.L;

import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

// TODO: move to package for generic re-usage

public class KnowledgeBaseListenerAdapterImpl implements KnowledgeBaseListener {
    private MyKbListAdapter kbListAdapter;
    private String logId;

    public KnowledgeBaseListenerAdapterImpl(MyKbListAdapter kbListAdapter, String logId) {
        this.kbListAdapter = kbListAdapter;
        this.logId = logId;
    }

    @Override
    public void topicAdded(SemanticTag tag) {
        addTagToAdapter("Topic tag added", tag);
    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {
        addTagToAdapter("Peer tag added", tag);
    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {
        addTagToAdapter("Location tag added", location);
    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {
        addTagToAdapter("Timespan tag added", time);
    }

    @Override
    public void topicRemoved(SemanticTag tag) {
        addTagToAdapter("Topic tag removed", tag);
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        addTagToAdapter("Peer tag removed", tag);
    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {
        addTagToAdapter("Location tag removed", tag);
    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {
        addTagToAdapter("Timespan tag removed", tag);
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
        addTagToAdapter("Tag changed", tag);
    }

    @Override
    public void contextPointAdded(ContextPoint cp) {
        final String data = L.cp2String(cp);
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Context point added", data));
    }

    @Override
    public void cpChanged(ContextPoint cp) {
        final String data = L.cp2String(cp);
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Context point changed", data));
    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {
        final String data = L.cp2String(cp);
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder("Context point removed", data));
    }

    private void addTagToAdapter(String type, SemanticTag tag) {
        String data;
        try {
            data = L.semanticTag2String(tag);
        } catch (SharkKBException e) {
            LogManager.addThrowable(logId, e);
            data = "An error occurred, see log for details.";
        }
        kbListAdapter.add(new MyKbListAdapter.MyDataHolder(type, data));
    }
}
