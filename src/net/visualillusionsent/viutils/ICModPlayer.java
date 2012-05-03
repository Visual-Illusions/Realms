package net.visualillusionsent.viutils;

/**
 * CanaryMod Player wrapper interface
 * <p>
 * Handles methods from Player inside of packages
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 *
 */
public interface ICModPlayer {
    
    /**
     * get's the player's name
     * @return name
     */
    public String getName();
    
    /**
     * sends the player a message
     * @param message
     */
    public void sendMessage(String message);
    
    /**
     * sends the player a notification
     * @param message
     */
    public void notify(String message);
    
    /**
     * drops the player's inventory
     */
    public void dropInventory();
    
    /**
     * checks if a player can use a command
     * @param cmd
     * @return true if the player can
     */
    public boolean canUseCommand(String cmd);
    
    /**
     * moves the player to a location
     * 
     * @param x
     * @param y
     * @param z
     * @param rotation
     * @param pitch
     */
    public void moveTo(double x, double y, double z, float rotation, float pitch);
    
    /**
     * gets the player's health
     * @return
     */
    public int getHealth();
    
    /**
     * sets the player's health
     * @param newVal
     */
    public void setHealth(int newVal);
    
    /**
     * does damage to the player
     * @param val
     */
    public void doDamage(int type, int amount);
    
    /**
     * heals the player
     * @param val
     */
    public void heal(int val);
    
    /**
     * gets whether the player is in creative mode
     * @return true if the player is
     */
    public boolean getMode();
    
    /**
     * checks if the player is invulnerable
     * 
     * @return true if the player is invulnerable
     */
    public boolean isDamageDisabled();
    
    /**
     * gets the player's x location
     * 
     * @return x
     */
    public double getX();
    
    /**
     * gets the player's y location
     * 
     * @return y
     */
    public double getY();
    
    /**
     * gets the player's z location
     * 
     * @return z
     */
    public double getZ();
    
    /**
     * gets the player's rotation
     * 
     * @return rotation
     */
    public double getRotation();
    
    /**
     * gets the player's pitch
     * 
     * @return pitch
     */
    public double getPitch();
    
    /**
     * gets the name of the world the player is in
     * 
     * @return world name
     */
    public String getWorldName();
    
    /**
     * gets the dimension the player is in
     * 
     * @return dimension
     *              -1 Nether 0 Normal 1 End
     */
    public int getDimension();
    
    /**
     * gets the index for the dimension the player is in
     * 
     * @return dimension index
     *              0 Normal 1 Nether 2 End
     */
    public int getDimIndex();
    
    /**
     * tells if the player is in specified group
     * 
     * @param groupname
     * @return true if the player is
     */
    public boolean isInGroup(String groupname);
    
    /**
     * tells is the player is an Admin
     * @return true if the player is
     */
    public boolean isAdmin();
    
    /**
     * gets this player's inventory contents
     * @return ICModItem array
     */
    public ICModItem[] getInvContents();
    
    /**
     * sets this player's inventory's contents
     * @param cItems
     */
    public void setInvContents(ICModItem[] cItems);
    
    /**
     * clears this player's inventory
     */
    public void clearInventory();
    
    /**
     * Gets the player Object
     * @return player
     */
    public Object getPlayer();
    
    //Overriden Object Methods
    public String toString();
    public boolean equals(Object obj);
    public int hashCode();
}
