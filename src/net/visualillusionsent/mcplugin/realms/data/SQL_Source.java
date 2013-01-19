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
package net.visualillusionsent.mcplugin.realms.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.visualillusionsent.mcmod.interfaces.Mod_Item;
import net.visualillusionsent.mcmod.interfaces.Mod_ItemEnchantment;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneConstructException;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.mcplugin.realms.zones.permission.Permission;
import net.visualillusionsent.mcplugin.realms.zones.permission.PermissionConstructException;
import net.visualillusionsent.mcplugin.realms.zones.polygon.PolygonArea;
import net.visualillusionsent.mcplugin.realms.zones.polygon.PolygonConstructException;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public abstract class SQL_Source implements DataSource {
    protected Connection conn;
    protected String zone_table = RealmsBase.getProperties().getStringVal("sql.zones.table");
    protected String inv_table = RealmsBase.getProperties().getStringVal("sql.inventories.table");

    public boolean load() {
        SQLException sqlex = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int load = 0;
        try {
            RealmsLogMan.info("Testing Zone table and creating if needed...");
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `" + zone_table + "` " + //
            "(`name` VARCHAR(30) NOT NULL," + //
            " `world` TEXT NOT NULL," + //
            " `dimension` int(1) NOT NULL," + //
            " `parent` TEXT NOT NULL," + //
            " `greeting` TEXT NOT NULL," + //
            " `farewell` TEXT NOT NULL," + //
            " `adventure` VARCHAR(10) NOT NULL," + //
            " `animals` VARCHAR(10) NOT NULL," + //
            " `burn` VARCHAR(10) NOT NULL," + //
            " `creative` VARCHAR(10) NOT NULL," + //
            " `dispensers` VARCHAR(10) NOT NULL," + //
            " `enderman` VARCHAR(10) NOT NULL," + //
            " `explode` VARCHAR(10) NOT NULL," + //
            " `fall` VARCHAR(10) NOT NULL," + //
            " `fire` VARCHAR(10) NOT NULL," + //
            " `flow` VARCHAR(10) NOT NULL," + //
            " `healing` VARCHAR(10) NOT NULL," + //
            " `physics` VARCHAR(10) NOT NULL," + //
            " `pistons` VARCHAR(10) NOT NULL," + //
            " `potion` VARCHAR(10) NOT NULL," + //
            " `pvp` VARCHAR(10) NOT NULL," + //
            " `restricted` VARCHAR(10) NOT NULL," + //
            " `sanctuary` VARCHAR(10) NOT NULL," + //
            " `starve` VARCHAR(10) NOT NULL," + //
            " `suffocate` VARCHAR(10) NOT NULL," + //
            " `polygon` TEXT NOT NULL," + //
            " `permissions` TEXT NOT NULL," + //
            " PRIMARY KEY (`name`))");
            RealmsLogMan.log(RLevel.GENERAL, "Executing Zone table statement...");
            ps.execute();
            RealmsLogMan.log(RLevel.GENERAL, "Closing statement...");
            ps.close();
            List<Object[]> delayLoad = new ArrayList<Object[]>();
            List<String> knownZones = new ArrayList<String>();

            RealmsLogMan.log(RLevel.GENERAL, "Preparing to read table...");
            ps = conn.prepareStatement("SELECT * FROM `" + zone_table + "`");
            RealmsLogMan.log(RLevel.GENERAL, "Executing statement to read table...");
            rs = ps.executeQuery();
            RealmsLogMan.log(RLevel.GENERAL, "Reading Zones...");
            while (rs.next()) {
                String name = rs.getString("name");
                knownZones.add(name);
                String world = rs.getString("world");
                String dimension = rs.getString("dimension");
                String parent = rs.getString("parent");
                String greeting = rs.getString("greeting").trim().isEmpty() ? null : rs.getString("greeting");
                String farewell = rs.getString("farewell").trim().isEmpty() ? null : rs.getString("farewell");

                String adventure = parseZoneFlagElement(rs.getString("adventure"));
                String animals = parseZoneFlagElement(rs.getString("animals"));
                String burn = parseZoneFlagElement(rs.getString("burn"));
                String creative = parseZoneFlagElement(rs.getString("creative"));
                String dispensers = parseZoneFlagElement(rs.getString("dispensers"));
                String enderman = parseZoneFlagElement(rs.getString("enderman"));
                String explode = parseZoneFlagElement(rs.getString("explode"));
                String fall = parseZoneFlagElement(rs.getString("fall"));
                String fire = parseZoneFlagElement(rs.getString("fire"));
                String flow = parseZoneFlagElement(rs.getString("flow"));
                String healing = parseZoneFlagElement(rs.getString("healing"));
                String physics = parseZoneFlagElement(rs.getString("physics"));
                String pistons = parseZoneFlagElement(rs.getString("pistons"));
                String potion = parseZoneFlagElement(rs.getString("potion"));
                String pvp = parseZoneFlagElement(rs.getString("pvp"));
                String restricted = parseZoneFlagElement(rs.getString("restricted"));
                String sanctuary = parseZoneFlagElement(rs.getString("sanctuary"));
                String starve = parseZoneFlagElement(rs.getString("starve"));
                String suffocate = parseZoneFlagElement(rs.getString("suffocate"));
                String testPoly = rs.getString("polygon");

                String[] poly = null;
                if (!testPoly.equals("null")) {
                    poly = testPoly.split(",");
                }
                String testPerm = rs.getString("permissions");
                String[] perms = null;
                if (!testPerm.trim().isEmpty()) {
                    perms = testPerm.split(":");
                }

                if (!name.startsWith("EVERYWHERE")) {
                    try {
                        ZoneLists.getZoneByName(parent);
                    }
                    catch (ZoneNotFoundException e) {
                        //save zone for delay load
                        delayLoad.add(new Object[] { name, world, dimension, parent, greeting, farewell, //
                        adventure, animals, burn, creative, dispensers, enderman, explode, fall, fire, flow, healing, physics, pistons, potion, pvp, restricted, sanctuary, starve, suffocate, //
                        poly, perms });
                        continue;
                    }
                }
                RealmsLogMan.log(RLevel.GENERAL, "Creating Zone: " + name);
                try {
                    Zone temp = new Zone(name, world, dimension, parent, greeting, farewell, //
                    adventure, animals, burn, creative, dispensers, enderman, explode, fall, fire, flow, healing, physics, pistons, potion, pvp, restricted, sanctuary, starve, suffocate);
                    load++;
                    if (poly != null) {
                        temp.setPolygon(new PolygonArea(temp, poly));
                    }
                    if (perms != null) {
                        for (String perm : perms) {
                            try {
                                Permission permTemp = new Permission(perm.split(","));
                                temp.setPermission(permTemp);
                            }
                            catch (PermissionConstructException e) {
                                RealmsLogMan.warning("Invaild permission for Zone: ".concat(name).concat(" Perm: ").concat(perm));
                            }
                        }
                    }
                }
                catch (ZoneConstructException e) {
                    RealmsLogMan.warning("Failed to construct Zone:".concat(name) + " Cause: " + e.getMessage());
                    RealmsLogMan.stacktrace(e);
                }
                catch (PolygonConstructException e) {
                    RealmsLogMan.warning("Failed to construct Polygon for Zone:".concat(name));
                    //RealmsLogMan.stacktrace(e);
                }
                RealmsLogMan.log(RLevel.GENERAL, "Zone created.");
            }

            RealmsLogMan.log(RLevel.GENERAL, "Checking delayed load Zones...");
            Iterator<Object[]> objIter = delayLoad.iterator();
            while (!delayLoad.isEmpty()) {
                while (objIter.hasNext()) {
                    Object[] obj = objIter.next();
                    try {
                        if (knownZones.contains((String) obj[3])) {
                            try {
                                ZoneLists.getZoneByName((String) obj[3]);
                            }
                            catch (ZoneNotFoundException e1) {
                                //Parent doesnt appear to have been loaded yet...
                                continue;
                            }
                            String[] zoneCon = new String[obj.length - 2];
                            System.arraycopy(obj, 0, zoneCon, 0, zoneCon.length);
                            Zone temp = new Zone(zoneCon);
                            load++;
                            if (obj[25] != null) {
                                temp.setPolygon(new PolygonArea(temp, (String[]) obj[25]));
                            }
                            if (obj[26] != null) {
                                for (String perm : (String[]) obj[26]) {
                                    try {
                                        Permission permTemp = new Permission(perm.split(","));
                                        temp.setPermission(permTemp);
                                    }
                                    catch (PermissionConstructException e) {
                                        RealmsLogMan.warning("Invaild permission for Zone: ".concat((String) obj[0]).concat(" Perm: ").concat(perm));
                                    }
                                }
                            }
                            objIter.remove();
                        }
                        else {
                            //OH NOES - Orphaned Zone, removed
                            RealmsLogMan.log(RLevel.GENERAL, "Zone: " + (String) obj[0] + " Does not have a vaild parent... Skipping...");
                            objIter.remove();
                        }
                    }
                    catch (ZoneConstructException e) {
                        RealmsLogMan.warning("Failed to construct Zone:".concat((String) obj[0]));
                    }
                    catch (PolygonConstructException e) {
                        RealmsLogMan.warning("Failed to construct Polygon for Zone:".concat((String) obj[0]));
                    }
                }
            }
            RealmsLogMan.info("Loaded ".concat(String.valueOf(load)).concat(" zones."));
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (AbstractMethodError e) {} //SQLite weird stuff
            catch (Exception e) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to load Zones...", sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    public boolean reloadZone(Zone zone) {
        SQLException sqlex = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM `" + zone_table + "` WHERE name=?");
            ps.setString(1, zone.getName());
            rs = ps.executeQuery();
            rs.next();
            String name = rs.getString("name");
            String world = rs.getString("world");
            String dimension = rs.getString("dimension");
            String parent = rs.getString("parent");
            String greeting = rs.getString("greeting").trim().isEmpty() ? null : rs.getString("greeting");
            String farewell = rs.getString("farewell").trim().isEmpty() ? null : rs.getString("farewell");
            String adventure = parseZoneFlagElement(rs.getString("adventure"));
            String animals = parseZoneFlagElement(rs.getString("animals"));
            String burn = parseZoneFlagElement(rs.getString("burn"));
            String creative = parseZoneFlagElement(rs.getString("creative"));
            String dispensers = parseZoneFlagElement(rs.getString("dispensers"));
            String enderman = parseZoneFlagElement(rs.getString("enderman"));
            String explode = parseZoneFlagElement(rs.getString("explode"));
            String fall = parseZoneFlagElement(rs.getString("fall"));
            String fire = parseZoneFlagElement(rs.getString("fire"));
            String flow = parseZoneFlagElement(rs.getString("flow"));
            String healing = parseZoneFlagElement(rs.getString("healing"));
            String physics = parseZoneFlagElement(rs.getString("physics"));
            String pistons = parseZoneFlagElement(rs.getString("pistons"));
            String potion = parseZoneFlagElement(rs.getString("potion"));
            String pvp = parseZoneFlagElement(rs.getString("pvp"));
            String restricted = parseZoneFlagElement(rs.getString("restricted"));
            String sanctuary = parseZoneFlagElement(rs.getString("sanctuary"));
            String starve = parseZoneFlagElement(rs.getString("starve"));
            String suffocate = parseZoneFlagElement(rs.getString("suffocate"));
            String testPoly = rs.getString("polygon");

            String[] poly = null;
            if (!testPoly.equals("null")) {
                poly = testPoly.split(",");
            }
            String testPerm = rs.getString("permissions");
            String[] perms = null;
            if (!testPerm.trim().isEmpty()) {
                perms = testPerm.split(":");
            }

            try {
                Zone temp = new Zone(name, world, dimension, parent, greeting, farewell, //
                adventure, animals, burn, creative, dispensers, enderman, explode, fall, fire, flow, healing, physics, pistons, potion, pvp, restricted, sanctuary, starve, suffocate);
                if (poly != null) {
                    temp.setPolygon(new PolygonArea(temp, poly));
                }
                if (perms != null) {
                    for (String perm : perms) {
                        try {
                            Permission permTemp = new Permission(perm.split(","));
                            temp.setPermission(permTemp);
                        }
                        catch (PermissionConstructException e) {
                            RealmsLogMan.warning("Invaild permission for Zone: ".concat(name).concat(" Perm: ").concat(perm));
                        }
                    }
                }
                RealmsLogMan.info("Zone ".concat(zone.getName()).concat(" reloaded."));
            }
            catch (ZoneConstructException e) {
                RealmsLogMan.warning("Failed to construct Zone:".concat(name) + " Cause: " + e.getMessage(), e);
            }
            catch (PolygonConstructException e) {
                RealmsLogMan.warning("Failed to construct Polygon for Zone:".concat(name));
            }
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (AbstractMethodError e) {} //SQLite weird stuff
            catch (Exception e) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to reload Zone:".concat(zone.getName()), sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    public boolean saveZone(Zone zone) {
        RealmsLogMan.info("Saving Zone: ".concat(zone.getName()));
        SQLException sqlex = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM " + zone_table + " WHERE name=?");
            ps.setString(1, zone.getName());
            rs = ps.executeQuery();
            boolean found = rs.next();
            if (found) {
                ps = conn.prepareStatement(//
                "UPDATE " + zone_table + " SET" + //
                " world=?," + // 1
                " dimension=?," + // 2
                " parent=?," + // 3
                " greeting=?," + // 4
                " farewell=?," + // 5
                " adventure=?," + // 6
                " animals=?," + // 7
                " burn=?," + // 8
                " creative=?," + // 9
                " dispensers=?," + // 10
                " enderman=?," + // 11
                " explode=?," + // 12
                " fall=?," + // 13
                " fire=?," + // 14
                " flow=?," + // 15
                " healing=?," + // 16
                " physics=?," + // 17
                " pistons=?," + // 18
                " potion=?," + // 19
                " pvp=?," + // 20
                " restricted=?," + // 21
                " sanctuary=?," + // 22
                " starve=?," + // 23
                " suffocate=?," + // 24
                " polygon=?," + // 25
                " permissions=?" + // 26
                " WHERE name=?"); //27

                ps.setString(1, zone.getWorld());
                ps.setInt(2, zone.getDimension());
                ps.setString(3, zone.getParent() != null ? zone.getParent().getName() : "null");
                ps.setString(4, zone.getGreeting() != null ? zone.getGreeting() : "");
                ps.setString(5, zone.getFarewell() != null ? zone.getFarewell() : "");
                ps.setString(6, zone.getAbsoluteAdventure().toString());
                ps.setString(7, zone.getAbsoluteAnimals().toString());
                ps.setString(8, zone.getAbsoluteBurn().toString());
                ps.setString(9, zone.getAbsoluteCreative().toString());
                ps.setString(10, zone.getAbsoluteDispensers().toString());
                ps.setString(11, zone.getAbsoluteEnderman().toString());
                ps.setString(12, zone.getAbsoluteExplode().toString());
                ps.setString(13, zone.getAbsoluteFall().toString());
                ps.setString(14, zone.getAbsoluteFire().toString());
                ps.setString(15, zone.getAbsoluteFlow().toString());
                ps.setString(16, zone.getAbsoluteFlow().toString());
                ps.setString(17, zone.getAbsolutePhysics().toString());
                ps.setString(18, zone.getAbsolutePistons().toString());
                ps.setString(19, zone.getAbsolutePotion().toString());
                ps.setString(20, zone.getAbsolutePVP().toString());
                ps.setString(21, zone.getAbsoluteRestricted().toString());
                ps.setString(22, zone.getAbsoluteSanctuary().toString());
                ps.setString(23, zone.getAbsoluteStarve().toString());
                ps.setString(24, zone.getAbsoluteSuffocate().toString());
                ps.setString(25, zone.isEmpty() ? "null" : zone.getPolygon().toString());

                StringBuilder permBuild = new StringBuilder();
                for (Permission perm : zone.getPerms()) {
                    permBuild.append(perm);
                    permBuild.append(":");
                }

                ps.setString(26, permBuild.toString());
                ps.setString(27, zone.getName());
                ps.execute();
            }
            else {
                ps.close();
                ps = conn.prepareStatement("INSERT INTO `" + zone_table + "`" + //
                " (name," + // 1
                "world," + // 2
                "dimension," + // 3
                "parent," + // 4
                "greeting," + // 5
                "farewell," + // 6
                "adventure," + // 7
                "animals," + // 8
                "burn," + // 9
                "creative," + // 10
                "dispensers," + // 11
                "enderman," + // 12
                "explode," + // 13
                "fall," + // 14
                "fire," + // 15
                "flow," + // 16
                "healing," + // 17
                "physics," + // 18
                "pistons," + // 19
                "potion," + // 20
                "pvp," + // 21
                "restricted," + // 22
                "sanctuary," + // 23
                "starve," + // 24
                "suffocate," + // 25
                "polygon," + // 26
                "permissions) " + // 27
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, zone.getName());
                ps.setString(2, zone.getWorld());
                ps.setInt(3, zone.getDimension());
                ps.setString(4, zone.getParent() != null ? zone.getParent().getName() : "null");
                ps.setString(5, zone.getGreeting() != null ? zone.getGreeting() : "");
                ps.setString(6, zone.getFarewell() != null ? zone.getFarewell() : "");
                ps.setString(7, zone.getAbsoluteAdventure().toString());
                ps.setString(8, zone.getAbsoluteAnimals().toString());
                ps.setString(9, zone.getAbsoluteBurn().toString());
                ps.setString(10, zone.getAbsoluteCreative().toString());
                ps.setString(11, zone.getAbsoluteDispensers().toString());
                ps.setString(12, zone.getAbsoluteEnderman().toString());
                ps.setString(13, zone.getAbsoluteExplode().toString());
                ps.setString(14, zone.getAbsoluteFall().toString());
                ps.setString(15, zone.getAbsoluteFire().toString());
                ps.setString(16, zone.getAbsoluteFlow().toString());
                ps.setString(17, zone.getAbsoluteFlow().toString());
                ps.setString(18, zone.getAbsolutePhysics().toString());
                ps.setString(19, zone.getAbsolutePistons().toString());
                ps.setString(20, zone.getAbsolutePotion().toString());
                ps.setString(21, zone.getAbsolutePVP().toString());
                ps.setString(22, zone.getAbsoluteRestricted().toString());
                ps.setString(23, zone.getAbsoluteSanctuary().toString());
                ps.setString(24, zone.getAbsoluteStarve().toString());
                ps.setString(25, zone.getAbsoluteSuffocate().toString());
                ps.setString(26, zone.isEmpty() ? "null" : zone.getPolygon().toString());

                StringBuilder permBuild = new StringBuilder();
                for (Permission perm : zone.getPerms()) {
                    permBuild.append(perm);
                    permBuild.append(":");
                }

                ps.setString(27, permBuild.toString());
                ps.execute();
            }
            RealmsLogMan.info("Zone saved!");
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (AbstractMethodError e) {} //SQLite weird stuff
            catch (Exception e) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to save Zone...", sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    public boolean deleteZone(Zone zone) {
        SQLException sqlex = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM " + zone_table + " WHERE name=?");
            ps.setString(1, zone.getName());
            ps.execute();
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (AbstractMethodError e) {} //SQLite weird stuff
            catch (Exception e) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to delete Zone...", sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean loadInventories() {
        SQLException sqlex = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            RealmsLogMan.info("Testing Inventory table and creating if needed...");
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `" + inv_table + "` " + //
            "(`name` VARCHAR(16) NOT NULL," + //
            " `item0` TEXT NOT NULL," + //
            " `item1` TEXT NOT NULL," + //
            " `item2` TEXT NOT NULL," + //
            " `item3` TEXT NOT NULL," + //
            " `item4` TEXT NOT NULL," + //
            " `item5` TEXT NOT NULL," + //
            " `item6` TEXT NOT NULL," + //
            " `item7` TEXT NOT NULL," + //
            " `item8` TEXT NOT NULL," + //
            " `item9` TEXT NOT NULL," + //
            " `item10` TEXT NOT NULL," + //
            " `item11` TEXT NOT NULL," + //
            " `item12` TEXT NOT NULL," + //
            " `item13` TEXT NOT NULL," + //
            " `item14` TEXT NOT NULL," + //
            " `item15` TEXT NOT NULL," + //
            " `item16` TEXT NOT NULL," + //
            " `item17` TEXT NOT NULL," + //
            " `item18` TEXT NOT NULL," + //
            " `item19` TEXT NOT NULL," + //
            " `item20` TEXT NOT NULL," + //
            " `item21` TEXT NOT NULL," + //
            " `item22` TEXT NOT NULL," + //
            " `item23` TEXT NOT NULL," + //
            " `item24` TEXT NOT NULL," + //
            " `item25` TEXT NOT NULL," + //
            " `item26` TEXT NOT NULL," + //
            " `item27` TEXT NOT NULL," + //
            " `item28` TEXT NOT NULL," + //
            " `item29` TEXT NOT NULL," + //
            " `item30` TEXT NOT NULL," + //
            " `item31` TEXT NOT NULL," + //
            " `item32` TEXT NOT NULL," + //
            " `item33` TEXT NOT NULL," + //
            " `item34` TEXT NOT NULL," + //
            " `item35` TEXT NOT NULL," + //
            " `item36` TEXT NOT NULL," + //
            " `item37` TEXT NOT NULL," + //
            " `item38` TEXT NOT NULL," + //
            " `item39` TEXT NOT NULL," + //
            " PRIMARY KEY (`name`))");
            ps.execute();
            ps.close();

            RealmsLogMan.info("Loading inventories...");
            ps = conn.prepareStatement("SELECT * FROM `" + inv_table + "`");
            rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                Mod_Item[] items = new Mod_Item[40];
                Arrays.fill(items, null);
                for (int index = 0; index < 40; index++) {
                    items[index] = constructFromString(rs.getString("item".concat(String.valueOf(index))));
                }
                RealmsBase.storeInventory(name, items);
            }
            RealmsLogMan.info("Loaded inventories.");
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (AbstractMethodError e) {} //SQLite weird stuff
            catch (Exception e) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to load inventories...", sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("resource")
    @Override
    public boolean saveInventory(Mod_User user, Mod_Item[] items) {
        RealmsLogMan.info("Saving inventory for User: ".concat(user.getName()));
        SQLException sqlex = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM " + inv_table + " WHERE name=?");
            ps.setString(1, user.getName());
            rs = ps.executeQuery();
            boolean found = rs.next();
            if (found) {
                RealmsLogMan.log(RLevel.GENERAL, "Inventory exists in database. Updating...");
                ps = conn.prepareStatement(//
                "UPDATE " + inv_table + " SET" + //
                " item0=?," + // 1
                " item1=?," + // 2
                " item2=?," + // 3
                " item3=?," + // 4
                " item4=?," + // 5
                " item5=?," + // 6
                " item6=?," + // 7
                " item7=?," + // 8
                " item8=?," + // 9
                " item9=?," + // 10
                " item10=?," + // 11
                " item11=?," + // 12
                " item12=?," + // 13
                " item13=?," + // 14
                " item14=?," + // 15
                " item15=?," + // 16
                " item16=?," + // 17
                " item17=?," + // 18
                " item18=?," + // 19
                " item19=?," + // 20
                " item20=?," + // 21
                " item21=?," + // 22
                " item22=?," + // 23
                " item23=?," + // 24
                " item24=?," + // 25
                " item25=?," + // 26
                " item26=?," + // 27
                " item27=?," + // 28
                " item28=?," + // 29
                " item29=?," + // 30
                " item30=?," + // 31
                " item31=?," + // 32
                " item32=?," + // 33
                " item33=?," + // 34
                " item34=?," + // 35
                " item35=?," + // 36
                " item36=?," + // 37
                " item37=?," + // 38
                " item38=?," + // 39
                " item39=?" + // 40
                " WHERE name=?"); //41

                for (int index = 0; index < 40; index++) {
                    ps.setString(index + 1, items[index] != null ? items[index].toString() : "null");
                }

                ps.setString(41, user.getName());
                ps.execute();
            }
            else {
                RealmsLogMan.log(RLevel.GENERAL, "Inventory does not exists in database. Inserting...");
                ps = conn.prepareStatement("INSERT INTO `" + inv_table + "`" + //
                " (name," + // 1
                "item0," + // 2
                "item1," + // 3
                "item2," + // 4
                "item3," + // 5
                "item4," + // 6
                "item5," + // 7
                "item6," + // 8
                "item7," + // 9
                "item8," + // 10
                "item9," + // 11
                "item10," + //12
                "item11," + // 13
                "item12," + // 14
                "item13," + // 15
                "item14," + // 16
                "item15," + // 17
                "item16," + // 18
                "item17," + // 19
                "item18," + // 20
                "item19," + // 21
                "item20," + // 22
                "item21," + // 23
                "item22," + // 24
                "item23," + // 25
                "item24," + // 26
                "item25," + // 27
                "item26," + // 28
                "item27," + // 29
                "item28," + // 30
                "item29," + // 31
                "item30," + // 32
                "item31," + // 33
                "item32," + // 34
                "item33," + // 35
                "item34," + // 36
                "item35," + // 37
                "item36," + // 38
                "item37," + // 39
                "item38," + // 40
                "item39) " + // 41
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, user.getName());
                for (int index = 0; index < 40; index++) {
                    ps.setString((index + 2), items[index] != null ? items[index].toString() : "null");
                }
                ps.execute();
            }
            RealmsLogMan.info("Inventory saved!");
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (AbstractMethodError e) {} //SQLite weird stuff
            catch (Exception e) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to save Inventory...", sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteInventory(Mod_User user) {
        SQLException sqlex = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM " + inv_table + " WHERE name=?");
            ps.setString(1, user.getName());
            ps.execute();
        }
        catch (SQLException sqle) {
            sqlex = sqle;
        }
        finally {
            try {
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            }
            catch (SQLException sqle) {}

            if (sqlex != null) {
                RealmsLogMan.severe("Failed to delete inventory...", sqlex);
            }
            else {
                return true;
            }
        }
        return false;
    }

    private final String parseZoneFlagElement(String text) {
        if (text.matches("on|off|inherit")) {
            return text;
        }
        return "inherit";
    }

    private final Mod_Item constructFromString(String item) {
        if (item.equals("null")) {
            return null;
        }
        try {
            String[] it = item.split(",");
            List<Mod_ItemEnchantment> enchants = new ArrayList<Mod_ItemEnchantment>();
            List<String> lore = new ArrayList<String>();
            int id = Integer.parseInt(it[0]);
            int amount = Integer.parseInt(it[1]);
            int damage = Integer.parseInt(it[2]);
            String name = it[3].equals("NO_NAME_FOR_THIS_ITEM") ? null : it[3];
            for (int index = 4; index < it.length; index++) {
                if (it[index].contains(":")) {
                    String[] ench = it[index].split(":");
                    int enchId = Integer.parseInt(ench[0]);
                    int enchLvl = Integer.parseInt(ench[1]);
                    enchants.add(RealmsBase.getServer().constructEnchantment(enchId, enchLvl));
                }
                else {
                    lore.add(it[index]);
                }
            }
            Mod_ItemEnchantment[] enchs = enchants.isEmpty() ? null : enchants.toArray(new Mod_ItemEnchantment[0]);
            String[] lores = lore.isEmpty() ? null : lore.toArray(new String[0]);
            return RealmsBase.getServer().constructItem(id, amount, damage, name, enchs, lores);
        }
        catch (Exception ex) {} //FAIL!
        return null;
    }
}
