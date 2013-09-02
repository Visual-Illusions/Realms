/* Copyright 2012 - 2013 Visual Illusions Entertainment.
 * This file is part of Realms.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * Source Code availible @ https://github.com/Visual-Illusions/Realms */
package net.visualillusionsent.realms.zones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.data.OutputAction;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.runnable.RealmsDefaultPermsSet;
import net.visualillusionsent.realms.zones.permission.Permission;
import net.visualillusionsent.realms.zones.permission.PermissionType;
import net.visualillusionsent.realms.zones.polygon.PolygonArea;
import net.visualillusionsent.utils.StringUtils;
import net.visualillusionsent.utils.TaskManager;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Zone{

    private String name, world;
    private int dimension = 0;
    private Zone parent;
    private String greeting;
    private String farewell;
    private PolygonArea polygon;
    private List<Permission> zoneperms = new ArrayList<Permission>();
    private List<Zone> children = new ArrayList<Zone>();
    private ZoneFlag adventure; // OFF = Survival Mode, ON = Adventure Mode
    private ZoneFlag animals; // OFF = NO Animals, ON = Animals
    private ZoneFlag burn; // OFF = Blocks may burn. ON = Blocks may not burn.
    private ZoneFlag creative; // OFF = Survival Mode, ON = Creative Mode
    private ZoneFlag dispensers; // OFF = no dispensing, ON = dispensing
    private ZoneFlag enderman; // OFF = No Enderman may not PickUp/Place Blocks. ON = Enderman may PickUp/Place Blocks
    private ZoneFlag explode; // OFF = entity may not explode, ON = entity may explode
    private ZoneFlag fall; // OFF = No Fall Damage, ON = Fall Damage
    private ZoneFlag fire; // OFF = NO Fire Damage, ON = Fire Damage
    private ZoneFlag flow; // OFF = Water/Lava may not flow. ON = Water/Lava may flow.
    private ZoneFlag healing; // Changed to boolean -- OFF = No Healing. ON = Healing.
    private ZoneFlag physics; // OFF = Sand/Gravel may not fall, ON = Sand/Gravel may fall
    private ZoneFlag pistons; // OFF = Pistons may not function, ON = Pistons may function
    private ZoneFlag potion; // OFF = no potion damage, ON = potions can damage
    private ZoneFlag pvp; // OFF = pvp disabled, ON = pvp enabled
    private ZoneFlag restricted; // OFF = Allow All, ON = Those without Authed permission take damage
    private ZoneFlag sanctuary; // OFF = zone is not a sanctuary, ON = zone is a sanctuary
    private ZoneFlag starve; // OFF = no Starvation, ON = Starvation
    private ZoneFlag suffocate; // OFF = No Suffocation, ON = Suffocation
    private boolean deleted = false;
    private boolean saving = false;

    // Regular Constructor
    public Zone(String name, Zone parent, String world, int dimension){
        this.name = name;
        this.parent = parent;
        this.polygon = null;
        this.greeting = null;
        this.farewell = null;
        this.world = world;
        this.dimension = dimension;
        setDefaults(parent == null);
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.children.add(this);
        }
        if (name.startsWith("EVERYWHERE")) {
            TaskManager.executeTask(new RealmsDefaultPermsSet(this));
        }
        ZoneLists.addZone(this);
        RealmsLogMan.info("Zone created: " + name);
        save();
    }

    // File Constructor
    public Zone(String... args) throws ZoneConstructException{
        try {
            this.name = args[0];
            this.world = args[1];
            this.dimension = Integer.valueOf(args[2]);
            if (name.startsWith("EVERYWHERE")) {
                this.parent = null;
            }
            else {
                try {
                    this.parent = args[3] != null ? ZoneLists.getZoneByName(args[3]) : null;
                }
                catch (ZoneNotFoundException ZNFE) {}
            }
            if (parent != null && !parent.getChildren().contains(this)) {
                parent.children.add(this);
            }
            if (args.length < 5 || args[4] == null || args[4].equalsIgnoreCase("null")) {
                this.greeting = null;
            }
            else {
                this.greeting = args[4];
            }
            if (args.length < 6 || args[5] == null || args[5].equalsIgnoreCase("null")) {
                this.farewell = null;
            }
            else {
                this.farewell = args[5];
            }
            if (args.length < 7) {
                setDefaults(name.toUpperCase().startsWith("EVERYWHERE"));
            }
            else {
                this.adventure = ZoneFlag.getZoneFlag(args[6]);
                this.animals = ZoneFlag.getZoneFlag(args[7]);
                this.burn = ZoneFlag.getZoneFlag(args[8]);
                this.creative = ZoneFlag.getZoneFlag(args[9]);
                this.dispensers = ZoneFlag.getZoneFlag(args[10]);
                this.enderman = ZoneFlag.getZoneFlag(args[11]);
                this.explode = ZoneFlag.getZoneFlag(args[12]);
                this.fall = ZoneFlag.getZoneFlag(args[13]);
                this.fire = ZoneFlag.getZoneFlag(args[14]);
                this.flow = ZoneFlag.getZoneFlag(args[15]);
                this.healing = ZoneFlag.getZoneFlag(args[16]);
                this.physics = ZoneFlag.getZoneFlag(args[17]);
                this.pistons = ZoneFlag.getZoneFlag(args[18]);
                this.potion = ZoneFlag.getZoneFlag(args[19]);
                this.pvp = ZoneFlag.getZoneFlag(args[20]);
                this.restricted = ZoneFlag.getZoneFlag(args[21]);
                this.sanctuary = ZoneFlag.getZoneFlag(args[22]);
                this.starve = ZoneFlag.getZoneFlag(args[23]);
                this.suffocate = ZoneFlag.getZoneFlag(args[24]);
            }
            this.children = new ArrayList<Zone>();
            if (parent != null && !parent.getChildren().contains(this)) {
                parent.children.add(this);
            }
            ZoneLists.addZone(this);
        }
        catch (Exception ex) {
            throw new ZoneConstructException("Args invalid", ex);
        }
    }

    private final void setDefaults(boolean isEverywhere){
        if (isEverywhere) {
            this.adventure = ZoneFlag.OFF;
            this.animals = ZoneFlag.ON;
            this.burn = ZoneFlag.ON;
            this.creative = ZoneFlag.OFF;
            this.dispensers = ZoneFlag.ON;
            this.enderman = ZoneFlag.ON;
            this.explode = ZoneFlag.ON;
            this.fall = ZoneFlag.ON;
            this.fire = ZoneFlag.ON;
            this.flow = ZoneFlag.ON;
            this.healing = ZoneFlag.OFF;
            this.physics = ZoneFlag.ON;
            this.pistons = ZoneFlag.ON;
            this.potion = ZoneFlag.ON;
            this.pvp = ZoneFlag.ON;
            this.restricted = ZoneFlag.OFF;
            this.sanctuary = ZoneFlag.OFF;
            this.starve = ZoneFlag.ON;
            this.suffocate = ZoneFlag.ON;
        }
        else {
            this.adventure = ZoneFlag.INHERIT;
            this.animals = ZoneFlag.INHERIT;
            this.burn = ZoneFlag.INHERIT;
            this.creative = ZoneFlag.INHERIT;
            this.dispensers = ZoneFlag.INHERIT;
            this.enderman = ZoneFlag.INHERIT;
            this.explode = ZoneFlag.INHERIT;
            this.fall = ZoneFlag.INHERIT;
            this.fire = ZoneFlag.INHERIT;
            this.flow = ZoneFlag.INHERIT;
            this.healing = ZoneFlag.INHERIT;
            this.physics = ZoneFlag.INHERIT;
            this.pistons = ZoneFlag.INHERIT;
            this.potion = ZoneFlag.INHERIT;
            this.pvp = ZoneFlag.INHERIT;
            this.restricted = ZoneFlag.INHERIT;
            this.sanctuary = ZoneFlag.INHERIT;
            this.starve = ZoneFlag.INHERIT;
            this.suffocate = ZoneFlag.INHERIT;
        }
    }

    /* Accessor Methods */
    public final String getName(){
        return name;
    }

    public final Zone getParent(){
        return parent;
    }

    public final String getWorld(){
        return world;
    }

    public final int getDimension(){
        return dimension;
    }

    public final String getGreeting(){
        return greeting;
    }

    public final String getFarewell(){
        return farewell;
    }

    public final List<Zone> getChildren(){
        return children;
    }

    public final boolean hasNoChildren(){
        return children.isEmpty();
    }

    public final PolygonArea getPolygon(){
        return polygon;
    }

    public final ZoneFlag getAbsoluteAdventure(){
        return adventure;
    }

    public final ZoneFlag getAbsoluteAnimals(){
        return animals;
    }

    public final ZoneFlag getAbsoluteBurn(){
        return burn;
    }

    public final ZoneFlag getAbsoluteCreative(){
        return creative;
    }

    public final ZoneFlag getAbsoluteDispensers(){
        return dispensers;
    }

    public final ZoneFlag getAbsoluteEnderman(){
        return enderman;
    }

    public final ZoneFlag getAbsoluteExplode(){
        return explode;
    }

    public final ZoneFlag getAbsoluteFall(){
        return fall;
    }

    public final ZoneFlag getAbsoluteFire(){
        return fire;
    }

    public final ZoneFlag getAbsoluteFlow(){
        return flow;
    }

    public final ZoneFlag getAbsoluteHealing(){
        return healing;
    }

    public final ZoneFlag getAbsolutePhysics(){
        return physics;
    }

    public final ZoneFlag getAbsolutePistons(){
        return pistons;
    }

    public final ZoneFlag getAbsolutePotion(){
        return potion;
    }

    public final ZoneFlag getAbsolutePVP(){
        return pvp;
    }

    public final ZoneFlag getAbsoluteRestricted(){
        return restricted;
    }

    public final ZoneFlag getAbsoluteSanctuary(){
        return sanctuary;
    }

    public final ZoneFlag getAbsoluteStarve(){
        return starve;
    }

    public final ZoneFlag getAbsoluteSuffocate(){
        return suffocate;
    }

    public final boolean getAdventure(){
        if (this.adventure.isInherit() && this.parent != null) {
            return parent.getAdventure();
        }
        else {
            return this.adventure.getValue();
        }
    }

    public final boolean getAnimals(){
        if (this.animals.isInherit() && this.parent != null) {
            return parent.getAnimals();
        }
        else {
            return this.animals.getValue();
        }
    }

    public final boolean getBurn(){
        if (this.burn.isInherit() && this.parent != null) {
            return parent.getBurn();
        }
        else {
            return this.burn.getValue();
        }
    }

    public final boolean getCreative(){
        if (this.creative.isInherit() && this.parent != null) {
            return parent.getCreative();
        }
        else {
            return this.creative.getValue();
        }
    }

    public final boolean getDispensers(){
        if (this.dispensers.isInherit() && this.parent != null) {
            return parent.getDispensers();
        }
        else {
            return this.dispensers.getValue();
        }
    }

    public final boolean getEnderman(){
        if (this.enderman.isInherit() && this.parent != null) {
            return parent.getEnderman();
        }
        else {
            return this.enderman.getValue();
        }
    }

    public final boolean getExplode(){
        if (this.explode.isInherit() && this.parent != null) {
            return parent.getExplode();
        }
        else {
            return this.explode.getValue();
        }
    }

    public final boolean getFall(){
        if (this.fall.isInherit() && this.parent != null) {
            return parent.getFall();
        }
        else {
            return this.fall.getValue();
        }
    }

    public final boolean getFire(){
        if (this.fire.isInherit() && this.parent != null) {
            return parent.getFire();
        }
        else {
            return this.fire.getValue();
        }
    }

    public final boolean getFlow(){
        if (this.flow.isInherit() && this.parent != null) {
            return parent.getFlow();
        }
        else {
            return this.flow.getValue();
        }
    }

    public final boolean getHealing(){
        if (this.healing.isInherit() && this.parent != null) {
            return parent.getHealing();
        }
        else {
            return this.healing.getValue();
        }
    }

    public final boolean getPhysics(){
        if (this.physics.isInherit() && this.parent != null) {
            return parent.getPhysics();
        }
        else {
            return this.physics.getValue();
        }
    }

    public final boolean getPistons(){
        if (this.pistons.isInherit() && this.parent != null) {
            return parent.getPistons();
        }
        else {
            return this.pistons.getValue();
        }
    }

    public final boolean getPotion(){
        if (this.potion.isInherit() && this.parent != null) {
            return parent.getPotion();
        }
        else {
            return this.potion.getValue();
        }
    }

    public final boolean getPVP(){
        if (this.pvp.isInherit() && this.parent != null) {
            return parent.getPVP();
        }
        else {
            return this.pvp.getValue();
        }
    }

    public final boolean getRestricted(){
        if (this.restricted.isInherit() && this.parent != null) {
            return parent.getRestricted();
        }
        else {
            return this.restricted.getValue();
        }
    }

    public final boolean getSanctuary(){
        if (this.sanctuary.isInherit() && this.parent != null) {
            return parent.getSanctuary();
        }
        else {
            return this.sanctuary.getValue();
        }
    }

    public final boolean getStarve(){
        if (this.starve.isInherit() && this.parent != null) {
            return parent.getStarve();
        }
        else {
            return this.starve.getValue();
        }
    }

    public final boolean getSuffocate(){
        if (this.suffocate.isInherit() && this.parent != null) {
            return parent.getSuffocate();
        }
        else {
            return this.suffocate.getValue();
        }
    }

    /* Mutator Methods */
    public final void removeChild(Zone child){
        children.remove(child);
    }

    public final void setPolygon(PolygonArea polygon){
        this.polygon = polygon;
    }

    public final void setWorld(String world){
        this.world = world;
    }

    public final void setDimension(int dim){
        this.dimension = dim;
    }

    public final void setParent(Zone newParent){
        parent = newParent;
        newParent.getChildren().add(this);
    }

    public final void setGreeting(String greeting){
        this.greeting = greeting;
        save();
    }

    public final void setFarewell(String farewell){
        this.farewell = farewell;
        save();
    }

    public final void setAdventure(ZoneFlag adventure){
        this.adventure = adventure;
        save();
    }

    public final void setAnimals(ZoneFlag animals){
        this.animals = animals;
        save();
    }

    public final void setBurn(ZoneFlag burn){
        this.burn = burn;
        save();
    }

    public final void setCreative(ZoneFlag creative){
        this.creative = creative;
        save();
    }

    public final void setDispensers(ZoneFlag dispensers){
        this.dispensers = dispensers;
        save();
    }

    public final void setEnderman(ZoneFlag enderman){
        // Dynamically generated
        this.enderman = enderman;
        save();
    }

    public final void setExplode(ZoneFlag entityexplode){
        this.explode = entityexplode;
        save();
    }

    public final void setFall(ZoneFlag fall){
        this.fall = fall;
        save();
    }

    public final void setFire(ZoneFlag fire){
        this.fire = fire;
        save();
    }

    public final void setFlow(ZoneFlag flow){
        this.flow = flow;
        save();
    }

    public final void setHealing(ZoneFlag healing){
        this.healing = healing;
        save();
    }

    public final void setPhysics(ZoneFlag physics){
        this.physics = physics;
        save();
    }

    public final void setPistons(ZoneFlag pistons){
        this.pistons = pistons;
        save();
    }

    public final void setPotion(ZoneFlag potion){
        this.potion = potion;
        save();
    }

    public final void setPVP(ZoneFlag pvp){
        this.pvp = pvp;
        save();
    }

    public final void setRestricted(ZoneFlag restricted){
        this.restricted = restricted;
        save();
    }

    public final void setSanctuary(ZoneFlag sanctuary){
        this.sanctuary = sanctuary;
        save();
    }

    public final void setStarve(ZoneFlag starve){
        this.starve = starve;
        save();
    }

    public final void setSuffocate(ZoneFlag suffocate){
        this.suffocate = suffocate;
        save();
    }

    /* Other Methods */
    public final void farewell(Mod_User player){
        if (farewell != null) {
            player.sendMessage(farewell.replace("@", "\u00A7"));
        }
    }

    public final void greet(Mod_User player){
        if (greeting != null) {
            player.sendMessage(greeting.replace("@", "\u00A7"));
        }
    }

    // Delete the zone
    public final void delete(){
        this.deleted = true;
        this.polygon.delete(false);
        for (Zone child : children) {
            child.setParent(parent);
        }
        parent.removeChild(this);
        parent = null;
        ZoneLists.removeZonefromPlayerZoneList(this);
        ZoneLists.removeZone(this);
        RealmsBase.getDataSourceHandler().addToQueue(OutputAction.DELETE_ZONE, this);
    }

    // Save Zone
    public final void save(){
        RealmsBase.getDataSourceHandler().addToQueue(OutputAction.SAVE_ZONE, this);
    }

    // Does this zone contain zero area?
    public final boolean isEmpty(){
        return polygon == null || polygon.isEmpty() || this.deleted;
    }

    public final boolean contains(Mod_Block block){
        if (isEmpty()) {
            if (name.equals("EVERYWHERE-" + block.getWorld().toUpperCase() + "-DIM" + block.getDimension())) {
                return true;
            }
            return false;
        }
        if (isInWorld(block.getWorld(), block.getDimension())) {
            return polygon.contains(block);
        }
        return false;
    }

    public final boolean contains(Mod_Entity entity){
        if (isEmpty()) {
            if (name.equals("EVERYWHERE-" + entity.getWorld().toUpperCase() + "-DIM" + entity.getDimension())) {
                return true;
            }
            return false;
        }
        if (isInWorld(entity.getWorld(), entity.getDimension())) {
            return polygon.contains(entity);
        }
        return false;
    }

    public final boolean contains(Zone zone){
        if (isEmpty()) {
            if (name.equals("EVERYWHERE-" + zone.getWorld().toUpperCase() + "-DIM" + zone.getDimension())) {
                return true;
            }
            return false;
        }
        if (isInWorld(zone.getWorld(), zone.getDimension())) {
            return polygon.verticesContain(zone.getPolygon());
        }
        return false;
    }

    public final Zone whichChildContains(Mod_Block block){
        for (Zone child : children) {
            if (child.contains(block)) {
                return child.whichChildContains(block);
            }
        }
        return this;
    }

    public final Zone whichChildContains(Mod_Entity entity){
        for (Zone child : children) {
            if (child.contains(entity)) {
                return child.whichChildContains(entity);
            }
        }
        return this;
    }

    public final boolean isInWorld(String world, int dim){
        return world != null && this.world != null ? this.world.equals(world) && this.dimension == dim : false;
    }

    /**
     * Gets a zone's flags
     * 
     * @param zone
     * @param showComb
     * @param showEnviro
     * @return flags
     */
    public final String[] getFlags(boolean showComb, boolean showEnviro){
        String[] environ = new String[5];
        String[] comb = new String[2];
        StringBuilder flags = new StringBuilder();
        // Start Environment Flags
        if (showEnviro) {
            flags.append(formatFlag("ADVENTURE", getAbsoluteAdventure().isInherit(), getAdventure(), true));
            flags.append(formatFlag("ANIMALS", getAbsoluteAnimals().isInherit(), getAnimals(), true));
            flags.append(formatFlag("BURN", getAbsoluteBurn().isInherit(), getBurn(), false));
            environ[0] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(formatFlag("CREATIVE", getAbsoluteCreative().isInherit(), getCreative(), true));
            flags.append(formatFlag("DISPENSERS", getAbsoluteDispensers().isInherit(), getDispensers(), true));
            flags.append(formatFlag("ENDERMAN", getAbsoluteEnderman().isInherit(), getEnderman(), false));
            environ[1] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(formatFlag("FALL", getAbsoluteFall().isInherit(), getFall(), true));
            flags.append(formatFlag("FIRE", getAbsoluteFire().isInherit(), getFire(), true));
            flags.append(formatFlag("FLOW", getAbsoluteFlow().isInherit(), getFlow(), false));
            environ[2] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(formatFlag("PHYSICS", getAbsolutePhysics().isInherit(), getPhysics(), true));
            flags.append(formatFlag("PISTONS", getAbsolutePistons().isInherit(), getPistons(), true));
            flags.append(formatFlag("RESTRICTED", getAbsoluteRestricted().isInherit(), getRestricted(), false));
            environ[3] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(formatFlag("SUFFOCATE", getAbsoluteSuffocate().isInherit(), getSuffocate(), true));
            flags.append(formatFlag("STARVE", getAbsoluteStarve().isInherit(), getStarve(), false));
            environ[4] = flags.toString();
            flags.delete(0, flags.length());
        }
        // End Environment Flags
        // Start Combat Flags
        if (showComb) {
            flags.append(formatFlag("EXPLODE", getAbsoluteExplode().isInherit(), getExplode(), true));
            flags.append(formatFlag("HEALING", getAbsoluteHealing().isInherit(), getHealing(), true));
            flags.append(formatFlag("POTION", getAbsolutePotion().isInherit(), getPotion(), false));
            comb[0] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(formatFlag("PVP", getAbsolutePVP().isInherit(), getPVP(), true));
            flags.append(formatFlag("SANCTUARY", getAbsoluteSanctuary().isInherit(), getSanctuary(), false));
            comb[1] = flags.toString();
        }
        // End Combat Flags
        if (showComb && !showEnviro) {
            return comb;
        }
        else if (!showComb && showEnviro) {
            return environ;
        }
        else {
            String[] allFlags = new String[environ.length + comb.length];
            System.arraycopy(environ, 0, allFlags, 0, environ.length);
            System.arraycopy(comb, 0, allFlags, environ.length, comb.length);
            return allFlags;
        }
    }

    private String formatFlag(String flag, boolean absolute, boolean on, boolean pad){
        StringBuffer tempRet = new StringBuffer(MCChatForm.ORANGE.toString());
        tempRet.append(flag);
        tempRet.append(": ");
        if (absolute) {
            tempRet.append(MCChatForm.PINK);
            if (on) {
                tempRet.append("ON");
            }
            else {
                tempRet.append("OFF");
            }
        }
        else if (on) {
            tempRet.append(MCChatForm.GREEN);
            tempRet.append("ON");
        }
        else {
            tempRet.append(MCChatForm.RED);
            tempRet.append("OFF");
        }
        String toRet = tempRet.toString();
        if (pad) {
            toRet = StringUtils.padCharRight(toRet, (26 - toRet.length()), ' ');
        }
        return toRet;
    }

    // Start Permission Checks
    /**
     * Set Permissions Method
     * Overrides previous permission if it existed Otherwise creates new
     * permission
     * 
     * @param String
     *            ownerName
     * @param PermType
     *            type
     * @param Zone
     *            zone
     * @param Boolean
     *            allowed
     * @param Boolean
     *            override
     */
    public final void setPermission(String ownerName, PermissionType type, boolean allowed, boolean override){
        Permission previous = getSpecificPermission(ownerName, type);
        if (previous != null) {
            zoneperms.remove(previous);
        }
        Permission perm = new Permission(ownerName, type, allowed, override);
        zoneperms.add(perm);
        save();
    }

    public final void setPermission(Permission perm){
        zoneperms.add(perm);
    }

    /**
     * Gets Specific Permission
     * 
     * @param ownerName
     * @param type
     * @return Permission
     */
    public final Permission getSpecificPermission(String ownerName, PermissionType type){
        for (Permission p : zoneperms) {
            if (p.getOwnerName().equals(ownerName) && p.getType().equals(type)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Permission Delete
     * 
     * @param ownerName
     * @param type
     * @param zone
     */
    public final void deletePermission(String ownerName, PermissionType type){
        Permission permission = getSpecificPermission(ownerName, type);
        if (permission != null) {
            zoneperms.remove(permission);
            save();
        }
    }

    /**
     * Delegate check
     * 
     * @param player
     * @param type
     * @param zone
     * @return boolean check result
     */
    public final boolean delegateCheck(Mod_User player, PermissionType type){
        if (getParent() != null) {
            if (getParent().permissionCheck(player, PermissionType.ALL)) {
                return true;
            }
        }
        if (type.equals(PermissionType.DELEGATE)) {
            return permissionCheck(player, PermissionType.ALL);
        }
        else {
            return permissionCheck(player, PermissionType.DELEGATE) && permissionCheck(player, type);
        }
    }

    /**
     * General Permission Check
     */
    public final boolean permissionCheck(Mod_User user, PermissionType type){
        Permission result = null;
        for (Permission p : zoneperms) {
            if (p.applicable(user, type)) {
                if (result == null) {
                    result = p;
                }
                else {
                    result = p.battle(result, p);
                }
            }
        }
        if (result == null) {
            if (getParent() != null) {
                return getParent().permissionCheck(user, type);
            }
            else {
                return RealmsBase.getProperties().getBooleanVal("grant.default");
            }
        }
        else {
            return result.getAllowed();
        }
    }

    public final List<Permission> getPerms(){
        List<Permission> theperms = new ArrayList<Permission>(zoneperms);
        Collections.copy(theperms, zoneperms);
        return theperms;
    }

    public final boolean isPendingDeletion(){
        return deleted;
    }

    public final boolean isSaving(){
        return saving;
    }

    public final void setSaving(boolean save){
        this.saving = save;
    }

    // End Permission checks
    @Override
    public final String toString(){
        StringBuffer toRet = new StringBuffer();
        toRet.append(name);
        toRet.append(',');
        toRet.append(world);
        toRet.append(',');
        toRet.append(dimension);
        toRet.append(',');
        toRet.append(parent == null ? "null" : parent.getName());
        toRet.append(',');
        toRet.append(greeting);
        toRet.append(',');
        toRet.append(farewell);
        toRet.append(',');
        toRet.append(adventure.toString());
        toRet.append(',');
        toRet.append(animals.toString());
        toRet.append(',');
        toRet.append(burn.toString());
        toRet.append(',');
        toRet.append(creative.toString());
        toRet.append(',');
        toRet.append(dispensers.toString());
        toRet.append(',');
        toRet.append(enderman.toString());
        toRet.append(',');
        toRet.append(explode.toString());
        toRet.append(',');
        toRet.append(fall.toString());
        toRet.append(',');
        toRet.append(fire.toString());
        toRet.append(',');
        toRet.append(flow.toString());
        toRet.append(',');
        toRet.append(healing.toString());
        toRet.append(',');
        toRet.append(physics.toString());
        toRet.append(',');
        toRet.append(pistons.toString());
        toRet.append(',');
        toRet.append(potion.toString());
        toRet.append(',');
        toRet.append(pvp.toString());
        toRet.append(',');
        toRet.append(restricted.toString());
        toRet.append(',');
        toRet.append(sanctuary.toString());
        toRet.append(',');
        toRet.append(starve.toString());
        toRet.append(',');
        toRet.append(suffocate.toString());
        return toRet.toString();
    }

    @Override
    public final int hashCode(){
        int hash = 7;
        hash = 31 * hash + dimension;
        hash = 31 * hash + (null == world ? 0 : world.hashCode());
        hash = 31 * hash + (null == name ? 0 : name.hashCode());
        return hash;
    }

    @Override
    public final boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Zone)) {
            return false;
        }
        Zone zone = (Zone) obj;
        if (RealmsBase.getProperties().getBooleanVal("zone.case.sensitive")) {
            return this.name.equals(zone.getName());
        }
        return this.name.toLowerCase().equals(zone.getName().toLowerCase());
    }
}
