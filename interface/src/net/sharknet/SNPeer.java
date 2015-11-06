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
public interface SNPeer {
    
    public Iterator<SNInterest> getInterests();
    
    public Iterator<SNInterest> getMatchedInterests(SNPeer peer);
    
    public SNProfile getProfile();
    
}
