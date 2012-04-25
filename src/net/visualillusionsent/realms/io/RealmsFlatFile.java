package net.visualillusionsent.realms.io;

import java.io.File;
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
     * Class Constructor
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
                for(String zonename : zones){
                    if(!zonename.endsWith("zone")){
                        continue;
                    }
                    PropsFile zone = new PropsFile(StoDir+zonename);
                    String[] zoned = new String[]{ zone.getString("ZoneName" ),
                                                   zone.getString("WorldName"),
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
                                                   zone.getString("Restricted"),
                                                   zone.getString("Respawn")
                                                 };
                    Zone theZone = new Zone(rhandle, zoned);
                    String Polygon = zone.getString("PolygonArea");
                    if(Polygon != null && !Polygon.equalsIgnoreCase("null")){
                        String[] polyed = zone.getString("PolygonArea").split(",");
                        new PolygonArea(rhandle, theZone, polyed);
                    }
                    String perms = zone.getString("Permissions");
                    if(perms != null && !perms.equalsIgnoreCase("null")){
                        String[] permed = zone.getString("Permissions").split(",");
                        for(String permission : permed){
                            String[] permsplit = permission.split(":");
                            theZone.setPermission(new Permission(permsplit[0], permsplit[1], (permsplit[2].equals("YES") ? true : false), (permsplit[3].equals("YES") ? true : false)));
                        }
                    }
                }
            }
            isInitialized = true;
            return true;
        }
        return false;
    }
    
    @Override
    public final void saveZone(final Zone zone) {
        new Thread(){
            public void run(){
                synchronized(zonelock){
                    PropsFile zonefile = new PropsFile(StoDir+String.format(ZoneFile, zone.getName()));
                    
                    zonefile.setString("ZoneName",      zone.getName());
                    zonefile.setString("WorldName",     zone.getWorld());
                    zonefile.setString("Dimension",     String.valueOf(zone.getDimension()));
                    zonefile.setString("ParentZone",    zone.getParent() == null ? "null" : zone.getParent().getName());
                    zonefile.setString("Greeting",      zone.getGreeting());
                    zonefile.setString("Farewell",      zone.getFarewell());
                    zonefile.setString("PVP",           zone.getAbsolutePVP().toString());
                    zonefile.setString("Sanctuary",     zone.getAbsoluteSanctuary().toString());
                    zonefile.setString("Creeper",       zone.getAbsoluteCreeper().toString());
                    zonefile.setString("Ghast",         zone.getAbsoluteGhast().toString());
                    zonefile.setString("Fall",          zone.getAbsoluteFall().toString());
                    zonefile.setString("Suffocate",     zone.getAbsoluteSuffocate().toString());
                    zonefile.setString("Fire",          zone.getAbsoluteFire().toString());
                    zonefile.setString("Animals",       zone.getAbsoluteAnimals().toString());
                    zonefile.setString("Physics",       zone.getAbsolutePhysics().toString());
                    zonefile.setString("Creative",      zone.getAbsoluteCreative().toString());
                    zonefile.setString("Pistons",       zone.getAbsolutePistons().toString());
                    zonefile.setString("Healing",       zone.getAbsoluteHealing().toString());
                    zonefile.setString("Enderman",      zone.getAbsoluteEnderman().toString());
                    zonefile.setString("Spread",        zone.getAbsoluteSpread().toString());
                    zonefile.setString("Flow",          zone.getAbsoluteFlow().toString());
                    zonefile.setString("TNT",           zone.getAbsoluteTNT().toString());
                    zonefile.setString("Potion",        zone.getAbsolutePotion().toString());
                    zonefile.setString("Starve",        zone.getAbsoluteStarve().toString());
                    zonefile.setString("Restricted",    zone.getAbsoluteRestricted().toString());
                    zonefile.setString("Respawn",       zone.getAbsoluteRespawn().toString());
                    zonefile.setString("PolygonArea",   zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                    
                    StringBuilder perms = new StringBuilder();
                    for(Permission perm : zone.getPerms()){
                        perms.append(perm.toString());
                        perms.append(',');
                    }
                    zonefile.save();
                }
            }
        }.start();
    }

    @Override
    public final boolean reloadZone(Zone theZone) {
        File file = new File(StoDir+String.format(ZoneFile, theZone.getName()));
        if(!file.exists()){
            return false;
        }
        synchronized(zonelock){
            PropsFile zone = new PropsFile(StoDir+String.format(ZoneFile, theZone.getName()));
            try{
                theZone.setWorld(zone.getString("WorldName"));
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
                theZone.setRespawn(Zone.ZoneFlag.getZoneFlag(zone.getString("Respawn")));
                String Polygon = zone.getString("PolygonArea");
                if(Polygon != null && !Polygon.equalsIgnoreCase("null")){
                    String[] polyed = zone.getString("PolygonArea").split(",");
                    new PolygonArea(rhandle, theZone, polyed);
                }
                String perms = zone.getString("Permissions");
                if(perms != null && !perms.equalsIgnoreCase("null")){
                    String[] permed = zone.getString("Permissions").split(",");
                    for(String permission : permed){
                        String[] permsplit = permission.split(":");
                        theZone.setPermission(new Permission(permsplit[0], permsplit[1], (permsplit[2].equals("YES") ? true : false), (permsplit[3].equals("YES") ? true : false)));
                    }
                }
            }
            catch(InvaildZoneFlagException izfe){
                return false;
            } 
            catch (ZoneNotFoundException znfe) {
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
        // TODO Auto-generated method stub
    }
}
