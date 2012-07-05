package net.visualillusionsent.viutils;

/**
 * CanaryMod Block wrapper interface
 * <p>
 * Handles methods from Block inside of packages
 * <p>
 * This file is part of the VI Utilities Package
 * (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 * 
 */
public interface ICModBlock {

    /**
     * gets a block's id
     * 
     * @return type
     */
    public int getType();

    /**
     * gets a block's metadata
     * 
     * @return data
     */
    public int getData();

    /**
     * gets a block's x position
     * 
     * @return x
     */
    public int getX();

    /**
     * gets a block's y position
     * 
     * @return y
     */
    public int getY();

    /**
     * gets a block's z position
     * 
     * @return
     */
    public int getZ();

    /**
     * gets a block's dimension
     * 
     * @return dimension
     */
    public int getDimension();

    /**
     * gets a block's dimension index
     * 
     * @return dimension index
     */
    public int getDimIndex();

    /**
     * gets a block's world by name
     * 
     * @return world name
     */
    public String getWorldName();

    /**
     * gets the block being wrapped
     * 
     * @return
     */
    public Object getBlock();

    //Overriden Object Methods
    @Override
    public String toString();

    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

}
