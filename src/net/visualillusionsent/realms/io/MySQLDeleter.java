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

public class MySQLDeleter extends Thread {
    private final RHandle rhandle;

    private final Zone zone;
    private final String zone_select = "SELECT * FROM RealmsZones WHERE Name = ?";
    private final String zone_delete = "DELETE FROM RealmsZones WHERE Name = ?";
    private final Object zone_lock = new Object();

    private final PolygonArea polyarea;
    private final String poly_select = "SELECT * FROM RealmsPolygons WHERE ZoneName = ?";
    private final String poly_delete = "DELETE FROM RealmsPolygons WHERE ZoneName = ?";
    private final Object poly_lock = new Object();

    private final Permission perm;
    private final String perm_select = "SELECT * FROM RealmsPermissions WHERE ZoneName = ?, Owner = ?, Type = ?";
    private final String perm_delete = "DELETE FROM RealmsPermissions WHERE Owner = ?, ZoneName = ?, Type = ?";
    private final Object perm_lock = new Object();

    private final String invOwner;
    private final String inv_select = "SELECT * FROM RealmsInventories WHERE PlayerName = ?";
    private final String inv_delete = "DELETE FROM RealmsInventories WHERE PlayerName = ?";
    private final Object inv_lock = new Object();

    private ICModConnection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public MySQLDeleter(RHandle rhandle, Zone zone) {
        this.rhandle = rhandle;
        this.zone = zone;
        polyarea = null;
        perm = null;
        invOwner = null;
    }

    public MySQLDeleter(RHandle rhandle, PolygonArea polyarea) {
        this.rhandle = rhandle;
        this.polyarea = polyarea;
        zone = null;
        perm = null;
        invOwner = null;
    }

    public MySQLDeleter(RHandle rhandle, Permission perm) {
        this.rhandle = rhandle;
        this.perm = perm;
        zone = null;
        polyarea = null;
        invOwner = null;
    }

    public MySQLDeleter(RHandle rhandle, Object[] obj) {
        this.rhandle = rhandle;
        this.invOwner = (String) obj[0];
        zone = null;
        polyarea = null;
        perm = null;
    }

    public final void run() {
        if (zone != null) {
            deleteZone();
        }
        else if (polyarea != null) {
            deletePolygon();
        }
        else if (perm != null) {
            deletePermission();
        }
        else if (invOwner != null) {
            deleteInventory();
        }
    }

    private final void deleteZone() {
        synchronized (zone_lock) {
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
                        ps = conn.getConnection().prepareStatement(zone_delete);
                        ps.setString(1, zone.getName());
                        ps.execute();
                    }
                }
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to delete MySQL data... (enable debuging for stacktraces)");
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

    private final void deletePolygon() {
        synchronized (poly_lock) {
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
                        ps = conn.getConnection().prepareStatement(poly_delete);
                        ps.setString(1, polyarea.getZone().getName());
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
                    catch (SQLException e) {
                        rhandle.log(Level.WARNING, "Failed to release MySQL connection...");
                    }
                }
            }
        }
    }

    private final void deletePermission() {
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
                        ps = conn.getConnection().prepareStatement(perm_delete);
                        ps.setString(1, perm.getZoneName());
                        ps.setString(2, perm.getOwnerName());
                        ps.setString(3, perm.getType().toString());
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
                    catch (SQLException e) {
                        rhandle.log(Level.WARNING, "Failed to release MySQL connection...");
                    }
                }
            }
        }
    }
    
    private final void deleteInventory() {
        synchronized (inv_lock) {
            try {
                conn = rhandle.getSQLConnection();
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to get MySQL Connection... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", sqle);
            }
            try {
                if (conn != null) {
                    ps = conn.getConnection().prepareStatement(inv_select);
                    ps.setString(1, invOwner);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        ps = conn.getConnection().prepareStatement(inv_delete);
                        ps.setString(1, invOwner);
                        ps.execute();
                    }
                }
            }
            catch (SQLException sqle) {
                rhandle.log(Level.SEVERE, "Failed to delete MySQL data... (enable debuging for stacktraces)");
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
