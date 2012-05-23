package net.visualillusionsent.realms.io;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.PropsFile;

/**
 * Realms FlatFile Data Handler
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class RealmsFlatFile extends RealmsData {
    private final String StoDir = "plugins/config/Realms/Zones/"; //Storage Directory
    private final File Directory = new File(StoDir); //Directory
    private final String ZoneFile = /*ZoneName*/"%s.zone"; //Zone File Name
    private Object zonelock = new Object(); //Lock Object
    private boolean isInitialized = false;
    private RHandle rhandle;
    
    /**
     * class constructor
     * 
     * @param realm
     */
    public RealmsFlatFile(RHandle rhandle){
        this.rhandle = rhandle;
        if(!Directory.exists()){
            Directory.mkdirs();
        }
    }

    /**
     * Loads Zones
     */
    public final boolean loadZones() {
        if(!isInitialized){
            synchronized(zonelock){
                rhandle.log(Level.INFO, "Loading Zones...");
                String[] zones = Directory.list();
                //Load Everywhere parents first
                for(String zonename : zones){
                    try {
                        if(!zonename.endsWith("zone")){
                            continue;
                        }
                        PropsFile zone = new PropsFile(StoDir+zonename);
                    
                        String[] zoned = new String[]{ zone.getString("ZoneName" ),
                                                       zone.getString("World"),
                                                       zone.getString("Dimension"),
                                                       zone.getString("ParentZone"),
                                                       zone.getString("Greeting"),
                                                       zone.getString("Farewell"),
                                                       zone.getString("PVP"),
                                                       zone.getString("Sanctuary"),
                                                       zone.getString("Creeper"),
                                                       zone.getString("Ghast"),
                                                       zone.getString("Fall"),
                                                       zone.getString("Suffocate"),
                                                       zone.getString("Fire"),
                                                       zone.getString("Animals"),
                                                       zone.getString("Physics"),
                                                       zone.getString("Creative"),
                                                       zone.getString("Pistons"),
                                                       zone.getString("Healing"),
                                                       zone.getString("Enderman"),
                                                       zone.getString("Spread"),
                                                       zone.getString("Flow"),
                                                       zone.getString("TNT"),
                                                       zone.getString("Potion"),
                                                       zone.getString("Starve"),
                                                       zone.getString("Restricted")
                                                     };
                        Zone theZone = new Zone(rhandle, zoned);
                        String Polygon = zone.getString("PolygonArea");
                        if(Polygon != null && !Polygon.equalsIgnoreCase("null")){
                            String[] polyed = zone.getString("PolygonArea").split(",");
                            theZone.setPolygon(new PolygonArea(rhandle, theZone, polyed));
                        }
                        String perms = zone.getString("Permissions");
                        if(perms != null && !perms.equalsIgnoreCase("null")){
                            String[] permed = zone.getString("Permissions").split(",");
                            for(String permission : permed){
                                if(permission == null || permission.length() == 0) continue;
                                String[] permsplit = permission.split("~");
                                try{
                                    theZone.setPermission(new Permission(permsplit[0], permsplit[1], (permsplit[2].equals("YES") ? true : false), (permsplit[3].equals("YES") ? true : false)));
                                }
                                catch(ArrayIndexOutOfBoundsException aioobe){
                                    rhandle.log(RLevel.DEBUGSEVERE, permission);
                                }
                            }
                        }
                    } catch (Exception e) {
                        rhandle.log(Level.WARNING, "[Realms] Failed to load Zone: "+zonename);
                        rhandle.log(RLevel.DEBUGSEVERE, "Failed to load Zone: "+zonename+" :", e);
                        continue;
                    }
                }
                for(String zonename : zones){ //set parents
                    if(!zonename.endsWith("zone")){
                        continue;
                    }
                    PropsFile zone = null;
                    try {
                        zone = new PropsFile(StoDir+zonename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(zone.getString("ParentZone") == null) continue;
                   
                    Zone parent = null, child = null;
                    try{
                        child = ZoneLists.getZoneByName(zone.getString("ZoneName"));
                    } catch (ZoneNotFoundException e) {
                        rhandle.log(Level.INFO, "child null: "+zone.getString("ZoneName"));
                    }
                    try {
                        parent = ZoneLists.getZoneByName(zone.getString("ParentZone"));
                    } catch (ZoneNotFoundException e) {
                        rhandle.log(Level.INFO, "parent null: "+zone.getString("ParentZone"));
                    }
                    if(parent != null && child != null){
                        child.setParent(parent);
                    }
                }
            }
            isInitialized = true;
            return true;
        }
        return false;
    }
    
    /**
     * saves a zone
     * @param zone
     */
    @Override
    public final void saveZone(Zone zone) {
        new SaveThread(rhandle, zone, true).start();
    }

    /**
     * reloads a zone
     * @param zone
     */
    @Override
    public final boolean reloadZone(Zone theZone) {
        File file = new File(StoDir+String.format(ZoneFile, theZone.getName()));
        if(!file.exists()){
            return false;
        }
        synchronized(zonelock){
            try{
                PropsFile zone = new PropsFile(StoDir+String.format(ZoneFile, theZone.getName()));
                theZone.setWorld(zone.getString("World"));
                theZone.setDimension(zone.getInt("Dimension"));
                theZone.setParent(zone.getString("ParentZone").equals("null") ? null : ZoneLists.getZoneByName(zone.getString("ParentZone")));
                theZone.setGreeting(zone.getString("Greeting").equals("null") ? null : zone.getString("Greeting"));
                theZone.setFarewell(zone.getString("Farewell").equals("null") ? null : zone.getString("Farewell"));
                theZone.setPVP(Zone.ZoneFlag.getZoneFlag(zone.getString("PVP")));
                theZone.setSanctuary(Zone.ZoneFlag.getZoneFlag(zone.getString("Sanctuary")));
                theZone.setCreeper(Zone.ZoneFlag.getZoneFlag(zone.getString("Creeper")));
                theZone.setGhast(Zone.ZoneFlag.getZoneFlag(zone.getString("Ghast")));
                theZone.setFall(Zone.ZoneFlag.getZoneFlag(zone.getString("Fall")));
                theZone.setSuffocate(Zone.ZoneFlag.getZoneFlag(zone.getString("Suffocate")));
                theZone.setFire(Zone.ZoneFlag.getZoneFlag(zone.getString("Fire")));
                theZone.setAnimals(Zone.ZoneFlag.getZoneFlag(zone.getString("Animals")));
                theZone.setPhysics(Zone.ZoneFlag.getZoneFlag(zone.getString("Physics")));
                theZone.setCreative(Zone.ZoneFlag.getZoneFlag(zone.getString("Creative")));
                theZone.setPistons(Zone.ZoneFlag.getZoneFlag(zone.getString("Pistons")));
                theZone.setHealing(Zone.ZoneFlag.getZoneFlag(zone.getString("Healing")));
                theZone.setEnderman(Zone.ZoneFlag.getZoneFlag(zone.getString("Enderman")));
                theZone.setSpread(Zone.ZoneFlag.getZoneFlag(zone.getString("Spread")));
                theZone.setFlow(Zone.ZoneFlag.getZoneFlag(zone.getString("Flow")));
                theZone.setTNT(Zone.ZoneFlag.getZoneFlag(zone.getString("TNT")));
                theZone.setPotion(Zone.ZoneFlag.getZoneFlag(zone.getString("Potion")));
                theZone.setStarve(Zone.ZoneFlag.getZoneFlag(zone.getString("Starve")));
                theZone.setRestricted(Zone.ZoneFlag.getZoneFlag(zone.getString("Restricted")));
                String Polygon = zone.getString("PolygonArea");
                if(Polygon != null && !Polygon.equalsIgnoreCase("null")){
                    String[] polyed = zone.getString("PolygonArea").split(",");
                    new PolygonArea(rhandle, theZone, polyed);
                }
                String perms = zone.getString("Permissions");
                if(perms != null && !perms.equalsIgnoreCase("null")){
                    String[] permed = zone.getString("Permissions").split("~");
                    for(String permission : permed){
                        if(permission == null || permission.length() == 0) continue;
                        String[] permsplit = permission.split(",");
                        try{
                            theZone.setPermission(new Permission(permsplit[0], permsplit[1], (permsplit[2].equals("YES") ? true : false), (permsplit[3].equals("YES") ? true : false)));
                        }
                        catch(ArrayIndexOutOfBoundsException aioobe){
                            rhandle.log(RLevel.DEBUGSEVERE, permission);
                        }
                    }
                }
            }
            //TODO Log error
            catch(InvaildZoneFlagException izfe){
                return false;
            } 
            catch (ZoneNotFoundException znfe) {
                return false;
            }
            catch (IOException e){
                return false;
            }
        }
        return true;
    }

    @Override
    public final void reloadAll() {
        new Thread(){
            public void run(){
                loadZones();
            }
        }.start();
    }

    @Override
    public void saveAll() {
        new SaveThread(rhandle, null, true).start();
    }
}
