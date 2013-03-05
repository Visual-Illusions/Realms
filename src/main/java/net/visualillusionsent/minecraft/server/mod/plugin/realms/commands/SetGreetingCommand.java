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
package net.visualillusionsent.minecraft.server.mod.plugin.realms.commands;

import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsTranslate;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.Zone;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.ZoneLists;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.permission.PermissionType;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Sets the greeting for a zone", name = "setgreeting", usage = "<zone|*> [greeting]")
final class SetGreetingCommand extends RealmsCommand{

    @Override
    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = caller instanceof Mod_User ? (Mod_User)caller : null;
        try{
            if(args[0].equals("*") && user == null){
                caller.sendError(RealmsTranslate.transMessage("star.invalid"));
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            if(user != null && !zone.permissionCheck(user, PermissionType.MESSAGE)){
                caller.sendError(RealmsTranslate.transformMessage("zone.perm.fail", "GREETINGS", zone.getName()));
                return;
            }
            StringBuilder greeting = new StringBuilder();
            for(int i = 1; i < args.length; i++){
                greeting.append(args[i]);
                greeting.append(" ");
            }
            String msg = greeting.toString().trim();
            zone.setGreeting(msg.isEmpty() ? null : msg);
            caller.sendMessage(msg.isEmpty() ? RealmsTranslate.transformMessage("msg.rvm", "GREETING") : RealmsTranslate.transformMessage("msg.set", "GREETING", msg.replaceAll("@", MCChatForm.MARKER.toString())));
        }
        catch(ZoneNotFoundException znfe){
            caller.sendError(znfe.getMessage());
        }
    }
}
