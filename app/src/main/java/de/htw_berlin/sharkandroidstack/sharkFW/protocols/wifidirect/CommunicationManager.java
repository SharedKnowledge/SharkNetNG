package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

/**
 * Created by micha on 28.01.16.
 */
public class CommunicationManager {

    private static CommunicationManager instance = null;

    public CommunicationManager() {}

    public CommunicationManager getInstance(){
        if(instance==null){
            this.instance = new CommunicationManager();
        }
        return instance;
    }


}
