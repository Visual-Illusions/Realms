package net.visualillusionsent.realms.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModItem;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms MySQL Data Handler
 * 
 * @author darkdiplomat
 */
public class RealmsMySQL extends RealmsData {
    /*Permissions Table Format*/
    private final String perms_table = "CREATE TABLE IF NOT EXISTS `RealmsPermissions`" +
                                        " (`ID` INT(255) NOT NULL AUTO_INCREMENT," +
                                        " `Owner` varchar(20) NOT NULL," +
                                        " `Type` varchar(20) NOT NULL," +
                                        " `ZoneName` TEXT NOT NULL," +
                                        " `Allowed` varchar(2) NOT NULL," +
                                        " `OVERRIDE` varchar(2) NOT NULL," +
                                        " PRIMARY KEY (`ID`))";
    
    /*Polygons Table Format*/
    private final String poly_table = "CREATE TABLE IF NOT EXISTS `RealmsPolygons` " +
                                        "(`ID` INT(255) NOT NULL AUTO_INCREMENT," +
                                        " `ZoneName` TEXT NOT NULL," +
                                        " `Ceiling` TEXT NOT NULL," +
                                        " `Floor` TEXT NOT NULL," +
                                        " `Vertices` TEXT NOT NULL," +
                                        " PRIMARY KEY (`ID`))";
    
    /*Zones Table Format*/
    private final String zone_table = "CREATE TABLE IF NOT EXISTS `RealmsZones` " +
                                        "(`ID` INT(255) NOT NULL AUTO_INCREMENT," +
                                        " `Name` TEXT NOT NULL," +
                                        " `World` TEXT NOT NULL," +
                                        " `Dimension` int(1) NOT NULL," +
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
                                        " `Animal` varchar(10) NOT NULL," +
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
                                        " PRIMARY KEY (`ID`))";
    
    /*Inventories Table Format*/
    private final String inv_table = "CREATE TABLE IF NOT EXISTS `RealmsInventories` " +
                                        "(`ID` INT(255) NOT NULL AUTO_INCREMENT," +
                                        "`PlayerName` varchar(20) NOT NULL," +
                                        "`Items` TEXT NOT NULL," +
                                        "PRIMARY KEY (`ID`))";
    
    /*Zones Table Insert Statement*/
    private final String zone_insert = "INSERT INTO RealmsZones (" +
                                        "Name,World,Dimension,Parent,Greeting,Farewell,PVP," +
                                        "Sanctuary,Creeper,Ghast,Fall,Suffocate," +
                                        "Fire,Animal,Physics,Creative,Pistons," +
                                        "Healing,Enderman,Spread,Flow,TNT," +
                                        "Potion,Starve,Restricted) " +
                                        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    /*Polygons Insert Statement*/
    private final String poly_insert = "INSERT INTO RealmsPolygons (ZoneName,Ceiling,Floor,Vertices) VALUES(?,?,?,?)";
    
    /*Permissions Insert Statement*/
    private final String perm_insert = "INSERT INTO RealmsPermissions (Owner,Type,ZoneName,Allowed,OVERRIDE) VALUES(?,?,?,?,?)";
    
    /*Inventories Insert Statement*/
    private final String inv_insert = "INSERT INTO RealmsInventories (PlayerName,Items) VALUES(?,?)";
    
    /*Zone Update Statement*/
    private final String zone_update = "UPDATE RealmsZones SET World = ?, Dimension = ?, Parent = ?, Greeting = ?, Farewell = ?," +
    		                            "PVP = ?, Sanctuary = ?, Creeper = ?, Ghast = ?, Fall = ?, Suffocate = ?, Fire = ?, Animal = ?," +
    		                            "Physics = ?, Creative = ?, Pistons = ?, Healing = ?, Enderman = ?, Spread = ?, Flow = ?, TNT = ?," +
                                        "Potion = ?, Starve = ?, Restricted = ? WHERE Name = ?";
    
    /*Polygon Update Statement*/
    private final String poly_update = "UPDATE RealmsPolygons SET Ceiling = ?, Floor = ?, Vertices = ?, WHERE ZoneName = ?";
    
    private final String UpdateRequest;
    private final String ur2 = "ALTER TABLE RealmsPolygons DROP COLUMN World";
    
    private RHandle rhandle;
    
    /*Synchronize Locks*/
    private Object polylock = new Object();
    private Object zonelock = new Object();
    private Object permlock = new Object();
    private Object invlock = new Object();
    
    private boolean isdumpingpoly = false;
    private boolean donedumpingpoly = false;
    private boolean isdumpingzone = false;
    private boolean donedumpingzone = false;
    private boolean isdumpingperm = false;
    private boolean donedumpingperm = false;
    private boolean isdumpinginv = false;
    private boolean donedumpinginv = false;
    
    /**
     * Class Constructor
     * 
     * @param realm
     */
    public RealmsMySQL(RHandle rhandle){
        this.rhandle = rhandle;
        UpdateRequest = "ALTER TABLE `RealmsZones` ADD COLUMN `World` varchar(64) NOT NULL DEFAULT ?, ADD COLUMN `Dimension` int(1) NOT NULL DEFAULT 0"; //To be used if adding something
        createTables();
        Update();
        loadzones();
        loadpolygons();
        loadpermissions();
        loadInv();
    }
    
    private void Update(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn = rhandle.getSQLConnection();
        }catch(SQLException SQLE){
            rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
        }
        if(conn != null){
            try{
                ps = conn.prepareStatement("SELECT World FROM RealmsZones");
                rs = ps.executeQuery();
                if (rs.next()){
                   //Don't do anything
                }
            }
            catch(SQLException SQLE){
                try{
                    ps = conn.prepareStatement(UpdateRequest);
                    ps.setString(1, rhandle.getServer().getDefaultWorldName());
                    ps.executeUpdate();
                }
                catch(SQLException SQLEE){
                    rhandle.log(Level.SEVERE, "MySQL exception in while adding Column: 'World' to Table: RealmsZones", SQLEE);
                }
            }
            try{
                ps = conn.prepareStatement("SELECT World FROM RealmsPolygons");
                rs = ps.executeQuery();
                if (rs.next()){
                    //Column found
                    ps = conn.prepareStatement(ur2);
                    ps.execute();
                }
            }
            catch(SQLException SQLE){
                //Column already gone, do nothing
            }
            finally{
                try{
                    if(ps != null && !ps.isClosed()){
                        ps.close();
                    }
                    if(rs != null && !rs.isClosed()){
                        rs.close();
                    }
                    rhandle.releaseConn();
                }
                catch (SQLException SQLE){
                    rhandle.log(Level.WARNING, "MySQL exception while releasing connection", SQLE);
                }
            }
        }
    }
    
    /**
     * Create Tables if they don't exist
     */
    private boolean createTables(){
        boolean toRet = true;
        Connection conn = null;
        Statement st = null;
        try{
            conn = rhandle.getSQLConnection();
        }catch(SQLException SQLE){
            rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            return false;
        }
        if(conn != null){
            try{
                st = conn.createStatement();
                st.addBatch(zone_table);
                st.addBatch(poly_table);
                st.addBatch(perms_table);
                st.addBatch(inv_table);
                st.executeBatch();
            }catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "MySQL exception while CreatingTables", SQLE);
                toRet = false;
            }
            finally{
                try{
                    if(st != null && !st.isClosed()){
                        st.close();
                    }
                    rhandle.releaseConn();
                }
                catch (SQLException SQLE){
                    rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                }
            }
        }
        return toRet;
    }
    
    /**
     * Loads Polygons
     */
    private boolean loadpolygons() {
        boolean toRet = true;
        synchronized(polylock){
            rhandle.log(Level.INFO, "Loading Polygons...");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
                return false;
            }
            String line = null;
            if(conn != null){
                try{
                    ps = conn.prepareStatement("SELECT * FROM RealmsPolygons");
                    rs = ps.executeQuery();
                    while (rs.next()){
                        String zonename = rs.getString("ZoneName");
                        if(zonename.equals("everywhere")){
                            zonename = "EVERYWHERE-"+rhandle.getServer().getDefaultWorldName()+"-DIM0";
                        }
                        line = zonename+","+ rs.getString("Ceiling")+","+rs.getString("Floor")+","+rs.getString("Vertices");
                        new PolygonArea(rhandle, ZoneLists.getZoneByName(zonename), line.split(","));
                    }
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception in while loading Polygons ", SQLE);
                    toRet = false;
                } 
                catch (ZoneNotFoundException ZNFE) {
                    rhandle.log(Level.SEVERE, "Zone not found! "+line.split(",")[0]);
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        if(rs != null && !rs.isClosed()){
                            rs.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        return toRet;
    }

    /**
     * Loads Zones
     */
    private boolean loadzones() {
        boolean toRet = true;
        synchronized(zonelock){
            rhandle.log(Level.INFO, "Loading Zones...");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
                return false;
            }
            if(conn != null){
                try{
                    ps = conn.prepareStatement("SELECT * FROM RealmsZones");
                    rs = ps.executeQuery();
                    while (rs.next()){
                        String zonename = rs.getString("Name");
                        if(zonename.equals("everywhere")){
                            zonename = "EVERYWHERE-"+rhandle.getServer().getDefaultWorldName()+"-DIM0";
                        }
                        String parent = rs.getString("Parent");
                        if(parent.equals("everywhere")){
                            parent = "EVERYWHERE-"+rhandle.getServer().getDefaultWorldName()+"-DIM0";
                        }
                        new Zone(rhandle, new String[]{zonename, rs.getString("World"), rs.getString("Dimension"), rs.getString("Parent"),
                                rs.getString("Greeting"), rs.getString("Farewell"), rs.getString("PVP"), rs.getString("Sanctuary"), rs.getString("Creeper"),
                                rs.getString("Ghast"), rs.getString("Fall"), rs.getString("Suffocate"), rs.getString("Fire"), rs.getString("Animal"), rs.getString("Physics"),
                                rs.getString("Creative"), rs.getString("Pistons"), rs.getString("Healing"), rs.getString("Enderman"), rs.getString("Spread"),
                                rs.getString("Flow"), rs.getString("TNT"), rs.getString("Potion"), rs.getString("Starve"), rs.getString("Restricted")});
                    }
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception in while loading Zones", SQLE);
                    toRet = false;
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        if(rs != null && !rs.isClosed()){
                            rs.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        return toRet;
    }

    /**
     * Loads Permissions
     */
    private boolean loadpermissions() {
        boolean toRet = true;
        synchronized(permlock){
            rhandle.log(Level.INFO, "Loading Permissions...");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
                return false;
            }
            String line = null;
            if(conn != null){
                try{
                    ps = conn.prepareStatement("SELECT * FROM RealmsPermissions");
                    rs = ps.executeQuery();
                    while (rs.next()){
                        line = rs.getString("ZoneName");
                        if(line.equals("everywhere")){
                            line = "EVERYWHERE-"+rhandle.getServer().getDefaultWorldName()+"-DIM0";
                        }
                        Zone zone = ZoneLists.getZoneByName(line);
                        zone.setPermission(new Permission(new String[]{rs.getString("Owner"), rs.getString("Type"), rs.getString("ZoneName"),
                                rs.getString("Allowed"), rs.getString("OVERRIDE")}));
                    }
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception while loading Permissions", SQLE);
                    toRet = false;
                } catch (ZoneNotFoundException ZNFE) {
                    rhandle.log(Level.SEVERE, "Zone not found! "+line);
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        if(rs != null && !rs.isClosed()){
                            rs.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        return toRet;
    }
    
    /**
     * Loads Inventories
     */
    private void loadInv(){
        synchronized(invlock){
            rhandle.log(Level.INFO, "Loading Stored Inventories...");
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            }
            if(conn != null){
                try{
                    ps = conn.prepareStatement("SELECT * FROM RealmsInventories");
                    rs = ps.executeQuery();
                    while (rs.next()){
                        String name = rs.getString("PlayerName");
                        String[] itemstring = rs.getString("Items").split(",");
                        ICModItem[] items = new ICModItem[40];
                        for(int i = 0; i < itemstring.length && i < 40; i++){
                            if(itemstring[i] != null || !itemstring[i].equals("null")){
                                continue;
                            }
                            String[] it = itemstring[i].split(":");
                            items[i] = rhandle.getServer().makeItem(Integer.valueOf(it[0]), Integer.valueOf(it[1]), Integer.valueOf(it[2]), Integer.valueOf(it[3]));
                        }
                        rhandle.storeInventory(rhandle.getServer().getPlayer(name), items);
                    }
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception while loading Inventories", SQLE);
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        if(rs != null && !rs.isClosed()){
                            rs.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
    }

    @Override
    public boolean dumppoly() {
        if(!isdumpingpoly){
            isdumpingpoly = true;
            dumppolydata();
            return true;
        }
        else if (!donedumpingpoly){
            return true;
        }
        isdumpingpoly = false;
        return false;
    }
    
    @Override
    public boolean dumpzone(){
        if(!isdumpingzone){
            isdumpingzone = true;
            dumpzonedata();
            return true;
        }
        else if (!donedumpingzone){
            return true;
        }
        isdumpingzone = false;
        donedumpingzone = false;
        return false;
    }
    
    @Override
    public boolean dumpperm() {
        if(!isdumpingperm){
            isdumpingperm = true;
            dumppermdata();
            return true;
        }
        else if (!donedumpingperm){
            return true;
        }
        isdumpingperm = false;
        donedumpingperm = false;
        return false;
    }
    
    @Override
    public boolean dumpinv() {
        if(!isdumpinginv){
            isdumpinginv = true;
            dumpInvData();
            return true;
        }
        else if (!donedumpinginv){
            return true;
        }
        isdumpinginv = false;
        donedumpinginv = false;
        return false;
    }
    
    /**
     * Saves Zones
     */
    private void dumpzonedata(){
        synchronized(zonelock){
            List<Zone> zones = ZoneLists.getZones();
            ArrayList<String> exists = new ArrayList<String>();
            String[] lines = null;
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            PreparedStatement update = null;
            PreparedStatement insert = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            }
            if(conn != null){
                try{
                    update =  conn.prepareStatement(zone_update);
                    insert = conn.prepareStatement(zone_insert);
                    ps = conn.prepareStatement("SELECT * FROM RealmsZones");
                    rs = ps.executeQuery();
                    while(rs.next()){
                        exists.add(rs.getString("Name"));
                    }
                    for(Zone zone : zones){
                        lines = zone.toString().split(",");
                        if(exists.contains(zone.getName())){
                            update.setString(1, lines[1]);
                            update.setString(2, lines[2]);
                            update.setString(3, lines[3]);
                            update.setString(4, lines[4]);
                            update.setString(5, lines[5]);
                            update.setString(6, lines[6]);
                            update.setString(7, lines[7]);
                            update.setString(8, lines[8]);
                            update.setString(9, lines[9]);
                            update.setString(10, lines[10]);
                            update.setString(11, lines[11]);
                            update.setString(12, lines[12]);
                            update.setString(13, lines[13]);
                            update.setString(14, lines[14]);
                            update.setString(15, lines[15]);
                            update.setString(16, lines[16]);
                            update.setString(17, lines[17]);
                            update.setString(18, lines[18]);
                            update.setString(19, lines[19]);
                            update.setString(20, lines[20]);
                            update.setString(21, lines[21]);
                            update.setString(22, lines[22]);
                            update.setString(23, lines[23]);
                            update.setString(24, lines[24]);
                            update.setString(25, lines[0]);
                            update.addBatch();
                        }
                        else{
                            insert.setString(1, lines[0]);
                            insert.setString(2, lines[1]);
                            insert.setString(3, lines[2]);
                            insert.setString(4, lines[3]);
                            insert.setString(5, lines[4]);
                            insert.setString(6, lines[5]);
                            insert.setString(7, lines[6]);
                            insert.setString(8, lines[7]);
                            insert.setString(9, lines[8]);
                            insert.setString(10, lines[9]);
                            insert.setString(11, lines[10]);
                            insert.setString(12, lines[11]);
                            insert.setString(13, lines[12]);
                            insert.setString(14, lines[13]);
                            insert.setString(15, lines[14]);
                            insert.setString(16, lines[15]);
                            insert.setString(17, lines[16]);
                            insert.setString(18, lines[17]);
                            insert.setString(19, lines[18]);
                            insert.setString(20, lines[19]);
                            insert.setString(21, lines[20]);
                            insert.setString(22, lines[21]);
                            insert.setString(23, lines[22]);
                            insert.setString(24, lines[23]);
                            insert.setString(25, lines[24]);
                            insert.addBatch();
                        }
                    }
                    insert.executeBatch();
                    update.executeBatch();
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception in while saving Zones", SQLE);
                }
                finally{
                    try{
                        if(rs != null && !rs.isClosed()){
                            rs.close();
                        }
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        donedumpingzone = true;
    }
    
    /**
     * Saves Polygons
     */
    private void dumppolydata(){
        synchronized(polylock){
            List<Zone> zones = ZoneLists.getZones();
            String[] lines = null;
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            PreparedStatement update = null;
            PreparedStatement insert = null;
            ArrayList<String> exists = new ArrayList<String>();
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            }
            if(conn != null){
                try{
                    ps = conn.prepareStatement("SELECT * FROM RealmsPolygons");
                    rs = ps.executeQuery();
                    while(rs.next()){
                        exists.add(rs.getString("ZoneName"));
                    }
                    insert = conn.prepareStatement(poly_insert);
                    update = conn.prepareStatement(poly_update);
                    for(Zone zone : zones){
                        lines = zone.getPolygon().toString().split(",");
                        String Verts = null;
                        StringBuilder BuildVerts = new StringBuilder();
                        for (int i = 3; i < lines.length; i++){
                            if(i != lines.length - 1){
                                BuildVerts.append(lines[i] + ",");
                            }
                            else{
                                BuildVerts.append(lines[i]);
                            }
                        }
                        Verts = BuildVerts.toString();
                        if(exists.contains(lines[0])){
                            update.setString(1, lines[1]);
                            update.setString(2, lines[2]);
                            update.setString(3, Verts);
                            update.setString(4, lines[0]);
                            update.addBatch();
                        }
                        else{
                            insert.setString(1, lines[0]);
                            insert.setString(2, lines[1]);
                            insert.setString(3, lines[2]);
                            insert.setString(4, Verts);
                            insert.addBatch();
                        }
                    }
                    insert.executeBatch();
                    update.executeBatch();
                }
                catch (SQLException SQLE) {
                    rhandle.log(Level.SEVERE, "MySQL exception in while saving Polygons", SQLE);
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        donedumpingpoly = true;
    }
    
    /**
     * Saves Permissions
     */
    private void dumppermdata(){
        synchronized(permlock){
            List<Zone> zones = ZoneLists.getZones();
            List<Permission> perms = new ArrayList<Permission>();
            for(Zone zone : zones){
                perms.addAll(zone.getPerms());
            }
            String[] lines = null;
            Connection conn = null;
            PreparedStatement ps = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            }
            if(conn != null){
                try{
                    ps = conn.prepareStatement("TRUNCATE TABLE RealmsPermissions");
                    ps.execute();
                    ps = conn.prepareStatement(perm_insert);
                    for(Permission perm : perms){
                        lines = perm.toString().split(",");
                        ps.setString(1, lines[0]);
                        ps.setString(2, lines[1]);
                        ps.setString(3, lines[2]);
                        ps.setString(4, lines[3]);
                        ps.setString(5, lines[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception in while saving Permissons", SQLE);
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        donedumpingperm = true;
    }
    
    /**
     * Saves Inventories
     */
    private void dumpInvData(){
        synchronized(invlock){
            HashMap<ICModPlayer, ICModItem[]> Inventories = rhandle.getInvMap();
            String[] StoredInv = new String[Inventories.size()];
            int i = 0;
            for(ICModPlayer key : Inventories.keySet()){
                StringBuilder build = new StringBuilder();
                build.append(key.getName());
                ICModItem[] items = Inventories.get(key);
                for(ICModItem item : items){
                    if(item != null){
                        build.append(","+item.getId()+":"+item.getAmount()+":"+item.getSlot()+":"+item.getDamage());
                    }
                    else{
                        build.append(",null");
                    }
                }
                StoredInv[i] = build.toString();
                i++;
            }
            String[] lines = null;
            Connection conn = null;
            PreparedStatement ps = null;
            try{
                conn = rhandle.getSQLConnection();
            }catch(SQLException SQLE){
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            }
            if(conn != null){
                try{
                    ps = conn.prepareStatement("TRUNCATE TABLE RealmsInventories");
                    ps.execute();
                    ps = conn.prepareStatement(inv_insert);
                    for(String key : StoredInv){
                        lines = key.split(",");
                        ps.setString(1, lines[0]);
                        ps.setString(2, lines[1]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                catch(SQLException SQLE){
                    rhandle.log(Level.SEVERE, "MySQL exception in while saving Inventories", SQLE);
                }
                finally{
                    try{
                        if(ps != null && !ps.isClosed()){
                            ps.close();
                        }
                        rhandle.releaseConn();
                    }
                    catch (SQLException SQLE){
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
        donedumpinginv = true;
    }

    @Override
    public boolean reloadAll() {
        if(!loadzones()){
            return false;
        }
        if(!loadpolygons()){
            return false;
        }
        if(!loadpermissions()){
            return false;
        }
        return true;
    }

    @Override
    public boolean reloadZones() {
        return loadzones();
    }

    @Override
    public boolean reloadPerms() {
        return loadpermissions();
    }

    @Override
    public boolean reloadPolys() {
        return loadpolygons();
    }
}
