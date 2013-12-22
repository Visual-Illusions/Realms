/*
 * This file is part of Realms.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import net.visualillusionsent.realms.lang.DataSourceType;
import net.visualillusionsent.realms.lang.InitializationError;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Server;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.data.DataSourceHandler;
import net.visualillusionsent.realms.data.OutputAction;
import net.visualillusionsent.realms.data.RealmsProps;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.tasks.AnimalRemover;
import net.visualillusionsent.realms.tasks.Healer;
import net.visualillusionsent.realms.tasks.MobRemover;
import net.visualillusionsent.realms.tasks.RestrictionDamager;
import net.visualillusionsent.realms.tasks.SynchronizedTask;
import net.visualillusionsent.realms.zones.Wand;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygon.Point;
import net.visualillusionsent.realms.zones.polygon.PolygonArea;
import net.visualillusionsent.utils.ProgramStatus;
import net.visualillusionsent.utils.VersionChecker;

/**
 * Realms base class
 * 
 * @author Jason (darkdiplomat)
 */
public final class RealmsBase {

    private static RealmsBase $;
    private final Mod_Server server;
    private final String name = "Realms";
    private final String version_check_URL = "http://visualillusionsent.net/minecraft/plugins/";
    private final String jar_Path = genJarPath();
    private final DataSourceHandler source_handler;
    private final RealmsProps props;
    private SynchronizedTask mobdes, animaldes, healer, restrictdam;
    private final HashMap<Mod_User, Wand> wands = new HashMap<Mod_User, Wand>();
    private final HashMap<String, Mod_Item[]> inventories = new HashMap<String, Mod_Item[]>();
    private String version;
    private String build;
    private ProgramStatus status;
    private static boolean loaded;

    public RealmsBase(Mod_Server server) {
        if ($ == null) {
            $ = this;
            this.server = server;
            RealmsLogMan.info("Realms v".concat(getVersion()).concat(status != ProgramStatus.STABLE ? " " + status.toString() : "").concat(" initializing..."));
            props = new RealmsProps();
            if (!props.initialize()) {
                throw new InitializationError("Properties failed to initialize...");
            }
            source_handler = new DataSourceHandler(DataSourceType.valueOf(props.getStringVal("datasource").toUpperCase()));
            initializeThreads();
            RealmsLogMan.info("Realms v".concat(getVersion()).concat(" initialized."));
            loaded = true;
        }
        else {
            throw new IllegalStateException("RealmsBase already initialized");
        }
    }

    private final String genJarPath() { // For when the jar isn't Realms.jar
        try {
            CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
            return codeSource.getLocation().toURI().getPath();
        }
        catch (URISyntaxException ex) {}
        return "plugins/Realms.jar";
    }

    private final void initializeThreads() {
        if (!props.getBooleanVal("sanctuary.mobs") && props.getLongVal("sanctuary.timeout") > 0) { // If allowing all mobs into Sanctuary area don't bother scheduling
            mobdes = server.addTaskToServer(new MobRemover(this), props.getLongVal("sanctuary.timeout"));
        }
        if (props.getLongVal("animals.timeout") > 0) {
            animaldes = server.addTaskToServer(new AnimalRemover(this), props.getLongVal("animals.timeout")); // Animals Runnable
        }
        if (props.getLongVal("healing.timeout") > 0) {
            healer = server.addTaskToServer(new Healer(this), props.getLongVal("healing.timeout")); // Healing Runnable
        }
        if (props.getLongVal("restrict.timeout") > 0) {
            restrictdam = server.addTaskToServer(new RestrictionDamager(this), props.getLongVal("restrict.timeout")); // Restricted Zone Damager Task
        }
    }

    /**
     * Terminates the threadhandler and closes the log file
     */
    public final void terminate() {
        loaded = false;
        server.removeTask($.mobdes);
        server.removeTask($.animaldes);
        server.removeTask($.healer);
        server.removeTask($.restrictdam);
        source_handler.killOutput();
        ZoneLists.clearOut();
        for (Wand wand : wands.values()) {
            wand.softReset();
        }
        wands.clear();
        $ = null;
        RealmsLogMan.killLogger();
    }

    public final static boolean isLoaded() {
        return loaded;
    }

    public final static DataSourceHandler getDataSourceHandler() {
        return $.source_handler;
    }

    public final static Mod_Server getServer() {
        return $.server;
    }

    public final static RealmsProps getProperties() {
        return $.props;
    }

    public final static Wand getPlayerWand(Mod_User user) {
        if ($.wands.containsKey(user)) {
            return $.wands.get(user);
        }
        Wand wand = new Wand(user);
        $.wands.put(user, wand);
        return wand;
    }

    public final static void removePlayerWand(Mod_User user) {
        synchronized ($.wands) {
            if ($.wands.containsKey(user)) {
                $.wands.get(user).softReset();
                $.wands.remove(user);
            }
        }
    }

    public final static void playerMessage(Mod_User user) {
        List<Zone> oldZoneList = ZoneLists.getplayerZones(user);
        Zone everywhere = ZoneLists.getEverywhere(user);
        List<Zone> newZoneList = ZoneLists.getZonesPlayerIsIn(everywhere, user);
        if (oldZoneList.isEmpty()) {
            ZoneLists.addplayerzones(user, newZoneList);
            return;
        }
        else if (oldZoneList.hashCode() != newZoneList.hashCode()) {
            for (Zone zone : oldZoneList) {
                if (!newZoneList.contains(zone)) {
                    zone.farewell(user);
                }
            }
            for (Zone zone : newZoneList) {
                if (!oldZoneList.contains(zone)) {
                    zone.greet(user);
                }
            }
            ZoneLists.addplayerzones(user, newZoneList);
        }
    }

    public final static void handleInventory(Mod_User user, boolean store) {
        if (store) {
            if (!$.inventories.containsKey(user.getName())) {
                Mod_Item[] items = user.getInventoryContents();
                $.inventories.put(user.getName(), items);
                $.source_handler.addToQueue(OutputAction.SAVE_INVENTORY, user, items);
                user.clearInventoryContents();
            }
        }
        else if ($.inventories.containsKey(user.getName())) {
            user.setInventoryContents($.inventories.get(user.getName()));
            $.inventories.remove(user.getName());
            $.source_handler.addToQueue(OutputAction.DELETE_INVENTORY, user);
        }
    }

    public final static void storeInventory(String name, Mod_Item[] items) {
        if (!$.inventories.containsKey(name)) {
            $.inventories.put(name, items);
        }
    }

    public final static String getVersion() {
        if ($.version == null) {
            $.generateVersion();
        }
        return $.version.concat(".").concat($.build);
    }

    private final String getRawVersion() {
        if ($.version == null) {
            $.generateVersion();
        }
        return $.version;
    }

    public final static boolean isBeta() {
        return $.status == ProgramStatus.BETA;
    }

    public final static boolean isReleaseCandidate() {
        return $.status == ProgramStatus.RELEASE_CANDIDATE;
    }

    private void generateVersion() {
        try {
            Manifest manifest = getManifest();
            Attributes mainAttribs = manifest.getMainAttributes();
            version = mainAttribs.getValue("Version");
            build = mainAttribs.getValue("Build");
            try {
                status = ProgramStatus.valueOf(mainAttribs.getValue("ProgramStatus"));
            }
            catch (IllegalArgumentException iaex) {
                status = ProgramStatus.UNKNOWN;
            }
        }
        catch (Exception e) {
            RealmsLogMan.warning(e.getMessage());
        }
        if (version == null) {
            version = "UNKNOWN";
        }
        else if (build == null) {
            build = "UNKNOWN";
        }
    }

    private final Manifest getManifest() throws Exception {
        Manifest toRet = null;
        Exception ex = null;
        JarFile jar = null;
        try {
            jar = new JarFile(jar_Path);
            toRet = jar.getManifest();
        }
        catch (Exception e) {
            ex = e;
        }
        finally {
            if (jar != null) {
                try {
                    jar.close();
                }
                catch (IOException e) {}
            }
            if (ex != null) {
                throw ex;
            }
        }
        return toRet;
    }

    public final static String[] commandAdjustment(String[] args, int adjust) {
        String[] newArgs = new String[0];
        if (args.length > adjust) {
            newArgs = new String[args.length - adjust];
            for (int index = 0; index < args.length; index++) {
                if (index <= (adjust - 1)) {
                    continue;
                }
                newArgs[index - adjust] = args[index];
            }
        }
        return newArgs;
    }

    public final static Point throwBack(Zone zone, Point oPoint) {
        PolygonArea area = zone.getPolygon();
        Point temp = oPoint.clone();
        if (area != null) {
            temp = area.getClosestPoint(temp);
            temp.x += 1;
            if (area.contains(temp)) {
                temp.x -= 2;
                if (area.contains(temp)) {
                    temp.x += 1;
                    temp.z += 1;
                    if (area.contains(temp)) {
                        temp.z -= 2;
                    }
                }
            }
            temp.y = $.server.getHighestY(temp.x, temp.z, zone.getWorld(), zone.getDimension());
        }
        return temp;
    }

    public final static String getJarPath() {
        return $.jar_Path;
    }
}
