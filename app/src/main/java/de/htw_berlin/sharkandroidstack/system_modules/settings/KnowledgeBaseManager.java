package de.htw_berlin.sharkandroidstack.system_modules.settings;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.util.HashMap;
import java.util.UUID;

import de.htw_berlin.sharkandroidstack.AndroidUtils;

/**
 * Created by mn-io on 22.01.16.
 */
public class KnowledgeBaseManager {

    public final static String implementationTypeEmpty = "empty KB";
    public final static String implementationTypeDummy = "simple with 2 Cp";
    public final static String[] implementationTypes = new String[]{implementationTypeEmpty, implementationTypeDummy};

    private final static HashMap<String, SharkKB> kbs = new HashMap<>();

    public static SharkKB getInMemoKb(String type, boolean reUse, String owner) throws SharkKBException {
        if (!reUse) {
            kbs.remove(type);
        }

        SharkKB cachedKB = kbs.get(type);
        if (cachedKB != null) {
            return cachedKB;
        }

        final PeerSemanticTag ownerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag(owner, owner + "Id", "tcp://localhost:5555");
        switch (type) {
            case implementationTypeEmpty:
                cachedKB = new InMemoSharkKB();
                break;
            case implementationTypeDummy:
                cachedKB = prepareKb(ownerSemanticTag);
                cachedKB.setOwner(ownerSemanticTag);
            default:
                throw new IllegalArgumentException("Type " + type + " not found within implemented types.");
        }

        kbs.put(type, cachedKB);
        return cachedKB;
    }

    public static SharkKB getInMemoKb(String type, boolean reUse) throws SharkKBException {
        return getInMemoKb(type, reUse, AndroidUtils.deviceId);
    }

    public static SharkKB getInMemoKb(String type) throws SharkKBException {
        return getInMemoKb(type, true, AndroidUtils.deviceId);
    }

    public static SharkKB getInMemoKbBySettings() throws SharkKBException {
        final String typeFromSettings = SettingsManager.getValue(SettingsManager.KEY_KB_PREFERENCES);
        return getInMemoKb(typeFromSettings, true);
    }

    public static void saveToCache() {
        //TODO: how can a Kb be serialized?
    }

    public static void restoreFromCache() {
        //TODO: how can a Kb be serialized?
    }

    private static SharkKB prepareKb(PeerSemanticTag owner) throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        final SemanticTag tag1 = kb.createSemanticTag(owner.getName() + " Semantic Tag 1", owner.getName() + " Subject Identifier 1");
        final SemanticTag tag2 = kb.createSemanticTag(owner.getName() + " Semantic Tag 2", owner.getName() + " Subject Identifier 2");
        final ContextCoordinates contextCoordinates1 = kb.createContextCoordinates(tag1, owner, null, null, null, null, SharkCS.DIRECTION_INOUT);
        final ContextCoordinates contextCoordinates2 = kb.createContextCoordinates(tag2, owner, null, null, null, null, SharkCS.DIRECTION_INOUT);

        kb.setOwner(owner);
        kb.createContextPoint(contextCoordinates1).addInformation(UUID.randomUUID().toString());
        kb.createContextPoint(contextCoordinates2).addInformation(UUID.randomUUID().toString());

        return kb;
    }
}
