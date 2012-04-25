package net.visualillusionsent.realms.io;

import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.PropsFile;

public class SaveThread extends Thread{
    private Zone zone;
    private boolean flatfile;
    private Object zonelock = new Object();
    
    public SaveThread(Zone zone, boolean flatfile){
        this.setName("Realms-SaveThread");
        this.zone = zone;
        this.flatfile = flatfile;
    }
    
    public void run(){
        if(flatfile){
            if(zone != null){
                synchronized(zonelock){
                    PropsFile zonefile = new PropsFile("plugins/config/Realms/Zones/"+zone.getName()+".zone");
                    
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
            else{
                synchronized(ZoneLists.getZones()){
                    for(Zone zone : ZoneLists.getZones()){
                        PropsFile zonefile = new PropsFile("plugins/config/Realms/Zones/"+zone.getName()+".zone");
                        
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
            }
        }
        else{
            if(zone != null){
                //TODO MySQL Save
            }
            else{
                //TODO MySQL Save
            }
        }
    }
}
