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

public class FlatFileSaver extends Thread {
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

    public FlatFileSaver(RHandle rhandle, Object obj) {
        this.rhandle = rhandle;
        if (obj instanceof Zone) {
            zone = (Zone) obj;
            polyarea = null;
            perm = null;
        }
        else if (obj instanceof PolygonArea) {
            polyarea = (PolygonArea) obj;
            zone = null;
            perm = null;
        }
        else if (obj instanceof Permission) {
            perm = (Permission) obj;
            zone = null;
            polyarea = null;
        }
        else {
            zone = null;
            polyarea = null;
            perm = null;
        }
        this.setName("FlatFileSaver-Thread");
    }

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
    }

    /**
     * Saves zone to flatfile zones.csv file.
     */
    private final void saveZone() {
        synchronized (zone_lock) {
            BufferedReader br = null;
            PrintWriter pw = null;
            File tempFile = new File(zone_csv.getAbsolutePath() + ".tmp");
            try {
                br = new BufferedReader(new FileReader(zone_csv));
                pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    if (!line.contains(zone.getName())) {
                        pw.println(line);
                        pw.flush();
                    }
                    else {
                        pw.println(zone.toString());
                        pw.flush();
                        found = true;
                    }
                }
                if (!found) {
                    pw.println(zone.toString());
                    pw.flush();
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
                    rhandle.log(Level.SEVERE, "Could not delete old zones file...");
                }
                else if (!tempFile.renameTo(zone_csv)) {
                    rhandle.log(Level.SEVERE, "Could not rename zones tempfile...");
                }
            }
        }
    }

    /**
     * Saves polygon area to flatfile polygons.csv file.
     */
    private final void savePolygon() {
        synchronized (poly_lock) {
            BufferedReader br = null;
            PrintWriter pw = null;
            File tempFile = new File(poly_csv.getAbsolutePath() + ".tmp");
            try {
                br = new BufferedReader(new FileReader(poly_csv));
                pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    if (!line.contains(polyarea.getZone().getName())) {
                        pw.println(line);
                        pw.flush();
                    }
                    else {
                        pw.println(polyarea.toString());
                        pw.flush();
                        found = true;
                    }
                }
                if (!found) {
                    pw.println(polyarea.toString());
                    pw.flush();
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
                    rhandle.log(Level.WARNING, "An IOException occured while closing polygons.csv...");
                }
                if (!poly_csv.delete()) {
                    rhandle.log(Level.WARNING, "Could not delete old polygons file...");
                }
                else if (!tempFile.renameTo(poly_csv)) {
                    rhandle.log(Level.WARNING, "Could not rename polygons tempfile...");
                }
            }
        }
    }

    /**
     * Saves polygon area to flatfile permissions.csv file.
     */
    private final void savePermission() {
        synchronized (perm_lock) {
            BufferedReader br = null;
            PrintWriter pw = null;
            File tempFile = new File(perm_csv.getAbsolutePath() + ".tmp");
            try {
                br = new BufferedReader(new FileReader(perm_csv));
                pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    if (!line.contains(perm.getZoneName()) && !line.contains(perm.getOwnerName()) && !line.contains(perm.getType().toString())) {
                        pw.println(line);
                        pw.flush();
                    }
                    else {
                        pw.println(perm.toString());
                        pw.flush();
                        found = true;
                    }
                }
                if (!found) {
                    pw.println(perm.toString());
                    pw.flush();
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
                    rhandle.log(Level.WARNING, "An IOException occured while closing polygons.csv...");
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
}
