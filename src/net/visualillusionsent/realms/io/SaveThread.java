package net.visualillusionsent.realms.io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.PropsFile;

public class SaveThread extends Thread{
    private RHandle rhandle;
    private Zone zone;
    private boolean flatfile;
    private Object zonelock = new Object();
    
    private final String Zone_Insert = "INSERT INTO RealmsZones (" +
            "Name,World,Dimension,Parent,Greeting,Farewell,PVP," +
            "Sanctuary,Creeper,Ghast,Fall,Suffocate," +
            "Fire,Animals,Physics,Creative,Pistons," +
            "Healing,Enderman,Spread,Flow,TNT," +
            "Potion,Starve,Restricted,PolygonArea,Permissions) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    private final String Zone_Update = "UPDATE RealmsZones SET World = ?, Dimension = ?, Parent = ?, Greeting = ?, Farewell = ?, PVP = ?, Sanctuary = ?, " +
    		                          "Creeper = ?, Ghast = ?, Fall = ?, Suffocate = ?, Fire = ?, Animals = ?, Physics = ?, Creative = ?, Pistons = ?, " +
    		                          "Healing = ?, Enderman = ?, Spread = ?, Flow = ?, TNT = ?, Potion = ?, Starve = ?, Restricted = ?, " +
    		                          "PolygonArea = ?, Permissions = ? WHERE Name = ?";
    
    public SaveThread(RHandle rhandle, Zone zone, boolean flatfile){
        this.setName("Realms-SaveThread");
        this.zone = zone;
        this.flatfile = flatfile;
        this.rhandle = rhandle;
    }
    
    public void run(){
        if(flatfile){
            if(zone != null){
                synchronized(zonelock){
                    try{
                        PropsFile zonefile = new PropsFile("plugins/config/Realms/Zones/"+zone.getName()+".zone");
                        
                        StringBuilder perms = new StringBuilder();
                        for(Permission perm : zone.getPerms()){
                            perms.append(perm.toString());
                            perms.append(',');
                        }
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
                        zonefile.setString("PolygonArea",   zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                        zonefile.setString("Permissions",   perms.toString());
                        
                        zonefile.save();
                    }
                    catch(IOException e){
                        //FIXME
                    }
                }
            }
            else{
                synchronized(ZoneLists.getZones()){
                    for(Zone zone : ZoneLists.getZones()){
                        try{
                            PropsFile zonefile = new PropsFile("plugins/config/Realms/Zones/"+zone.getName()+".zone");
                            
                            StringBuilder perms = new StringBuilder();
                            synchronized(zone.getPerms()){
                                for(Permission perm : zone.getPerms()){
                                    perms.append(perm.toString());
                                    perms.append('~');
                                }
                            }
                            
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
                            zonefile.setString("PolygonArea",   zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                            zonefile.setString("Permissions", perms.toString());
                            
                            zonefile.save();
                        }
                        catch(IOException e){
                            //FIXME
                        }
                    }
                }
            }
        }
        else{
            if(zone != null){
                synchronized(zonelock){
                    String[] lines = null;
                    Connection conn = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try{
                        conn = rhandle.getSQLConnection();
                    }catch(SQLException SQLE){
                        rhandle.log(Level.SEVERE, "Unable to set MySQL Connection @ loadZones... (verify your settings/MySQL Server)");
                        rhandle.log(RLevel.DEBUGSEVERE, "Unable to set MySQL Connection @ loadZones: ", SQLE);
                    }
                    if(conn != null){
                        try{
                            boolean inc = false;
                            ps = conn.prepareStatement("SELECT ID FROM RealmsZones WHERE Name = ?");
                            ps.setString(1, zone.getName());
                            rs = ps.executeQuery();
                            if (rs.next()){
                                ps = conn.prepareStatement(Zone_Update);
                                inc = true;
                            }
                            else{
                                ps = conn.prepareStatement(Zone_Insert);
                            }
                            
                            lines = zone.toString().split(",");
                            StringBuilder perms = new StringBuilder();
                            synchronized(zone.getPerms()){
                                for(Permission perm : zone.getPerms()){
                                    perms.append(perm.toString());
                                    perms.append(',');
                                }
                            }
                            
                            int incro = inc ? 1 : 0;
                            for(int index = 0; index < 24; index++){
                                ps.setString(index+1, lines[index+incro]);
                            }
                           
                            if(inc){
                                ps.setString(25, zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                                ps.setString(26, perms.toString());
                                ps.setString(27, lines[0]);
                            }
                            else{
                                ps.setString(25, lines[25]);
                                ps.setString(26, zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                                ps.setString(27, perms.toString());
                            }
                            
                            ps.execute();
                            
                        }
                        catch(SQLException SQLE){
                            rhandle.log(Level.SEVERE, "MySQL exception @ saveZones... (verify your settings/MySQL Server)");
                            Logger.getLogger("Minecraft").log(Level.SEVERE, "MySQL exception @ saveZones: ", SQLE);
                        }
                        finally{
                            try{
                                if(rs != null && !rs.isClosed()){
                                    rs.close();
                                }                          
                                if(ps != null && !ps.isClosed()){
                                    ps.close();
                                }
                                if(conn != null && !conn.isClosed()){
                                    conn.close();
                                }
                            }
                            catch (SQLException SQLE){
                                rhandle.log(Level.WARNING, "MySQL exception while closing connection @ SaveZones...");
                                rhandle.log(RLevel.DEBUGWARNING, "MySQL exception while closing connection @ SaveZones: ", SQLE);
                            }
                        }
                    }
                }
            }
            else{
                synchronized(ZoneLists.getZones()){
                    String[] lines = null;
                    Connection conn = null;
                    PreparedStatement ps = null;
                    PreparedStatement insert = null;
                    PreparedStatement update = null;
                    ResultSet rs = null;
                    try{
                        conn = rhandle.getSQLConnection();
                    }catch(SQLException SQLE){
                        rhandle.log(Level.SEVERE, "Unable to set MySQL Connection @ loadZones... (verify your settings/MySQL Server)");
                        rhandle.log(RLevel.DEBUGSEVERE, "Unable to set MySQL Connection @ loadZones: ", SQLE);
                    }
                    if(conn != null){
                        try{
                            insert = conn.prepareStatement(Zone_Insert);
                            update = conn.prepareStatement(Zone_Update);
                            boolean hasUpdate = false, hasInsert = false;
                            for(Zone zone : ZoneLists.getZones()){
                                boolean inc = false;
                                ps = conn.prepareStatement("SELECT ID FROM RealmsZones WHERE Name = ?");
                                ps.setString(1, zone.getName());
                                rs = ps.executeQuery();
                                if (rs.next()){
                                    inc = true;
                                }
                            
                                lines = zone.toString().split(",");
                                StringBuilder perms = new StringBuilder();
                                synchronized(zone.getPerms()){
                                    for(Permission perm : zone.getPerms()){
                                        perms.append(perm.toString());
                                        perms.append(',');
                                    }
                                }
                                
                                if(inc){
                                    for(int index = 1; index < 24; index++){
                                        update.setString(index, lines[index]);
                                    }
                                    update.setString(25, zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                                    update.setString(26, perms.toString());
                                    update.setString(27, lines[0]);
                                    update.addBatch();
                                }
                                else{
                                    for(int index = 0; index < 25; index++){
                                        insert.setString(index+1, lines[index]);
                                    }
                                    insert.setString(26, zone.getPolygon() == null ? "null" : zone.getPolygon().toString());
                                    insert.setString(27, perms.toString());
                                    insert.addBatch();
                                }
                            }
                            if(hasUpdate){
                                update.executeBatch();
                            }
                            if(hasInsert){
                                insert.executeBatch();
                            }
                        }
                        catch(SQLException SQLE){
                            rhandle.log(Level.SEVERE, "MySQL exception @ saveZones... (verify your settings/MySQL Server)");
                            Logger.getLogger("Minecraft").log(Level.SEVERE, "MySQL exception @ saveZones: ", SQLE);
                        }
                        finally{
                            try{
                                if(rs != null && !rs.isClosed()){
                                    rs.close();
                                } 
                                if(ps != null && !ps.isClosed()){
                                    ps.close();
                                }
                                if(insert != null && !insert.isClosed()){
                                    insert.close();
                                }
                                if(update != null && !update.isClosed()){
                                    update.close();
                                }
                                if(conn != null && !conn.isClosed()){
                                    conn.close();
                                }
                            }
                            catch (SQLException SQLE){
                                rhandle.log(Level.WARNING, "MySQL exception while closing connection @ SaveZones...");
                                rhandle.log(RLevel.DEBUGWARNING, "MySQL exception while closing connection @ SaveZones: ", SQLE);
                            }
                        }
                    }
                }
            }
        }
    }
}
