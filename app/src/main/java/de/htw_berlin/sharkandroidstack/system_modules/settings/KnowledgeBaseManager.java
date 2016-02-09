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

    public final static String implementationTypeEmpty = "empty";
    public final static String implementationTypeSimple = "simple";
    public final static String implementationTypeSimpleWith2Cp = "simple with 2 Cp";
    public final static String[] implementationTypes = new String[]{implementationTypeEmpty, implementationTypeSimple, implementationTypeSimpleWith2Cp};

    private final static HashMap<String, SharkKB> kbs = new HashMap<>();

    public static SharkKB getInMemoKb(String type, boolean reUse) throws SharkKBException {
        SharkKB cachedKB = getSharkKbFromCache(type, reUse);
        if (cachedKB != null) {
            return cachedKB;
        }

        cachedKB = createSharkKb(type);
        kbs.put(type, cachedKB);
        return cachedKB;
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

    private static SharkKB getSharkKbFromCache(String type, boolean reUse) {
        if (!reUse) {
            kbs.remove(type);
        }

        return kbs.get(type);
    }

    private static SharkKB createSharkKb(String type) throws SharkKBException {
        SharkKB cachedKB;
        switch (type) {
            case implementationTypeEmpty:
                cachedKB = new InMemoSharkKB();
                break;
            case implementationTypeSimple:
                final String value = SettingsManager.getValue(SettingsManager.KEY_KB_OWNER_PREFERENCES, AndroidUtils.deviceId);
                cachedKB = prepareKb(value, false);
                break;
            case implementationTypeSimpleWith2Cp:
                final String value1 = SettingsManager.getValue(SettingsManager.KEY_KB_OWNER_PREFERENCES, AndroidUtils.deviceId);
                cachedKB = prepareKb(value1, true);
                break;
            default:
                throw new IllegalArgumentException(String.format("Type %s not found within implemented types.", type));
        }
        return cachedKB;
    }

    private static SharkKB prepareKb(String owner, boolean withCp) throws SharkKBException {
        final PeerSemanticTag ownerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag(owner, owner + "Id", "tcp://localhost:5555");

        SharkKB kb = new InMemoSharkKB();
        kb.setOwner(ownerSemanticTag);

        if (withCp) {
            final SemanticTag tag1 = InMemoSharkKB.createInMemoSemanticTag(ownerSemanticTag.getName() + " Semantic Tag 1", ownerSemanticTag.getName() + " Subject Identifier 1");
            final SemanticTag tag2 = InMemoSharkKB.createInMemoSemanticTag(ownerSemanticTag.getName() + " Semantic Tag 2", ownerSemanticTag.getName() + " Subject Identifier 2");
            final ContextCoordinates contextCoordinates1 = kb.createContextCoordinates(tag1, ownerSemanticTag, null, null, null, null, SharkCS.DIRECTION_INOUT);
            final ContextCoordinates contextCoordinates2 = kb.createContextCoordinates(tag2, ownerSemanticTag, null, null, null, null, SharkCS.DIRECTION_INOUT);

            kb.createContextPoint(contextCoordinates1).addInformation(UUID.randomUUID().toString());
            kb.createContextPoint(contextCoordinates2).addInformation(UUID.randomUUID().toString());
        }

        return kb;
    }
}
