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
import java.util.List;

import net.visualillusionsent.mcplugin.realms.RealmsPluginAPI;
import net.visualillusionsent.mcplugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Canary_RealmsAPIListener {
    private final static RealmsPluginAPI zch = new RealmsPluginAPI();

    /**
     * This class should never be constructed
     */
    private Canary_RealmsAPIListener() {}

    /**
     * Checks if a {@link Zone} with the specified name exists
     * 
     * @param zoneName
     *            the name of the {@link Zone} to check
     * @return {@code true} if the {@link Zone} exists, {@code false} otherwise
     */
    public final static boolean nameCheck(String zoneName) {
        if (zoneName == null) {
            return false;
        }
        return zch.nameCheck(zoneName);
    }

    public final static String[] getPlayerZoneNames(Player player) {
        if (player == null) {
            return new String[] { "none" };
        }
        Canary_User user = new Canary_User(player);
        return zch.getPlayerZoneNames(user);
    }

    public final static boolean changeZoneFlag(String zoneName, String flag, String setting) {
        return zch.changeZoneFlag(zoneName, flag, setting);
    }

    public final static Boolean checkZoneFlag(String zoneName, String flag, boolean absolute) {
        return zch.checkZoneFlag(zoneName, flag, absolute);
    }

    public final static boolean createZone(String zoneName, String parentName, String world, int dimension) {
        return zch.createZone(zoneName, parentName, world, dimension);
    }

    public final static boolean deleteZone(String zoneName) {
        return zch.deleteZone(zoneName);
    }

    public final static boolean setZonePolygon(String zoneName, int ceiling, int floor, List<Integer[]> verticies) {
        return zch.setZonePolygon(zoneName, ceiling, floor, verticies);
    }

    public final static boolean setZonePermission(String zoneName, String userName, String type, boolean grant) {
        return zch.setZonePermission(zoneName, userName, type, grant);
    }

    public final static Boolean checkZonePermission(Player player, Block block, String permType) {
        Canary_User user = new Canary_User(player);
        Canary_Block cBlock = new Canary_Block(block);
        return zch.checkZonePermission(user, cBlock, permType);
    }

    public final static Boolean checkZonePermission(Player player, String zoneName, String permType) {
        Canary_User user = new Canary_User(player);
        return zch.checkZonePermission(user, zoneName, permType);
    }
}
