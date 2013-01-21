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
package net.visualillusionsent.mcplugin.realms.commands;

import net.visualillusionsent.mcmod.interfaces.Mod_Caller;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.runnable.RealmsDefaultPermsSet;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.mcplugin.realms.zones.permission.PermissionType;
import net.visualillusionsent.utils.TaskManager;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Creates a new zone", name = "createzone", usage = "<zone> [parent]", minParam = 1, maxParam = 2)
final class CreateZoneCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;

        if (args.length < 2 && user == null) {
            caller.sendError(RealmsTranslate.transMessage("parent.nospecify"));
        }

        if (args[0].toUpperCase().startsWith("EVERYWHERE")) {
            caller.sendError(RealmsTranslate.transMessage("zname.start.every"));
            return;
        }

        try {
            Zone zone = null;
            try {
                zone = ZoneLists.getZoneByName(args[0]);
            }
            catch (ZoneNotFoundException znfe) {} //It's good to not be found

            if (zone != null) {
                caller.sendError(RealmsTranslate.transformMessage("already.exists", args[0]));
                return;
            }

            Zone parentZone = args.length > 1 ? ZoneLists.getZoneByName(args[1]) : ZoneLists.getEverywhere(user);

            if (user != null && !parentZone.permissionCheck(user, PermissionType.ZONING)) {
                caller.sendError(RealmsTranslate.transformMessage("zoning.error", args[0]));
                return;
            }
            else if (args[0].equalsIgnoreCase("null")) {
                caller.sendError(RealmsTranslate.transMessage("zname.null"));
                return;
            }
            else if (!args[0].matches("[_a-zA-Z0-9\\-]+")) {
                caller.sendError(RealmsTranslate.transMessage("zname.spec.chars"));
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

            caller.sendError(RealmsTranslate.transformMessage("zone.create", args[0], parentZone.getName()));
            caller.sendError(RealmsTranslate.transformMessage("wand.use", args[0]));
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
