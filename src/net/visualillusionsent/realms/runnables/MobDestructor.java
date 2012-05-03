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
    private String debug = "Killed Mob - Name: '%s' @ Location: X: '%d' Y: '%d' Z: '%d' World: '%s' Dimension: '%d'";
    
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
                        
                        boolean destroyed = false;
                        //Check if Mob is a Creeper in a Creeper Disabled Zone
                        if(theMob.getName().equals("Creeper") && !myZone.getCreeper()){
                            theMob.destroy();
                            destroyed = true;
                        }
                        //Check if Mob is a Ghast in a Ghast Disabled Zone
                        else if(theMob.getName().equals("Ghast") && !myZone.getGhast()){
                            theMob.destroy();
                            destroyed = true;
                        }
                        //Check if Mob is in a Mob Disabled Zone
                        else if (myZone.getSanctuary()) {
                            //Mob is in Mob Disable Zone and needs Destroyed
                            theMob.destroy();
                            destroyed = true;
                        }
                        
                        if(destroyed){
                            //Debugging
                            rhandle.log(RLevel.MOB_DESTROY, String.format(debug, theMob.getName(), Math.floor(theMob.getX()), Math.floor(theMob.getY()),
                                                            Math.floor(theMob.getZ()), theMob.getWorldName(), theMob.getDimension()));
                        }
                    }
                }
            }
        }
        catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in MobDestructor. (Don't worry Not a major issue)");
        }
    }
}
