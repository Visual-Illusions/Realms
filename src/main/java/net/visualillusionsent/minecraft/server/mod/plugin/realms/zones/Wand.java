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
package net.visualillusionsent.minecraft.server.mod.plugin.realms.zones;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.permission.PermissionType;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.polygon.Point;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.polygon.PolygonArea;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Wand{

    private enum Mode {
        DEFAULT,
        POLYGON,
        SET_CEILING,
        SET_FLOOR;
    }

    private final String verts = MCChatForm.ORANGE + "# VERTICIES: " + MCChatForm.BLUE + "%d";
    private final String floor_ceiling_height = MCChatForm.ORANGE + "FLOOR: " + MCChatForm.BLUE + "%d" + MCChatForm.ORANGE + "  CEILING: " + MCChatForm.BLUE + "%d" + MCChatForm.ORANGE + "  HEIGHT: " + MCChatForm.BLUE + "%d";
    private final String area_volume = MCChatForm.ORANGE + "AREA: " + MCChatForm.BLUE + "%d" + MCChatForm.ORANGE + "  VOLUME: " + MCChatForm.BLUE + "%d";

    private final int pylonType;
    private final int pylonHeight;
    private final Mod_User user;
    private Mode mode = Mode.DEFAULT;
    private PolygonArea workingPolygon;
    private List<Mod_Block> savedBlocks = new ArrayList<Mod_Block>();

    public Wand(Mod_User user){
        this.pylonType = RealmsBase.getProperties().getIntVal("pylon.type");
        this.pylonHeight = RealmsBase.getProperties().getIntVal("pylon.height");
        this.user = user;
    }

    // Reset wand to default mode
    public final void reset(){
        if (mode != Mode.DEFAULT) {
            workingPolygon.cancelEdit();
            workingPolygon = null;
            mode = Mode.DEFAULT;
            resetAllSavedBlocks();
            RealmsBase.removePlayerWand(user);
        }
    }

    public final void softReset(){
        if (mode != Mode.DEFAULT) {
            workingPolygon.cancelEdit();
            workingPolygon = null;
            mode = Mode.DEFAULT;
            resetAllSavedBlocks();
        }
    }

    // Reset all saved blocks in the column x,z
    private final void removePylon(int x, int z){
        Iterator<Mod_Block> itr = savedBlocks.iterator();
        while (itr.hasNext()) {
            Mod_Block block = itr.next();
            if (block.getX() == x && block.getZ() == z) {
                RealmsBase.getServer().setBlock(block.getX(), block.getY(), block.getZ(), block.getType(), block.getData(), block.getDimension(), block.getWorld());
                itr.remove();
            }
        }
    }

    // Resets all saved blocks
    private final void resetAllSavedBlocks(){
        for (Mod_Block block : savedBlocks) {
            RealmsBase.getServer().setBlock(block.getX(), block.getY(), block.getZ(), block.getType(), block.getData(), block.getDimension(), block.getWorld());
        }
        savedBlocks = new ArrayList<Mod_Block>();
    }

    // Add saved block
    private final void addSavedBlock(Mod_Block block){
        savedBlocks.add(block);
    }

    // Creates a pylon above the specified block.
    // Saves the original blocks into wand's savedBlocks list
    private final void createPylon(Mod_Block block){
        for (int y = 1; y < pylonHeight; y++) {
            Mod_Block storeblock = RealmsBase.getServer().getBlockAt(block.getX(), block.getY() + y, block.getZ(), block.getDimension(), block.getWorld());
            addSavedBlock(storeblock);
            RealmsBase.getServer().setBlock(block.getX(), block.getY() + y, block.getZ(), pylonType, 0, block.getDimension(), block.getWorld());
        }
    }

    private final void createPylon(Point point, int dim, String world){
        for (int y = 1; y < pylonHeight; y++) {
            Mod_Block storeblock = RealmsBase.getServer().getBlockAt(point.x, point.y + y, point.z, dim, world);
            addSavedBlock(storeblock);
            RealmsBase.getServer().setBlock(point.x, point.y + y, point.z, pylonType, 0, dim, world);
        }
    }

    // WAND COMMANDS
    public final boolean wandCommand(String[] command){
        if (command.length < 1) {
            user.sendError("wand.sub.missing1");
            user.sendError("wand.sub.missing2");
            user.sendError("cancel, reset, save, show, edit, setfloor, setceiling");
            return true;
        }
        // Cancel operation
        if (command[0].equalsIgnoreCase("cancel")) {
            if (mode != Mode.POLYGON) {
                user.sendError("not.edit.zone");
                return true;
            }
            reset();
            user.sendMessage("wand.cancel");
            return true;
        }
        // Reset operation
        if (command[0].equalsIgnoreCase("reset")) {
            if (mode != Mode.POLYGON) {
                user.sendError("not.edit.zone");
                return true;
            }
            Zone zone = workingPolygon.getZone();
            softReset();
            user.sendMessage("wand.pylons.removed");
            if (zone.getPolygon() == null) {
                zone.setPolygon(new PolygonArea(zone));
            }
            mode = Mode.POLYGON;
            workingPolygon = zone.getPolygon();
            List<Point> oldVertices = workingPolygon.edit();
            for (Point p : oldVertices) {
                createPylon(p, user.getDimension(), user.getWorld());
            }
            user.sendMessage("wand.edit.ready", zone.getName());
            return true;
        }
        // Save vertices
        if (command[0].equalsIgnoreCase("save")) {
            if (mode != Mode.DEFAULT) {
                if (workingPolygon.workingVerticesCleared()) {
                    user.sendMessage("wand.save.nopylons");
                    workingPolygon.save();
                    reset();
                    return true;
                }
                else {
                    if (!workingPolygon.validPolygon(user)) {
                        user.sendError("polygon.bad");
                        return true;
                    }
                    workingPolygon.save();
                    reset();
                    user.sendMessage("wand.save");
                    return true;
                }
            }
            user.sendError("wand.not.editing");
            return true;
        }
        // Show vertices
        if (command[0].equalsIgnoreCase("show")) {
            int x1, z1, x2, z2;
            PolygonArea thePolygon = null;
            switch (command.length) {
                case 1:
                    user.sendMessage(MCChatForm.CYAN.concat("Usage: /realms wand show <x1,z1 x2,z2> <zone>"));
                    return true;
                case 2:
                    if (mode != Mode.POLYGON) {
                        user.sendMessage(MCChatForm.CYAN.concat("Usage: /realms wand show <x1,z1 x2,z2> <zone>"));
                        return true;
                    }
                    else {
                        thePolygon = workingPolygon;
                    }
                case 3:
                    if (thePolygon == null) {
                        try {
                            Zone zone = ZoneLists.getZoneByName(command[1]);
                            thePolygon = zone.getPolygon();
                        }
                        catch (ZoneNotFoundException znfe) {
                            user.sendError(znfe.getMessage());
                            return true;
                        }
                    }
                    if (thePolygon == null || thePolygon.getVertices().isEmpty()) {
                        user.sendError("zone.no.verticies");
                        return true;
                    }
                    x1 = thePolygon.getVertices().get(0).x;
                    x2 = thePolygon.getVertices().get(0).x;
                    z1 = thePolygon.getVertices().get(0).z;
                    z2 = thePolygon.getVertices().get(0).z;
                    for (Point p : thePolygon.getVertices()) {
                        if (p.x < x1) {
                            x1 = p.x;
                        }
                        if (p.x > x2) {
                            x2 = p.x;
                        }
                        if (p.z < z1) {
                            z1 = p.z;
                        }
                        if (p.z > z2) {
                            z2 = p.z;
                        }
                    }
                    user.sendMessage(MCChatForm.CYAN + "Using bounding coords of: (" + MCChatForm.ORANGE + x1 + MCChatForm.CYAN + "," + MCChatForm.ORANGE + z1 + MCChatForm.CYAN + ") (" + MCChatForm.ORANGE + x2 + MCChatForm.CYAN + "," + MCChatForm.ORANGE + z2 + MCChatForm.CYAN + ")");
                    break;
                case 4:
                    if (mode != Mode.DEFAULT) {
                        user.sendMessage(MCChatForm.CYAN.concat("Wand not in default mode"));
                        return true;
                    }
                    else {
                        thePolygon = workingPolygon;
                    }
                default:
                    try {
                        String[] coord1 = command[1].split(",");
                        String[] coord2 = command[2].split(",");
                        if (coord1.length < 2 || coord2.length < 2) {
                            user.sendMessage(MCChatForm.CYAN.concat("Usage: / realms wand show <x1,z1 x2,z2> <zone>"));
                            return true;
                        }
                        x1 = Integer.parseInt(coord1[0]);
                        z1 = Integer.parseInt(coord1[1]);
                        x2 = Integer.parseInt(coord2[0]);
                        z2 = Integer.parseInt(coord2[1]);
                    }
                    catch (NumberFormatException e) {
                        user.sendMessage(MCChatForm.CYAN.concat("Usage: /realms wand show <x1,z1 x2,z2> <zone>"));
                        return true;
                    }
                    if (thePolygon == null) {
                        try {
                            Zone zone = ZoneLists.getZoneByName(command[3]);
                            thePolygon = zone.getPolygon();
                        }
                        catch (ZoneNotFoundException znfe) {
                            user.sendError(znfe.getMessage());
                            return true;
                        }
                    }
                    break;
            }
            int scalex = (int) Math.floor((Math.abs(x1 - x2) - 20) / 40.) + 1;
            if (scalex < 1) {
                scalex = 1;
            }
            int countx = (int) Math.ceil(Math.abs(x1 - x2) / scalex);
            int scalez = (int) Math.floor((Math.abs(z1 - z2) - 7) / 14.) + 1;
            if (scalez < 1) {
                scalez = 1;
            }
            int countz = (int) Math.ceil(Math.abs(z1 - z2) / scalez);
            if (command.length < 4) {
                x1 = x1 - scalex;
                x2 = x2 + scalex;
                z1 = z1 - scalez;
                z2 = z2 + scalez;
            }
            if (scalex > 1) {
                user.sendMessage(MCChatForm.CYAN + "Using x scaling factor of " + MCChatForm.YELLOW + scalex);
            }
            if (scalez > 1) {
                user.sendMessage(MCChatForm.CYAN + "Using z scaling factor of " + MCChatForm.YELLOW + scalez);
            }
            int i, j = 0;
            for (i = -1; i < countz; i++) {
                boolean color = false;
                boolean first = false;
                int zcoord = i * scalez + z1 + (int) Math.floor(scalez / 2);
                StringBuilder messageString = new StringBuilder(MCChatForm.GREEN.toString());
                if (i == -1) {
                    messageString.append(" ");
                }
                else {
                    messageString.append(Math.abs(zcoord) % 10);
                }
                for (j = 0; j < countx; j++) {
                    int xcoord = j * scalex + x1 + (int) Math.floor(scalex / 2);
                    if (i == -1) {
                        messageString.append(Math.abs(xcoord) % 10);
                        continue;
                    }
                    if (PolygonArea.contains(thePolygon.getVertices(), new Point(xcoord, 64, zcoord), 0, 256)) {
                        if (!color || !first) {
                            messageString.append(MCChatForm.BLUE);
                            color = true;
                            first = true;
                        }
                        messageString.append("X");
                    }
                    else {
                        if (color || !first) {
                            messageString.append(MCChatForm.GRAY);
                            color = false;
                            first = true;
                        }
                        messageString.append("-");
                    }
                }
                user.sendMessage(messageString.toString());
            }
            return true;
        }
        // Edit zone
        if (command[0].equalsIgnoreCase("edit")) {
            // Wand must be in default mode
            if (mode != Mode.DEFAULT) {
                user.sendError("wand.not.default");
                return true;
            }
            // Zone name must be provided
            if (command.length < 2) {
                user.sendError("wand.no.name");
                return true;
            }
            // Cannot edit the "everywhere" zone!
            if (command[1].toUpperCase().startsWith("EVERYWHERE")) {
                user.sendError("everywhere.noedit");
                return true;
            }
            // Get zone
            try {
                Zone zone = ZoneLists.getZoneByName(command[1]);
                if (zone.getPolygon() == null) {
                    zone.setPolygon(new PolygonArea(zone));
                }
                // Zone must be in "saved" mode
                if (!zone.getPolygon().getMode().equalsIgnoreCase("saved")) {
                    user.sendError("zone.inedit");
                    return true;
                }
                // Player must have zoning permission
                if (!zone.permissionCheck(user, PermissionType.ZONING)) {
                    user.sendError("zoning.error");
                    return true;
                }
                if (!zone.getWorld().equals(user.getWorld())) {
                    user.sendError("zone.world.error");
                    return true;
                }
                else if (zone.getDimension() != user.getDimension()) {
                    user.sendError("zone.dimension.error");
                    return true;
                }
                // Passed all checks!
                mode = Mode.POLYGON;
                workingPolygon = zone.getPolygon();
                List<Point> oldVertices = workingPolygon.edit();
                for (Point p : oldVertices) {
                    createPylon(p, user.getDimension(), user.getWorld());
                }
                user.sendMessage("wand.edit.ready", zone.getName());
                return true;
            }
            catch (ZoneNotFoundException znfe) {
                user.sendError(znfe.getMessage());
                return true;
            }
        }
        // setFloor
        if (command[0].equalsIgnoreCase("setfloor") && mode == Mode.POLYGON) {
            if (command.length == 2) {
                try {
                    int floor = Integer.parseInt(command[1]);
                    workingPolygon.setWorkingFloor(floor);
                    user.sendMessage("zone.floor.set", workingPolygon.getZone().getName(), String.valueOf(floor));
                    return true;
                }
                catch (NumberFormatException NFE) {
                    user.sendError("zone.floor.invalid", command[1]);
                    return true;
                }
            }
            else {
                mode = Mode.SET_FLOOR; // wand.setfcmode
                user.sendMessage("wand.setfcmode", "FLOOR", workingPolygon.getZone().getName());
                return true;
            }
        }
        // setCeiling
        if (command[0].equalsIgnoreCase("setceiling") && mode == Mode.POLYGON) {
            if (command.length == 3) {
                try {
                    int ceiling = Integer.parseInt(command[1]);
                    workingPolygon.setWorkingCeiling(ceiling);
                    user.sendMessage("zone.ceiling.set", workingPolygon.getZone().getName(), String.valueOf(ceiling));
                    return true;
                }
                catch (Exception e) {
                    user.sendError("zone.ceiling.invalid", command[1]);
                    return true;
                }
            }
            else {
                mode = Mode.SET_CEILING;
                user.sendMessage("wand.setfcmode", "CEILING", workingPolygon.getZone().getName());
                return true;
            }
        }
        // None of the above
        user.sendError("wand.sub.unknown");
        return true;
    }

    // WAND CLICK
    public final boolean wandClick(Mod_Block block){
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        String world = user.getWorld();
        int dim = user.getDimension();
        // By default wand is in get info mode
        if (mode == Mode.DEFAULT) {
            Zone zone = ZoneLists.getInZone(block);
            user.sendMessage(MCChatForm.CYAN + "--- ZONE: " + MCChatForm.YELLOW + zone.getName() + MCChatForm.CYAN + " ---");
            if (!zone.isEmpty()) {
                user.sendMessage(String.format(verts, zone.getPolygon().getVertices().size()));
                user.sendMessage(String.format(floor_ceiling_height, zone.getPolygon().getFloor(), zone.getPolygon().getCeiling(), zone.getPolygon().getHeight()));
                user.sendMessage(String.format(area_volume, zone.getPolygon().getArea(), zone.getPolygon().getVolume()));
            }
            else if (zone.getName().startsWith("EVERYWHERE")) {
                user.sendMessage(String.format(verts, 0));
                user.sendMessage(String.format(floor_ceiling_height, 0, 256, 256));
                user.sendMessage(String.format(area_volume, Integer.MAX_VALUE, Integer.MAX_VALUE));
            }
            else {
                user.sendMessage(String.format(verts, 0));
                user.sendMessage(String.format(floor_ceiling_height, 0, 0, 0));
                user.sendMessage(String.format(area_volume, 0, 0));
            }

            String[] flags = zone.getFlags(true, true);
            for (String mess : flags) {
                user.sendMessage(mess);
            }
            return true;
        }
        // workingPolygon must not be null for remaining wand actions
        if (workingPolygon == null) {
            user.sendError("wand.nozone");
            reset();
            return true;
        }
        if (workingPolygon.getZone().getWorld() == null) {
            workingPolygon.getZone().setWorld(world);
            workingPolygon.getZone().setDimension(user.getDimension());
        }
        else if (!workingPolygon.getZone().getWorld().equals(world)) {
            user.sendError("wand.click.world.invalid");
            reset();
            return true;
        }
        else if (workingPolygon.getZone().getDimension() != dim) {
            user.sendError("wand.click.dimension.invalid");
            reset();
            return true;
        }
        if (mode == Mode.SET_CEILING) {
            workingPolygon.setWorkingCeiling(y);
            mode = Mode.POLYGON;
            user.sendMessage("zone.ceiling.set", String.valueOf(y));
            user.sendMessage("wand.define", workingPolygon.getZone().getName());
            return true;
        }
        if (mode == Mode.SET_FLOOR) {
            workingPolygon.setWorkingFloor(y);
            mode = Mode.POLYGON;
            user.sendMessage("zone.floor.set", String.valueOf(y));
            user.sendMessage("wand.define", workingPolygon.getZone().getName());
            return true;
        }
        if (mode == Mode.POLYGON) {
            // Remove last vertex
            if (workingPolygon.containsWorkingVertex(block)) {
                workingPolygon.removeWorkingVertex(block);
                removePylon(block.getX(), block.getZ());
                user.sendMessage("vertex.remove", String.valueOf(x), String.valueOf(z));
                return true;
            }
            // Check chests
            for (int i = 0; i < pylonHeight; i++) {
                Mod_Block testblock = RealmsBase.getServer().getBlockAt(block.getX(), block.getY() + i, block.getZ(), dim, world);
                if (testblock.getType() == 54) {
                    user.sendError("pylon.chest");
                    return true;
                }
            }
            // The vertex must be valid
            if (!workingPolygon.validVertex(user, block)) {
                return true;
            }
            List<Point> removedVertices = workingPolygon.addVertex(user, block);
            for (Point p : removedVertices) {
                user.sendMessage("vertex.remove", String.valueOf(p.x), String.valueOf(p.y), String.valueOf(p.z));
                removePylon(p.x, p.z);
            }
            createPylon(block);
            user.sendMessage("vertex.add", String.valueOf(x), String.valueOf(z));
            return true;
        }
        user.sendError("wand.mode.invalid");
        reset();
        return true;
    }
}
