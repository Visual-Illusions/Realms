package net.visualillusionsent.realms.io.threads;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Healing<br>
 * Heals players in Healing enabled zones<br>
 * This file is part of Realms
 * @author darkdiplomat
 */
public class Healer extends Thread{
    private RHandle rhandle;
    private String debug = "Healing Player - Name: '%s' in World: '%s' Dimension: '%s' @ Location: X: '%s' Y: '%s' Z: '%s'";
    
    /**
     * class constructor
     * @param RHandle
     */
    public Healer(RHandle rhandle) {
        this.rhandle = rhandle;
        this.setName("Healer-Thread");
        this.setDaemon(true);
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
                            rhandle.log(RLevel.PLAYER_HEAL, String.format(debug, thePlayer.getName(), thePlayer.getWorldName(), thePlayer.getDimension(), 
                                                                                 Math.floor(thePlayer.getX()), Math.floor(thePlayer.getY()), Math.floor(thePlayer.getZ()))); //Debugging
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in Healer. (Don't worry Not a major issue)");
        }
    }
}
