/*
 * This file is part of Realms.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.zones.InvaildZoneFlagException;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneFlag;
import net.visualillusionsent.realms.zones.ZoneFlagTypes;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.InvaildPermissionTypeException;
import net.visualillusionsent.realms.zones.permission.PermissionType;
import net.visualillusionsent.realms.zones.polygon.PolygonArea;
import net.visualillusionsent.realms.zones.polygon.PolygonConstructException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jason (darkdiplomat)
 */
public final class RealmsPluginAPI {

    public final boolean nameCheck(String zoneName) {
        try {
            ZoneLists.getZoneByName(zoneName);
            return true;
        }
        catch (ZoneNotFoundException ZNFE) {
        }
        return false;
    }

    public final String[] getPlayerZoneNames(Mod_User user) {
        String[] herp = new String[]{ "none" };
        List<Zone> zones = ZoneLists.getplayerZones(user);
        List<String> names = new ArrayList<String>();
        Iterator<Zone> zoneIter = zones.iterator();
        while (zoneIter.hasNext()) {
            names.add(zoneIter.next().getName());
        }
        return names.toArray(herp);
    }

    public final boolean changeZoneFlag(String zoneName, String flag, String setting) {
        try {
            Zone zone = ZoneLists.getZoneByName(zoneName);
            ZoneFlag theSetting = ZoneFlag.getZoneFlag(setting.toUpperCase());
            ZoneFlagTypes theType = ZoneFlagTypes.valueOf(flag.toUpperCase());
            if (zone.getName().startsWith("EVERYWHERE") && theSetting.equals(ZoneFlag.INHERIT)) {
                return false;
            }
            switch (theType) {
                case ADVENTURE:
                    zone.setAdventure(theSetting);
                    return true;
                case ANIMALS:
                    zone.setAnimals(theSetting);
                    return true;
                case BURN:
                    zone.setBurn(theSetting);
                    return true;
                case CREATIVE:
                    zone.setCreative(theSetting);
                    return true;
                case DISPENSERS:
                    zone.setDispensers(theSetting);
                    return true;
                case ENDERMAN:
                    zone.setEnderman(theSetting);
                    return true;
                case EXPLODE:
                    zone.setExplode(theSetting);
                    return true;
                case FALL:
                    zone.setFall(theSetting);
                    return true;
                case FIRE:
                    zone.setFire(theSetting);
                    return true;
                case FLOW:
                    zone.setFlow(theSetting);
                    return true;
                case HEALING:
                    zone.setHealing(theSetting);
                    return true;
                case PHYSICS:
                    zone.setPhysics(theSetting);
                    return true;
                case PISTONS:
                    zone.setPistons(theSetting);
                    return true;
                case POTION:
                    zone.setPotion(theSetting);
                    return true;
                case PVP:
                    zone.setPVP(theSetting);
                    return true;
                case RESTRICTED:
                    zone.setRestricted(theSetting);
                    return true;
                case SANCTUARY:
                    zone.setSanctuary(theSetting);
                    return true;
                case STARVE:
                    zone.setStarve(theSetting);
                    return true;
                case SUFFOCATE:
                    zone.setSuffocate(theSetting);
                    return true;
            }
        }
        catch (InvaildZoneFlagException izte) {
            RealmsLogMan.warning("[API] A Plugin requested a ZoneFlag change that was INVAILD!");
        }
        catch (ZoneNotFoundException znfe) {
            RealmsLogMan.warning("[API] A Plugin gave a Zone name that was INVAILD!");
        }
        catch (IllegalArgumentException iae) {
            RealmsLogMan.warning("[API] A Plugin requested a ZoneFlagType that was INVAILD!");
        }
        return false;
    }

    public final Boolean checkZoneFlag(String zoneName, String flag, boolean absolute) {
        try {
            Zone zone = ZoneLists.getZoneByName(zoneName);
            ZoneFlagTypes theType = ZoneFlagTypes.valueOf(flag.toUpperCase());
            switch (theType) {
                case ADVENTURE:
                    return absolute ? zone.getAbsoluteAdventure().getValue() : zone.getAdventure();
                case ANIMALS:
                    return absolute ? zone.getAbsoluteAnimals().getValue() : zone.getAnimals();
                case BURN:
                    return absolute ? zone.getAbsoluteBurn().getValue() : zone.getBurn();
                case CREATIVE:
                    return absolute ? zone.getAbsoluteCreative().getValue() : zone.getCreative();
                case DISPENSERS:
                    return absolute ? zone.getAbsoluteDispensers().getValue() : zone.getDispensers();
                case ENDERMAN:
                    return absolute ? zone.getAbsoluteEnderman().getValue() : zone.getEnderman();
                case EXPLODE:
                    return absolute ? zone.getAbsoluteExplode().getValue() : zone.getExplode();
                case FALL:
                    return absolute ? zone.getAbsoluteFall().getValue() : zone.getFall();
                case FIRE:
                    return absolute ? zone.getAbsoluteFire().getValue() : zone.getFire();
                case FLOW:
                    return absolute ? zone.getAbsoluteFlow().getValue() : zone.getFlow();
                case HEALING:
                    return absolute ? zone.getAbsoluteHealing().getValue() : zone.getHealing();
                case PHYSICS:
                    return absolute ? zone.getAbsolutePhysics().getValue() : zone.getPhysics();
                case PISTONS:
                    return absolute ? zone.getAbsolutePistons().getValue() : zone.getPistons();
                case POTION:
                    return absolute ? zone.getAbsolutePotion().getValue() : zone.getPotion();
                case PVP:
                    return absolute ? zone.getAbsolutePVP().getValue() : zone.getPVP();
                case RESTRICTED:
                    return absolute ? zone.getAbsoluteRestricted().getValue() : zone.getRestricted();
                case SANCTUARY:
                    return absolute ? zone.getAbsoluteSanctuary().getValue() : zone.getSanctuary();
                case STARVE:
                    return absolute ? zone.getAbsoluteStarve().getValue() : zone.getStarve();
                case SUFFOCATE:
                    return absolute ? zone.getAbsoluteSuffocate().getValue() : zone.getSuffocate();
            }
        }
        catch (IllegalArgumentException iae) {
            RealmsLogMan.warning("[API] A plugin requested a ZoneFlagType that was INVAILD!");
        }
        catch (ZoneNotFoundException znfe) {
            RealmsLogMan.warning("[API] A plugin gave a Zone name that was INVAILD!");
        }
        return null;
    }

    public final boolean createZone(String zoneName, String parentName, String world, int dimension) {
        try {
            if (!ZoneLists.isZone(zoneName)) {
                Zone parent = ZoneLists.getZoneByName(parentName);
                new Zone(zoneName, parent, world, dimension);
                return true;
            }
            else {
                RealmsLogMan.warning("[API] A Plugin tried to create a Zone with an already existing name!");
            }
        }
        catch (ZoneNotFoundException e) {
            RealmsLogMan.warning("[API] A plugin gave a Zone name that was INVAILD!");
        }
        return false;
    }

    public final boolean deleteZone(String zoneName) {
        if (zoneName.startsWith("EVERYWHERE")) {
            return false;
        }
        try {
            Zone zone = ZoneLists.getZoneByName(zoneName);
            zone.delete();
            return true;
        }
        catch (ZoneNotFoundException znfe) {
            RealmsLogMan.warning("[API] A Plugin gave a Zone name that was INVAILD!");
        }
        return false;
    }

    public final boolean setZonePolygon(String zoneName, int ceiling, int floor, List<Integer[]> verticies) {
        if (zoneName.startsWith(zoneName)) {
            return false;
        }
        try {
            Zone zone = ZoneLists.getZoneByName(zoneName);
            String[] verts = (String.valueOf(ceiling) + "," + String.valueOf(floor) + "," + Arrays.toString(verticies.toArray(new Integer[0])).replace("[", "").replace("]", "")).split(",");
            PolygonArea polygon = new PolygonArea(zone, verts);
            for (Zone check : zone.getParent().getChildren()) {
                if (check.contains(zone)) {
                    return false;
                }
            }
            zone.setPolygon(polygon);
            return true;
        }
        catch (ZoneNotFoundException znfe) {
            RealmsLogMan.warning("[API] A Plugin gave a Zone name that was INVAILD!");
        }
        catch (PolygonConstructException e) {
            RealmsLogMan.warning("[API] A Plugin gave bad arguments to create a PolygonArea!");
        }
        return false;
    }

    public final boolean setZonePermission(String zoneName, String userName, String type, boolean grant) {
        try {
            Zone zone = ZoneLists.getZoneByName(zoneName);
            PermissionType permtype = PermissionType.getTypeFromString(type);
            zone.setPermission(userName, permtype, grant, false);
            return true;
        }
        catch (InvaildPermissionTypeException e) {
            RealmsLogMan.warning("[API] A Plugin requested a PermType change that was INVAILD!");
        }
        catch (ZoneNotFoundException e) {
            RealmsLogMan.warning("[API] A Plugin gave a Zone name that was INVAILD!");
        }
        return false;
    }

    public final Boolean checkZonePermission(Mod_User user, Mod_Block block, String permType) {
        try {
            PermissionType type = PermissionType.getTypeFromString(permType.toUpperCase());
            Zone zone = ZoneLists.getInZone(block);
            return Boolean.valueOf(zone.permissionCheck(user, type));
        }
        catch (InvaildPermissionTypeException ipte) {
            RealmsLogMan.warning("[API] A Plugin requested a PermType that was INVAILD!");
        }
        return null;
    }

    public final Boolean checkZonePermission(Mod_User user, String zoneName, String permType) {
        try {
            PermissionType type = PermissionType.getTypeFromString(permType.toUpperCase());
            Zone zone = ZoneLists.getZoneByName(zoneName);
            return Boolean.valueOf(zone.permissionCheck(user, type));
        }
        catch (InvaildPermissionTypeException ipte) {
            RealmsLogMan.warning("[API] A Plugin requested a PermType that was INVAILD!");
        }
        catch (ZoneNotFoundException e) {
            RealmsLogMan.warning("[API] A Plugin gave a Zone name that was INVAILD!");
        }
        return null;
    }
}
