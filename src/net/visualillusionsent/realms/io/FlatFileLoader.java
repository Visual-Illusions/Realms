package net.visualillusionsent.realms.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModItem;

/**
 * Realms FlatFile Data Handler
 * 
 * @author darkdiplomat
 */
public class FlatFileLoader implements RealmsData {
    private final String zoneFormat = "name,parent,greeting,farewell,world"; //Zone Format Header
    private final String permissionFormat = "player/group,type,zone,allowed,override"; //Permissons Format Header
    private final String polygonFormat = "zone,ceiling,floor,world,x1,y1,z1,x2,y2,z2,x3,y3,z3"; //Polygon Format Header
    private final String invFormat = "PlayerName,Item0,Item1,Item2,Item3,Item4,Item5,Item6,Item7,Item8,Item9," + //Inventory Format Header
                                     "Item10,Item11,Item12,Item13,Item14,Item15,Item16,Item17,Item18,Item19," +
                                     "Item20,Item21,Item22,Item23,Item24,Item25,Item26,Item27,Item28,Item29," +
                                     "Item30,Item31,Item32,Item33,Item34,Item35,Item36,Item37,Item38,Item39";
    private final File storage_dir = new File("plugins/config/Realms/"); //Storage Directory
    private final File zone_file = new File(storage_dir.getPath() + "/zones.csv"); //Zone File Name
    private final File poly_file = new File(storage_dir.getPath() + "/polygons.csv"); //Polygon File Name
    private final File perm_file = new File(storage_dir.getPath() + "/permissions.csv"); //Permission File Name
    private final File inv_file = new File(storage_dir.getPath() + "/inventories.csv"); //Inventory File Name

    /* Synchronize Locks */
    private Object polylock = new Object();
    private Object zonelock = new Object();
    private Object permlock = new Object();
    private Object invlock = new Object();

    private RHandle rhandle;

    /**
     * Class Constructor
     * 
     * @param RHandle
     */
    public FlatFileLoader(RHandle rhandle) {
        this.rhandle = rhandle;
        if (!storage_dir.exists()) {
            storage_dir.mkdirs();
        }

        PrintWriter pw = null;
        if (!zone_file.exists()) { //Check existence and make file if non-existent
            try {
                pw = new PrintWriter(new FileWriter(zone_file));
                pw.println(zoneFormat);
                pw.flush();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Failed to create to " + zone_file.getPath());
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
            }
        }
        if (!poly_file.exists()) { //Check existence and make file if non-existent
            try {
                pw = new PrintWriter(new FileWriter(poly_file));
                pw.println(polygonFormat);
                pw.flush();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Failed to create to " + poly_file.getPath());
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
            }
        }
        if (!perm_file.exists()) { //Check existence and make file if non-existent
            try {
                pw = new PrintWriter(new FileWriter(perm_file));
                pw.println(permissionFormat);
                pw.flush();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Failed to create to " + perm_file.getPath());
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
            }
        }
        if (!inv_file.exists()) { //Check existence and make file if non-existent
            try {
                pw = new PrintWriter(new FileWriter(inv_file));
                pw.println(invFormat);
                pw.flush();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Failed to create to " + inv_file.getPath());
            }
            finally {
                if (pw != null) {
                    pw.close();
                }
            }
        }
    }

    /**
     * Handles loading data
     */
    @Override
    public final boolean load() {
        if (!loadzones()) { //Load Zones
            return false;
        }
        if (!loadpolygons()) { //Load Polygons
            return false;
        }
        if (!loadpermissions()) { //Load Permissions
            return false;
        }
        loadinventories(); //Load Inventories
        rhandle.setLoaded();
        return true;
    }

    /**
     * Loads Zones
     */
    private final boolean loadzones() {
        synchronized (zonelock) {
            rhandle.log(Level.INFO, "Loading Zones...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(zone_file));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#") || line.equals("") || line.startsWith("?") || line.startsWith(zoneFormat)) {
                        continue;
                    }
                    String[] args = line.split(",");
                    if (args.length < 24) {
                        args = insertInto(args);
                    }
                    new Zone(rhandle, args);
                }
                in.close();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + zone_file.getPath() + " (Are you sure you formatted it correctly?)", IOE);
                return false;
            }
        }
        return true;
    }

    /**
     * Loads Polygons
     */
    private final boolean loadpolygons() {
        synchronized (polylock) {
            rhandle.log(Level.INFO, "Loading Polygons...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(poly_file));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#") || line.equals("") || line.startsWith("?") || line.contains(polygonFormat)) {
                        continue;
                    }
                    try {
                        String[] args = line.split(",");
                        if (args.length < 5) {
                            continue;
                        }
                        if (!divisableBy3(args.length)) {
                            args = removeIndex(args, 3, ",").split(",");
                        }
                        new PolygonArea(rhandle, ZoneLists.getZoneByName(args[0]), args);
                    }
                    catch (ZoneNotFoundException ZNFE) {
                        rhandle.log(Level.WARNING, "Zone was Not Found: " + line.split(",")[0]);
                    }
                }
                in.close();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + poly_file.getPath() + " (Are you sure you formatted it correctly?)", IOE);
                return false;
            }
        }
        return true;
    }

    /**
     * Loads Permissions
     */
    private final boolean loadpermissions() {
        synchronized (permlock) {
            rhandle.log(Level.INFO, "Loading Permissions...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(perm_file));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#") || line.equals("") || line.startsWith("?") || line.contains(permissionFormat)) {
                        continue;
                    }
                    String[] args = line.split(",");
                    try {
                        if (args[2].equals("everywhere")) {
                            args[2] = "EVERYWHERE-" + rhandle.getServer().getDefaultWorldName().toUpperCase() + "-DIM" + "0";
                        }
                        Zone zone = ZoneLists.getZoneByName(args[2]);
                        zone.setPermission(new Permission(args));
                    }
                    catch (ZoneNotFoundException ZNFE) {
                        rhandle.log(Level.WARNING, "Zone was Not Found: " + args[2]);
                    }
                }
                in.close();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + perm_file.getPath() + " (Are you sure you formatted it correctly?)", IOE);
                return false;
            }
        }
        return true;
    }

    /**
     * Loads Inventories
     */
    private final void loadinventories() {
        synchronized (invlock) {
            rhandle.log(Level.INFO, "Loading Stored Inventories...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(inv_file));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("#") || line.equals("") || line.startsWith("?") || line.contains(invFormat)) {
                        continue;
                    }
                    String[] inLine = line.split(",");
                    String name = inLine[0];
                    ICModItem[] items = new ICModItem[40];
                    for (int i = 1; i < inLine.length && i < 40; i++) {
                        if (inLine[i] != null || !inLine[i].equals("null")) {
                            continue;
                        }
                        String[] it = inLine[i].split(":");
                        items[i] = rhandle.getServer().makeItem(Integer.valueOf(it[0]), Integer.valueOf(it[1]), Integer.valueOf(it[2]), Integer.valueOf(it[3]));
                    }
                    rhandle.storeInventory(rhandle.getServer().getPlayer(name), items);
                }
                in.close();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + inv_file.getPath() + " (Are you sure you formatted it correctly?)", IOE);
            }
        }
    }

    private final String removeIndex(String[] args, int remove, String spacer) {
        StringBuilder build = new StringBuilder();
        for (int index = 0; index < args.length; index++) {
            if (index == remove) {
                index++;
            }
            build.append(args[index]);
            build.append(spacer);
        }
        return build.toString();
    }

    private final boolean divisableBy3(int number) {
        double result = number / 3;
        double check = Math.floor(number / 3);
        return result == check;
    }

    private final String[] insertInto(String[] args) {
        String[] newArgs = new String[args.length + 2];
        int push = 2;
        newArgs[0] = args[0];
        newArgs[1] = rhandle.getServer().getDefaultWorldName();
        newArgs[2] = "0";
        if (args[1].equals("everywhere")) {
            args[1] = "EVERYWHERE-" + rhandle.getServer().getDefaultWorldName().toUpperCase() + "-DIM" + "0";
        }
        for (int index = 1; index < args.length; index++) {
            newArgs[index + push] = args[index];
        }
        return newArgs;
    }

    /**
     * Handles reloading all data
     */
    @Override
    public final boolean reloadAll() {
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

    /**
     * Handles reloading all zones
     */
    @Override
    public final boolean reloadZones() {
        return loadzones();
    }

    /**
     * Handles reloading all Permissions
     */
    @Override
    public final boolean reloadPerms() {
        return loadpermissions();
    }

    /**
     * Handles reloading all Polygons
     */
    @Override
    public final boolean reloadPolys() {
        return loadpolygons();
    }
}
