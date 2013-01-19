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

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import net.visualillusionsent.mcmod.interfaces.Mod_Caller;
import net.visualillusionsent.mcmod.interfaces.Mod_Item;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class DataSourceHandler {
    private final OutputQueue queue;
    private final DataSource source;
    private final OutputThread outThread;

    public DataSourceHandler(DataSourceType type) throws DataSourceException {
        if (type == DataSourceType.XML) {
            testJDOM();
            source = new XML_Source();
        }
        else if (type == DataSourceType.MYSQL) {
            testMySQLDriver();
            source = new MySQL_Source();
        }
        else if (type == DataSourceType.SQLITE) {
            testSQLiteDriver();
            source = new SQLite_Source();
        }
        else {
            throw new DataSourceException("Invaild DataSourceType...");
        }

        if (!source.load()) {
            throw new DataSourceException("Database load failure...");
        }

        if (!source.loadInventories()) {
            throw new DataSourceException("Database load failure...");
        }

        queue = new OutputQueue();
        outThread = new OutputThread(this, source);
        outThread.start();
    }

    public void addToQueue(OutputAction action, Zone zone) {
        queue.add(new DataSourceActionContainer(action, zone, null, null));
    }

    public void addToQueue(OutputAction action, Mod_User user) {
        queue.add(new DataSourceActionContainer(action, null, user, null));
    }

    public void addToQueue(OutputAction action, Mod_User user, Mod_Item[] items) {
        queue.add(new DataSourceActionContainer(action, null, user, items));
    }

    OutputQueue getQueue() {
        return queue;
    }

    private void clearQueue() {
        queue.clear();
    }

    public void killOutput() {
        clearQueue();
        outThread.terminate();
        if (source.getType() == DataSourceType.SQLITE) {
            ((SQLite_Source) source).closeDatabase();
        }
    }

    public void reloadZone(Mod_Caller caller, Zone zone) {
        if (source.reloadZone(zone)) {
            caller.sendMessage(RealmsTranslate.transformMessage("zone.reload.sucess", zone.getName()));
        }
        else {
            caller.sendError(RealmsTranslate.transformMessage("zone.reload.fail", zone.getName()));
        }
    }

    private final void testJDOM() throws DataSourceException {
        try {
            Class.forName("org.jdom.JDOMException");
        }
        catch (ClassNotFoundException cnfe) {
            throw new DataSourceException(cnfe, DataSourceType.XML);
        }
    }

    private final boolean canFindSQLDriver(String driver) {
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            Driver drive = en.nextElement();
            if (drive.getClass().getName().equals(driver)) {
                return true;
            }
        }
        return false;
    }

    private final void testSQLiteDriver() throws DataSourceException {
        if (!canFindSQLDriver("org.sqlite.JDBC")) {
            try {
                Class.forName("org.sqlite.JDBC");
            }
            catch (ClassNotFoundException cnfe) {
                throw new DataSourceException(cnfe, DataSourceType.SQLITE);
            }
        }
    }

    private final void testMySQLDriver() throws DataSourceException {
        if (!canFindSQLDriver("com.mysql.jdbc.Driver")) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            }
            catch (ClassNotFoundException cnfe) {
                throw new DataSourceException(cnfe, DataSourceType.MYSQL);
            }
        }
    }
}
