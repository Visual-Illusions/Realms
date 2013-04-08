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
package net.visualillusionsent.minecraft.server.mod.plugin.realms.data;

import java.io.File;
import java.util.ArrayList;
import net.visualillusionsent.lang.DataSourceType;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.utils.FileUtils;
import net.visualillusionsent.utils.PropertiesFile;
import net.visualillusionsent.utils.UtilityException;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class RealmsProps{

    private final String dir_Path = "plugins" + File.separator + "Realms" + File.separator;
    private final String props_Path = dir_Path.concat("Realms.ini");
    private PropertiesFile props_File;
    private static ArrayList<Integer> interact_Block = new ArrayList<Integer>();
    private static ArrayList<Integer> interact_Item = new ArrayList<Integer>();
    private static ArrayList<String> commandOverride = new ArrayList<String>();
    private boolean isInitialized = false;

    /**
     * class constructor
     * 
     * @param rhandle
     */
    public RealmsProps(){}

    public synchronized boolean initialize(){
        RealmsLogMan.info("Initializing properties...");
        if (isInitialized) {
            RealmsLogMan.severe("HERP DERP...");
            return false;
        }
        /* Check Storage Directory */
        File dir = new File(dir_Path);
        if (!dir.exists()) {
            RealmsLogMan.info("Creating directories...");
            if (!dir.mkdirs()) {
                RealmsLogMan.severe("Failed to create directories...");
                return false;
            }
        }
        /* Check Properties File */
        File props = new File(props_Path);
        if (!props.exists()) { // Create a new one if non-existent
            RealmsLogMan.info("Creating default properties...");
            try {
                FileUtils.cloneFileFromJar(new File("plugins/Realms.jar").getAbsolutePath(), "resources/default_config.ini", props_Path);
            }
            catch (UtilityException ue) {
                RealmsLogMan.severe("Failed to create Properties..." + ue.getMessage());
                return false;
            }
        }
        try {
            RealmsLogMan.info("Loading properties...");
            props_File = new PropertiesFile(props_Path); // Initialize Properties
            RealmsLogMan.info("Testing properties...");
            // Test properties
            props_File.getInt("wand.type");
            props_File.getInt("pylon.type");
            props_File.getInt("pylon.height");
            props_File.getBoolean("zone.case.sensitive");
            props_File.getLong("sanctuary.timeout");
            props_File.getLong("healing.timeout");
            props_File.getLong("animals.timeout");
            props_File.getLong("restrict.timeout");
            props_File.getBoolean("grant.default");
            props_File.getBoolean("grant.override");
            props_File.getBoolean("sanctuary.mobs");
            props_File.getBoolean("player.explode");
            props_File.getBoolean("tnt.activate");
            props_File.getBoolean("restrict.kill");
            props_File.getString("restrict.message");
            String opBlocks = props_File.getString("interact.blocks");
            addInteractBlock(opBlocks.split(","));
            String opItem = props_File.getString("interact.items");
            addInteractItem(opItem.split(","));
            String WC = props_File.getString("command.override");
            addOverrideCommands(WC.split(","));
            String source = props_File.getString("datasource").toUpperCase();
            DataSourceType.valueOf(source);
            props_File.getString("sql.user");
            props_File.getString("sql.password");
            props_File.getString("sql.database.url");
            props_File.getString("sql.zones.table");
            props_File.getString("sql.inventories.table");
            props_File.getString("lang.locale");
            props_File.getBoolean("check.unstable");
            RealmsLogMan.info("Properties tests passed!");
        }
        catch (UtilityException uex) {
            RealmsLogMan.severe("Failed to load Properties..." + uex.getMessage());
            return false;
        }
        catch (IllegalArgumentException iaex) {
            RealmsLogMan.severe("Failed to understand datasource type...");
            return false;
        }
        return true;
    }

    /**
     * Adds operable Blocks to list
     * 
     * @param ids
     */
    private void addInteractBlock(String[] ids){
        if (ids != null) {
            int bid = 0;
            for (String id : ids) {
                try {
                    bid = Integer.parseInt(id.trim());
                }
                catch (NumberFormatException NFE) {
                    RealmsLogMan.warning("Interact Block ID: " + id + " is invalid!");
                    continue;
                }
                interact_Block.add(bid);
            }
        }
    }

    /**
     * Adds operable Items to list
     * 
     * @param ids
     */
    private void addInteractItem(String[] ids){
        if (ids != null) {
            int bid = 0;
            for (String id : ids) {
                try {
                    bid = Integer.parseInt(id.trim());
                }
                catch (NumberFormatException NFE) {
                    RealmsLogMan.warning("Interact Item ID: " + id + " is invalid!");
                    continue;
                }
                interact_Item.add(bid);
            }
        }
    }

    /**
     * Adds Commands to Override Command Blocking list
     * 
     * @param cmds
     */
    private void addOverrideCommands(String[] cmds){
        if (cmds != null) {
            for (String cmd : cmds) {
                commandOverride.add(cmd.trim());
            }
        }
    }

    public final String getStringVal(String key){
        try {
            return props_File.getString(key);
        }
        catch (UtilityException e) {
            RealmsLogMan.warning("Realms.ini is missing key/value for Key: " + key);
        }
        return "*MISSING VALUE*";
    }

    public final int getIntVal(String key){
        try {
            return props_File.getInt(key);
        }
        catch (UtilityException e) {
            RealmsLogMan.warning("Realms.ini is missing key/value for Key: " + key);
        }
        return (int) Double.NaN; // will probably blow up
    }

    public final long getLongVal(String key){
        try {
            return props_File.getInt(key);
        }
        catch (UtilityException e) {
            RealmsLogMan.warning("Realms.ini is missing key/value for Key: " + key);
        }
        return (long) Double.NaN; // will probably blow up
    }

    public final Boolean getBooleanVal(String key){
        try {
            return props_File.getBoolean(key);
        }
        catch (UtilityException e) {
            RealmsLogMan.warning("Realms.ini is missing key/value for Key: " + key);
        }
        return null;
    }

    public final boolean isCommandAllowed(String[] args){
        int argc = 0;
        StringBuilder verify = new StringBuilder();
        while (argc < args.length) {
            verify.append(args[argc]);
            if (commandOverride.contains(verify.toString())) {
                return true;
            }
            verify.append(" ");
            argc++;
        }
        return false;
    }

    public final boolean isInteractBlock(int id){
        return interact_Block.contains(id);
    }

    public final boolean isInteractItem(int id){
        return interact_Item.contains(id);
    }

    public final void reload(){
        props_File.reload();
    }
}
