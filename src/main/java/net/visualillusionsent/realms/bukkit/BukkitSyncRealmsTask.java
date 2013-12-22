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
package net.visualillusionsent.realms.bukkit;

import net.visualillusionsent.realms.tasks.SynchronizedTask;

public class BukkitSyncRealmsTask implements SynchronizedTask{
    private final int taskId;

    public BukkitSyncRealmsTask(int taskId){
        this.taskId = taskId;
    }

    public int getTaskId(){
        return taskId;
    }
}
