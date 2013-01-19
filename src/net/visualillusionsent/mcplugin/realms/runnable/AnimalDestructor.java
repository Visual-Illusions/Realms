/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 */
package net.visualillusionsent.mcplugin.realms.runnable;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.mcmod.interfaces.Mod_Entity;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class AnimalDestructor implements Runnable {
    private final String debug = "Killed Animal - Name: '%s' in Zone: '%s' (World: '%s' Dimension: '%d' X: '%.2f' Y: '%.2f' Z: '%.2f')";

    public AnimalDestructor(RealmsBase base) {}

    @Override
    public final void run() {
        try {
            //Animals Lists
            List<Mod_Entity> animalList = RealmsBase.getServer().getAnimals();

            //Check Animal List
            if (!animalList.isEmpty()) {
                for (Mod_Entity theAnimal : animalList) {
                    //Get Zone Animal is in
                    Zone theZone = ZoneLists.getInZone(theAnimal);
                    //Check if Animal is in a Animal Disabled Zone
                    if (!theZone.getAnimals()) {
                        //Animal is in Animal Disable Zone and needs Destroyed
                        theAnimal.destroy();

                        //Debugging
                        RealmsLogMan.log(RLevel.ANIMAL_DESTROY, String.format(debug, theAnimal.getName(), theZone.getName(), theAnimal.getWorld(), theAnimal.getDimension(), theAnimal.getX(), theAnimal.getY(), theAnimal.getZ()));
                    }
                }
            }
        }
        catch (ConcurrentModificationException CME) {
            RealmsLogMan.log(RLevel.GENERAL, "Concurrent Modification Exception in AnimalsDestructor. (Don't worry Not a major issue)");
        }
    }
}
