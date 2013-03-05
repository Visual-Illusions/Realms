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
package net.visualillusionsent.minecraft.server.mod.plugin.realms.data;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.visualillusionsent.lang.DataSourceError;
import net.visualillusionsent.lang.DataSourceType;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class PostgreSQL_Source extends SQL_Source{

    public PostgreSQL_Source(){
        testConnection();
    }

    @Override
    public final DataSourceType getType(){
        return DataSourceType.POSTGRESQL;
    }

    @Override
    public final void load(){
        testConnection();
        try{
            PreparedStatement ps = null;
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
            ps.execute();
            ps.close();
        }
        catch(SQLException sqlex){
            throw new DataSourceError(sqlex);
        }
        super.load();
    }

    @Override
    public synchronized final boolean reloadZone(Zone zone){
        try{
            testConnection();
            return super.reloadZone(zone);
        }
        catch(DataSourceError dse){
            RealmsLogMan.severe("DataSource Error while trying to reload Zone:".concat(zone.getName()));
            RealmsLogMan.stacktrace(dse);
        }
        return false;
    }

    @Override
    public synchronized final boolean saveZone(Zone zone){
        try{
            testConnection();
            return super.saveZone(zone);
        }
        catch(DataSourceError dse){
            RealmsLogMan.severe("DataSource Error while trying to save Zone: " + zone.getName());
            RealmsLogMan.stacktrace(dse);
        }
        return false;
    }

    @Override
    public synchronized final boolean deleteZone(Zone zone){
        try{
            testConnection();
            return super.deleteZone(zone);
        }
        catch(DataSourceError dse){
            RealmsLogMan.severe("Datasource Error while deleting Zone: " + zone.getName());
            RealmsLogMan.stacktrace(dse);
        }
        return false;
    }

    @Override
    public synchronized final void loadInventories(){
        testConnection();
        super.loadInventories();
    }

    @Override
    public synchronized final boolean saveInventory(Mod_User user, Mod_Item[] items){
        try{
            testConnection();
            return super.saveInventory(user, items);
        }
        catch(DataSourceError dse){
            RealmsLogMan.severe("DataSource Error while trying to save inventory for User: " + user.getName() + "...");
            RealmsLogMan.stacktrace(dse);
        }
        return false;
    }

    @Override
    public synchronized final boolean deleteInventory(Mod_User user){
        try{
            testConnection();
            return super.deleteInventory(user);
        }
        catch(DataSourceError dse){
            RealmsLogMan.severe("DataSource Error while trying to delete inventory for User: " + user.getName() + "...");
            RealmsLogMan.stacktrace(dse);
        }
        return false;
    }

    private final void testConnection(){
        try{
            if(conn == null || conn.isClosed() || !conn.isValid(2)){
                conn = DriverManager.getConnection("jdbc:postgresql://" + RealmsBase.getProperties().getStringVal("sql.database.url"), RealmsBase.getProperties().getStringVal("sql.user"), RealmsBase.getProperties().getStringVal("sql.password"));
            }
        }
        catch(SQLException sqle){
            throw new DataSourceError(sqle);
        }
    }
}
