import net.visualillusionsent.minecraft.server.mod.interfaces.SynchronizedTask;

public final class CanaryClassicSyncRealmsTask extends Thread implements SynchronizedTask{
    private final Runnable runnable;
    private final long delay;
    private volatile boolean killed;

    public CanaryClassicSyncRealmsTask(Runnable runnable, long delay){
        this.runnable = runnable;
        this.delay = delay;
    }

    @Override
    public void run(){
        new Thread(){
            public final void run(){
                while (!killed) {
                    try {
                        sleep(delay);
                    }
                    catch (InterruptedException ex) {}
                    etc.getServer().addToServerQueue(runnable, 0);
                }
            }
        };
    }

    void kill(){
        this.killed = true;
    }
}
