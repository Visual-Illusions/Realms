import net.visualillusionsent.viutils.ICModItem;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * CanaryMod-Player wrapper class
 * <p>
 * This class is a bridge to send Player data from the 'default package' to
 * 'packaged classes'.
 * <p>
 * This file is part of the VI Utilities Package
 * (net.visualillusionsent.viutils)
 * 
 * @author Jason Jones
 * @see ICModPlayer
 */
public class CModPlayer implements ICModPlayer {
    private Player player;

    public CModPlayer(Player player) {
        this.player = player;
    }

    @Override
    public final String getName() {
        return player.getName();
    }

    @Override
    public final void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public final void notify(String message) {
        player.notify(message);
    }

    @Override
    public final void dropInventory() {
        player.dropInventory();
    }

    @Override
    public final boolean canUseCommand(String cmd) {
        return player.canUseCommand(cmd);
    }

    @Override
    public final void moveTo(double x, double y, double z, float rotation, float pitch) {
        player.teleportTo(x, y, z, rotation, pitch);
    }

    @Override
    public final int getHealth() {
        return player.getHealth();
    }

    @Override
    public final void setHealth(int newVal) {
        player.setHealth(newVal);
    }

    @Override
    public final void heal(int newVal) {
        int newHealth = player.getHealth() + newVal;
        player.setHealth(newHealth);
    }

    @Override
    public final void doDamage(int type, int amount) {
        try {
            switch (type) {
                case 1:
                    player.getUser().a(ODamageSource.l, amount);
                    break; //EXPLOSION
                case 2:
                    player.getUser().a(ODamageSource.k, amount);
                    break; //RESTRICT(Generic)
            }
        }
        catch (Exception e) {
            //Possible change to the Notchian so revert to old methods
            int newHealth = player.getHealth() - amount;
            player.setHealth(newHealth);
        }
    }

    @Override
    public final boolean getMode() {
        return player.getMode();
    }

    @Override
    public final boolean isDamageDisabled() {
        return player.isDamageDisabled();
    }

    @Override
    public final double getX() {
        return player.getX();
    }

    @Override
    public final double getY() {
        return player.getY();
    }

    @Override
    public final double getZ() {
        return player.getZ();
    }

    @Override
    public final double getRotation() {
        return player.getRotation();
    }

    @Override
    public final double getPitch() {
        return player.getPitch();
    }

    @Override
    public final String getWorldName() {
        return player.getWorld() != null ? player.getWorld().getName() : "null";
    }

    @Override
    public final int getDimension() {
        return player.getWorld().getType().getId();
    }

    @Override
    public final int getDimIndex() {
        return player.getWorld().getType().toIndex();
    }

    @Override
    public final boolean isInGroup(String group) {
        return player.isInGroup(group);
    }

    @Override
    public final boolean isAdmin() {
        return player.isAdmin();
    }

    @Override
    public final ICModItem[] getInvContents() {
        ICModItem[] inv = new ICModItem[40];
        Item[] items = player.getInventory().getContents();
        for (int i = 0; i < 40; i++) {
            if (items[i] != null) {
                inv[i] = new CModItem(items[i]);
            }
        }
        return inv;
    }

    @Override
    public final void setInvContents(ICModItem[] cItems) {
        Item[] items = new Item[40];
        for (ICModItem theItem : cItems) {
            if (theItem != null) {
                items[theItem.getSlot()] = (Item) theItem.getItem();
            }
        }
        player.getInventory().setContents(items);
        player.getInventory().update();
    }

    @Override
    public final void clearInventory() {
        player.getInventory().setContents(new Item[40]);
    }

    @Override
    public final Object getPlayer() {
        return player;
    }

    @Override
    public final String toString() {
        return player.toString();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof CModPlayer) {
            CModPlayer cPlayer = (CModPlayer) obj;
            return player.equals(cPlayer.getPlayer());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return player.hashCode();
    }
}
