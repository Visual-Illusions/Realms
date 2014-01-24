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

import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.permission.PermissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Jason (darkdiplomat)
 */
public class RealmsTabCompleteUtil {
    public static final Pattern flagCmdPattern = Pattern.compile("(adventure|animals|burn|creative|dispense|enderman|explode|fall|fire|flow|healing|physics|pistons|potion|pvp|restricted|sanctuary|starve|suffocate)");
    public static final Pattern greetingCmdPattern = Pattern.compile("set(farewell|greeting)");
    public static final Pattern permCmdPattern = Pattern.compile("(grant|deny)perm");
    public static final Pattern stranglersPattern = Pattern.compile("(reloadzone|deletezone|permlist)");
    
    public static final String[] adminSubs1 = new String[]{ "configreload", "givemepermission", "reloadzone" };
    
    public static final String[] subs1 = new String[]{ 
            "createzone", "deleteperm", "deletezone", "denyperm", "grantperm", "help", "permlist",
            "adventure", "animals", "burn", "creative", "dispense", "enderman", "explode", "fall",
            "setfarewell", "fire", "flow", "setgreeting", "healing", "physics", "pistons", "potion",
            "pvp", "restricted", "sanctuary", "starve", "suffocate", "wand", "zonelist"
    };
    
    public static final String[] flagVal = new String[]{ "on", "off", "inherit" };
    
    public static final String[] zoneNameOrStar(){
        ArrayList<String> zones = new ArrayList<String>();
        zones.add("*");
        for(Zone zone : ZoneLists.getZones()){
            zones.add(zone.getName());
        }
        return zones.toArray(new String[zones.size()]);
    }
    
    public static final String[] permTypes(){
        ArrayList<String> perms = new ArrayList<String>();
        for(PermissionType type : PermissionType.values()){
            perms.add(type.toString());
        }
        return perms.toArray(new String[perms.size()]);
    }

    public static boolean startsWith(String partial, String possible) {
        return possible.regionMatches(true, 0, partial, 0, partial.length());
    }

    public static List<String> matchTo(String[] args, String[] possible) {
        String lastArg = args[args.length - 1];
        ArrayList<String> matches = new ArrayList<String>();

        for (int index = 0; index < possible.length; index++) {
            if (startsWith(lastArg, possible[index])) {
                matches.add(possible[index]);
            }
        }
        return matches;
    }
}
