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
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Gives all permission to zone", name = "givemepermission", usage = "", noConsole = true, adminReq = true)
final class GiveMePermissionCommand extends RealmsCommand{

    @Override
    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = (Mod_User) caller;
        Zone zone = ZoneLists.getInZone(user);
        zone.setPermission(user.getName(), PermissionType.ALL, true, true);
        user.sendMessage("grant.all.perms", zone.getName());
    }
}
