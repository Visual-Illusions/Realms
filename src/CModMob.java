import net.visualillusionsent.viutils.ICModMob;

/**
 * CanaryMod-Mob wrapper class
 * <p>
 * This class is a bridge to send Mob data from the 'default package' to
 * 'packaged classes'.
 * <p>
 * This file is part of the VI Utilities Package
 * (net.visualillusionsent.viutils)
 * 
 * @author Jason Jones
 * @see ICModMob
 */
public class CModMob implements ICModMob {
    private Mob mob;

    public CModMob(Mob mob) {
        this.mob = mob;
    }

    @Override
    public final void destroy() {
        mob.destroy();
    }

    @Override
    public final String getName() {
        return mob.getName();
    }

    @Override
    public final double getX() {
        return mob.getX();
    }

    @Override
    public final double getY() {
        return mob.getY();
    }

    @Override
    public final double getZ() {
        return mob.getZ();
    }

    @Override
    public final String getWorldName() {
        return mob.getWorld() != null ? mob.getWorld().getName() : "null";
    }

    @Override
    public final int getDimension() {
        return mob.getWorld().getType().getId();
    }

    @Override
    public final int getDimIndex() {
        return mob.getWorld().getType().toIndex();
    }

    @Override
    public final Object getMob() {
        return mob;
    }

    @Override
    public final String toString() {
        return mob.toString();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof CModMob) {
            CModMob cMob = (CModMob) obj;
            return mob.equals(cMob.getMob());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return mob.hashCode();
    }
}
