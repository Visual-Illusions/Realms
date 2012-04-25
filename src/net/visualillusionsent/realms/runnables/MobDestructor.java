package net.visualillusionsent.realms.runnables;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModMob;

/**
 * Realms mob destruction runnable
 * <p>
 * Destroys all mobs in mob disabled zones
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class MobDestructor implements Runnable{
    private RHandle rhandle;
    
    /**
     * class constructor
     * 
     * @param RHandle
     */
    public MobDestructor(RHandle rhandle) {
        this.rhandle = rhandle;
    }
    
    /**
     * Runs the destructions of Mobs
     */
    public void run() {
        try{
            //Mob Lists
            List<ICModMob> mobList = rhandle.getServer().getMobList();
        
            //Check Mob List
            synchronized(mobList){
                if (!mobList.isEmpty()){
                    for(ICModMob theMob: mobList){
                        //Get Zone Mob is in
                        Zone myZone = ZoneLists.getZone(rhandle.getEverywhere(theMob.getWorldName(), theMob.getDimIndex()), theMob);
                        //Check if Mob is in a Mob Disabled Zone
                        if (myZone.getSanctuary()) {
                            //Mob is in Mob Disable Zone and needs Destroyed
                            theMob.destroy(); //Kill Animal
                            rhandle.log(RLevel.DEBUGINFO, "Killed Mob - Name: '" + theMob.getName()+ 
                                                          "' at Location - X: '"+Math.floor(theMob.getX())+"' Y: '"+Math.floor(theMob.getY())+"' Z: '"+Math.floor(theMob.getZ())+
                                                          "' World: '"+theMob.getWorldName()+"' Dimension: '"+ theMob.getDimension()+"'");//Debugging
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in MobDestructor. (Don't worry Not a major issue)");
        }
    }
}
