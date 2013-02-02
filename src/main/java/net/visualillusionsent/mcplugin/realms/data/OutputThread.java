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

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
final class OutputThread extends Thread {
    private final DataSourceHandler handler;
    private final DataSource source;
    private volatile boolean running = true;

    public OutputThread(DataSourceHandler handler, DataSource source) {
        this.handler = handler;
        this.source = source;
    }

    public void run() {
        while (running) {
            try {
                DataSourceActionContainer act = handler.getQueue().next();
                if (act != null) {
                    OutputAction action = act.getAction();
                    if (action == OutputAction.SAVE_ZONE) {
                        source.saveZone(act.getZone());
                    }
                    else if (action == OutputAction.DELETE_ZONE) {
                        source.deleteZone(act.getZone());
                    }
                    else if (action == OutputAction.SAVE_INVENTORY) {
                        source.saveInventory(act.getUser(), act.getItems());
                    }
                    else if (action == OutputAction.DELETE_INVENTORY) {
                        source.deleteInventory(act.getUser());
                    }
                }
            }
            catch (Exception e) {}
        }
    }

    public final void terminate() {
        running = false;
        interrupt();
    }
}
