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

import java.sql.DriverManager;
import java.sql.SQLException;

import net.visualillusionsent.mcmod.interfaces.Mod_Item;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class MySQL_Source extends SQL_Source{

    public MySQL_Source() throws DataSourceException{
        testConnection();
    }

    @Override
    public final DataSourceType getType(){
        return DataSourceType.MYSQL;
    }

    @Override
    public final boolean load(){
        try{
            testConnection();
            return super.load();
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("DataSource Exception while trying to load zones");
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    @Override
    public final boolean reloadZone(Zone zone){
        try{
            testConnection();
            return super.reloadZone(zone);
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("DataSource Exception while trying to reload Zone:".concat(zone.getName()));
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    @Override
    public boolean saveZone(Zone zone){
        try{
            testConnection();
            return super.saveZone(zone);
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("DataSource Exception while trying to save Zone: " + zone.getName());
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    @Override
    public boolean deleteZone(Zone zone){
        try{
            testConnection();
            return super.deleteZone(zone);
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("Datasource exception while deleting Zone: " + zone.getName());
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    @Override
    public boolean loadInventories(){
        try{
            testConnection();
            return super.loadInventories();
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("DataSource Exception while trying to load inventories...");
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    @Override
    public boolean saveInventory(Mod_User user, Mod_Item[] items){
        try{
            testConnection();
            return super.saveInventory(user, items);
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("DataSource Exception while trying to save inventory for User: " + user.getName() + "...");
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    @Override
    public boolean deleteInventory(Mod_User user){
        try{
            testConnection();
            return super.deleteInventory(user);
        }
        catch(DataSourceException dsex){
            RealmsLogMan.severe("DataSource Exception while trying to delete inventory for User: " + user.getName() + "...");
            RealmsLogMan.stacktrace(dsex);
        }
        return false;
    }

    private final void testConnection() throws DataSourceException{
        try{
            if(conn == null || conn.isClosed() || !conn.isValid(2)){
                conn = DriverManager.getConnection("jdbc:mysql://" + RealmsBase.getProperties().getStringVal("sql.database.url"), RealmsBase.getProperties().getStringVal("sql.user"), RealmsBase.getProperties().getStringVal("sql.password"));
            }
        }
        catch(SQLException sqle){
            throw new DataSourceException(sqle);
        }
    }
}
