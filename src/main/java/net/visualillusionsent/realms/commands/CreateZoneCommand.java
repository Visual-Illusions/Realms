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

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.tasks.RealmsDefaultPermsSet;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.PermissionType;
import net.visualillusionsent.utils.TaskManager;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Creates a new zone", name = "createzone", usage = "<zone> [parent]", minParam = 1, maxParam = 2)
final class CreateZoneCommand extends RealmsCommand{

    @Override
    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        if (args.length < 2 && user == null) {
            caller.sendError("parent.nospecify");
        }
        if (args[0].toUpperCase().startsWith("EVERYWHERE")) {
            caller.sendError("zname.start.every");
            return;
        }
        try {
            Zone zone = null;
            try {
                zone = ZoneLists.getZoneByName(args[0]);
            }
            catch (ZoneNotFoundException znfe) {} // It's good to not be found
            if (zone != null) {
                caller.sendError("already.exists", args[0]);
                return;
            }
            Zone parentZone = args.length > 1 ? ZoneLists.getZoneByName(args[1]) : ZoneLists.getEverywhere(user);
            if (user != null && !parentZone.permissionCheck(user, PermissionType.ZONING)) {
                caller.sendError("zoning.error", args[0]);
                return;
            }
            else if (args[0].equalsIgnoreCase("null")) {
                caller.sendError("zname.null");
                return;
            }
            else if (!args[0].matches("[_a-zA-Z0-9\\-]+")) {
                caller.sendError("zname.spec.chars");
                return;
            }
            // Made it past all the checks, create the zone!
            zone = new Zone(args[0], parentZone, parentZone.getWorld(), parentZone.getDimension());
            if (user != null) {
                zone.setPermission(user.getName(), PermissionType.ALL, true, true);
            }
            else {
                TaskManager.executeTask(new RealmsDefaultPermsSet(zone));
            }
            caller.sendError("zone.create", args[0], parentZone.getName());
            caller.sendError("wand.use", args[0]);
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
