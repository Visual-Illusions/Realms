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
package net.visualillusionsent.realms.data;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsTranslate;
import net.visualillusionsent.realms.lang.DataSourceError;
import net.visualillusionsent.realms.lang.DataSourceType;
import net.visualillusionsent.realms.zones.Zone;

/**
 * @author Jason (darkdiplomat)
 */
public class DataSourceHandler {

    private final OutputQueue queue;
    private final DataSource source;
    private final OutputThread outThread;

    public DataSourceHandler(DataSourceType type) {
        type.testDriver();
        if (type == DataSourceType.XML) {
            source = new XML_Source();
        }
        else if (type == DataSourceType.MYSQL) {
            source = new MySQL_Source();
        }
        else if (type == DataSourceType.SQLITE) {
            source = new SQLite_Source();
        }
        else if (type == DataSourceType.POSTGRESQL) {
            source = new PostgreSQL_Source();
        }
        else {
            throw new DataSourceError("Invaild DataSourceType...");
        }
        source.load();
        source.loadInventories();
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
}
