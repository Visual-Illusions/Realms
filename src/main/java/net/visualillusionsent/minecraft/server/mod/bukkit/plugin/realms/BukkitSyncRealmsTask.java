package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.realms;

import net.visualillusionsent.minecraft.server.mod.interfaces.SynchronizedTask;

public class BukkitSyncRealmsTask implements SynchronizedTask{
    private final int taskId;

    public BukkitSyncRealmsTask(int taskId){
        this.taskId = taskId;
    }

    public int getTaskId(){
        return taskId;
    }
}
