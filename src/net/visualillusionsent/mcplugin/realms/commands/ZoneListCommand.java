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
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Gets the list of Zones", name = "zonelist", usage = "[page#|all]", maxParam = 1)
final class ZoneListCommand extends RealmsCommand {

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        StringBuffer zones = new StringBuffer();
        for (Zone zone : ZoneLists.getZones()) {
            zones.append(zone.getName() + "::");
        }
        String[] zonesplit = zones.toString().split("::");

        int total = (int) Math.ceil(zonesplit.length / 9.0F);
        if (total < 1) {
            total = 1;
        }

        int show = 0;
        try {
            show = Integer.parseInt(args[1]);
        }
        catch (Exception e) {
            show = 1;
        }

        if (show > total || show < 1) {
            show = 1;
        }
        int page = 9 * show;
        int start = 9 * show - 9;
        caller.sendMessage("\u00A7AList all \u00A76ZONES \u00A7A Page \u00A7E" + show + "\u00A7A of \u00A7E" + total);
        for (int index = start; index < page && index < zonesplit.length; index++) {
            caller.sendMessage(ChatColors.ORANGE + zonesplit[index]);
        }
    }

}
