package net.visualillusionsent.realms.io;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Healing
 * <p>
 * Heals players in Healing enabled zones
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class Healer implements Runnable{
    private RHandle rhandle;
    
    /**
     * class constructor
     * 
     * @param RHandle
     */
    public Healer(RHandle rhandle) {
        this.rhandle = rhandle;
    }
    
    /**
     * Runs the healing of Players
     */
    public void run() {
        try{
            /*Player Lists*/
            List<ICModPlayer> playerList = ZoneLists.getInHealing();
                    
            /*Check Player List*/
            if (!playerList.isEmpty()){
                synchronized(playerList){
                    for(ICModPlayer thePlayer : playerList){
                        int health = thePlayer.getHealth();
                        if(health < 20){ //Need Healing
                            thePlayer.heal(1); //Heal
                            rhandle.log(RLevel.DEBUGINFO, "Trying to heal Player: " + thePlayer.getName() + " at location " + Math.floor(thePlayer.getX()) + "," + Math.floor(thePlayer.getY()) + "," + Math.floor(thePlayer.getZ())); //Debugging
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in Healer. (Don't worry Not a major issue)");
        }
    }
}
