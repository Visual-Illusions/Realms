package net.visualillusionsent.realms.runnables;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
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
    private String debug = "Killed Animal - Name: '%s' @ Location: X: '%d' Y: '%d' Z: '%d' World: '%s' Dimension: '%d'";
    
    /**
     * class constructor
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
                            theAnimal.destroy();
                            
                            //Debugging
                            rhandle.log(RLevel.ANIMAL_DESTROY, String.format(debug, theAnimal.getName(), Math.floor(theAnimal.getX()), Math.floor(theAnimal.getY()),
                                    Math.floor(theAnimal.getZ()), theAnimal.getWorldName(), theAnimal.getDimension()));
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in AnimalsThread. (Don't worry Not a major issue)");
        }
    }
}
