import net.visualillusionsent.viutils.ICModItem;

/**
 * CanaryMod-Item wrapper class
 * <p>
 * This class is a bridge to send Item data from the 'default package' to
 * 'packaged classes'.
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author Jason Jones
 * @see ICModItem
 */
public class CModItem implements ICModItem{
    private Item item;
    
    public CModItem(Item item){
        this.item = item;
    }
    
    @Override
    public int getId() {
        return item.getItemId();
    }

    @Override
    public int getDamage() {
        return item.getDamage();
    }

    @Override
    public int getAmount() {
        return item.getAmount();
    }
    
    public int getSlot(){
        return item.getSlot();
    }

    @Override
    public Object getItem() {
        return item;
    }
    
    public String toString(){
        StringBuilder toRet = new StringBuilder();
        toRet.append(item.getItemId());
        toRet.append(':');
        toRet.append(item.getDamage());
        toRet.append(':');
        toRet.append(item.getAmount());
        toRet.append(':');
        toRet.append(item.getSlot());
        return toRet.toString();
    }
}
