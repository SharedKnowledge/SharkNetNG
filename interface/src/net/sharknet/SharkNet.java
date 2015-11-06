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
public interface SharkNet {
    
    public Radar getRadar();
    
    public Iterator<Chat> getChats();
    
    public Iterator<SNInterest> getMyInterests();
    
    public Iterator<SNPeer> getContacts();
    
    public Iterator<SNPeer> getFriends();
    
    public Iterator<SNPeer> getBestfriends();
    
    public Iterator<SNInterest> getInterests(SNPeer peer);
    
    public SNProfile getProfile();
    
    public Timeline getTimeline();
    
    public Settings getSettings();
}
