package net.visualillusionsent.minecraft.server.mod.canary.plugin.realms;

import net.canarymod.plugin.Plugin;
import net.canarymod.tasks.ServerTask;
import net.visualillusionsent.minecraft.server.mod.interfaces.SynchronizedTask;

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
