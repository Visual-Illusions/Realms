import net.visualillusionsent.viutils.ICModMob;

/**
 * CanaryMod-Mob wrapper class
 * <p>
 * This class is a bridge to send Mob data from the 'default package' to
 * 'packaged classes'.
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author Jason Jones
 * @see ICModMob
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
        return mob.getWorld() != null ? mob.getWorld().getName() : "null";
    }

    @Override
    public int getDimension() {
        return mob.getWorld().getType().getId();
    }

    @Override
    public int getDimIndex() {
        return mob.getWorld().getType().toIndex();
    }
    
    @Override
    public Object getMob(){
        return mob;
    }
    
    @Override
    public String toString(){
        return mob.toString();
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof CModMob){
            CModMob cMob = (CModMob) obj;
            return mob.equals(cMob.getMob());
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return mob.hashCode();
    }
}
