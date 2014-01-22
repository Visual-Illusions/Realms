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

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.InvaildPermissionTypeException;
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Deletes a player's permission in a zone", name = "deleteperm", usage = "<player> <permission> <zone|*>", minParam = 3, maxParam = 3)
final class DeletePermissionCommand extends RealmsCommand{

    @Override
    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        try {
            PermissionType type = PermissionType.getTypeFromString(args[1]);
            if (args[2].equals("*") && user == null) {
                caller.sendError("star.invalid");
                return;
            }
            Zone zone = args[2].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[2]);
            if (user != null && !zone.delegateCheck(user, type)) {
                caller.sendError("delegate.fail", type.name(), zone.getName());
                return;
            }
            zone.deletePermission(args[0], type);
            caller.sendMessage("delete.perm.success", args[0], type.name(), zone.getName());
        }
        catch (InvaildPermissionTypeException ipte) {
            caller.sendError(ipte.getMessage());
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
