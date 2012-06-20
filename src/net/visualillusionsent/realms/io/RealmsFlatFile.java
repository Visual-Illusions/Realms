package net.visualillusionsent.realms.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.viutils.ICModItem;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms FlatFile Data Handler
 * 
 * @author darkdiplomat
 */
public class RealmsFlatFile extends RealmsData {
    private final String zoneFormat = "name,parent,greeting,farewell,world"; //Zone Format Header
    private final String permissionFormat = "player/group,type,zone,allowed,override"; //Permissons Format Header
    private final String polygonFormat = "zone,ceiling,floor,world,x1,y1,z1,x2,y2,z2,x3,y3,z3"; //Polygon Format Header
    private final String invFormat = "PlayerName,Item0,Item1,Item2,Item3,Item4,Item5,Item6,Item7,Item8,Item9," + //Inventory Format Header
                                     "Item10,Item11,Item12,Item13,Item14,Item15,Item16,Item17,Item18,Item19," +
                                     "Item20,Item21,Item22,Item23,Item24,Item25,Item26,Item27,Item28,Item29," +
                                     "Item30,Item31,Item32,Item33,Item34,Item35,Item36,Item37,Item38,Item39";
    private final String StoDir = "plugins/config/Realms/"; //Storage Directory
    private final String PolyFile = "polygons.csv"; //Polygon File Name
    private final String ZoneFile = "zones.csv"; //Zone File Name
    private final String PermFile = "permissions.csv"; //Permission File Name
    private final String InvFile = "inventories.csv"; //Inventory File Name
    
    
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
    
    private File Storage; //Storage Directory
    private File Polygons; //Polygons File
    private File Zones; //Zones File
    private File Permissions; //Permissoins File
    private File Inventory; //Inventories File
    
    
    private RHandle rhandle;
    
    /**
     * Class Constructor
     * 
     * @param realm
     */
    public RealmsFlatFile(RHandle rhandle){
        this.rhandle = rhandle;
        Storage = new File(StoDir);
        if(!Storage.exists()){
            Storage.mkdirs();
        }
        Zones = new File(StoDir+ZoneFile); //Initialize Zones File
        if(!Zones.exists()){ //Check existence and make file if non-existent
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Zones, false));
                bw.write(zoneFormat);
                bw.newLine();
                bw.flush();
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to create to "+ZoneFile);
            }
        }
        Polygons = new File(StoDir+PolyFile); //Initialize Polygons File
        if(!Polygons.exists()){ //Check existence and make file if non-existent
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Polygons, false));
                bw.write(polygonFormat);
                bw.newLine();
                bw.flush();
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to create to "+PolyFile);
            }
        }
        Permissions = new File(StoDir+PermFile); //Initialize Permissions File
        if(!Permissions.exists()){ //Check existence and make file if non-existent
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Permissions, false));
                bw.write(permissionFormat);
                bw.newLine();
                bw.flush();
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to create to "+PermFile);
            }
        }
        Inventory = new File(StoDir+InvFile); //Initialize Inventories File
        if(!Inventory.exists()){ //Check existence and make file if non-existent
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Inventory, false));
                bw.write(invFormat);
                bw.newLine();
                bw.flush();
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to create to "+InvFile);
            }
        }
        
        loadzones(); //Load Zones
        loadpolygons(); //Load Polygons
        loadpermissions(); //Load Permissions
        loadinventories(); //Load Inventories
    }

    /**
     * Loads Zones
     */
    private boolean loadzones() {
        synchronized(zonelock){
            rhandle.log(Level.INFO, "Loading Zones...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(StoDir+ZoneFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if(line.startsWith("#") || line.equals("") || line.startsWith("?") || line.startsWith(zoneFormat)){
                        continue;
                    }
                    String[] args = line.split(",");
                    if(args.length < 24){
                        args = insertInto(args);
                    }
                    new Zone(rhandle, args);
                }
                in.close();
            }
            catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + StoDir+ZoneFile + " (Are you sure you formatted it correctly?)", IOE);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Loads Polygons
     */
    private boolean loadpolygons() {
        synchronized(polylock){
            rhandle.log(Level.INFO, "Loading Polygons...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(StoDir+PolyFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if(line.startsWith("#") || line.equals("") || line.startsWith("?") || line.contains(polygonFormat)){
                        continue;
                    }
                    try { 
                        String[] args = line.split(",");
                        if(args.length < 5){
                            continue;
                        }
                        if(!divisableBy3(args.length)){
                            args = removeIndex(args, 3, ",").split(",");
                        }
                        new PolygonArea(rhandle, ZoneLists.getZoneByName(args[0]), args); 
                    } catch (ZoneNotFoundException ZNFE) { 
                        rhandle.log(Level.WARNING, "Zone was Not Found: "+line.split(",")[0]);
                    }
                }
                in.close();
            } catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + StoDir+PolyFile + " (Are you sure you formatted it correctly?)", IOE);
                return false;
            }
        }
        return true;
    }

    /**
     * Loads Permissions
     */
    private boolean loadpermissions() {
        synchronized(permlock){
            rhandle.log(Level.INFO, "Loading Permissions...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(StoDir+PermFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if(line.startsWith("#") || line.equals("") || line.startsWith("?") || line.contains(permissionFormat)){
                        continue;
                    }
                    String[] args = line.split(",");
                    try { 
                        if(args[2].equals("everywhere")){
                            args[2] = "EVERYWHERE-"+rhandle.getServer().getDefaultWorldName().toUpperCase()+"-DIM"+"0";
                        }
                        Zone zone = ZoneLists.getZoneByName(args[2]);
                        zone.setPermission(new Permission(args));
                    } catch (ZoneNotFoundException ZNFE) { 
                        rhandle.log(Level.WARNING, "Zone was Not Found: "+args[2]);
                    }
                }
                in.close();
            } catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + StoDir+PermFile + " (Are you sure you formatted it correctly?)", IOE);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Loads Inventories
     */
    private void loadinventories() {
        synchronized(invlock){
            rhandle.log(Level.INFO, "Loading Stored Inventories...");
            try {
                BufferedReader in = new BufferedReader(new FileReader(StoDir+InvFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if(line.startsWith("#") || line.equals("") || line.startsWith("?") || line.contains(invFormat)){
                        continue;
                    }
                    String[] inLine = line.split(",");
                    String name = inLine[0];
                    ICModItem[] items = new ICModItem[40];
                    for(int i = 1; i < inLine.length && i < 40; i++){
                        if(inLine[i] != null || !inLine[i].equals("null")){
                            continue;
                        }
                        String[] it = inLine[i].split(":");
                        items[i] = rhandle.getServer().makeItem(Integer.valueOf(it[0]), Integer.valueOf(it[1]), Integer.valueOf(it[2]), Integer.valueOf(it[3]));
                    }
                    rhandle.storeInventory(rhandle.getServer().getPlayer(name), items);
                }
                in.close();
            } catch (IOException IOE) {
                rhandle.log(Level.SEVERE, "Exception while reading " + StoDir+InvFile + " (Are you sure you formatted it correctly?)", IOE);
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
        donedumpingpoly = false;
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
            dumpinvdata();
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
     * Saves Polygons
     */
    private void dumppolydata(){
        synchronized(polylock){
            int i = 0;
            List<Zone> zones = ZoneLists.getZones();
            String[] lines = new String[zones.size()];
            for(Zone zone : zones){
                if(zone.getPolygon() != null){
                    lines[i] = zone.getPolygon().toString();
                    i++;
                }
            }
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Polygons , false));
                bw.write(polygonFormat);
                bw.newLine();
                for(String writeline : lines) {
                    if(writeline != null){
                        bw.write(writeline);
                        bw.newLine();
                    }
                }
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to write to "+PolyFile);
            }
        }
        donedumpingpoly = true;
    }
    
    /**
     * Saves Zones
     */
    private void dumpzonedata(){
        synchronized(zonelock){
            int i = 0;
            List<Zone> zones = ZoneLists.getZones();
            String[] lines = new String[zones.size()];
            for(Zone zone : zones){
                lines[i] = zone.toString();
                i++;
            }
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Zones, false));
                bw.write(zoneFormat);
                bw.newLine();
                for(String writeline : lines) {
                    bw.write(writeline);
                    bw.newLine();
                }
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to write to "+ZoneFile);
            }
        }
        donedumpingzone = true;
    }
    
    /**
     * Saves Permissions
     */
    private void dumppermdata(){
        synchronized(permlock){
            int i = 0;
            List<Zone> zones = ZoneLists.getZones();
            List<Permission> perms = new ArrayList<Permission>();
            for(Zone zone : zones){
                perms.addAll(zone.getPerms());
            }
            String[] lines = new String[perms.size()];
            for(Permission perm : perms){
                lines[i] = perm.toString();
                i++;
            }
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Permissions, false));
                bw.write(permissionFormat);
                bw.newLine();
                for(String writeline : lines) {
                    bw.write(writeline);
                    bw.newLine();
                }
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to write to "+PermFile);
            }
        }
        donedumpingperm = true;
    }
    
    /**
     * Saves Inventories
     */
    private void dumpinvdata(){
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
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(Inventory, false));
                bw.write(invFormat);
                bw.newLine();
                for(String writeline : StoredInv) {
                    bw.write(writeline);
                    bw.newLine();
                }
                bw.close();
            }catch(IOException IOE){
                rhandle.log(Level.SEVERE, "Failed to write to "+InvFile);
            }
        }
        donedumpinginv = true;
    }
    
    private String removeIndex(String[] args, int remove, String spacer){
        StringBuilder build = new StringBuilder();
        for(int index = 0; index < args.length; index++){
            if(index == remove){ 
                index++;
            }
            build.append(args[index]);
            build.append(spacer);
        }
        return build.toString();
    }
    
    private boolean divisableBy3(int number){
        double result = (number / 3);
        double check = Math.floor(number / 3);
        return result == check;
    }
    
    private String[] insertInto(String[] args){
        String[] newArgs = new String[args.length+2];
        int push = 2;
        newArgs[0] = args[0];
        newArgs[1] = rhandle.getServer().getDefaultWorldName();
        newArgs[2] = "0";
        if(args[1].equals("everywhere")){
            args[1] = "EVERYWHERE-"+rhandle.getServer().getDefaultWorldName().toUpperCase()+"-DIM"+"0";
        }
        for(int index = 1; index < args.length; index++){
            newArgs[index+push] = (args[index]);
        }
        return newArgs;
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

/*******************************************************************************\
* Realms v5.x                                                                   *
* Copyright (C) 2012 Visual Illusions Entertainment                             *
* @author darkdiplomat <darkdiplomat@visualillusionsent.net>                    *
*                                                                               *
* This file is part of Realms                                                   *
*                                                                               *
* This program is free software: you can redistribute it and/or modify          *
* it under the terms of the GNU General Public License as published by          *
* the Free Software Foundation, either version 3 of the License, or             *
* (at your option) any later version.                                           *
*                                                                               *
* This program is distributed in the hope that it will be useful,               *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                          *
* See the GNU General Public License for more details.                          *
*                                                                               *
* You should have received a copy of the GNU General Public License             *
* along with this program.  If not, see http://www.gnu.org/licenses/gpl.html    *
*                                                                               *
\*******************************************************************************/
