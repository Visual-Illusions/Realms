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

import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * @author Jason (darkdiplomat)
 */
final class OutputQueue {

    private LinkedList<DataSourceActionContainer> queue;

    public OutputQueue() {
        queue = new LinkedList<DataSourceActionContainer>();
    }

    public final void add(DataSourceActionContainer dsac) {
        synchronized (queue) {
            queue.add(dsac);
            queue.notify();
        }
    }

    public final DataSourceActionContainer next() {
        DataSourceActionContainer dsac = null;
        if (queue.isEmpty()) {
            synchronized (queue) {
                try {
                    queue.wait();
                }
                catch (InterruptedException iex) {
                    // Interrupted
                    RealmsLogMan.log(RLevel.GENERAL, "InterruptedException occured in OutputQueue");
                    return null;
                }
            }
        }
        try {
            dsac = queue.getFirst();
            queue.removeFirst();
        }
        catch (NoSuchElementException nseex) {
            throw new InternalError("Race hazard in LinkedList object.");
        }
        return dsac;
    }

    public final void clear() {
        synchronized (queue) {
            queue.clear();
        }
    }
}
