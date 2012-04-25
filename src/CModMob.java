import net.visualillusionsent.viutils.ICModMob;

/**
 * CanaryMod Mob Adapter class
 * <p>
 * This class is a bridge to send Mob data from the default <unnamed package> to
 * packaged classes.
 * <p>
 * This file is part of Visual Illusions Utilities (viutils)
 * 
 * @author darkdiplomat
 *
 */
public class CModMob implements ICModMob{
    private Mob mob;
    
    public CModMob(Mob mob){
        this.mob = mob;
    }

    @Override
    public void destroy() {
       mob.destroy();
    }
    
    @Override
    public String getName(){
        return mob.getName();
    }

    @Override
    public double getX() {
        return mob.getX();
    }

    @Override
    public double getY() {
        return mob.getY();
    }

    @Override
    public double getZ() {
        return mob.getZ();
    }

    @Override
    public String getWorldName() {
        return mob.getWorld().getName();
    }

    @Override
    public int getDimension() {
        return mob.getWorld().getType().getId();
    }

    @Override
    public int getDimIndex() {
        return mob.getWorld().getType().toIndex();
    }
    
    
    public int hashCode(){
        return mob.hashCode();
    }
    
    public boolean equals(Object obj){
        return mob.equals(obj);
    }

}
