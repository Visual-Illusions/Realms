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
package net.visualillusionsent.realms.commands;

import net.visualillusionsent.minecraft.plugin.ChatFormat;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Sets the farewell for a zone", name = "setfarewell", usage = "<zone|*> [farewell]")
final class SetFarewellCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        try {
            if (args[0].equals("*") && user == null) {
                caller.sendError("star.invalid");
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            if (user != null && !zone.permissionCheck(user, PermissionType.MESSAGE)) {
                caller.sendError("zone.perm.fail", "FAREWELLS", zone.getName());
                return;
            }
            StringBuilder farewell = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                farewell.append(args[i]);
                farewell.append(" ");
            }
            String msg = farewell.toString().trim();
            zone.setFarewell(msg.isEmpty() ? null : msg);
            caller.sendMessage(msg.isEmpty() ? "msg.rmv" : "msg.set", "FAREWELL", msg.isEmpty() ? "" : msg.replaceAll("@", ChatFormat.MARKER.toString()));
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
