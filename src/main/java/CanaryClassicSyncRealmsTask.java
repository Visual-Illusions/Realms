import java.util.TimerTask;
import net.visualillusionsent.minecraft.server.mod.interfaces.SynchronizedTask;

public final class CanaryClassicSyncRealmsTask extends TimerTask implements SynchronizedTask{
    private final Runnable runnable;

    public CanaryClassicSyncRealmsTask(Runnable runnable){
        this.runnable = runnable;
    }

    @Override
    public void run(){
        etc.getServer().addToServerQueue(runnable, 0);
    }
}
