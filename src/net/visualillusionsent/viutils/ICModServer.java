package net.visualillusionsent.viutils;

import java.util.List;

/**
 * CanaryMod Server wrapper interface
 * <p>
 * Handles methods from Server inside of packages
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 *
 */
public interface ICModServer {
    
    /**
     * gets a list of Mobs
     * 
     * @return moblist
     */
    public List<ICModMob> getMobList();
    
    /**
     * gets a list of Players
     * 
     * @return playerlist
     */
    public List<ICModPlayer> getPlayerList();
    
    /**
     * gets a list of animals
     * 
     * @return animallist
     */
    public List<ICModMob> getAnimalList();
    
    /**
     * gets a block at specificed position
     * 
     * @param x
     * @param y
     * @param z
     * @param dim
     * @param WorldName
     * @return block
     */
    public ICModBlock getBlockAt(int x, int y, int z, int dim, String WorldName);
    
    /**
     * sets specified block
     * @param block
     */
    public void setBlock(ICModBlock block);
    
    /**
     * sets a block of specified type and at specified location
     *  
     * @param type
     * @param x
     * @param y
     * @param z
     * @param worldname
     * @param dim
     */
    public void setBlock(int type, int x, int y, int z, String worldname, int dim);
    
    /**
     * gets the default world's name
     * 
     * @return worldname
     */
    public String getDefaultWorldName();
    
    /**
     * gets the block at the highest y position at specified x,z position
     * @param x
     * @param z
     * @param worldname
     * @param dim
     * @return y
     */
    public int getHighestBlockY(int x, int z, String worldname, int dim);
    
    /**
     * gets the default group name
     * 
     * @return default group name
     */
    public String getDefaultGroup();
}
