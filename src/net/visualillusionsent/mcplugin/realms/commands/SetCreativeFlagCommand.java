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
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.zones.InvaildZoneFlagException;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneFlag;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.mcplugin.realms.zones.permission.PermissionType;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Sets a zones Creative Flag", name = "creative", usage = "<zone|*> <on|off|inherit>", minParam = 2, maxParam = 2)
final class SetCreativeFlagCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;

        try {
            ZoneFlag theFlag = ZoneFlag.getZoneFlag(args[1]);
            if (args[0].equals("*") && user == null) {
                caller.sendError(RealmsTranslate.transMessage("star.invalid"));
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);

            if (user != null && !zone.permissionCheck(user, PermissionType.ENVIRONMENT)) {
                caller.sendError(RealmsTranslate.transformMessage("zone.perm.fail", "CREATIVE", zone.getName()));
                return;
            }

            if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(ZoneFlag.INHERIT)) {
                caller.sendError(RealmsTranslate.transformMessage("zoneflag.set.fail", zone.getName()));
                return;
            }

            if (zone.getAdventure()) {
                caller.sendError(RealmsTranslate.transformMessage("creative.adventure.noset", "CREATIVE", "ADVENTURE"));
                return;
            }

            zone.setCreative(theFlag);
            caller.sendMessage(RealmsTranslate.transformMessage("zoneflag.set", "CREATIVE", (zone.getCreative() ? ChatColors.GREEN.concat("ON") : ChatColors.RED.concat("OFF")).concat(zone.getAbsoluteCreative().equals(ZoneFlag.INHERIT) ? ChatColors.PINK.concat("(INHERITED)") : ""), zone.getName()));

        }
        catch (InvaildZoneFlagException ife) {
            caller.sendError(RealmsTranslate.transformMessage("zoneflag.invalid", "CREATIVE"));
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
