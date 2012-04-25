package net.visualillusionsent.realms.io;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModMob;

/**
 * Realms animal destruction runnable
 * <p>
 * Destroys all animals in animal disabled zones
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class AnimalDestructor implements Runnable{
    private RHandle rhandle;
    
    /**
     * Class Constructor
     * 
     * @param RHandle
     */
    public AnimalDestructor(RHandle rhandle) {
        this.rhandle = rhandle;
    }
    
    /**
     * Runs the destructions of Animals
     */
    public void run() {
        try{
            //Animals Lists
            List<ICModMob> animalList = rhandle.getServer().getAnimalList();
        
            //Check Animal List
            synchronized(animalList){
                if (!animalList.isEmpty()){
                    for(ICModMob theAnimal : animalList){
                        //Get Zone Animal is in
                        Zone myZone = ZoneLists.getZone(rhandle.getEverywhere(theAnimal.getWorldName(), theAnimal.getDimIndex()), theAnimal);
                        //Check if Animal is in a Animal Disabled Zone
                        if (!myZone.getAnimals()) {
                            //Animal is in Animal Disable Zone and needs Destroyed
                            theAnimal.destroy(); //Kill Animal
                            rhandle.log(RLevel.DEBUGINFO, "Killed Animal - Name: '" + theAnimal.getName()+ 
                                                          "' at Location - X: '"+Math.floor(theAnimal.getX())+"' Y: '"+Math.floor(theAnimal.getY())+"' Z: '"+Math.floor(theAnimal.getZ())+
                                                          "' World: '"+theAnimal.getWorldName()+"' Dimension: '"+ theAnimal.getDimension()+"'");//Debugging
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in AnimalsThread. (Don't worry Not a major issue)");
        }
    }
}
