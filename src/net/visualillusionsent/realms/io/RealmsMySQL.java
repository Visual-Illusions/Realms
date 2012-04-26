package net.visualillusionsent.realms.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;

/**
 * Realms MySQL Data Handler
 * 
 * @author darkdiplomat
 */
public class RealmsMySQL extends RealmsData {
    
    private final String Zone_Table = "CREATE TABLE IF NOT EXISTS `RealmsZones` " +
                                        "(`ID` INT(255) NOT NULL AUTO_INCREMENT," +
                                        " `Name` TEXT NOT NULL," +
                                        " `World` TEXT NOT NULL," +
                                        " `Dimension` INT(2) NOT NULL,"+
                                        " `Parent` TEXT NOT NULL," +
                                        " `Greeting` TEXT NOT NULL," +
                                        " `Farewell` TEXT NOT NULL," +
                                        " `PVP` varchar(10) NOT NULL," +
                                        " `Sanctuary` varchar(10) NOT NULL," +
                                        " `Creeper` varchar(10) NOT NULL," +
                                        " `Ghast` varchar(10) NOT NULL," +
                                        " `Fall` varchar(10) NOT NULL," +
                                        " `Suffocate` varchar(10) NOT NULL," +
                                        " `Fire` varchar(10) NOT NULL," +
                                        " `Animals` varchar(10) NOT NULL," +
                                        " `Physics` varchar(10) NOT NULL," +
                                        " `Creative` varchar(10) NOT NULL," +
                                        " `Pistons` varchar(10) NOT NULL," +
                                        " `Healing` varchar(10) NOT NULL," +
                                        " `Enderman` varchar(10) NOT NULL," +
                                        " `Spread` varchar(10) NOT NULL, " +
                                        " `Flow` varchar(10) NOT NULL," +
                                        " `TNT` varchar(10) NOT NULL," +
                                        " `Potion` varchar(10) NOT NULL," +
                                        " `Starve` varchar(10) NOT NULL," +
                                        " `Restricted` varchar(10) NOT NULL," +
                                        " `Respawn` varchar(10) NOT NULL," +
                                        " `PolygonArea` TEXT NOT NULL," +
                                        " `Permissions` TEXT NOT NULL," +
                                        " PRIMARY KEY (`ID`))";
    
    private final String UpdateRequest = "ALTER TABLE `RealmsZones` ADD COLUMN `<Column>` <typedata> NOT NULL;"; //To be used if adding something
    
    private RHandle rhandle;
    private Object zonelock = new Object();
    
    /**
     * class constructor
     * 
     * @param realm
     */
    public RealmsMySQL(RHandle rhandle){
        this.rhandle = rhandle;
        createTables();
    }
    
    protected void Update(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn = rhandle.getSQLConnection();
        }catch(SQLException SQLE){
            rhandle.log(Level.SEVERE, "Unable to set MySQL Connection");
            rhandle.log(RLevel.DEBUGSEVERE, "Unable to set MySQL Connection", SQLE);
        }
        if(conn != null){
            try{
                ps = conn.prepareStatement("SELECT Restricted FROM RealmsZones");
                rs = ps.executeQuery();
                if (rs.next()){
                   //Don't do anything
                }
            }
            catch(SQLException SQLE){
                try{
                    ps = conn.prepareStatement(UpdateRequest);
                    ps.executeUpdate();
                }
                catch(SQLException SQLEE){
                    rhandle.log(Level.SEVERE, "MySQL exception @ Update... (verify your settings/MySQL Server)");
                    rhandle.log(RLevel.DEBUGSEVERE, "MySQL exception @ Update: ", SQLE);
                }
            } 
            finally{
                try{
                    if(ps != null && !ps.isClosed()){
                        ps.close();
                    }
                    if(rs != null && !rs.isClosed()){
                        rs.close();
                    }
                    if(conn != null && !conn.isClosed()){
                        conn.close();
                    }
                }
                catch (SQLException SQLE){
                    rhandle.log(Level.WARNING, "MySQL exception while closing connection");
                }
            }
        }
    }
    
    /**
     * Create Tables if they don't exist
     */
    private void createTables(){
        Connection conn = null;
        Statement st = null;
        try{
            conn = rhandle.getSQLConnection();
        }catch(SQLException SQLE){
            rhandle.log(Level.SEVERE, "Unable to set MySQL Connection @ createTables... (verify your settings/MySQL Server)");
            rhandle.log(RLevel.DEBUGSEVERE, "Unable to set MySQL Connection @ createTables: ", SQLE);
        }
        if(conn != null){
            try{
                st = conn.createStatement();
                st.execute(Zone_Table);
            }catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "MySQL exception @ createTables... (verify your settings/MySQL Server)");
                rhandle.log(RLevel.DEBUGSEVERE, "MySQL exception @ createTables: ", SQLE);
            }
            finally{
                try{
                    if(st != null && !st.isClosed()){
                        st.close();
                    }
                    if(conn != null && !conn.isClosed()){
                        conn.close();
                    }
                }
                catch (SQLException SQLE){
                    rhandle.log(Level.WARNING, "MySQL exception while closing connection @ CreateTables...");
                    rhandle.log(RLevel.DEBUGWARNING, "MySQL exception while closing connection @ CreateTables: ", SQLE);
                }
            }
        }
    }
    
    public boolean loadZones() {
        synchronized(zonelock){
            rhandle.log(Level.INFO, "[Realms] {MySQL} Loading Zones...");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection @ loadZones... (verify your settings/MySQL Server)");
                rhandle.log(RLevel.DEBUGSEVERE, "Unable to set MySQL Connection @ loadZones: ", SQLE);
                return false;
            }
            try{
                ps = conn.prepareStatement("SELECT * FROM RealmsZones");
                rs = ps.executeQuery();
                while (rs.next()){
                    String zonename = rs.getString("Name");
                    try{
                        String[] zoned = new String[]{ zonename,
                                                       rs.getString("World"),
                                                       rs.getString("Dimension"),
                                                       rs.getString("Parent"),
                                                       rs.getString("Greeting"), 
                                                       rs.getString("Farewell"), 
                                                       rs.getString("PVP"),
                                                       rs.getString("Sanctuary"), 
                                                       rs.getString("Creeper"),
                                                       rs.getString("Ghast"),
                                                       rs.getString("Fall"),
                                                       rs.getString("Suffocate"),
                                                       rs.getString("Fire"),
                                                       rs.getString("Animals"),
                                                       rs.getString("Physics"),
                                                       rs.getString("Creative"),
                                                       rs.getString("Pistons"),
                                                       rs.getString("Healing"),
                                                       rs.getString("Enderman"),
                                                       rs.getString("Spread"),
                                                       rs.getString("Flow"),
                                                       rs.getString("TNT"),
                                                       rs.getString("Potion"),
                                                       rs.getString("Starve"),
                                                       rs.getString("Restricted"),
                                                       rs.getString("Respawn")
                                                     };
                        
                        Zone theZone = new Zone(rhandle, zoned);
                        String Polygon = rs.getString("PolygonArea");
                        if(Polygon != null && !Polygon.equalsIgnoreCase("null")){
                            String[] polyed = rs.getString("PolygonArea").split(",");
                            new PolygonArea(rhandle, theZone, polyed);
                        }
                        String perms = rs.getString("Permissions");
                        if(perms != null && !perms.equalsIgnoreCase("null")){
                            String[] permed = rs.getString("Permissions").split(",");
                            for(String permission : permed){
                                String[] permsplit = permission.split(":");
                                theZone.setPermission(new Permission(permsplit[0], permsplit[1], (permsplit[2].equals("YES") ? true : false), (permsplit[3].equals("YES") ? true : false)));
                            }
                        }
                    }
                    catch(Exception e){
                        rhandle.log(Level.WARNING, "[Realms] Failed to load Zone: "+zonename);
                        rhandle.log(RLevel.DEBUGSEVERE, "Failed to load Zone: "+zonename+" :", e);
                    }
                }
            }
            catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "MySQL exception @ loadZones... (verify your settings/MySQL Server)");
                rhandle.log(RLevel.DEBUGSEVERE, "MySQL exception @ loadZones: ", SQLE);
                return false;
            }
            finally{
                try{
                    if(ps != null && !ps.isClosed()){
                        ps.close();
                    }
                    if(rs != null && !rs.isClosed()){
                        rs.close();
                    }
                    if(conn != null && !conn.isClosed()){
                        conn.close();
                    }
                }
                catch (SQLException SQLE){
                    rhandle.log(Level.WARNING, "MySQL exception while closing connection @ loadZones...");
                    rhandle.log(RLevel.DEBUGWARNING, "MySQL exception while closing connection @ loadZones: ", SQLE);
                }
            }
        }
        return true;
    }
    
    @Override
    public void saveZone(Zone zone) {
        new SaveThread(rhandle, zone, false).start();
    }

    @Override
    public boolean reloadZone(Zone zone) {
        synchronized(zonelock){
            rhandle.log(Level.INFO, "[Realms] Reloading Zone: '"+zone.getName()+"'...");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection @ loadZones... (verify your settings/MySQL Server)");
                rhandle.log(RLevel.DEBUGSEVERE, "Unable to set MySQL Connection @ loadZones: ", SQLE);
                return false;
            }
            try{
                ps = conn.prepareStatement("SELECT * FROM RealmsZones WHERE Name = ?");
                ps.setString(1, zone.getName());
                rs = ps.executeQuery();
                if (rs.next()){
                    String zonename = rs.getString("Name");
                    try{
                        String[] zoned = new String[]{ zonename,
                                                       rs.getString("World"),
                                                       rs.getString("Dimension"),
                                                       rs.getString("Parent"),
                                                       rs.getString("Greeting"), 
                                                       rs.getString("Farewell"), 
                                                       rs.getString("PVP"),
                                                       rs.getString("Sanctuary"), 
                                                       rs.getString("Creeper"),
                                                       rs.getString("Ghast"),
                                                       rs.getString("Fall"),
                                                       rs.getString("Suffocate"),
                                                       rs.getString("Fire"),
                                                       rs.getString("Animals"),
                                                       rs.getString("Physics"),
                                                       rs.getString("Creative"),
                                                       rs.getString("Pistons"),
                                                       rs.getString("Healing"),
                                                       rs.getString("Enderman"),
                                                       rs.getString("Spread"),
                                                       rs.getString("Flow"),
                                                       rs.getString("TNT"),
                                                       rs.getString("Potion"),
                                                       rs.getString("Starve"),
                                                       rs.getString("Restricted"),
                                                       rs.getString("Respawn")
                                                     };
                        
                        Zone theZone = new Zone(rhandle, zoned);
                        String Polygon = rs.getString("PolygonArea");
                        if(Polygon != null && !Polygon.equalsIgnoreCase("null")){
                            String[] polyed = rs.getString("PolygonArea").split(",");
                            new PolygonArea(rhandle, theZone, polyed);
                        }
                        String perms = rs.getString("Permissions");
                        if(perms != null && !perms.equalsIgnoreCase("null")){
                            String[] permed = rs.getString("Permissions").split(",");
                            for(String permission : permed){
                                String[] permsplit = permission.split(":");
                                theZone.setPermission(new Permission(permsplit[0], permsplit[1], (permsplit[2].equals("YES") ? true : false), (permsplit[3].equals("YES") ? true : false)));
                            }
                        }
                    }
                    catch(Exception e){
                        rhandle.log(Level.WARNING, "[Realms] Failed to reload Zone: "+zonename);
                        rhandle.log(RLevel.DEBUGSEVERE, "Failed to reload Zone: "+zonename+" :", e);
                    }
                }
            }
            catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "MySQL exception @ reloadZone... (verify your settings/MySQL Server)");
                rhandle.log(RLevel.DEBUGSEVERE, "MySQL exception @ reloadZone: ", SQLE);
                return false;
            }
            finally{
                try{
                    if(ps != null && !ps.isClosed()){
                        ps.close();
                    }
                    if(rs != null && !rs.isClosed()){
                        rs.close();
                    }
                    if(conn != null && !conn.isClosed()){
                        conn.close();
                    }
                }
                catch (SQLException SQLE){
                    rhandle.log(Level.WARNING, "MySQL exception while closing connection @ loadZones...");
                    rhandle.log(RLevel.DEBUGWARNING, "MySQL exception while closing connection @ loadZones: ", SQLE);
                }
            }
        }
        return true;
    }

    @Override
    public void reloadAll() {
        new Thread(){
            public void run(){
                loadZones();
            }
        }.start();
    }

    @Override
    public void saveAll() {
        new SaveThread(rhandle, null, false).start();
    }
}
