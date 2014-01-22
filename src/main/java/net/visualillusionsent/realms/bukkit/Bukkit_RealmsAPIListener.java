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
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms.bukkit;

import java.util.List;

import net.visualillusionsent.realms.RealmsPluginAPI;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneFlag;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Jason (darkdiplomat)
 */
public class Bukkit_RealmsAPIListener{

    private final static RealmsPluginAPI zch = new RealmsPluginAPI();

    /**
     * This class should never be constructed
     */
    private Bukkit_RealmsAPIListener(){}

    /**
     * Checks if a {@link Zone} with the specified name exists
     * 
     * @param zoneName
     *            the name of the {@link Zone} to check
     * @return {@code true} if the {@link Zone} exists, {@code false} otherwise
     */
    public final static boolean nameCheck(String zoneName){
        if(zoneName == null){
            return false;
        }
        return zch.nameCheck(zoneName);
    }

    /**
     * Gets the names of the {@link Zone}s the {@link Player} is currently in
     * 
     * @param player
     *            the player to check zones for
     * @return {@link String} array of the names
     */
    public final static String[] getPlayerZoneNames(Player player){
        if(player == null){
            return new String[]{ "none" };
        }
        Bukkit_User user = new Bukkit_User(player);
        return zch.getPlayerZoneNames(user);
    }

    /**
     * Changes a {@link ZoneFlag}
     * 
     * @param zoneName
     *            the name of a {@link Zone} to change a flag for
     * @param flag
     *            the name of the {@link ZoneFlag} to be changed
     * @param setting
     *            the setting to set it to (ON/OFF/INHERIT)
     * @return {@code true} if successful, {@code false} otherwise
     */
    public final static boolean changeZoneFlag(String zoneName, String flag, String setting){
        return zch.changeZoneFlag(zoneName, flag, setting);
    }

    /**
     * Checks a {@link ZoneFlag}
     * 
     * @param zoneName
     *            the name of a {@link Zone} to check a flag for
     * @param flag
     *            the name of the {@link ZoneFlag} to be checked
     * @param absolute
     *            wether to check the absolute setting for a flag (inheritance checks true/false)
     * @return {@code true} is ON, {@code false} if OFF or INHERIT, {@code null} if error
     */
    public final static Boolean checkZoneFlag(String zoneName, String flag, boolean absolute){
        return zch.checkZoneFlag(zoneName, flag, absolute);
    }

    /**
     * Creates a {@link Zone}
     * 
     * @param zoneName
     *            the name of the {@link Zone} to create
     * @param parentName
     *            the name of the {@link Zone} to set as the parent
     * @param world
     *            the name of the {@link World} the {@link Zone} should be in
     * @param dimension
     *            the index of the {@link World.Dimension} the {@link Zone} should be in
     * @return {@code true} if successful, {@code false} otherwise
     */
    public final static boolean createZone(String zoneName, String parentName, String world, int dimension){
        return zch.createZone(zoneName, parentName, world, dimension);
    }

    /**
     * Deletes a {@link Zone}
     * 
     * @param zoneName
     *            the name of the {@link Zone} to delete
     * @return {@code true} if successful, {@code false} otherwise
     */
    public final static boolean deleteZone(String zoneName){
        return zch.deleteZone(zoneName);
    }

    /**
     * Sets a {@link Zone}'s Polygon
     * 
     * @param zoneName
     *            the name of the {@link Zone} to set a polygon for
     * @param ceiling
     *            the Y coordinate of the ceiling for the {@link Zone}
     * @param floor
     *            the Y coordinate of the floor for the {@link Zone}
     * @param verticies
     *            the Verticies for the points of the Polygon listed as X1 Y1 Z1 X2 Y2 Z2 and so on
     * @return {@code true} if successful, {@code false} otherwise
     */
    public final static boolean setZonePolygon(String zoneName, int ceiling, int floor, List<Integer[]> verticies){
        return zch.setZonePolygon(zoneName, ceiling, floor, verticies);
    }

    /**
     * Sets a {@link Player}'s permission in a zone
     * 
     * @param zoneName
     *            the name of the {@link Zone} to set a {@link Player}'s permission
     * @param userName
     *            the name oft the {@link Player} to set a permission for
     * @param type
     *            the type of permission to set
     * @param grant
     *            {@code boolean} value, {@code true} to Grant, {@code false} to deny
     * @return {@code true} if successful, {@code false} otherwise
     */
    public final static boolean setZonePermission(String zoneName, String userName, String type, boolean grant){
        return zch.setZonePermission(zoneName, userName, type, grant);
    }

    /**
     * Checks a {@link Player}'s permission within a {@link Zone}
     * 
     * @param player
     *            the {@link Player} to check permission for
     * @param block
     *            the {@link block} to get the {@link Zone} for
     * @param permType
     *            the type of permission to check
     * @return {@code true} if allowed, {@code false} if denied, {@code null} if error
     */
    public final static Boolean checkZonePermission(Player player, Block block, String permType){
        Bukkit_User user = new Bukkit_User(player);
        Bukkit_Block cBlock = new Bukkit_Block(block);
        return zch.checkZonePermission(user, cBlock, permType);
    }

    /**
     * Checks a {@link Player}'s permission within a {@link Zone}
     * 
     * @param player
     *            the {@link Player} to check permission for
     * @param zoneName
     *            the name of the {@link Zone} to get a permission for
     * @param permType
     *            the type of permission to check
     * @return {@code true} if allowed, {@code false} if denied, {@code null} if error
     */
    public final static Boolean checkZonePermission(Player player, String zoneName, String permType){
        Bukkit_User user = new Bukkit_User(player);
        return zch.checkZonePermission(user, zoneName, permType);
    }
}
