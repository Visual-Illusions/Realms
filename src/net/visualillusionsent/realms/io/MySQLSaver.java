package net.visualillusionsent.realms.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModConnection;
import net.visualillusionsent.viutils.ICModItem;

public final class MySQLSaver extends Thread {
    private final RHandle rhandle;

    private final Zone zone;
    private final String zone_insert = "INSERT INTO RealmsZones (" +
            "Name,World,Dimension,Parent,Greeting,Farewell,PVP," +
            "Sanctuary,Creeper,Ghast,Fall,Suffocate," +
            "Fire,Animal,Physics,Creative,Pistons," +
            "Healing,Enderman,Spread,Flow,TNT," +
            "Potion,Starve,Restricted) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String zone_update = "UPDATE RealmsZones SET World = ?, Dimension = ?, Parent = ?, Greeting = ?, Farewell = ?," +
            "PVP = ?, Sanctuary = ?, Creeper = ?, Ghast = ?, Fall = ?, Suffocate = ?, Fire = ?, Animal = ?," +
            "Physics = ?, Creative = ?, Pistons = ?, Healing = ?, Enderman = ?, Spread = ?, Flow = ?, TNT = ?," +
            "Potion = ?, Starve = ?, Restricted = ? WHERE Name = ?";
    private final String zone_select = "SELECT * FROM RealmsZones WHERE Name = ?";
    private final Object zone_lock = new Object();

    private final PolygonArea polyarea;
    private final String poly_select = "SELECT * FROM RealmsPolygons WHERE ZoneName = ?";
    private final String poly_insert = "INSERT INTO RealmsPolygons (ZoneName,Ceiling,Floor,Vertices) VALUES(?,?,?,?)";
    private final String poly_update = "UPDATE RealmsPolygons SET Ceiling = ?, Floor = ?, Vertices = ? WHERE ZoneName = ?";
    private final Object poly_lock = new Object();

    private final Permission perm;
    private final String perm_select = "SELECT * FROM RealmsPermissions WHERE ZoneName = ?, Owner = ?, Type = ?";
    private final String perm_insert = "INSERT INTO RealmsPermissions (Owner,Type,ZoneName,Allowed,OVERRIDE) VALUES(?,?,?,?,?)";
    private final String perm_update = "UPDATE RealmsPermissions SET Allowed = ?, OVERRIDE = ? WHERE Owner = ?, ZoneName = ?, Type = ?";
    private final Object perm_lock = new Object();

    private final Object[] inventory;
    private final String inv_select = "SELECT * FROM RealmsInventories WHERE Player = ?";
    private final String inv_insert = "INSERT INTO RealmsInventories (PlayerName,Items) VALUES(?,?)";
    private final String inv_update = "UPDATE RealmsInventories SET Items = ? WHERE PlayerName = ?";
    private final Object inv_lock = new Object();

    private ICModConnection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public MySQLSaver(RHandle rhandle, Zone zone) {
        this.rhandle = rhandle;
        this.zone = zone;
        polyarea = null;
        perm = null;
        inventory = null;
    }

    public MySQLSaver(RHandle rhandle, PolygonArea polyarea) {
        this.rhandle = rhandle;
        this.polyarea = polyarea;
        zone = null;
        perm = null;
        inventory = null;
    }

    public MySQLSaver(RHandle rhandle, Permission perm) {
        this.rhandle = rhandle;
        this.perm = perm;
        zone = null;
        polyarea = null;
        inventory = null;
    }

    public MySQLSaver(RHandle rhandle, Object[] obj) {
        this.rhandle = rhandle;
        inventory = obj;
        zone = null;
        polyarea = null;
        perm = null;
    }

    @Override
    public final void run() {
        if (zone != null) {
            saveZone();
        }
        else if (polyarea != null) {
            savePolygon();
        }
        else if (perm != null) {
            savePermission();
        }
        else if (inventory != null) {
            saveInventory();
        }
    }

    private final void saveZone() {
        synchronized (zone_lock) {
            boolean exists = false;
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to get MySQL Connection... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
            }
            try {
                if (conn != null) {
                    ps = conn.getConnection().prepareStatement(zone_select);
                    ps.setString(1, zone.getName());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        exists = true;
                    }
                    String[] lines = zone.toString().split(",");
                    if (exists) {
                        ps = conn.getConnection().prepareStatement(zone_update);
                        ps.setString(1, lines[1]);
                        ps.setString(2, lines[2]);
                        ps.setString(3, lines[3]);
                        ps.setString(4, lines[4]);
                        ps.setString(5, lines[5]);
                        ps.setString(6, lines[6]);
                        ps.setString(7, lines[7]);
                        ps.setString(8, lines[8]);
                        ps.setString(9, lines[9]);
                        ps.setString(10, lines[10]);
                        ps.setString(11, lines[11]);
                        ps.setString(12, lines[12]);
                        ps.setString(13, lines[13]);
                        ps.setString(14, lines[14]);
                        ps.setString(15, lines[15]);
                        ps.setString(16, lines[16]);
                        ps.setString(17, lines[17]);
                        ps.setString(18, lines[18]);
                        ps.setString(19, lines[19]);
                        ps.setString(20, lines[20]);
                        ps.setString(21, lines[21]);
                        ps.setString(22, lines[22]);
                        ps.setString(23, lines[23]);
                        ps.setString(24, lines[24]);
                        ps.setString(25, lines[0]);
                    }
                    else {
                        ps = conn.getConnection().prepareStatement(zone_insert);
                        ps.setString(1, lines[0]);
                        ps.setString(2, lines[1]);
                        ps.setString(3, lines[2]);
                        ps.setString(4, lines[3]);
                        ps.setString(5, lines[4]);
                        ps.setString(6, lines[5]);
                        ps.setString(7, lines[6]);
                        ps.setString(8, lines[7]);
                        ps.setString(9, lines[8]);
                        ps.setString(10, lines[9]);
                        ps.setString(11, lines[10]);
                        ps.setString(12, lines[11]);
                        ps.setString(13, lines[12]);
                        ps.setString(14, lines[13]);
                        ps.setString(15, lines[14]);
                        ps.setString(16, lines[15]);
                        ps.setString(17, lines[16]);
                        ps.setString(18, lines[17]);
                        ps.setString(19, lines[18]);
                        ps.setString(20, lines[19]);
                        ps.setString(21, lines[20]);
                        ps.setString(22, lines[21]);
                        ps.setString(23, lines[22]);
                        ps.setString(24, lines[23]);
                        ps.setString(25, lines[24]);
                    }
                    ps.execute();
                }
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to update MySQL data... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
            }
            finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (rs != null && !rs.isClosed()) {
                        rs.close();
                    }
                    if (conn != null) {
                        conn.release();
                        conn = null;
                    }
                }
                catch (SQLException sqle) {
                    rhandle.log(Level.WARNING, "Failed to release MySQL connection...");
                }
            }
        }
    }

    /**
     * Saves polygon area to MySQL table RealmsPolygons.
     */
    private final void savePolygon() {
        synchronized (poly_lock) {
            boolean exists = false;
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to get MySQL Connection... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
            }
            if (conn != null) {
                try {
                    ps = conn.getConnection().prepareStatement(poly_select);
                    ps.setString(1, polyarea.getZone().getName());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        exists = true;
                    }
                    String[] lines = polyarea.toString().split(",");
                    String Verts = null;
                    StringBuilder BuildVerts = new StringBuilder();
                    for (int i = 3; i < lines.length; i++) {
                        if (i != lines.length - 1) {
                            BuildVerts.append(lines[i] + ",");
                        }
                        else {
                            BuildVerts.append(lines[i]);
                        }
                    }
                    Verts = BuildVerts.toString();
                    if (exists) {
                        ps = conn.getConnection().prepareStatement(poly_update);
                        ps.setString(1, lines[1]);
                        ps.setString(2, lines[2]);
                        ps.setString(3, Verts);
                        ps.setString(4, lines[0]);
                    }
                    else {
                        ps = conn.getConnection().prepareStatement(poly_insert);
                        ps.setString(1, lines[0]);
                        ps.setString(2, lines[1]);
                        ps.setString(3, lines[2]);
                        ps.setString(4, Verts);
                    }
                    ps.execute();
                }
                catch (SQLException sqle) {
                    rhandle.log(Level.SEVERE, "Failed to update MySQL data... (enable debuging for stacktraces)");
                    rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
                }
                finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                        if (conn != null) {
                            conn.release();
                            conn = null;
                        }
                    }
                    catch (SQLException e) {
                        rhandle.log(Level.WARNING, "Failed to release MySQL connection...");
                    }
                }
            }
        }
    }

    /**
     * Saves permission to MySQL table RealmsPermissions.
     */
    private final void savePermission() {
        synchronized (perm_lock) {
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to get MySQL Connection... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
            }
            if (conn != null) {
                try {
                    ps = conn.getConnection().prepareStatement(perm_select);
                    ps.setString(1, perm.getZoneName());
                    ps.setString(2, perm.getOwnerName());
                    ps.setString(3, perm.getType().toString());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        ps = conn.getConnection().prepareStatement(perm_update);
                        ps.setString(1, perm.getAllowed() ? "1" : "0");
                        ps.setString(2, perm.getOverride() ? "1" : "0");
                        ps.setString(3, perm.getOwnerName());
                        ps.setString(4, perm.getZoneName());
                        ps.setString(5, perm.getType().toString());
                    }
                    else {
                        String[] lines = perm.toString().split(",");
                        ps = conn.getConnection().prepareStatement(perm_insert);
                        ps.setString(1, lines[0]);
                        ps.setString(2, lines[1]);
                        ps.setString(3, lines[2]);
                        ps.setString(4, lines[3]);
                        ps.setString(5, lines[4]);
                    }
                    ps.execute();
                }
                catch (SQLException sqle) {
                    rhandle.log(Level.SEVERE, "Failed to update MySQL data... (enable debuging for stacktraces)");
                    rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
                }
                finally {
                    try {
                        if (ps != null && !ps.isClosed()) {
                            ps.close();
                        }
                        if (rs != null && !rs.isClosed()) {
                            rs.close();
                        }
                        if (conn != null) {
                            conn.release();
                            conn = null;
                        }
                    }
                    catch (SQLException e) {
                        rhandle.log(Level.WARNING, "Failed to release MySQL connection...");
                    }
                }
            }
        }
    }

    /**
     * Saves a player's stored inventory.
     */
    private final void saveInventory() {
        synchronized (inv_lock) {
            String playername = null;
            ICModItem[] items = null;
            String inv = null;
            if (inventory[0] instanceof String) {
                playername = (String) inventory[0];
            }
            if (inventory[1] instanceof ICModItem[]) {
                items = (ICModItem[]) inventory[1];
            }

            if (playername != null && items != null) {
                StringBuilder build = new StringBuilder();
                build.append(playername);
                for (ICModItem item : items) {
                    if (item != null) {
                        build.append("," + item.getId() + ":" + item.getAmount() + ":" + item.getSlot() + ":" + item.getDamage());
                    }
                    else {
                        build.append(",null");
                    }
                }
                inv = build.toString();

                try {
                    conn = rhandle.getSQLConnection();
                }
                catch (SQLException sqle) {
                    rhandle.log(Level.SEVERE, "Failed to get MySQL Connection... (enable debuging for stacktraces)");
                    rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
                }
                if (conn != null) {
                    try {
                        ps = conn.getConnection().prepareStatement(inv_select);
                        ps.setString(1, playername);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            ps = conn.getConnection().prepareStatement(inv_update);
                            ps.setString(1, inv);
                            ps.setString(2, playername);
                        }
                        else {
                            ps = conn.getConnection().prepareStatement(inv_insert);
                            ps.setString(1, playername);
                            ps.setString(2, inv);
                        }
                    }
                    catch (SQLException sqle) {
                        rhandle.log(Level.SEVERE, "Failed to update MySQL data... (enable debuging for stacktraces)");
                        rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
                    }
                    finally {
                        try {
                            if (ps != null && !ps.isClosed()) {
                                ps.close();
                            }
                            if (rs != null && !rs.isClosed()) {
                                rs.close();
                            }
                            if (conn != null) {
                                conn.release();
                                conn = null;
                            }
                        }
                        catch (SQLException e) {
                            rhandle.log(Level.WARNING, "Failed to release MySQL connection...");
                        }
                    }
                }
            }
        }
    }
}
