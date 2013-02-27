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
package net.visualillusionsent.mcplugin.realms.runnable;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.mcmod.interfaces.Mod_Entity;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.polygon.Point;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class MobRemover implements Runnable{

    private final String debugDestroy = "Killed Mob - Name: '%s' in Zone: '%s' (World: '%s' Dimension: '%d' X: '%.2f' Y: '%.2f' Z: '%.2f')";
    private final String debugMove = "Moved Mob - Name: '%s' in Zone: '%s' (World: '%s' Dimension: '%d' X: '%.2f' Y: '%.2f' Z: '%.2f')";

    public MobRemover(RealmsBase realmsBase){}

    @Override
    public final void run(){
        try{
            //Mob Lists
            List<Mod_Entity> mobList = RealmsBase.getServer().getMobs();
            //Check Mob List
            synchronized(mobList){
                if(!mobList.isEmpty()){
                    for(Mod_Entity theMob : mobList){
                        //Get Zone Mob is in
                        Zone theZone = ZoneLists.getInZone(theMob);
                        if(theZone.getSanctuary()){
                            if(RealmsBase.getProperties().getBooleanVal("sanctuary.mobs.die")){
                                //Destory mob
                                theMob.destroy();
                                RealmsLogMan.log(RLevel.MOB_REMOVER, String.format(debugDestroy, theZone.getName(), theMob.getName(), theMob.getWorld(), theMob.getDimension(), theMob.getX(), theMob.getY(), theMob.getZ()));
                            }
                            else{
                                //Move Mob
                                Point thrown = RealmsBase.throwBack(theZone, theMob.getLocationPoint());
                                theMob.teleportTo(thrown.x + 0.5D, thrown.y + 0.5D, thrown.z + 0.5D, theMob.getRotation(), theMob.getPitch());
                                RealmsLogMan.log(RLevel.MOB_REMOVER, String.format(debugMove, theZone.getName(), theMob.getName(), theMob.getWorld(), theMob.getDimension(), theMob.getX(), theMob.getY(), theMob.getZ()));
                            }
                        }
                    }
                }
            }
        }
        catch(ConcurrentModificationException CME){
            RealmsLogMan.log(RLevel.GENERAL, "Concurrent Modification Exception in MobRemover thread. (Non-Issue)");
        }
        catch(Exception ex){
            RealmsLogMan.log(RLevel.GENERAL, "Unhandled Exception occured in in MobRemover thread. (Non-Issue)");
        }
    }
}
