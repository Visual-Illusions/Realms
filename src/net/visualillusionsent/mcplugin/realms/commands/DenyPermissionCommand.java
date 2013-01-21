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

import net.visualillusionsent.mcmod.interfaces.ChatColors;
import net.visualillusionsent.mcmod.interfaces.Mod_Caller;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.mcplugin.realms.zones.permission.InvaildPermissionTypeException;
import net.visualillusionsent.mcplugin.realms.zones.permission.PermissionType;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Denies a player a permission in a zone", name = "denyperm", usage = "<player> <permission> <zone|*> ['OVERRIDE']", minParam = 3, maxParam = 4)
final class DenyPermissionCommand extends RealmsCommand {

    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;

        try {
            PermissionType type = PermissionType.getTypeFromString(args[1]);
            if (args[2].equals("*")) {
                if (user == null) {
                    caller.sendError(RealmsTranslate.transMessage("star.invalid"));
                    return;
                }
            }
            Zone zone = args[2].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[2]);

            if (user != null && !zone.delegateCheck(user, type)) {
                user.sendError(RealmsTranslate.transformMessage("delegate.perm.fail", type.name(), zone.getName()));
                return;
            }

            if (args[0].contains(",")) {
                caller.sendError(RealmsTranslate.transMessage("player.name.commas"));
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
                caller.sendMessage(RealmsTranslate.transMessage("delegate.warning1"));
                caller.sendError(RealmsTranslate.transMessage("delegate.warning2"));
                caller.sendError(RealmsTranslate.transMessage("delegate.warning3"));
                caller.sendError(RealmsTranslate.transformMessage("delegate.warning4", "deny", args[1], args[2]));
            }

            // Made it past all the checks!
            zone.setPermission(args[0], type, false, override);
            caller.sendMessage(RealmsTranslate.transformMessage("delegate.perm", ChatColors.RED.concat("Denied"), args[0], type.name(), zone.getName()));
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
        catch (InvaildPermissionTypeException ipte) {
            caller.sendError(ipte.getMessage());
        }
    }

}
