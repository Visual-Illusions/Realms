import net.visualillusionsent.viutils.ICModBlock;

/**
 * CanaryMod-Block wrapper class
 * <p>
 * This class is a bridge to send Block data from the 'default package' to
 * 'packaged classes'.
 * <p>
 * This file is part of the VI Utilities Package
 * (net.visualillusionsent.viutils)
 * 
 * @author Jason Jones
 * @see ICModBlock
 */
public class CModBlock implements ICModBlock {

    private Block block; //The block being wrapped

    /**
     * class constructor
     * 
     * @param block
     *            The block being wrapped.
     */
    public CModBlock(Block block) {
        this.block = block;
    }

    @Override
    public final int getType() {
        return block.getType();
    }

    @Override
    public final int getData() {
        return block.getData();
    }

    @Override
    public final int getX() {
        return block.getX();
    }

    @Override
    public final int getY() {
        return block.getY();
    }

    @Override
    public final int getZ() {
        return block.getZ();
    }

    @Override
    public final int getDimension() {
        return block.getWorld().getType().getId();
    }

    @Override
    public final int getDimIndex() {
        return block.getWorld().getType().toIndex();
    }

    @Override
    public final String getWorldName() {
        return block.getWorld() != null ? block.getWorld().getName() : "null";
    }

    @Override
    public final Object getBlock() {
        return block;
    }

    @Override
    public final String toString() {
        return block.toString();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof CModBlock) {
            CModBlock cBlock = (CModBlock) obj;
            return block.equals(cBlock.getBlock());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return block.hashCode();
    }
}
