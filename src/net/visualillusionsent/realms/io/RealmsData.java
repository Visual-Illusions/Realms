package net.visualillusionsent.realms.io;

/**
 * Data Constructor
 * 
 * @author darkdiplomat
 */
public interface RealmsData {
    public boolean load();

    public boolean reloadAll();

    public boolean reloadZones();

    public boolean reloadPerms();

    public boolean reloadPolys();
}
