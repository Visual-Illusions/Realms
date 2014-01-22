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
import net.visualillusionsent.realms.RealmsTranslate;
import net.visualillusionsent.realms.zones.InvaildZoneFlagException;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneFlag;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Sets a zones Adventure Flag", name = "adventure", usage = "<zone|*> <on|off|inherit>", minParam = 2, maxParam = 2)
final class SetAdventureFlagCommand extends RealmsCommand{

    @Override
    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        try {
            ZoneFlag theFlag = ZoneFlag.getZoneFlag(args[1]);
            if (args[0].equals("*") && user == null) {
                caller.sendError(RealmsTranslate.transMessage("star.invalid"));
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            if (user != null && !zone.permissionCheck(user, PermissionType.ENVIRONMENT)) {
                caller.sendError("zone.perm.fail", "ADVENTURE", zone.getName());
                return;
            }
            if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(ZoneFlag.INHERIT)) {
                caller.sendError("zoneflag.set.fail", zone.getName());
                return;
            }
            if (zone.getCreative()) {
                caller.sendError("creative.adventure.noset", "ADVENTURE", "CREATIVE");
                return;
            }
            zone.setAdventure(theFlag);
            caller.sendMessage("zoneflag.set", "ADVENTURE", (zone.getAdventure() ? ChatFormat.GREEN.concat("ON") : ChatFormat.RED.concat("OFF")).concat(zone.getAbsoluteAdventure().equals(ZoneFlag.INHERIT) ? ChatFormat.PINK.concat("(INHERITED)") : ""), zone.getName());
        }
        catch (InvaildZoneFlagException ife) {
            caller.sendError("zoneflag.invalid", "ADVENTURE");
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
