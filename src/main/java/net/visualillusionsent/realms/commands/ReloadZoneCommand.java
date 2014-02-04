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
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Reloads the specified Zone", name = "reloadzone", usage = "<zone|*>", minParam = 1, maxParam = 1, adminReq = true)
final class ReloadZoneCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        Mod_User user = caller.isConsole() ? null : (Mod_User) caller;
        try {
            if (args[0].equals("*") && user == null) {
                caller.sendError("star.invalid");
                return;
            }
            final Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            caller.sendMessage("zone.reload.wait");
            new ReloadHandler(caller, zone).start();
        }
        catch (ZoneNotFoundException znfe) {
            caller.sendError(znfe.getMessage());
        }
    }

    private final class ReloadHandler extends Thread {

        private final Mod_Caller caller;
        private final Zone zone;

        public ReloadHandler(Mod_Caller caller, Zone zone) {
            this.caller = caller;
            this.zone = zone;
        }

        public void run() {
            RealmsBase.getDataSourceHandler().reloadZone(caller, zone);
        }
    }
}
