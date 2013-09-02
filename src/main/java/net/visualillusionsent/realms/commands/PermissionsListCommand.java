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
package net.visualillusionsent.realms.commands;

import java.util.ArrayList;
import java.util.List;

import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsTranslate;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.Permission;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Gets the List of Permissions for a Zone", name = "permlist", usage = "<zone|*> [page#|all]", minParam = 1, maxParam = 2)
final class PermissionsListCommand extends RealmsCommand{

    private final String LIST = MCChatForm.CYAN.concat("List all Permissions attached to Zone: ").concat(MCChatForm.BLUE.toString()).concat("%s");
    private final String PAGE = MCChatForm.CYAN.concat(" Page ").concat(MCChatForm.PINK.toString()).concat("%d").concat(MCChatForm.CYAN.toString()).concat(" of ").concat(MCChatForm.PURPLE.toString()).concat("%d");
    private final String INFO_LINE = MCChatForm.ORANGE.concat("PLAYER/GROUP ").concat(MCChatForm.YELLOW.toString()).concat("PERMISSION").concat(MCChatForm.GREEN.toString()).concat(" GRANTED").concat(MCChatForm.CYAN.toString()).concat("/").concat(MCChatForm.RED.toString()).concat("DENIED ").concat(MCChatForm.TURQUOISE.toString()).concat("OVERRIDDEN");

    final void execute(Mod_Caller caller, String[] args){
        Mod_User user = caller.isConsole() ? null : (Mod_User)caller;
        try{
            if(args[0].equals("*") && user == null){
                caller.sendError(RealmsTranslate.transMessage("star.invalid"));
                return;
            }
            Zone zone = args[0].equals("*") ? ZoneLists.getInZone(user) : ZoneLists.getZoneByName(args[0]);
            List<String> perms = new ArrayList<String>();
            for(Permission p : zone.getPerms()){
                perms.add(p.toString());
            }
            String[] permsplit = new String[0];
            permsplit = perms.toArray(permsplit);
            if(args.length > 1 && args[1].toLowerCase().equals("all")){
                caller.sendMessage(String.format(LIST, zone.getName()));
                caller.sendMessage(INFO_LINE);
                for(int index = 0; index < permsplit.length; index++){
                    StringBuilder toSend = new StringBuilder();
                    String[] permission = permsplit[index].split(",");
                    toSend.append(MCChatForm.ORANGE).append(permission[0]).append(MCChatForm.YELLOW).append(permission[1].toUpperCase());
                    if(permission[3].equals("0")){
                        toSend.append(MCChatForm.LIGHT_RED).append(" DENIED ");
                    }
                    else{
                        toSend.append(MCChatForm.GREEN).append(" GRANTED ");
                    }
                    if(permission[4].equals("1")){
                        toSend.append(MCChatForm.TURQUOISE).append(" TRUE");
                    }
                    caller.sendMessage(toSend.toString());
                }
            }
            else{
                int total = (int)Math.ceil(permsplit.length / 5.0F);
                int show = 1;
                if(total < 1){
                    total = 1;
                }
                try{
                    show = Integer.parseInt(args[1]);
                }
                catch(Exception e){ //Either NumberFormat or ArrayIndexOutOfBounds
                    show = 1;
                }
                if(show > total || show < 1){
                    show = 1;
                }
                int page = 5 * show;
                int start = 5 * show - 5;
                caller.sendMessage(String.format(LIST, zone.getName()).concat(String.format(PAGE, show, total)));
                caller.sendMessage(INFO_LINE);
                for(int index = start; index < page && index < permsplit.length; index++){
                    StringBuilder toSend = new StringBuilder();
                    String[] permission = permsplit[index].split(",");
                    toSend.append(MCChatForm.ORANGE).append(permission[0]).append(" ").append(MCChatForm.YELLOW).append(permission[1].toUpperCase());
                    if(permission[2].equals("false")){
                        toSend.append(MCChatForm.RED).append(" DENIED ");
                    }
                    else{
                        toSend.append(MCChatForm.GREEN).append(" GRANTED ");
                    }
                    if(permission[3].equals("true")){
                        toSend.append(MCChatForm.TURQUOISE).append(" TRUE");
                    }
                    caller.sendMessage(toSend.toString());
                }
            }
        }
        catch(ZoneNotFoundException znfe){
            caller.sendError(znfe.getMessage());
        }
    }
}
