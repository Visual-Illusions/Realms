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

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Deletes a zone", name = "deletezone", usage = "<zone|*>", minParam = 1, maxParam = 1)
final class DeleteZoneCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        try {
            if (args[0].equals("*") && user == null) {
                caller.sendError("star.invalid");
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            if (zone.getName().startsWith("EVERYWHERE")) {
                caller.sendError("delete.everywhere");
                return;
            }
            if (user != null && !zone.getParent().permissionCheck(user, PermissionType.ZONING)) {
                caller.sendError("delete.zone.fail");
                return;
            }
            zone.delete();
            caller.sendMessage("delete.zone", args[0]);
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
            return;
        }
    }
}
