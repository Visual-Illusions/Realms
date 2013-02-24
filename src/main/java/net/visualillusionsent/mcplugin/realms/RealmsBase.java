/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 */
package net.visualillusionsent.mcplugin.realms;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.visualillusionsent.mcmod.interfaces.Mod_Item;
import net.visualillusionsent.mcmod.interfaces.Mod_Server;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.data.DataSourceException;
import net.visualillusionsent.mcplugin.realms.data.DataSourceHandler;
import net.visualillusionsent.mcplugin.realms.data.DataSourceType;
import net.visualillusionsent.mcplugin.realms.data.OutputAction;
import net.visualillusionsent.mcplugin.realms.data.RealmsProps;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.runnable.AnimalRemover;
import net.visualillusionsent.mcplugin.realms.runnable.Healer;
import net.visualillusionsent.mcplugin.realms.runnable.MobRemover;
import net.visualillusionsent.mcplugin.realms.runnable.RestrictionDamager;
import net.visualillusionsent.mcplugin.realms.zones.Wand;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.polygon.Point;
import net.visualillusionsent.mcplugin.realms.zones.polygon.PolygonArea;
import net.visualillusionsent.utils.TaskManager;
import net.visualillusionsent.utils.UpdateException;
import net.visualillusionsent.utils.Updater;
import net.visualillusionsent.utils.VersionChecker;
import net.visualillusionsent.utils.VersionChecker.ProgramStatus;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class RealmsBase{

    private static RealmsBase $;
    private final Mod_Server server;
    private final String name = "Realms";
    private final String version_check_URL = "http://visualillusionsent.net/minecraft/plugins/";
    private final String download_URL = "http://dl.visualillusionsent.net/?download=Realms.jar";
    private final String jar_Path = "plugins/Realms.jar";
    private final DataSourceHandler source_handler;
    private final RealmsProps props;
    private final VersionChecker vc;
    private final Updater updater;
    private final MobRemover mobdes = new MobRemover(this);
    private final AnimalRemover animaldes = new AnimalRemover(this);
    private final Healer healer = new Healer(this);
    private final RestrictionDamager restrictdam = new RestrictionDamager(this);
    private final HashMap<Mod_User, Wand> wands = new HashMap<Mod_User, Wand>();
    private final HashMap<String, Mod_Item[]> inventories = new HashMap<String, Mod_Item[]>();
    private String version;
    private String build;
    private boolean beta;
    private boolean rc;
    private static boolean loaded;

    public RealmsBase(Mod_Server server){
        if($ == null){
            $ = this;
            this.server = server;
            RealmsLogMan.info("Realms v".concat(getVersion()).concat(isBeta() ? " BETA" : isReleaseCandidate() ? " RC" : "").concat(" initializing..."));
            if(beta){
                RealmsLogMan.warning("Realms has declared itself as a 'BETA' build. Production use is not advised!");
            }
            else if(rc){
                RealmsLogMan.info("Realms has declared itself as a 'Release Candidate' build. Expect some bugs.");
            }
            props = new RealmsProps();
            if(!props.initialize()){
                throw new RealmsInitializeException();
            }
            try{
                source_handler = new DataSourceHandler(DataSourceType.valueOf(props.getStringVal("datasource").toUpperCase()));
            }
            catch(DataSourceException e){
                throw new RealmsInitializeException(e);
            }
            vc = new VersionChecker(name, getRawVersion(), build, version_check_URL, (beta ? ProgramStatus.BETA : rc ? ProgramStatus.RELEASE_CANADATE : ProgramStatus.STABLE), props.getBooleanVal("check.unstable"));
            Boolean islatest = vc.isLatest();
            if(islatest == null){
                RealmsLogMan.warning("VersionCheckerError: " + vc.getErrorMessage());
            }
            else if(!vc.isLatest()){
                RealmsLogMan.warning(vc.getUpdateAvailibleMessage());
                RealmsLogMan.warning("You can view update info @ http://wiki.visualillusionsent.net/Realms#ChangeLog");
            }
            updater = new Updater(download_URL, jar_Path, name);
            initializeThreads();
            RealmsLogMan.info("Realms v".concat(getVersion()).concat(" initialized."));
            loaded = true;
        }
        else{
            throw new IllegalStateException();
        }
    }

    private final void initializeThreads(){
        if(!props.getBooleanVal("sanctuary.mobs") && props.getLongVal("sanctuary.timeout") > 0){ //If allowing all mobs into Sanctuary area don't bother scheduling
            TaskManager.scheduleContinuedTaskInSeconds(mobdes, props.getLongVal("sanctuary.timeout"), props.getLongVal("sanctuary.timeout")); //Sanctuary Runnable
        }
        if(props.getLongVal("animals.timeout") > 0){
            TaskManager.scheduleContinuedTaskInSeconds(animaldes, props.getLongVal("animals.timeout"), props.getLongVal("animals.timeout")); //Animals Runnable
        }
        if(props.getLongVal("healing.timeout") > 0){
            TaskManager.scheduleContinuedTaskInSeconds(healer, props.getLongVal("healing.timeout"), props.getLongVal("healing.timeout")); //Healing Runnable
        }
        if(props.getLongVal("restrict.timeout") > 0){
            TaskManager.scheduleContinuedTaskInSeconds(restrictdam, props.getLongVal("restrict.timeout"), props.getLongVal("restrict.timeout")); //Restricted Zone Damager Task
        }
    }

    /**
     * Terminates the threadhandler and closes the log file
     */
    public final void terminate(){
        TaskManager.removeTask($.mobdes);
        TaskManager.removeTask($.animaldes);
        TaskManager.removeTask($.healer);
        TaskManager.removeTask($.restrictdam);
        TaskManager.terminateThreadPool();
        source_handler.killOutput();
        ZoneLists.clearOut();
        for(Wand wand : wands.values()){
            wand.softReset();
        }
        wands.clear();
        RealmsLogMan.killLogger();
    }

    public final static boolean isLoaded(){
        return loaded;
    }

    public final static DataSourceHandler getDataSourceHandler(){
        return $.source_handler;
    }

    public final static Mod_Server getServer(){
        return $.server;
    }

    public final static RealmsProps getProperties(){
        return $.props;
    }

    public final static Wand getPlayerWand(Mod_User user){
        if($.wands.containsKey(user)){
            return $.wands.get(user);
        }
        Wand wand = new Wand(user);
        $.wands.put(user, wand);
        return wand;
    }

    public final static void removePlayerWand(Mod_User user){
        synchronized($.wands){
            if($.wands.containsKey(user)){
                $.wands.get(user).softReset();
                $.wands.remove(user);
            }
        }
    }

    public final static void playerMessage(Mod_User user){
        List<Zone> oldZoneList = ZoneLists.getplayerZones(user);
        Zone everywhere = ZoneLists.getEverywhere(user);
        List<Zone> newZoneList = ZoneLists.getZonesPlayerIsIn(everywhere, user);
        if(oldZoneList.isEmpty()){
            ZoneLists.addplayerzones(user, newZoneList);
            return;
        }
        else if(oldZoneList.hashCode() != newZoneList.hashCode()){
            for(Zone zone : oldZoneList){
                if(!newZoneList.contains(zone)){
                    zone.farewell(user);
                }
            }
            for(Zone zone : newZoneList){
                if(!oldZoneList.contains(zone)){
                    zone.greet(user);
                }
            }
            ZoneLists.addplayerzones(user, newZoneList);
        }
    }

    public final static void handleInventory(Mod_User user, boolean store){
        if(store){
            if(!$.inventories.containsKey(user.getName())){
                Mod_Item[] items = user.getInventoryContents();
                $.inventories.put(user.getName(), items);
                $.source_handler.addToQueue(OutputAction.SAVE_INVENTORY, user, items);
                user.clearInventoryContents();
            }
        }
        else if($.inventories.containsKey(user.getName())){
            user.setInventoryContents($.inventories.get(user.getName()));
            $.inventories.remove(user.getName());
            $.source_handler.addToQueue(OutputAction.DELETE_INVENTORY, user);
        }
    }

    public final static void storeInventory(String name, Mod_Item[] items){
        if(!$.inventories.containsKey(name)){
            $.inventories.put(name, items);
        }
    }

    public final static String update(){
        try{
            $.updater.performUpdate();
        }
        catch(UpdateException ue){
            return ue.getMessage();
        }
        return "Update Successful! Please verify that the libraries used by Realms are also up to date!";
    }

    public final static boolean isLatest(){
        return $.vc.isLatest();
    }

    public final static String getCurrent(){
        return $.vc.getCurrentVersion();
    }

    public final static String getVersion(){
        if($.version == null){
            $.generateVersion();
        }
        return $.version.concat(".").concat($.build);
    }

    private final String getRawVersion(){
        if($.version == null){
            $.generateVersion();
        }
        return $.version;
    }

    public final static boolean isBeta(){
        return $.beta;
    }

    public final static boolean isReleaseCandidate(){
        return $.rc;
    }

    private void generateVersion(){
        try{
            Manifest manifest = getManifest();
            Attributes mainAttribs = manifest.getMainAttributes();
            version = mainAttribs.getValue("Version");
            build = mainAttribs.getValue("Build");
            beta = Boolean.parseBoolean(mainAttribs.getValue("Beta"));
            rc = Boolean.parseBoolean(mainAttribs.getValue("ReleaseCanidate"));
        }
        catch(Exception e){
            RealmsLogMan.warning(e.getMessage());
        }
        if(version == null){
            version = "UNKNOWN";
        }
        else if(build == null){
            build = "UNKNOWN";
        }
    }

    private final Manifest getManifest() throws Exception{
        Manifest toRet = null;
        Exception ex = null;
        JarFile jar = null;
        try{
            jar = new JarFile(jar_Path);
            toRet = jar.getManifest();
        }
        catch(Exception e){
            ex = e;
        }
        finally{
            if(jar != null){
                try{
                    jar.close();
                }
                catch(IOException e){}
            }
            if(ex != null){
                throw ex;
            }
        }
        return toRet;
    }

    public final static String[] commandAdjustment(String[] args, int adjust){
        String[] newArgs = new String[0];
        if(args.length > adjust){
            newArgs = new String[args.length - adjust];
            for(int index = 0; index < args.length; index++){
                if(index <= (adjust - 1)){
                    continue;
                }
                newArgs[index - adjust] = args[index];
            }
        }
        return newArgs;
    }

    public final static Point throwBack(Zone zone, Point oPoint){
        PolygonArea area = zone.getPolygon();
        Point temp = oPoint.clone();
        if(area != null){
            temp = area.getClosestPoint(temp);
            temp.x += 1;
            if(area.contains(temp)){
                temp.x -= 2;
                if(area.contains(temp)){
                    temp.x += 1;
                    temp.z += 1;
                    if(area.contains(temp)){
                        temp.z -= 2;
                    }
                }
            }
            temp.y = $.server.getHighestY(temp.x, temp.z, zone.getWorld(), zone.getDimension());
        }
        return temp;
    }
}
