/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 */
package net.visualillusionsent.realms.runnable;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygon.Point;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class AnimalRemover implements Runnable{

    private final String debugDestroy = "Killed Animal - Name: '%s' in Zone: '%s' (World: '%s' Dimension: '%d' X: '%.2f' Y: '%.2f' Z: '%.2f')";
    private final String debugMove = "Moved Animal - Name: '%s' in Zone: '%s' (World: '%s' Dimension: '%d' X: '%.2f' Y: '%.2f' Z: '%.2f')";

    public AnimalRemover(RealmsBase base){}

    @Override
    public final void run(){
        try{
            //Animals Lists
            List<Mod_Entity> animalList = RealmsBase.getServer().getAnimals();
            //Check Animal List
            if(!animalList.isEmpty()){
                for(Mod_Entity theAnimal : animalList){
                    //Get Zone Animal is in
                    Zone theZone = ZoneLists.getInZone(theAnimal);
                    //Check if Animal is in a Animal Disabled Zone
                    if(!theZone.getAnimals()){
                        if(RealmsBase.getProperties().getBooleanVal("sanctuary.animals.die")){
                            //Destroy Animal
                            theAnimal.destroy();
                            RealmsLogMan.log(RLevel.ANIMAL_DESTROY, String.format(debugDestroy, theAnimal.getName(), theZone.getName(), theAnimal.getWorld(), theAnimal.getDimension(), theAnimal.getX(), theAnimal.getY(), theAnimal.getZ()));
                        }
                        else{
                            //Move Animal
                            Point thrown = RealmsBase.throwBack(theZone, theAnimal.getLocationPoint());
                            theAnimal.teleportTo(thrown.x + 0.5D, thrown.y + 0.5D, thrown.z + 0.5D, theAnimal.getRotation(), theAnimal.getPitch());
                            RealmsLogMan.log(RLevel.MOB_REMOVER, String.format(debugMove, theZone.getName(), theAnimal.getName(), theAnimal.getWorld(), theAnimal.getDimension(), theAnimal.getX(), theAnimal.getY(), theAnimal.getZ()));
                        }
                    }
                }
            }
        }
        catch(ConcurrentModificationException CME){
            RealmsLogMan.log(RLevel.GENERAL, "Concurrent Modification Exception in AnimalsDestructor thread. (Non-Issue)");
        }
        catch(Exception ex){
            RealmsLogMan.log(RLevel.GENERAL, "Unhandled Exception occured in AnimalsDestructor thread. (Non-Issue)");
        }
    }
}
