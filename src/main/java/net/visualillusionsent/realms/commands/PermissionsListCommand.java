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

import java.util.ArrayList;
import java.util.List;

import net.visualillusionsent.minecraft.plugin.ChatFormat;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsTranslate;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.permission.Permission;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Gets the List of Permissions for a Zone", name = "permlist", usage = "<zone|*> [page#|all]", minParam = 1, maxParam = 2)
final class PermissionsListCommand extends RealmsCommand{

    private final String LIST = ChatFormat.CYAN.concat("List all Permissions attached to Zone: ").concat(ChatFormat.BLUE.toString()).concat("%s");
    private final String PAGE = ChatFormat.CYAN.concat(" Page ").concat(ChatFormat.PINK.toString()).concat("%d").concat(ChatFormat.CYAN.toString()).concat(" of ").concat(ChatFormat.PURPLE.toString()).concat("%d");
    private final String INFO_LINE = ChatFormat.ORANGE.concat("PLAYER/GROUP ").concat(ChatFormat.YELLOW.toString()).concat("PERMISSION").concat(ChatFormat.GREEN.toString()).concat(" GRANTED").concat(ChatFormat.CYAN.toString()).concat("/").concat(ChatFormat.RED.toString()).concat("DENIED ").concat(ChatFormat.TURQUOISE.toString()).concat("OVERRIDDEN");

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
                    toSend.append(ChatFormat.ORANGE).append(permission[0]).append(ChatFormat.YELLOW).append(permission[1].toUpperCase());
                    if(permission[3].equals("0")){
                        toSend.append(ChatFormat.LIGHT_RED).append(" DENIED ");
                    }
                    else{
                        toSend.append(ChatFormat.GREEN).append(" GRANTED ");
                    }
                    if(permission[4].equals("1")){
                        toSend.append(ChatFormat.TURQUOISE).append(" TRUE");
                    }
                    caller.sendMessage(toSend.toString());
                }
            }
            else{
                int total = (int)Math.ceil(permsplit.length / 5.0F);
                int show;
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
                    toSend.append(ChatFormat.ORANGE).append(permission[0]).append(" ").append(ChatFormat.YELLOW).append(permission[1].toUpperCase());
                    if(permission[2].equals("false")){
                        toSend.append(ChatFormat.RED).append(" DENIED ");
                    }
                    else{
                        toSend.append(ChatFormat.GREEN).append(" GRANTED ");
                    }
                    if(permission[3].equals("true")){
                        toSend.append(ChatFormat.TURQUOISE).append(" TRUE");
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
