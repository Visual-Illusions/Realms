package net.visualillusionsent.realms.io;

import net.visualillusionsent.realms.zones.Zone;

/**
 * abstract class for handling data
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public abstract class RealmsData {
    public abstract void saveZone(Zone zone);
    public abstract boolean reloadZone(Zone zone);
    public abstract void reloadAll();
    public abstract void saveAll();
    public abstract boolean loadZones();
}
