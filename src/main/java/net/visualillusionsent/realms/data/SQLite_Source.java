/*
 * This file is part of Realms.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
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
package net.visualillusionsent.realms.data;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.visualillusionsent.realms.lang.DataSourceError;
import net.visualillusionsent.realms.lang.DataSourceType;
import net.visualillusionsent.realms.RealmsBase;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class SQLite_Source extends SQL_Source{

    public SQLite_Source(){
        String db_Path = RealmsBase.getProperties().getStringVal("sql.database.url");
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:".concat(db_Path));
        }
        catch(SQLException sqle){
            throw new DataSourceError(sqle);
        }
        zone_table = zone_table + '.' + db_Path;
        inv_table = inv_table + '.' + db_Path;
    }

    @Override
    public final DataSourceType getType(){
        return DataSourceType.SQLITE;
    }

    @Override
    public final void load(){
        SQLException sqlex = null;
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS`" + zone_table + "` " + //
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
