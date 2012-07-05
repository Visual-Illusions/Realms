package net.visualillusionsent.realms.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModConnection;
import net.visualillusionsent.viutils.ICModItem;

/**
 * Realms MySQL Data Handler
 * 
 * @author Jason Jones
 */
public class MySQLLoader implements RealmsData {
    /* Permissions Table Format */
    private final String perms_table = "CREATE TABLE IF NOT EXISTS `RealmsPermissions`" +
            " (`ID` INT(255) NOT NULL AUTO_INCREMENT," +
            " `Owner` varchar(20) NOT NULL," +
            " `Type` varchar(20) NOT NULL," +
            " `ZoneName` TEXT NOT NULL," +
            " `Allowed` varchar(2) NOT NULL," +
            " `OVERRIDE` varchar(2) NOT NULL," +
            " PRIMARY KEY (`ID`))";

    /* Polygons Table Format */
    private final String poly_table = "CREATE TABLE IF NOT EXISTS `RealmsPolygons` " +
            "(`ID` INT(255) NOT NULL AUTO_INCREMENT," +
            " `ZoneName` TEXT NOT NULL," +
            " `Ceiling` TEXT NOT NULL," +
            " `Floor` TEXT NOT NULL," +
            " `Vertices` TEXT NOT NULL," +
            " PRIMARY KEY (`ID`))";

    /* Zones Table Format */
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

    /* Inventories Table Format */
    private final String inv_table = "CREATE TABLE IF NOT EXISTS `RealmsInventories` " +
            "(`ID` INT(255) NOT NULL AUTO_INCREMENT," +
            "`PlayerName` varchar(20) NOT NULL," +
            "`Items` TEXT NOT NULL," +
            "PRIMARY KEY (`ID`))";

    private final String UpdateRequest;
    private final String ur2 = "ALTER TABLE RealmsPolygons DROP COLUMN World";

    private RHandle rhandle;

    /* Synchronize Locks */
    private Object polylock = new Object();
    private Object zonelock = new Object();
    private Object permlock = new Object();
    private Object invlock = new Object();

    /**
     * Class Constructor
     * 
     * @param realm
     */
    public MySQLLoader(RHandle rhandle) {
        this.rhandle = rhandle;
        UpdateRequest = "ALTER TABLE `RealmsZones` ADD COLUMN `World` varchar(64) NOT NULL DEFAULT ?, ADD COLUMN `Dimension` int(1) NOT NULL DEFAULT 0"; //To be used if adding something
        createTables();
        Update();
    }

    @Override
    public final boolean load() {
        if (!loadzones()) {
            return false;
        }
        if (!loadpolygons()) {
            return false;
        }
        if (!loadpermissions()) {
            return false;
        }
        loadInv();
        rhandle.setLoaded();
        return true;
    }

    private void Update() {
        ICModConnection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = rhandle.getSQLConnection();
        }
        catch (SQLException SQLE) {
            rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
        }
        if (conn != null) {
            try {
                ps = conn.getConnection().prepareStatement("SELECT World FROM RealmsZones");
                rs = ps.executeQuery();
                if (rs.next()) {
                    //Don't do anything
                }
            }
            catch (SQLException SQLE) {
                try {
                    ps = conn.getConnection().prepareStatement(UpdateRequest);
                    ps.setString(1, rhandle.getServer().getDefaultWorldName());
                    ps.executeUpdate();
                }
                catch (SQLException SQLEE) {
                    rhandle.log(Level.SEVERE, "MySQL exception in while adding Column: 'World' to Table: RealmsZones", SQLEE);
                }
            }
            try {
                ps = conn.getConnection().prepareStatement("SELECT World FROM RealmsPolygons");
                rs = ps.executeQuery();
                if (rs.next()) {
                    //Column found
                    ps = conn.getConnection().prepareStatement(ur2);
                    ps.execute();
                }
            }
            catch (SQLException SQLE) {
                //Column already gone, do nothing
            }
            finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (rs != null && !rs.isClosed()) {
                        rs.close();
                    }
                    conn.release();
                }
                catch (SQLException SQLE) {
                    rhandle.log(Level.WARNING, "MySQL exception while releasing connection", SQLE);
                }
            }
        }
    }

    /**
     * Create Tables if they don't exist
     */
    private boolean createTables() {
        boolean toRet = true;
        ICModConnection conn = null;
        Statement st = null;
        try {
            conn = rhandle.getSQLConnection();
        }
        catch (SQLException SQLE) {
            rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            return false;
        }
        if (conn != null) {
            try {
                st = conn.getConnection().createStatement();
                st.addBatch(zone_table);
                st.addBatch(poly_table);
                st.addBatch(perms_table);
                st.addBatch(inv_table);
                st.executeBatch();
            }
            catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "MySQL exception while CreatingTables", SQLE);
                toRet = false;
            }
            finally {
                try {
                    if (st != null && !st.isClosed()) {
                        st.close();
                    }
                    conn.release();
                }
                catch (SQLException SQLE) {
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
        synchronized (polylock) {
            rhandle.log(Level.INFO, "Loading Polygons...");
            ICModConnection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
                return false;
            }
            String line = null;
            if (conn != null) {
                try {
                    ps = conn.getConnection().prepareStatement("SELECT * FROM RealmsPolygons");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        String zonename = rs.getString("ZoneName");
                        if (zonename.equals("everywhere")) {
                            zonename = "EVERYWHERE-" + rhandle.getServer().getDefaultWorldName() + "-DIM0";
                        }
                        line = zonename + "," + rs.getString("Ceiling") + "," + rs.getString("Floor") + "," + rs.getString("Vertices");
                        new PolygonArea(rhandle, ZoneLists.getZoneByName(zonename), line.split(","));
                    }
                }
                catch (SQLException SQLE) {
                    rhandle.log(Level.SEVERE, "MySQL exception in while loading Polygons ", SQLE);
                    toRet = false;
                }
                catch (ZoneNotFoundException ZNFE) {
                    rhandle.log(Level.SEVERE, "Zone not found! " + line.split(",")[0]);
                }
                finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                        conn.release();
                    }
                    catch (SQLException SQLE) {
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
        synchronized (zonelock) {
            rhandle.log(Level.INFO, "Loading Zones...");
            ICModConnection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
                return false;
            }
            if (conn != null) {
                try {
                    ps = conn.getConnection().prepareStatement("SELECT * FROM RealmsZones");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        String zonename = rs.getString("Name");
                        if (zonename.equals("everywhere")) {
                            zonename = "EVERYWHERE-" + rhandle.getServer().getDefaultWorldName() + "-DIM0";
                        }
                        String parent = rs.getString("Parent");
                        if (parent.equals("everywhere")) {
                            parent = "EVERYWHERE-" + rhandle.getServer().getDefaultWorldName() + "-DIM0";
                        }
                        new Zone(rhandle, new String[] { zonename, rs.getString("World"), rs.getString("Dimension"), rs.getString("Parent"),
                                rs.getString("Greeting"), rs.getString("Farewell"), rs.getString("PVP"), rs.getString("Sanctuary"), rs.getString("Creeper"),
                                rs.getString("Ghast"), rs.getString("Fall"), rs.getString("Suffocate"), rs.getString("Fire"), rs.getString("Animal"), rs.getString("Physics"),
                                rs.getString("Creative"), rs.getString("Pistons"), rs.getString("Healing"), rs.getString("Enderman"), rs.getString("Spread"),
                                rs.getString("Flow"), rs.getString("TNT"), rs.getString("Potion"), rs.getString("Starve"), rs.getString("Restricted") });
                    }
                }
                catch (SQLException SQLE) {
                    rhandle.log(Level.SEVERE, "MySQL exception in while loading Zones", SQLE);
                    toRet = false;
                }
                finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                        conn.release();
                    }
                    catch (SQLException SQLE) {
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
        synchronized (permlock) {
            rhandle.log(Level.INFO, "Loading Permissions...");
            ICModConnection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
                return false;
            }
            String line = null;
            if (conn != null) {
                try {
                    ps = conn.getConnection().prepareStatement("SELECT * FROM RealmsPermissions");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        line = rs.getString("ZoneName");
                        if (line.equals("everywhere")) {
                            line = "EVERYWHERE-" + rhandle.getServer().getDefaultWorldName() + "-DIM0";
                        }
                        Zone zone = ZoneLists.getZoneByName(line);
                        zone.setPermission(new Permission(new String[] { rs.getString("Owner"), rs.getString("Type"), rs.getString("ZoneName"),
                                rs.getString("Allowed"), rs.getString("OVERRIDE") }));
                    }
                }
                catch (SQLException SQLE) {
                    rhandle.log(Level.SEVERE, "MySQL exception while loading Permissions", SQLE);
                    toRet = false;
                }
                catch (ZoneNotFoundException ZNFE) {
                    rhandle.log(Level.SEVERE, "Zone not found! " + line);
                }
                finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                        conn.release();
                    }
                    catch (SQLException SQLE) {
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
    private void loadInv() {
        synchronized (invlock) {
            rhandle.log(Level.INFO, "Loading Stored Inventories...");
            ICModConnection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException SQLE) {
                rhandle.log(Level.SEVERE, "Unable to set MySQL Connection", SQLE);
            }
            if (conn != null) {
                try {
                    ps = conn.getConnection().prepareStatement("SELECT * FROM RealmsInventories");
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        String name = rs.getString("PlayerName");
                        String[] itemstring = rs.getString("Items").split(",");
                        ICModItem[] items = new ICModItem[40];
                        for (int i = 0; i < itemstring.length && i < 40; i++) {
                            if (itemstring[i] != null || !itemstring[i].equals("null")) {
                                continue;
                            }
                            String[] it = itemstring[i].split(":");
                            items[i] = rhandle.getServer().makeItem(Integer.valueOf(it[0]), Integer.valueOf(it[1]), Integer.valueOf(it[2]), Integer.valueOf(it[3]));
                        }
                        rhandle.storeInventory(rhandle.getServer().getPlayer(name), items);
                    }
                }
                catch (SQLException SQLE) {
                    rhandle.log(Level.SEVERE, "MySQL exception while loading Inventories", SQLE);
                }
                finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                        conn.release();
                    }
                    catch (SQLException SQLE) {
                        rhandle.log(Level.WARNING, "MySQL exception while closing connection", SQLE);
                    }
                }
            }
        }
    }

    @Override
    public boolean reloadAll() {
        if (!loadzones()) {
            return false;
        }
        if (!loadpolygons()) {
            return false;
        }
        if (!loadpermissions()) {
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
