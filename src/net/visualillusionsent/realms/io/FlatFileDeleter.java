package net.visualillusionsent.realms.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModItem;

public class FlatFileDeleter extends Thread {
    private final RHandle rhandle;

    private final Zone zone;
    private final File zone_csv = new File("plugins/config/Realms/zones.csv");
    private final Object zone_lock = new Object();

    private final PolygonArea polyarea;
    private final File poly_csv = new File("plugins/config/Realms/polygons.csv");
    private final Object poly_lock = new Object();

    private final Permission perm;
    private final File perm_csv = new File("plugins/config/Realms/permissions.csv");
    private final Object perm_lock = new Object();

    private final Object[] inventory;
    private final File inv_csv = new File("plugins/config/Realms/inventories.csv");
    private final Object inv_lock = new Object();

    public FlatFileDeleter(RHandle rhandle, Zone zone) {
        this.rhandle = rhandle;
        this.zone = zone;
        polyarea = null;
        perm = null;
        inventory = null;
    }

    public FlatFileDeleter(RHandle rhandle, PolygonArea polyarea) {
        this.rhandle = rhandle;
        this.polyarea = polyarea;
        zone = null;
        perm = null;
        inventory = null;
    }

    public FlatFileDeleter(RHandle rhandle, Permission perm) {
        this.rhandle = rhandle;
        this.perm = perm;
        zone = null;
        polyarea = null;
        inventory = null;
    }

    public FlatFileDeleter(RHandle rhandle, Object[] obj) {
        this.rhandle = rhandle;
        this.inventory = obj;
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
        else if (inventory != null){
            deleteInventory();
        }
    }

    /**
     * Deletes a zone from the zones.csv
     */
    private final void deleteZone() {
        synchronized (zone_lock) {
            BufferedReader br = null;
            PrintWriter pw = null;
            File tempFile = new File(zone_csv.getAbsolutePath() + ".tmp");
            try {
                br = new BufferedReader(new FileReader(zone_csv));
                pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().equals(zone.toString())) {
                        pw.println(line);
                        pw.flush();
                    }
                }
            }
            catch (FileNotFoundException ex) {
                rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
            }
            catch (IOException ex) {
                rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
                try {
                    if (br != null) {
                        br.close();
                    }
                }
                catch (IOException e) {
                    rhandle.log(Level.WARNING, "An IOException occured while closing zones.csv...");
                }
                if (!zone_csv.delete()) {
                    rhandle.log(Level.WARNING, "Could not delete old zones file...");
                }
                else if (!tempFile.renameTo(zone_csv)) {
                    rhandle.log(Level.WARNING, "Could not rename zones tempfile...");
                }
            }
        }
    }

    /**
     * Deletes a polygon from polygons.csv
     */
    private final void deletePolygon() {
        synchronized (poly_lock) {
            BufferedReader br = null;
            PrintWriter pw = null;
            File tempFile = new File(poly_csv.getAbsolutePath() + ".tmp");
            try {
                br = new BufferedReader(new FileReader(poly_csv));
                pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().equals(polyarea.toString())) {
                        pw.println(line);
                        pw.flush();
                    }
                }
            }
            catch (FileNotFoundException ex) {
                rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
            }
            catch (IOException ex) {
                rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
                try {
                    if (br != null) {
                        br.close();
                    }
                }
                catch (IOException e) {
                    rhandle.log(Level.WARNING, "An IOException occured while closing zones.csv...");
                }
                if (!poly_csv.delete()) {
                    rhandle.log(Level.WARNING, "Could not delete old zones file...");
                }
                else if (!tempFile.renameTo(poly_csv)) {
                    rhandle.log(Level.WARNING, "Could not rename zones tempfile...");
                }
            }
        }
    }

    /**
     * Deletes a permission from permissions.csv
     */
    private final void deletePermission() {
        synchronized (perm_lock) {
            BufferedReader br = null;
            PrintWriter pw = null;
            File tempFile = new File(perm_csv.getAbsolutePath() + ".tmp");
            try {
                br = new BufferedReader(new FileReader(perm_csv));
                pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().equals(perm.toString())) {
                        pw.println(line);
                        pw.flush();
                    }
                }
            }
            catch (FileNotFoundException ex) {
                rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
            }
            catch (IOException ex) {
                rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
                try {
                    if (br != null) {
                        br.close();
                    }
                }
                catch (IOException e) {
                    rhandle.log(Level.WARNING, "An IOException occured while closing permissions.csv...");
                }
                if (!perm_csv.delete()) {
                    rhandle.log(Level.WARNING, "Could not delete old permissions file...");
                }
                else if (!tempFile.renameTo(perm_csv)) {
                    rhandle.log(Level.WARNING, "Could not rename permissions tempfile...");
                }
            }
        }
    }
    
    /**
     * Deletes an inventory from inventories.csv
     */
    private final void deleteInventory(){
        synchronized(inv_lock){
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
            
                BufferedReader br = null;
                PrintWriter pw = null;
                File tempFile = new File(perm_csv.getAbsolutePath() + ".tmp");
                try {
                    br = new BufferedReader(new FileReader(inv_csv));
                    pw = new PrintWriter(new FileWriter(tempFile));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().equals(inv)) {
                            pw.println(line);
                            pw.flush();
                        }
                    }
                }
                catch (FileNotFoundException ex) {
                    rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                    rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
                }
                catch (IOException ex) {
                    rhandle.log(Level.SEVERE, "An unhandled exception occured... (enable debuging for stacktraces)");
                    rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", ex);
                }
                finally {
                    if (pw != null) {
                        pw.close();
                    }
                    try {
                        if (br != null) {
                            br.close();
                        }
                    }
                    catch (IOException e) {
                        rhandle.log(Level.WARNING, "An IOException occured while closing inventories.csv...");
                    }
                    if (!perm_csv.delete()) {
                        rhandle.log(Level.WARNING, "Could not delete old inventories file...");
                    }
                    else if (!tempFile.renameTo(perm_csv)) {
                        rhandle.log(Level.WARNING, "Could not rename inventories tempfile...");
                    }
                }
            }
        }
    }
}
