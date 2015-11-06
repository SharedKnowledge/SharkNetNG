/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharknet;

import java.util.Iterator;

/**
 *
 * @author micha
 */
public interface SNInterest {
    
    public Iterator<SNTopic> getTopics();
    
    public Iterator<SNLocation> getLocations();
    
    public Iterator<SNPeer> getBelievers();
    
    public Iterator<SNPeer> getCommunicationPartners();
    
    public Iterator<SNPeer> getOwners();
    
    public Iterator<SNTime> getTimeranges();
    
    public boolean isProvider();
    
    public boolean isSeeker();
    
    
    
}
