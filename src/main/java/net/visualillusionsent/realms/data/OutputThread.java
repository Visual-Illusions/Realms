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

/**
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
                        if (!act.getZone().isPendingDeletion()) {
                            if (!act.getZone().isSaving()) {
                                act.getZone().setSaving(true);
                                source.saveZone(act.getZone());
                                act.getZone().setSaving(false);
                            }
                        }
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
            catch (Exception ex) {
            }
        }
    }

    public final void terminate() {
        running = false;
        interrupt();
    }
}
