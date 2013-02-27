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
package net.visualillusionsent.mcplugin.realms.data;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.visualillusionsent.lang.DataSourceError;
import net.visualillusionsent.lang.DataSourceType;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class SQLite_Source extends SQL_Source{

    private final String db_Path;

    public SQLite_Source(){
        db_Path = RealmsBase.getProperties().getStringVal("sql.database.url");
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:".concat(db_Path));
        }
        catch(SQLException sqle){
            throw new DataSourceError(sqle);
        }
    }

    @Override
    public final DataSourceType getType(){
        return DataSourceType.SQLITE;
    }

    @Override
    public final void load(){
        File test = new File(db_Path);
        SQLException sqlex = null;
        PreparedStatement ps = null;
        try{
            if(!test.exists()){
                RealmsLogMan.info("Realms DataBase not found. Creating...");
                ps = conn.prepareStatement("CREATE TABLE `" + zone_table + "` " + //
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
            }
            super.load();
        }
        catch(SQLException sqle){
            sqlex = sqle;
        }
        finally{
            try{
                if(ps != null && !ps.isClosed()){
                    ps.close();
                }
            }
            catch(SQLException sqle){}
            if(sqlex != null){
                throw new DataSourceError(sqlex);
            }
        }
    }

    final void closeDatabase(){
        try{
            conn.close();
        }
        catch(SQLException e){}
    }
}
