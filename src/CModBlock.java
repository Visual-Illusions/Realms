import net.visualillusionsent.viutils.ICModBlock;

/**
 * CanaryMod Block wrapper class
 * <p>
 * This class is a bridge to send Block data from the default <unnamed package> to
 * packaged classes.
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 * @see ICModBlock
 *
 */
public class CModBlock implements ICModBlock{
    
    private Block block; //The block being wrapped
    
    /**
     * class constructor
     * 
     * @param block     The block being wrapped.
     */
    public CModBlock(Block block){
        this.block = block;
    }

    @Override
    public int getType() {
        return block.getType();
    }

    @Override
    public int getData() {
        return block.getData();
    }

    @Override
    public int getX() {
        return block.getX();
    }

    @Override
    public int getY() {
        return block.getY();
    }

    @Override
    public int getZ() {
        return block.getZ();
    }
    
    @Override
    public int getDimension(){
        return block.getWorld().getType().getId();
    }
    
    @Override
    public int getDimIndex(){
        return block.getWorld().getType().toIndex();
    }

    @Override
    public String getWorldName() {
        return block.getWorld() != null ? block.getWorld().getName() : "null";
    }

    @Override
    public Object getBlock() {
        return block;
    }
    
    
    @Override
    public String toString(){
        return block.toString();
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof CModBlock){
            CModBlock cBlock = (CModBlock) obj;
            return block.equals(cBlock.getBlock());
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return block.hashCode();
    }
}
