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
package net.visualillusionsent.realms.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.visualillusionsent.realms.lang.DataSourceError;
import net.visualillusionsent.realms.lang.DataSourceType;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneConstructException;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.Permission;
import net.visualillusionsent.realms.zones.permission.PermissionConstructException;
import net.visualillusionsent.realms.zones.polygon.PolygonArea;
import net.visualillusionsent.realms.zones.polygon.PolygonConstructException;
import net.visualillusionsent.utils.SystemUtils;

import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
final class XML_Source implements DataSource{

    private final Format xmlform = Format.getPrettyFormat().setExpandEmptyElements(true).setOmitDeclaration(true).setOmitEncoding(true).setLineSeparator(SystemUtils.LINE_SEP);
    private final XMLOutputter outputter = new XMLOutputter(xmlform);
    private final SAXBuilder builder = new SAXBuilder();
    private final String zone_Path = "plugins/Realms/zones.xml";
    private final String inv_Path = "plugins/Realms/invs.xml";
    private FileWriter writer;

    @Override
    public final DataSourceType getType(){
        return DataSourceType.XML;
    }

    @Override
    public final void load(){
        RealmsLogMan.info("Loading Zones...");
        File zoneFile = new File(zone_Path);
        Exception ex = null;
        int load = 0;
        if(!zoneFile.exists()){
            RealmsLogMan.info("Zones file not found. Creating...");
            Element zones = new Element("zones");
            Document root = new Document(zones);
            zones.addContent(new Comment("Modifing this file while server is running may cause issues!"));
            zones.addContent(new Comment("Use either the modify command or shut down the server."));
            try{
                writer = new FileWriter(zone_Path);
                outputter.output(root, writer);
            }
            catch(IOException e){
                ex = e;
            }
            finally{
                try{
                    if(writer != null){
                        writer.close();
                    }
                }
                catch(IOException e){}
                writer = null;
                if(ex != null){
                    throw new DataSourceError(ex);
                }
            }
        }
        else{
            List<Object[]> delayLoad = new ArrayList<Object[]>();
            List<String> knownZones = new ArrayList<String>();
            try{
                Document doc = builder.build(zoneFile);
                Element root = doc.getRootElement();
                List<Element> zones = root.getChildren();
                for(Element zone : zones){
                    String name = zone.getChildText("name");
                    knownZones.add(name);
                    String world = zone.getChild("world") != null ? zone.getChildText("world") : "world";
                    String dimension = zone.getChild("dimension") != null ? zone.getChildText("dimension") : "0";
                    String parent = zone.getChild("parent") != null ? zone.getChildText("parent") : genParent(name, world, dimension);
                    String greeting = zone.getChild("greeting") != null ? zone.getChildText("greeting").trim().isEmpty() ? null : zone.getChildText("greeting") : null;
                    String farewell = zone.getChild("farewell") != null ? zone.getChildText("farewell").trim().isEmpty() ? null : zone.getChildText("farewell") : null;
                    String adventure = parseZoneFlagElement(zone.getChild("adventure"));
                    String animals = parseZoneFlagElement(zone.getChild("animals"));
                    String burn = parseZoneFlagElement(zone.getChild("burn"));
                    String creative = parseZoneFlagElement(zone.getChild("creative"));
                    String dispensers = parseZoneFlagElement(zone.getChild("dispensers"));
                    String enderman = parseZoneFlagElement(zone.getChild("enderman"));
                    String explode = parseZoneFlagElement(zone.getChild("explode"));
                    String fall = parseZoneFlagElement(zone.getChild("fall"));
                    String fire = parseZoneFlagElement(zone.getChild("fire"));
                    String flow = parseZoneFlagElement(zone.getChild("flow"));
                    String healing = parseZoneFlagElement(zone.getChild("healing"));
                    String physics = parseZoneFlagElement(zone.getChild("physics"));
                    String pistons = parseZoneFlagElement(zone.getChild("pistons"));
                    String potion = parseZoneFlagElement(zone.getChild("potion"));
                    String pvp = parseZoneFlagElement(zone.getChild("pvp"));
                    String restricted = parseZoneFlagElement(zone.getChild("restricted"));
                    String sanctuary = parseZoneFlagElement(zone.getChild("sanctuary"));
                    String starve = parseZoneFlagElement(zone.getChild("starve"));
                    String suffocate = parseZoneFlagElement(zone.getChild("suffocate"));
                    String testPoly = zone.getChildText("polygon");
                    String[] poly = null;
                    if(!testPoly.equals("null")){
                        poly = testPoly.split(",");
                    }
                    String testPerm = zone.getChildText("permissions");
                    String[] perms = null;
                    if(!testPerm.trim().isEmpty()){
                        perms = testPerm.split(":");
                    }
                    if(!name.startsWith("EVERYWHERE")){
                        try{
                            ZoneLists.getZoneByName(parent);
                        }
                        catch(ZoneNotFoundException e){
                            //save zone for delay load
                            delayLoad.add(new Object[]{ name, world, dimension, parent, greeting, farewell, //
                            adventure, animals, burn, creative, dispensers, enderman, explode, fall, fire, flow, healing, physics, pistons, potion, pvp, restricted, sanctuary, starve, suffocate, //
                            poly, perms });
                            continue;
                        }
                    }
                    try{
                        Zone temp = new Zone(name, world, dimension, parent, greeting, farewell, //
                        adventure, animals, burn, creative, dispensers, enderman, explode, fall, fire, flow, healing, physics, pistons, potion, pvp, restricted, sanctuary, starve, suffocate);
                        load++;
                        if(poly != null){
                            temp.setPolygon(new PolygonArea(temp, poly));
                        }
                        if(perms != null){
                            for(String perm : perms){
                                try{
                                    Permission permTemp = new Permission(perm.split(","));
                                    temp.setPermission(permTemp);
                                }
                                catch(PermissionConstructException pcex){
                                    RealmsLogMan.warning("Invaild permission for Zone: ".concat(name).concat(" Perm: ").concat(perm));
                                    RealmsLogMan.stacktrace(pcex);
                                }
                            }
                        }
                    }
                    catch(ZoneConstructException zcex){
                        RealmsLogMan.warning("Failed to construct Zone:".concat(name) + " Cause: " + zcex.getMessage());
                        RealmsLogMan.stacktrace(zcex);
                    }
                    catch(PolygonConstructException pcex){
                        RealmsLogMan.warning("Failed to construct Polygon for Zone:".concat(name));
                        RealmsLogMan.stacktrace(pcex);
                    }
                }
                Iterator<Object[]> objIter = delayLoad.iterator();
                while(!delayLoad.isEmpty()){
                    while(objIter.hasNext()){
                        Object[] obj = objIter.next();
                        try{
                            if(knownZones.contains((String)obj[3])){
                                try{
                                    ZoneLists.getZoneByName((String)obj[3]);
                                }
                                catch(ZoneNotFoundException e1){
                                    //Parent doesnt appear to have been loaded yet...
                                    continue;
                                }
                                String[] zoneCon = new String[obj.length - 2];
                                System.arraycopy(obj, 0, zoneCon, 0, zoneCon.length);
                                Zone temp = new Zone(zoneCon);
                                load++;
                                if(obj[25] != null){
                                    temp.setPolygon(new PolygonArea(temp, (String[])obj[25]));
                                }
                                if(obj[26] != null){
                                    for(String perm : (String[])obj[26]){
                                        try{
                                            Permission permTemp = new Permission(perm.split(","));
                                            temp.setPermission(permTemp);
                                        }
                                        catch(PermissionConstructException e){
                                            RealmsLogMan.warning("Invaild permission for Zone: ".concat((String)obj[0]).concat(" Perm: ").concat(perm));
                                        }
                                    }
                                }
                            }
                            else{
                                //OH NOES - Orphaned Zone, removed
                                objIter.remove();
                            }
                        }
                        catch(ZoneConstructException zcex){
                            RealmsLogMan.warning("Failed to construct Zone:".concat((String)obj[0]));
                            RealmsLogMan.stacktrace(zcex);
                        }
                        catch(PolygonConstructException pcex){
                            RealmsLogMan.warning("Failed to construct Polygon for Zone:".concat((String)obj[0]));
                            RealmsLogMan.stacktrace(pcex);
                        }
                    }
                }
                RealmsLogMan.info("Loaded ".concat(String.valueOf(load)).concat(" zones."));
            }
            catch(JDOMException jdomex){
                throw new DataSourceError(jdomex);
            }
            catch(IOException ioex){
                throw new DataSourceError(ioex);
            }
        }
    }

    @Override
    public synchronized final boolean reloadZone(Zone reloading){
        File zoneFile = new File(zone_Path);
        try{
            Document doc = builder.build(zoneFile);
            Element root = doc.getRootElement();
            List<Element> zones = root.getChildren();
            for(Element zone : zones){
                String name = zone.getChildText("name");
                if(name.equals(reloading.getName())){
                    String world = zone.getChild("world") != null ? zone.getChildText("world") : "world";
                    String dimension = zone.getChild("dimension") != null ? zone.getChildText("dimension") : "0";
                    String parent = zone.getChild("parent") != null ? zone.getChildText("parent") : genParent(name, world, dimension);
                    String greeting = zone.getChild("greeting") != null ? zone.getChildText("greeting").trim().isEmpty() ? null : zone.getChildText("greeting") : null;
                    String farewell = zone.getChild("farewell") != null ? zone.getChildText("farewell").trim().isEmpty() ? null : zone.getChildText("farewell") : null;
                    String adventure = parseZoneFlagElement(zone.getChild("adventure"));
                    String animals = parseZoneFlagElement(zone.getChild("animals"));
                    String burn = parseZoneFlagElement(zone.getChild("burn"));
                    String creative = parseZoneFlagElement(zone.getChild("creative"));
                    String dispensers = parseZoneFlagElement(zone.getChild("dispensers"));
                    String enderman = parseZoneFlagElement(zone.getChild("enderman"));
                    String entityexplode = parseZoneFlagElement(zone.getChild("explode"));
                    String fall = parseZoneFlagElement(zone.getChild("fall"));
                    String fire = parseZoneFlagElement(zone.getChild("fire"));
                    String flow = parseZoneFlagElement(zone.getChild("flow"));
                    String healing = parseZoneFlagElement(zone.getChild("healing"));
                    String physics = parseZoneFlagElement(zone.getChild("physics"));
                    String pistons = parseZoneFlagElement(zone.getChild("pistons"));
                    String potion = parseZoneFlagElement(zone.getChild("potion"));
                    String pvp = parseZoneFlagElement(zone.getChild("pvp"));
                    String restricted = parseZoneFlagElement(zone.getChild("restricted"));
                    String sanctuary = parseZoneFlagElement(zone.getChild("sanctuary"));
                    String starve = parseZoneFlagElement(zone.getChild("starve"));
                    String suffocate = parseZoneFlagElement(zone.getChild("suffocate"));
                    String testPoly = zone.getChildText("polygon");
                    String[] poly = null;
                    if(!testPoly.equals("null")){
                        poly = testPoly.split(",");
                    }
                    String testPerm = zone.getChildText("permissions");
                    String[] perms = null;
                    if(!testPerm.trim().isEmpty()){
                        perms = testPerm.split(":");
                    }
                    try{
                        Zone temp = new Zone(name, world, dimension, parent, greeting, farewell, //
                        adventure, animals, burn, creative, dispensers, enderman, entityexplode, fall, fire, flow, healing, physics, pistons, potion, pvp, restricted, sanctuary, starve, suffocate);
                        if(poly != null){
                            temp.setPolygon(new PolygonArea(temp, poly));
                        }
                        if(perms != null){
                            for(String perm : perms){
                                try{
                                    Permission permTemp = new Permission(perm.split(","));
                                    temp.setPermission(permTemp);
                                }
                                catch(PermissionConstructException e){
                                    RealmsLogMan.warning("Invaild permission for Zone: ".concat(name).concat(" Perm: ").concat(perm));
                                }
                            }
                        }
                    }
                    catch(ZoneConstructException zcex){
                        RealmsLogMan.warning("Failed to construct Zone:".concat(name) + " Cause: " + zcex.getMessage());
                        RealmsLogMan.stacktrace(zcex);
                    }
                    catch(PolygonConstructException pcex){
                        RealmsLogMan.warning("Failed to construct Polygon for Zone:".concat(name));
                        RealmsLogMan.stacktrace(pcex);
                    }
                }
            }
            RealmsLogMan.info("Zone ".concat(reloading.getName()).concat(" reloaded."));
            return true;
        }
        catch(JDOMException jdomex){
            RealmsLogMan.severe("JDOM Exception while loading Zones file...");
            RealmsLogMan.stacktrace(jdomex);
        }
        catch(IOException ioex){
            RealmsLogMan.severe("Input/Output Exception while trying to read Zones file...");
            RealmsLogMan.stacktrace(ioex);
        }
        return false;
    }

    @Override
    public synchronized final boolean saveZone(Zone zone){
        RealmsLogMan.info("Saving Zone: ".concat(zone.getName()));
        synchronized(lock){
            File zoneFile = new File(zone_Path);
            Exception ex = null;
            try{
                Document doc = builder.build(zoneFile);
                Element root = doc.getRootElement();
                List<Element> zones = root.getChildren();
                boolean found = false;
                for(Element zoneEl : zones){
                    String name = zoneEl.getChildText("name");
                    if(name.equals(zone.getName())){
                        setElement(zoneEl, "world", zone.getWorld());
                        setElement(zoneEl, "dimension", String.valueOf(zone.getDimension()));
                        setElement(zoneEl, "parent", zone.getParent() == null ? "null" : zone.getParent().getName());
                        setElement(zoneEl, "greeting", zone.getGreeting());
                        setElement(zoneEl, "farewell", zone.getFarewell());
                        setElement(zoneEl, "adventure", zone.getAbsoluteAdventure().toString());
                        setElement(zoneEl, "animals", zone.getAbsoluteAnimals().toString());
                        setElement(zoneEl, "burn", zone.getAbsoluteBurn().toString());
                        setElement(zoneEl, "creative", zone.getAbsoluteCreative().toString());
                        setElement(zoneEl, "dispensers", zone.getAbsoluteDispensers().toString());
                        setElement(zoneEl, "enderman", zone.getAbsoluteEnderman().toString());
                        setElement(zoneEl, "explode", zone.getAbsoluteExplode().toString());
                        setElement(zoneEl, "fall", zone.getAbsoluteFall().toString());
                        setElement(zoneEl, "fire", zone.getAbsoluteFire().toString());
                        setElement(zoneEl, "flow", zone.getAbsoluteFlow().toString());
                        setElement(zoneEl, "healing", zone.getAbsoluteHealing().toString());
                        setElement(zoneEl, "physics", zone.getAbsolutePhysics().toString());
                        setElement(zoneEl, "pistons", zone.getAbsolutePistons().toString());
                        setElement(zoneEl, "potion", zone.getAbsolutePotion().toString());
                        setElement(zoneEl, "pvp", zone.getAbsolutePVP().toString());
                        setElement(zoneEl, "restricted", zone.getAbsoluteRestricted().toString());
                        setElement(zoneEl, "sanctuary", zone.getAbsoluteSanctuary().toString());
                        setElement(zoneEl, "starve", zone.getAbsoluteStarve().toString());
                        setElement(zoneEl, "suffocate", zone.getAbsoluteSuffocate().toString());
                        setElement(zoneEl, "polygon", zone.isEmpty() ? "null" : zone.getPolygon().toString());
                        StringBuilder permBuild = new StringBuilder();
                        for(Permission perm : zone.getPerms()){
                            permBuild.append(perm);
                            permBuild.append(":");
                        }
                        zoneEl.getChild("permissions").setText(permBuild.toString());
                        found = true;
                        break;
                    }
                }
                if(!found){
                    Element newZone = new Element("zone");
                    Element child = new Element("name").setText(zone.getName());
                    newZone.addContent(child);
                    setElement(newZone, "world", zone.getWorld());
                    setElement(newZone, "dimension", String.valueOf(zone.getDimension()));
                    setElement(newZone, "parent", zone.getParent() == null ? "null" : zone.getParent().getName());
                    setElement(newZone, "greeting", zone.getGreeting());
                    setElement(newZone, "farewell", zone.getFarewell());
                    setElement(newZone, "adventure", zone.getAbsoluteAdventure().toString());
                    setElement(newZone, "animals", zone.getAbsoluteAnimals().toString());
                    setElement(newZone, "burn", zone.getAbsoluteBurn().toString());
                    setElement(newZone, "creative", zone.getAbsoluteCreative().toString());
                    setElement(newZone, "dispensers", zone.getAbsoluteDispensers().toString());
                    setElement(newZone, "enderman", zone.getAbsoluteEnderman().toString());
                    setElement(newZone, "explode", zone.getAbsoluteExplode().toString());
                    setElement(newZone, "fall", zone.getAbsoluteFall().toString());
                    setElement(newZone, "fire", zone.getAbsoluteFire().toString());
                    setElement(newZone, "flow", zone.getAbsoluteFlow().toString());
                    setElement(newZone, "healing", zone.getAbsoluteHealing().toString());
                    setElement(newZone, "physics", zone.getAbsolutePhysics().toString());
                    setElement(newZone, "pistons", zone.getAbsolutePistons().toString());
                    setElement(newZone, "potion", zone.getAbsolutePotion().toString());
                    setElement(newZone, "pvp", zone.getAbsolutePVP().toString());
                    setElement(newZone, "restricted", zone.getAbsoluteRestricted().toString());
                    setElement(newZone, "sanctuary", zone.getAbsoluteSanctuary().toString());
                    setElement(newZone, "starve", zone.getAbsoluteStarve().toString());
                    setElement(newZone, "suffocate", zone.getAbsoluteSuffocate().toString());
                    setElement(newZone, "polygon", zone.isEmpty() ? "null" : zone.getPolygon().toString());
                    StringBuilder permBuild = new StringBuilder();
                    for(Permission perm : zone.getPerms()){
                        permBuild.append(perm);
                        permBuild.append(":");
                    }
                    child = new Element("permissions").setText(permBuild.toString());
                    newZone.addContent(child);
                    root.addContent(newZone);
                }
                try{
                    writer = new FileWriter(zone_Path);
                    outputter.output(root, writer);
                }
                catch(IOException e){
                    ex = e;
                }
                finally{
                    try{
                        if(writer != null){
                            writer.close();
                        }
                    }
                    catch(IOException e){}
                    writer = null;
                    if(ex != null){
                        RealmsLogMan.severe("Input/Output Exception while trying to write to Zones file...");
                        RealmsLogMan.stacktrace(ex);
                        return false;
                    }
                }
                RealmsLogMan.info("Zone: ".concat(zone.getName()) + " saved!");
                return true;
            }
            catch(JDOMException jdomex){
                RealmsLogMan.severe("JDOM Exception while trying to save Zone: " + zone.getName());
                RealmsLogMan.stacktrace(jdomex);
            }
            catch(IOException ioex){
                RealmsLogMan.severe("Input/Output Exception while trying to save Zone: " + zone.getName());
                RealmsLogMan.stacktrace(ioex);
            }
        }
        return false;
    }

    @Override
    public synchronized final boolean deleteZone(Zone zone){
        RealmsLogMan.info("Deleting Zone: ".concat(zone.getName()));
        synchronized(lock){
            File zoneFile = new File(zone_Path);
            Exception ex = null;
            try{
                Document doc = builder.build(zoneFile);
                Element root = doc.getRootElement();
                List<Element> zones = root.getChildren();
                Content child = null;
                for(Element zoneEl : zones){
                    String name = zoneEl.getChildText("name");
                    if(name.equals(zone.getName())){
                        child = zoneEl.detach();
                    }
                }
                if(child != null){
                    writer = new FileWriter(zone_Path);
                    outputter.output(root, writer);
                }
            }
            catch(JDOMException jdomex){
                ex = jdomex;
            }
            catch(IOException ioex){
                ex = ioex;
            }
            finally{
                try{
                    if(writer != null){
                        writer.close();
                    }
                }
                catch(IOException e){}
                writer = null;
                if(ex != null){
                    RealmsLogMan.severe("Failed to write to Zones file...");
                    RealmsLogMan.stacktrace(ex);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public final void loadInventories(){
        RealmsLogMan.info("Loading Inventories...");
        File invFile = new File(inv_Path);
        Exception ex = null;
        if(!invFile.exists()){
            RealmsLogMan.info("Inventories file not found. Creating...");
            Element invs = new Element("inventories");
            Document root = new Document(invs);
            invs.addContent(new Comment("Modifing this file while server is running may cause issues!"));
            invs.addContent(new Comment("Use either the modify command or shut down the server."));
            try{
                writer = new FileWriter(inv_Path);
                outputter.output(root, writer);
            }
            catch(IOException e){
                ex = e;
            }
            finally{
                try{
                    if(writer != null){
                        writer.close();
                    }
                }
                catch(IOException e){}
                writer = null;
                if(ex != null){
                    throw new DataSourceError(ex);
                }
            }
        }
        else{
            try{
                Document doc = builder.build(invFile);
                Element root = doc.getRootElement();
                List<Element> invs = root.getChildren();
                for(Element inv : invs){
                    String name = inv.getChildText("name");
                    Mod_Item[] items = new Mod_Item[40];
                    Arrays.fill(items, null);
                    for(int index = 0; index < 40; index++){
                        items[index] = constructFromString(inv.getChildText("item".concat(String.valueOf(index))));
                    }
                    RealmsBase.storeInventory(name, items);
                }
            }
            catch(JDOMException jdomex){
                throw new DataSourceError(jdomex);
            }
            catch(IOException ioex){
                throw new DataSourceError(ioex);
            }
        }
    }

    @Override
    public synchronized final boolean saveInventory(Mod_User user, Mod_Item[] items){
        RealmsLogMan.info("Saving inventory for User: ".concat(user.getName()));
        synchronized(lock){
            File invFile = new File(inv_Path);
            Exception ex = null;
            try{
                Document doc = builder.build(invFile);
                Element root = doc.getRootElement();
                List<Element> invs = root.getChildren();
                boolean found = false;
                for(Element inv : invs){
                    String name = inv.getChildText("name");
                    if(name.equals(user.getName())){
                        for(int index = 0; index < 40; index++){
                            setElement(inv, "item".concat(String.valueOf(index)), items[index] != null ? items[index].toString() : "null");
                        }
                        found = true;
                        break;
                    }
                }
                if(!found){
                    Element newInv = new Element("inventory");
                    Element child = new Element("name").setText(user.getName());
                    newInv.addContent(child);
                    for(int index = 0; index < 40; index++){
                        setElement(newInv, "item".concat(String.valueOf(index)), items[index].toString());
                    }
                }
                try{
                    writer = new FileWriter(zone_Path);
                    outputter.output(root, writer);
                }
                catch(IOException e){
                    ex = e;
                }
                finally{
                    try{
                        if(writer != null){
                            writer.close();
                        }
                    }
                    catch(IOException e){}
                    writer = null;
                    if(ex != null){
                        RealmsLogMan.severe("Failed to write to Zones file...");
                        RealmsLogMan.stacktrace(ex);
                        return false;
                    }
                }
                RealmsLogMan.info("Inventory for User: ".concat(user.getName()) + " saved!");
                return true;
            }
            catch(JDOMException jdomex){
                RealmsLogMan.severe("JDOM Exception while trying to write to inventories file...");
                RealmsLogMan.stacktrace(jdomex);
            }
            catch(IOException ioex){
                RealmsLogMan.severe("Failed to write to inventories file...");
                RealmsLogMan.stacktrace(ioex);
            }
        }
        return false;
    }

    @Override
    public synchronized final boolean deleteInventory(Mod_User user){
        RealmsLogMan.info("Deleting Inventory for User: ".concat(user.getName()));
        synchronized(lock){
            File zoneFile = new File(inv_Path);
            Exception ex = null;
            try{
                Document doc = builder.build(zoneFile);
                Element root = doc.getRootElement();
                List<Element> zones = root.getChildren();
                Content child = null;
                for(Element zoneEl : zones){
                    String name = zoneEl.getChildText("name");
                    if(name.equals(user.getName())){
                        child = zoneEl.detach();
                    }
                }
                if(child != null){
                    writer = new FileWriter(inv_Path);
                    outputter.output(root, writer);
                }
            }
            catch(JDOMException e){
                ex = e;
            }
            catch(IOException e){
                ex = e;
            }
            finally{
                try{
                    if(writer != null){
                        writer.close();
                    }
                }
                catch(IOException e){}
                writer = null;
                if(ex != null){
                    RealmsLogMan.severe("Failed to write to inventories file...");
                    RealmsLogMan.stacktrace(ex);
                    return false;
                }
            }
        }
        return true;
    }

    private final String genParent(String name, String world, String dimension){
        if(!name.startsWith("EVERYWHERE")){
            return "EVERYWHERE-" + world.toUpperCase() + "-DIM" + dimension;
        }
        return "null";
    }

    private final String parseZoneFlagElement(Element element){
        if(element != null){
            if(element.getText().matches("on|off|inherit")){
                return element.getText();
            }
        }
        return "inherit";
    }

    private final void setElement(Element element, String child, String text){
        if(element.getChild(child) != null){
            element.getChild(child).setText(text);
        }
        else{
            Element newChild = new Element(child).setText(text);
            element.addContent(newChild);
        }
    }

    private final Mod_Item constructFromString(String item){
        if(item.equals("null")){
            return null;
        }
        try{
            String[] it = item.split(",");
            List<Mod_ItemEnchantment> enchants = new ArrayList<Mod_ItemEnchantment>();
            List<String> lore = new ArrayList<String>();
            int id = Integer.parseInt(it[0]);
            int amount = Integer.parseInt(it[1]);
            int damage = Integer.parseInt(it[2]);
            String name = it[3].equals("NO_NAME_FOR_THIS_ITEM") ? null : it[4];
            for(int index = 4; index < it.length; index++){
                if(it[index].contains(":")){
                    String[] ench = it[index].split(":");
                    int enchId = Integer.parseInt(ench[0]);
                    int enchLvl = Integer.parseInt(ench[1]);
                    enchants.add(RealmsBase.getServer().constructEnchantment(enchId, enchLvl));
                }
                else{
                    lore.add(it[index]);
                }
            }
            Mod_ItemEnchantment[] enchs = enchants.isEmpty() ? null : enchants.toArray(new Mod_ItemEnchantment[0]);
            String[] lores = lore.isEmpty() ? null : lore.toArray(new String[0]);
            return RealmsBase.getServer().constructItem(id, amount, damage, name, enchs, lores);
        }
        catch(Exception ex){} //FAIL!
        return null;
    }
}
