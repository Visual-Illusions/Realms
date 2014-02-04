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
package net.visualillusionsent.realms.canary;

import net.canarymod.plugin.Plugin;
import net.canarymod.tasks.ServerTask;
import net.visualillusionsent.realms.tasks.SynchronizedTask;

/**
 * @author Jason (darkdiplomat)
 */
public final class CanarySyncRealmsTask extends ServerTask implements SynchronizedTask {

    private final Runnable runnable;

    public CanarySyncRealmsTask(Plugin realms, Runnable runnable, long delay) {
        super(realms, delay * 20 /* Bring up delay by 20TPS*/, true);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}
