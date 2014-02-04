/*
 * This file is part of Realms.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms.commands;

import net.visualillusionsent.minecraft.plugin.ChatFormat;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.InvaildPermissionTypeException;
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Grants a player a permission in a zone", name = "grantperm", usage = "<player> <permission> <zone|*> ['OVERRIDE']", minParam = 3, maxParam = 4)
final class GrantPermissionCommand extends RealmsCommand {

    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        try {
            PermissionType type = PermissionType.getTypeFromString(args[1]);
            if (args[2].equals("*")) {
                if (user == null) {
                    caller.sendError("star.invalid");
                    return;
                }
            }
            Zone zone = args[2].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[2]);
            if (user != null && !zone.delegateCheck(user, type)) {
                user.sendError("delegate.perm.fail", type.name(), zone.getName());
                return;
            }
            if (args[0].contains(",")) {
                caller.sendError("player.name.commas");
                return;
            }
            // Give warning message for default group permissions
            boolean override = args.length == 4 && args[3].toUpperCase().equals("OVERRIDE");
            String defaultGroupName = RealmsBase.getServer().getDefaultGroupName();
            String tempString = "";
            if (args[0].startsWith("g:")) {
                tempString = args[0].replaceAll("g:", "");
            }
            if (!override && (args[0].equalsIgnoreCase(defaultGroupName) || defaultGroupName.equalsIgnoreCase(tempString))) {
                caller.sendMessage("delegate.warning1");
                caller.sendError("delegate.warning2");
                caller.sendError("delegate.warning3");
                caller.sendError("delegate.warning4", "deny", args[1], args[2]);
            }
            // Made it past all the checks!
            zone.setPermission(args[0], type, true, override);
            caller.sendMessage("delegate.perm", ChatFormat.GREEN.concat("Granted"), args[0], type.name(), zone.getName());
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
        catch (InvaildPermissionTypeException ipte) {
            caller.sendError(ipte.getMessage());
        }
    }
}
