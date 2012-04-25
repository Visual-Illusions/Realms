package net.visualillusionsent.viutils;

/**
 * CanaryMod Mob wrapper interface
 * <p>
 * Handles methods from Player inside of packages
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 *
 */
public interface ICModMob {
    
    /**
     * destroys the mob
     */
    public void destroy();
    
    /**
     * gets the mob's name
     * @return mob's name
     */
    public String getName();
    
    /**
     * get mob's x position
     * @return x
     */
    public double getX();
    
    /**
     * gets mob's y position
     * @return y
     */
    public double getY();
    
    /**
     * gets mob's z position
     * @return z
     */
    public double getZ();
    
    /**
     * gets the name of the world the mob is in
     * @return world name
     */
    public String getWorldName();
    
    /**
     * gets the dimension the mob is in
     *              -1 Nether  0 Normal  1 TheEnd
     * @return dimension
     */
    public int getDimension();
    
    /**
     * gets the mob's dimension by index (0-2)
     * @return dimension index
     */
    public int getDimIndex();

}
