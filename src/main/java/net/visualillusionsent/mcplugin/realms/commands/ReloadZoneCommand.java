/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 */
package net.visualillusionsent.mcplugin.realms.commands;

import net.visualillusionsent.mcmod.interfaces.Mod_Caller;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;
import net.visualillusionsent.mcplugin.realms.zones.ZoneNotFoundException;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Reloads the specified Zone", name = "reloadzone", usage = "<zone|*>", minParam = 1, maxParam = 1, adminReq = true)
final class ReloadZoneCommand extends RealmsCommand{

    @Override
    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = caller.isConsole() ? null : (Mod_User)caller;
        try{
            if(args[0].equals("*") && user == null){
                caller.sendError(RealmsTranslate.transMessage("star.invalid"));
                return;
            }
            final Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            caller.sendMessage(RealmsTranslate.transMessage("zone.reload.wait"));
            new ReloadHandler(caller, zone).start();
        }
        catch(ZoneNotFoundException znfe){
            caller.sendError(znfe.getMessage());
        }
    }

    private final class ReloadHandler extends Thread{

        private final Mod_Caller caller;
        private final Zone zone;

        public ReloadHandler(Mod_Caller caller, Zone zone){
            this.caller = caller;
            this.zone = zone;
        }

        public void run(){
            RealmsBase.getDataSourceHandler().reloadZone(caller, zone);
        }
    }
}
