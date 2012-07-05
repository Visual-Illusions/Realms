package net.visualillusionsent.realms.io.threads;

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
public class AnimalDestructor extends Thread {
    private RHandle rhandle;
    private String debug = "Killed Animal - Name: '%s' in Zone: '%s' (World: '%s' Dimension: '%s' X: '%s' Y: '%s' Z: '%s')";

    /**
     * class constructor
     * 
     * @param RHandle
     */
    public AnimalDestructor(RHandle rhandle) {
        this.rhandle = rhandle;
        this.setName("AnimalDestructor-Thread");
        this.setDaemon(true);
    }

    /**
     * Runs the destructions of Animals
     */
    @Override
    public void run() {
        try {
            //Animals Lists
            List<ICModMob> animalList = rhandle.getServer().getAnimalList();

            //Check Animal List
            synchronized (animalList) {
                if (!animalList.isEmpty()) {
                    for (ICModMob theAnimal : animalList) {
                        //Get Zone Animal is in
                        Zone theZone = ZoneLists.getZone(rhandle.getEverywhere(theAnimal.getWorldName(), theAnimal.getDimIndex()), theAnimal);
                        //Check if Animal is in a Animal Disabled Zone
                        if (!theZone.getAnimals()) {
                            //Animal is in Animal Disable Zone and needs Destroyed
                            theAnimal.destroy();

                            //Debugging
                            rhandle.log(RLevel.ANIMAL_DESTROY, String.format(debug, theAnimal.getName(), theZone.getName(), theAnimal.getWorldName(), theAnimal.getDimension(),
                                    Math.floor(theAnimal.getX()), Math.floor(theAnimal.getY()), Math.floor(theAnimal.getZ())));
                        }
                    }
                }
            }
        }
        catch (ConcurrentModificationException CME) {
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in AnimalsThread. (Don't worry Not a major issue)");
        }
    }
}
