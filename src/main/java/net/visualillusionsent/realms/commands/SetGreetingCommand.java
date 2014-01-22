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
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.PermissionType;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Sets the greeting for a zone", name = "setgreeting", usage = "<zone|*> [greeting]")
final class SetGreetingCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller instanceof Mod_User ? (Mod_User) caller : null;
        try {
            if (args[0].equals("*") && user == null) {
                caller.sendError("star.invalid");
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            if (user != null && !zone.permissionCheck(user, PermissionType.MESSAGE)) {
                caller.sendError("zone.perm.fail", "GREETINGS", zone.getName());
                return;
            }
            StringBuilder greeting = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                greeting.append(args[i]);
                greeting.append(" ");
            }
            String msg = greeting.toString().trim();
            zone.setGreeting(msg.isEmpty() ? null : msg);
            caller.sendMessage(msg.isEmpty() ? "msg.rmv" : "msg.set", "GREETING", msg.isEmpty() ? "" : msg.replaceAll("@", ChatFormat.MARKER.toString()));
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }
}
