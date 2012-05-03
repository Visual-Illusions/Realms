import net.visualillusionsent.viutils.ICModItem;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * CanaryMod Player wrapper class
 * <p>
 * This class is a bridge to send Player data from the default <unnamed package> to
 * packaged classes.
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 * @see ICModPlayer
 */
public class CModPlayer implements ICModPlayer{
    private Player player;
    
    public CModPlayer(Player player){
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void notify(String message) {
        player.notify(message);
    }

    @Override
    public void dropInventory() {
        player.dropInventory();
    }

    @Override
    public boolean canUseCommand(String cmd) {
        return player.canUseCommand(cmd);
    }

    @Override
    public void moveTo(double x, double y, double z, float rotation, float pitch) {
        player.teleportTo(x, y, z, rotation, pitch);
    }

    @Override
    public int getHealth() {
        return player.getHealth();
    }

    @Override
    public void setHealth(int newVal) {
        player.setHealth(newVal);
    }
    
    @Override
    public void heal(int newVal){
        int newHealth = player.getHealth() + newVal;
        player.setHealth(newHealth);
    }
    
    @Override
    public void doDamage(int type, int amount) {
        try{
            switch(type){
            case 1: player.getUser().a(ODamageSource.l, amount); //EXPLOSION
            case 2: player.getUser().a(ODamageSource.h, amount); //RESTRICT(Cactus)
            }
        }
        catch(Exception e){
            //Possible change to the Notchian so revert to old methods
            int newHealth = player.getHealth() - amount;
            player.setHealth(newHealth); 
        }
    }

    @Override
    public boolean getMode() {
        return player.getMode();
    }

    @Override
    public boolean isDamageDisabled() {
        return player.isDamageDisabled();
    }

    @Override
    public double getX() {
        return player.getX();
    }

    @Override
    public double getY() {
        return player.getY();
    }

    @Override
    public double getZ() {
        return player.getZ();
    }

    @Override
    public double getRotation() {
        return player.getRotation();
    }

    @Override
    public double getPitch() {
        return player.getPitch();
    }

    @Override
    public String getWorldName() {
        return player.getWorld().getName();
    }

    @Override
    public int getDimension() {
        return player.getWorld().getType().getId();
    }

    @Override
    public int getDimIndex() {
        return player.getWorld().getType().toIndex();
    }
    
    @Override
    public boolean isInGroup(String group){
        return player.isInGroup(group);
    }
    
    @Override
    public boolean isAdmin(){
        return player.isAdmin();
    }
    
    @Override
    public ICModItem[] getInvContents(){
        ICModItem[] inv = new ICModItem[40];
        Item[] items = player.getInventory().getContents();
        for(int i = 0; i < 40; i++){
            if(items[i] != null){
                inv[i] = new CModItem(items[i]);
            }
        }
        return inv;
    }
    
    @Override
    public void setInvContents(ICModItem[] cItems){
        Item[] items = new Item[40];
        for(ICModItem theItem : cItems){
            if(theItem != null){
                items[theItem.getSlot()] = (Item)theItem.getItem();
            }
        }
        player.getInventory().setContents(items);
        player.getInventory().update();
    }
    
    @Override
    public void clearInventory(){
        player.getInventory().setContents(new Item[40]);
    }
    
    @Override
    public Object getPlayer(){
        return player;
    }
    
    @Override
    public String toString(){
        return player.toString();
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof CModPlayer){
            CModPlayer cPlayer = (CModPlayer) obj;
            return player.equals(cPlayer.getPlayer());
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return player.hashCode();
    }
}
