package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;

/**
 * Created by mn-io on 26.01.2016.
 */
public abstract class SharkDemoIdentity {

    public abstract SharkKB getKB();

    public abstract String getName();

    public abstract String getLongName();

    public abstract PeerSemanticTag getPeer();

    public abstract void sendInformation(PeerSemanticTag peer) throws SharkProtocolNotSupportedException, IOException, SharkSecurityException, SharkKBException;

    public abstract void addInformation(String msg) throws SharkKBException, SharkProtocolNotSupportedException, IOException, SharkSecurityException;
}
