/* Copyright 2012 - 2013 Visual Illusions Entertainment.
 * This file is part of Realms.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * Source Code availible @ https://github.com/Visual-Illusions/Realms */
package net.visualillusionsent.minecraft.server.mod.plugin.realms.zones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class ZoneLists{

    private final static ConcurrentHashMap<String, Zone> zones;
    private final static ConcurrentHashMap<String, List<Zone>> playerZoneList;
    private final static List<String> inRestricted;
    private final static List<String> inHealing;
    private final static List<String> inCreative;
    private final static List<String> inAdventure;
    static {
        zones = new ConcurrentHashMap<String, Zone>();
        playerZoneList = new ConcurrentHashMap<String, List<Zone>>();
        inRestricted = new ArrayList<String>();
        inHealing = new ArrayList<String>();
        inCreative = new ArrayList<String>();
        inAdventure = new ArrayList<String>();
    }

    private ZoneLists(){}

    public final static void addZone(Zone zone){
        zones.put(zone.getName(), zone);
    }

    public final static boolean removeZone(Zone zone){
        return zones.remove(zone.getName()) != null;
    }

    public final static Collection<Zone> getZones(){
        return Collections.synchronizedCollection(zones.values());
    }

    public final static boolean isZone(String zone){
        return zones.containsKey(zone);
    }

    public final static Zone getZoneByName(String name) throws ZoneNotFoundException{
        if (!RealmsBase.getProperties().getBooleanVal("zone.case.sensitive")) {
            for (String zoneName : zones.keySet()) {
                if (zoneName.toLowerCase().equals(name.toLowerCase())) {
                    return zones.get(zoneName);
                }
            }
        }
        else if (zones.containsKey(name)) {
            return zones.get(name);
        }
        throw new ZoneNotFoundException(name);
    }

    public final static Zone getInZone(Mod_Entity entity){
        Zone zone = getEverywhere(entity);
        if (!zone.hasNoChildren()) {
            return zone.whichChildContains(entity);
        }
        return zone;
    }

    public final static Zone getInZone(Mod_Block block){
        Zone zone = getEverywhere(block);
        if (!zone.hasNoChildren()) {
            return zone.whichChildContains(block);
        }
        return zone;
    }

    public final static List<Zone> getZonesPlayerIsIn(Zone zone, Mod_User player){
        ArrayList<Zone> nzl = new ArrayList<Zone>();
        nzl.add(zone);
        for (Zone child : zone.getChildren()) {
            if (child.contains(player)) {
                nzl.add(child);
            }
            for (Zone children : child.getChildren()) {
                if (children.contains(player)) {
                    nzl.add(children);
                }
            }
        }
        return nzl;
    }

    public final static List<Zone> getplayerZones(Mod_User user){
        if (!playerZoneList.containsKey(user.getName())) {
            playerZoneList.put(user.getName(), new ArrayList<Zone>());
        }
        return Collections.unmodifiableList(playerZoneList.get(user.getName()));
    }

    public final static boolean removeZonefromPlayerZoneList(Zone zone){
        boolean toRet = true;
        for (String key : playerZoneList.keySet()) {
            if (playerZoneList.get(key).contains(zone)) {
                toRet &= playerZoneList.get(key).remove(zone);
            }
        }
        return toRet;
    }

    public final static void addplayerzones(Mod_User user, List<Zone> zones){
        playerZoneList.put(user.getName(), zones);
    }

    public final static boolean isInRestricted(Mod_User user){
        return inRestricted.contains(user.getName());
    }

    public final static void addInRestricted(Mod_User user){
        inRestricted.add(user.getName());
    }

    public final static void removeInRestricted(Mod_User user){
        if (inRestricted.contains(user.getName())) {
            inRestricted.remove(user.getName());
        }
    }

    public final static List<String> getInRestricted(){
        return new ArrayList<String>(inRestricted);
    }

    public final static void addInHealing(Mod_User user){
        inHealing.add(user.getName());
    }

    public final static void removeInHealing(Mod_User user){
        if (inHealing.contains(user.getName())) {
            inHealing.remove(user.getName());
        }
    }

    public final static List<String> getInHealing(){
        return Collections.unmodifiableList(inHealing);
    }

    public final static boolean isInCreative(Mod_User user){
        return inCreative.contains(user.getName());
    }

    public final static void addInCreative(Mod_User user){
        inCreative.add(user.getName());
    }

    public final static void removeInCreative(Mod_User user){
        if (inCreative.contains(user.getName())) {
            inCreative.remove(user.getName());
        }
    }

    public final static boolean isInAdventure(Mod_User user){
        return inAdventure.contains(user.getName());
    }

    public final static void addInAdventure(Mod_User user){
        inAdventure.add(user.getName());
    }

    public final static void removeInAdventure(Mod_User user){
        if (inAdventure.contains(user.getName())) {
            inAdventure.remove(user.getName());
        }
    }

    public final static Zone getEverywhere(Mod_Entity entity){
        return getEverywhere(entity.getWorld(), entity.getDimension());
    }

    public final static Zone getEverywhere(Mod_Block block){
        return getEverywhere(block.getWorld(), block.getDimension());
    }

    public final static Zone getEverywhere(String world, int dim){
        if (world == null) {
            return getEverywhere(RealmsBase.getServer().getDefaultWorldName().toUpperCase(), dim);
        }
        String everywherename = String.format("EVERYWHERE-%s-DIM%d", world.toUpperCase(), dim);
        if (zones.containsKey(everywherename)) {
            return zones.get(everywherename);
        }
        Zone evr = new Zone(everywherename, null, world, dim);
        return evr;
    }

    public final static void clearOut(){
        zones.clear();
        playerZoneList.clear();
        inRestricted.clear();
        inHealing.clear();
        inCreative.clear();
        inAdventure.clear();
    }
}
