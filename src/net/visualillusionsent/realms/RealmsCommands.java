package net.visualillusionsent.realms;

import net.visualillusionsent.realms.io.InvaildZoneFlagException;
import net.visualillusionsent.realms.io.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ChatColor;

/**
 * Realms Player Commands
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public enum RealmsCommands {
    
    /**
     * Gives Admins permission over a zone
     */
    GIVEMEPERMISSION ("givemepermission", "") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(player.isAdmin()){
                Zone zone = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimension()), player);
                zone.setPermission(player.getName(), Permission.PermType.ALL, true, true);
                player.sendMessage(ChatColor.CYAN+"Granted you "+ChatColor.YELLOW+"'ALL'"+ChatColor.CYAN+" permission to zone "+ChatColor.ORANGE+"'"+zone.getName()+"'");
                zone.save();
                return true;
            }
            return false;
        }
    },
    
    /**
     * Deletes a Zone
     */
    DELETEZONE ("deletezone", "<zone name>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = command[0];
            if(zoneName.toUpperCase().startsWith("EVERYWHERE")){
                player.notify("You cannot delete the "+ChatColor.ORANGE+"'EVERYWHERE'"+ChatColor.LIGHT_RED+" zone!");
                return true;
            }
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(zone.getParent() == null){
                    player.notify("The zone "+ChatColor.ORANGE+"'"+zoneName+"'"+ChatColor.LIGHT_RED+" does not have a parent zone!");
                    return true;
                }
                if(!zone.getParent().permissionCheck(player, Permission.PermType.ZONING)){
                    player.notify("You do not have permission to delete this zone!");
                    return true;
                }
                zone.delete();
                player.sendMessage(ChatColor.CYAN+"Deleted "+ChatColor.ORANGE+"'"+zoneName+"'"+ChatColor.CYAN+", moved all children to parent zone.");
                return true;
            }
            catch(ZoneNotFoundException ZNFE){
                player.notify(String.format(NFE, zoneName));
                return true;
            }
        }
    },
    
    /**
     * Sets the Greeting Message
     */
    SETGREETING ("setgreeting", "<zone|*> [greeting]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player), player).getName();
            if(!command[0].equals("*")){
                zoneName = command[0];
            }
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.MESSAGE)){
                    player.notify("You do not have permission to set the "+ChatColor.ORANGE+"GREETING"+ChatColor.LIGHT_RED+" of this zone!");
                    return true;
                }
                StringBuilder greeting = new StringBuilder();
                for(int i = 3; i < command.length; i++) {
                    if(command[i].contains(",")){
                        player.sendMessage(ChatColor.ORANGE+"GREETINGS"+ChatColor.LIGHT_RED+" cannot contain "+ChatColor.YELLOW+"COMMAS"+ChatColor.LIGHT_RED+"!");
                        return true;
                    }
                    greeting.append(command[i]);
                }
                zone.setGreeting(greeting.length() == 0 ? null : greeting.toString().trim());
                player.sendMessage(ChatColor.ORANGE+"GREETING "+ChatColor.CYAN+" set to "+ChatColor.WHITE+(greeting.length() == 0 ? "null" : greeting.toString().trim().replace("@", "\u00A7")));
                zone.save();
                return true;
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
        }
    },
    
    /**
     * Sets the Farewell Message
     */
    SETFAREWELL ("setfarewell", "<zone|*> [greeting]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player), player).getName();
            if(!command[0].equals("*")){
                zoneName = command[0];
            }
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.MESSAGE)){
                    player.notify("You do not have permission to set the "+ChatColor.ORANGE+"FAREWELL"+ChatColor.LIGHT_RED+" of this zone!");
                    return true;
                }
                StringBuilder farewell = new StringBuilder();
                for(int i = 3; i < command.length; i++) {
                    if(command[i].contains(",")){
                        player.sendMessage(ChatColor.ORANGE+"GREETINGS"+ChatColor.LIGHT_RED+" cannot contain "+ChatColor.YELLOW+"COMMAS"+ChatColor.LIGHT_RED+"!");
                        return true;
                    }
                    farewell.append(command[i]);
                }
                zone.setFarewell(farewell.length() == 0 ? null : farewell.toString().trim());
                player.sendMessage(ChatColor.ORANGE+"GREETING "+ChatColor.CYAN+" set to "+ChatColor.WHITE+(farewell.length() == 0 ? "null" : farewell.toString().trim().replace("@", "\u00A7")));
                zone.save();
                return true;
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
        }
    },
    
    /**
     * Displays a list of Permissions for the zone
     */
    PERMISSION ("permission", "<zone|*> [page#]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player), player).getName();
            if(!command[0].equals("*")){
                zoneName = command[0];
            }
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                StringBuffer perm = new StringBuffer();
                for(Permission p : zone.getPerms()){
                    perm.append(p.toString() + "::");
                }
                String[] permsplit = perm.toString().split("::");
                int total = 0, c = 1;
                for (int pl = permsplit.length; pl > 5; pl -= 5){
                    total++;
                    c = pl;
                }
                if (c > 0) total++;
                int show = 1;
                try{
                    show = Integer.parseInt(command[1]);
                }
                catch (Exception e){
                    show = 1;
                }
                if (show > total || show < 1){
                    show = 1;
                }
                int page = 5 * show;
                int start = 5 * show - 5;
                player.sendMessage(ChatColor.CYAN+"List all permissions attached to "+ChatColor.ORANGE+zoneName+ChatColor.CYAN+" Page "+ChatColor.YELLOW+show+ChatColor.CYAN+" of "+ChatColor.YELLOW+total);
                player.sendMessage(ChatColor.ORANGE+"PLAYER/GROUP "+ChatColor.YELLOW+"PERMISSION"+ChatColor.GREEN+" GRANTED"+ChatColor.CYAN+"/"+ChatColor.LIGHT_RED+"DENIED "+ChatColor.TURQUOISE+"OVERRIDDEN");
                for (int i = start; i < page && i < permsplit.length; i++){
                    String grantdeny = ChatColor.GREEN+"GRANTED";
                    String override = "";
                    String[] permission = permsplit[i].split(",");
                    if (permission[3].equals("0")){
                        grantdeny = ChatColor.LIGHT_RED+"DENIED";
                    }
                    if (permission[4].equals("1")){
                        override = ChatColor.TURQUOISE+"TRUE";
                    }
                    player.sendMessage(ChatColor.ORANGE+permission[0]+" "+ChatColor.YELLOW+permission[1].toUpperCase()+" "+grantdeny+" "+override);
                }
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
            return true;
        }
    },
    
    /**
     * Deletes a Player Permission
     */
    DELETE ("delete", "<playername> <PermType> [zone]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(2, command)){
                player.notify(getUsage());
                return true;
            }
            String playerName = command[0];
            Permission.PermType type = null;
            try {
                type = Permission.PermType.getTypeFromString(command[2]);
            } catch (InvaildPermissionTypeException IPTE) {
                player.notify("The type "+ChatColor.ORANGE+"'"+command[2]+"'"+ChatColor.LIGHT_RED+" is not valid!");
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player), player).getName();
            if(command.length > 2){
                zoneName = command[2];
            }
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.delegateCheck(player, type)){
                    player.notify("You do not have permission to delete "+ChatColor.ORANGE+type+ChatColor.LIGHT_RED+" permissions in the zone "+ChatColor.ORANGE+zone.getName());
                    return true;
                }
                zone.deletePermission(playerName, type);
                player.sendMessage(ChatColor.CYAN+"Deleted all of "+ChatColor.ORANGE+playerName+"'s"+ChatColor.CYAN+" permissions to "+ChatColor.ORANGE+type+ChatColor.CYAN+" in "+ChatColor.ORANGE+zone.getName());
                return true;
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
        }
    },
    
    /**
     * Grants a Player Permission
     */
    GRANT ("grant", "<playername> <permissiontype> <zone> [override]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                player.notify(getUsage());
                return true;
            }
            return rhandle.doGrantDeny(player, command, true);
        }
    },
    /**
     * Denies a Player Permission
     */
    DENY ("deny", "<playername> <permissiontype> <zone> [override]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                player.notify(getUsage());
                return true;
            }
            return rhandle.doGrantDeny(player, command, false);
        }
    },
    
    /**
     * Creates a new Zone
     */
    CREATEZONE ("createzone", "<zone> [parentzone]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = command[0];
            String parentZoneName = "EVERYWHERE-"+player.getWorldName()+"-DIM"+player.getDimIndex();
            
            if(zoneName.toUpperCase().startsWith("EVERYWHERE")){
                player.notify("A new zone cannot start with 'EVERYWHERE'!");
                return true;
            }
            
            if(command.length > 1){
                parentZoneName = command[1];
            }
            
            try{
                Zone parentZone = ZoneLists.getZoneByName(parentZoneName);
                for(Zone zone : ZoneLists.getZones()){
                    if(zone.getName().equalsIgnoreCase(zoneName)){
                        player.notify("A zone with the name "+ChatColor.ORANGE+"'"+zoneName+"'"+ChatColor.LIGHT_RED+" already exists!");
                        return true;
                    }
                }
                
                if(!parentZone.permissionCheck(player, Permission.PermType.ZONING)){
                    player.notify("You do not have "+ChatColor.YELLOW+"ZONING"+ChatColor.LIGHT_RED+" permission within "+ChatColor.ORANGE+parentZone.getName());
                    return true;
                }
                else if (zoneName.equalsIgnoreCase("null")){
                    player.notify("Zones cannot be named "+ChatColor.ORANGE+"NULL"+ChatColor.LIGHT_RED+"!");
                    return true;
                }
                else if(!zoneName.matches("[_a-zA-Z0-9\\-]+")){
                    player.notify(ChatColor.ORANGE+"ZONE NAMES"+ChatColor.LIGHT_RED+" cannot contain "+ChatColor.YELLOW+"'SPECIAL CHARACTERS'"+ChatColor.LIGHT_RED+"!");
                    return true;
                }
                // Made it past all the checks!
                Zone zone = new Zone(rhandle, zoneName, parentZone, player.getWorldName(), player.getDimIndex());
                zone.setPermission(player.getName(), Permission.PermType.ALL, true, true);
                player.sendMessage(ChatColor.CYAN+"Created zone "+ChatColor.ORANGE+zoneName+ChatColor.CYAN+" within "+ChatColor.ORANGE+parentZoneName);
                player.sendMessage(ChatColor.CYAN+"Use "+ChatColor.YELLOW+"'/wand edit "+ChatColor.ORANGE+zoneName+"'"+ChatColor.CYAN+" to edit this zone's area.");
                return true;
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
        }
    },
    
    /**
     * Sets PVP Zone Flag
     */
    PVP ("pvp", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.COMBAT)){
                    player.notify(String.format(NOPERMWITHIN, "PVP", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setPVP(theFlag);
                player.sendMessage(String.format(FLAGSET, "PVP", (zone.getPVP() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsolutePVP().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "PVP"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets SANCTUARY Zone Flag
     */
    SANCTUARY ("sanctuary", "[zone] <on|off|inherit>") {        
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.COMBAT)){
                    player.notify(String.format(NOPERMWITHIN, "SANCTUARY", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setSanctuary(theFlag);
                player.sendMessage(String.format(FLAGSET, "SANCTUARY", (zone.getSanctuary() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteSanctuary().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "SANCTUARY"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets CREEPER Zone Flag
     */
    CREEPER ("creeper", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.COMBAT)){
                    player.notify(String.format(NOPERMWITHIN, "CREEPER", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setCreeper(theFlag);
                player.sendMessage(String.format(FLAGSET, "CREEPER", (zone.getCreeper() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteCreeper().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "CREEPER"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets POTION Zone Flag
     */
    POTION ("potion", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.COMBAT)){
                    player.notify(String.format(NOPERMWITHIN, "POTION", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setPotion(theFlag);
                player.sendMessage(String.format(FLAGSET, "POTION", (zone.getPotion() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsolutePotion().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "POTION"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets GHAST Zone Flag
     */
    GHAST ("ghast", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.COMBAT)){
                    player.notify(String.format(NOPERMWITHIN, "GHAST", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setGhast(theFlag);
                player.sendMessage(String.format(FLAGSET, "GHAST", (zone.getGhast() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteGhast().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "GHAST"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets FALL Zone Flag
     */
    FALL ("fall", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "FALL", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setFall(theFlag);
                player.sendMessage(String.format(FLAGSET, "FALL", (zone.getFall() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteFall().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "FALL"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets SUFFOCATE Zone Flag
     */
    SUFFOCATE ("suffocate", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "SUFFOCATE", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setSuffocate(theFlag);
                player.sendMessage(String.format(FLAGSET, "SUFFOCATE", (zone.getFall() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteSuffocate().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "SUFFOCATE"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets FIRE Zone Flag
     */
    FIRE ("fire", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "FIRE", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setFire(theFlag);
                player.sendMessage(String.format(FLAGSET, "FIRE", (zone.getFire() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteFire().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "FIRE"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets ANIMALS Zone Flag
     */
    ANIMALS ("animals", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "ANIMALS", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setAnimals(theFlag);
                player.sendMessage(String.format(FLAGSET, "ANIMALS", (zone.getAnimals() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteAnimals().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "ANIMALS"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets PHYSICS Zone Flag
     */
    PHYSICS ("physics", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "PHYSICS", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setPhysics(theFlag);
                player.sendMessage(String.format(FLAGSET, "PHYSICS", (zone.getPhysics() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsolutePhysics().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "PHYSICS"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets CREATIVE Zone Flag
     */
    CREATIVE ("creative", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "CREATIVE", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setCreative(theFlag);
                player.sendMessage(String.format(FLAGSET, "CREATIVE", (zone.getCreative() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteCreative().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "CREATIVE"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets PISTONS Zone Flag
     */
    PISTONS ("pistons", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "PISTONS", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setPistons(theFlag);
                player.sendMessage(String.format(FLAGSET, "PISTONS", (zone.getPistons() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsolutePistons().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "PISTONS"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets ENDERMAN Zone Flag
     */
    ENDERMAN ("enderman", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "ENDERMAN", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setEnderman(theFlag);
                player.sendMessage(String.format(FLAGSET, "ENDERMAN", (zone.getEnderman() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteEnderman().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "ENDERMAN"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets FLOW Zone Flag
     */
    FLOW ("flow", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "FLOW", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setFlow(theFlag);
                player.sendMessage(String.format(FLAGSET, "FLOW", (zone.getFlow() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteFlow().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "FLOW"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets SPREAD Zone Flag
     */
    SPREAD ("spread", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "SPREAD", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setSpread(theFlag);
                player.sendMessage(String.format(FLAGSET, "SPREAD", (zone.getSpread() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteSpread().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "SPREAD"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets HEALING Zone Flag
     */
    HEALING ("healing", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "HEALING", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setHealing(theFlag);
                player.sendMessage(String.format(FLAGSET, "HEALING", (zone.getHealing() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteHealing().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "HEALING"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets TNT Zone Flag
     */
    TNT ("tnt", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "TNT", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setTNT(theFlag);
                player.sendMessage(String.format(FLAGSET, "TNT", (zone.getTNT() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteTNT().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "TNT"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets STARVE Zone Flag
     */
    STARVE ("starve", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "STARVE", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setStarve(theFlag);
                player.sendMessage(String.format(FLAGSET, "STARVE", (zone.getStarve() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteStarve().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "STARVE"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets RESTRICTED Zone Flag
     */
    RESTRICTED ("restricted", "[zone] <on|off|inherit>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(1, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = ZoneLists.getZone(rhandle.getEverywhere(player.getWorldName(), player.getDimIndex()), player).getName();
            try{
                Zone.ZoneFlag theFlag;
                if(command.length > 1){
                    zoneName = command[0];
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[1].toUpperCase());
                }
                else{
                    theFlag = Zone.ZoneFlag.getZoneFlag(command[0].toUpperCase());
                }
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(!zone.permissionCheck(player, Permission.PermType.ENVIRONMENT)){
                    player.notify(String.format(NOPERMWITHIN, "RESTRICTED", zoneName));
                    return true;
                }
                
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    player.notify(String.format(NOSET, zoneName));
                    return true;
                }
                zone.setRestricted(theFlag);
                player.sendMessage(String.format(FLAGSET, "RESTRICTED", (zone.getRestricted() ? "\u00A72ON " : "\u00A74OFF ")+(zone.getAbsoluteRestricted().equals(Zone.ZoneFlag.INHERIT) ? "\u00A7D(INHERITED)" : ""), zoneName ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                player.notify(String.format(INVALIDFLAG, "RESTRICTED"));
            } 
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Displays Zone's COMBAT Settings
     */
    COMBAT ("combat", "<zone>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = command[2];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                player.sendMessage("\u00A7E'COMBAT'\u00A7B settings for Zone: \u00A76"+zoneName);
                String[] comb = zone.getFlags(true, false);
                for(String mess : comb){
                    player.sendMessage(mess);
                }
                return true;
            }
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
        }
    },
    /**
     * Displays Zone's ENVIRONMENT Settings
     */
    ENVIRONMENT ("environment", "<zone>") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                player.notify(getUsage());
                return true;
            }
            String zoneName = command[2];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                String[] enviro = zone.getFlags(false, true);
                for(String mess : enviro){
                    player.sendMessage(mess);
                }
            }
            catch (ZoneNotFoundException ZNFE) {
                player.notify(String.format(NFE, zoneName));
                return true;
            }
            return true;
        }
    },
    /**
     * Check Realms' Version
     */
    VERSION ("version", "") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            player.sendMessage("\u00A76----- \u00A77Realms v"+rhandle.getVersion()+" by \u00A7ADarkDiplomat \u00A76-----");
            if(!rhandle.isLatest()){
                player.sendMessage("\u00A76----- \u00A77Update Availible: "+rhandle.getCurrent()+" \u00A76-----");
            }
            else{
                player.sendMessage("\u00A76----- \u00A77Latest Version is Installed\u00A76 -----");
            }
            return true;
        }
    },
    /**
     * Displays list of All Zones
     */
    LIST ("list", "[page#]") {
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            StringBuffer zones = new StringBuffer();
            for(Zone zone : ZoneLists.getZones()){
                zones.append(zone.getName() + "::");
            }
            String[] zonesplit = zones.toString().split("::");
            int total = 0, c = 1;
            for (int pl = zonesplit.length; pl > 9; pl -= 9){
                total++;
                c = pl;
            }
            if (c > 0) total++;
            int show = 0;
            try{
                show = Integer.parseInt(command[2]);
            }catch (NumberFormatException nfe){
                show = 1;
            }
            catch(ArrayIndexOutOfBoundsException AIOOBE){
                show = 1;
            }
            if (show > total || show < 1){
                show = 1;
            }
            int page = 9 * show;
            int start = 9 * show - 9;
            player.sendMessage("\u00A7AList all \u00A76ZONES \u00A7A Page \u00A7E" + show + "\u00A7A of \u00A7E" + total);
            for (int i = start; i < page && i < zonesplit.length; i++){
                player.sendMessage(ChatColor.ORANGE+zonesplit[i]);
            }
            return true;
        }
    },
    
    /**
     * ReloadZone command
     */
    RELOADZONE ("reloadzone", "<zone>"){
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(player.isAdmin()){
                if(!commandargsCheck(1, command)){
                    player.notify(getUsage());
                    return true;
                }
                String zoneName = command[0];
                try{
                    Zone zone = ZoneLists.getZoneByName(zoneName);
                    if(rhandle.getDataSource().reloadZone(zone)){
                        player.sendMessage(ChatColor.CYAN+"Zone reloaded successfully");
                    }
                    else{
                        player.notify("An error occurred durring reload...");
                    }
                }
                catch (ZoneNotFoundException ZNFE) {
                    player.notify(String.format(NFE, zoneName));
                }
                return true;
            }
            return false;
        }
    },
    
    /**
     * ReloadAll command
     */
    RELOADALL ("reloadall", ""){
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(player.isAdmin()){
                rhandle.getDataSource().reloadAll();
                player.sendMessage(ChatColor.CYAN+"Zones reloaded!");
                return true;
            }
            return false;
        }
    },
    
    SAVEZONE ("savezone", "<zone>"){
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(player.isAdmin()){
                if(!commandargsCheck(1, command)){
                    player.notify(getUsage());
                    return true;
                }
                String zoneName = command[0];
                try{
                    Zone zone = ZoneLists.getZoneByName(zoneName);
                    zone.save();
                    player.sendMessage("Zone: "+zoneName+" has been saved!");
                }
                catch (ZoneNotFoundException ZNFE) {
                    player.notify(String.format(NFE, zoneName));
                }
                return true;
            }
            return false;
        }
    },
    
    SAVEALL ("saveall", ""){
        @Override
        public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
            if(player.isAdmin()){
                synchronized(ZoneLists.getZones()){
                    for(Zone zone : ZoneLists.getZones()){
                        zone.save();
                    }
                }
                player.sendMessage("All Zones have been saved!");
                return true;
            }
            return false;
        }
    },
    
    /**
     * Invalid Command
     */
    INVALID ("", "<command> <arguments>");
    
    private String commandName;
    private final String usage;
    
    /**
     * Command Object
     * 
     * @param commandName
     * @param usage
     */
    private RealmsCommands(String commandName, String usage) {
        this.commandName = commandName;
        this.usage = usage;
    }

    /**
     * Get the Command Name
     */
    public String getCommandName() {
        return commandName;
    }
    
    /**
     * Executes Realms Command
     * 
     * @param player
     * @param command
     * @param realm
     * @return command result
     */
    public boolean execute(ICModPlayer player, String[] command, RHandle rhandle) {
        player.notify("Realms command not understood.");
        return true;
    }
    
    /**
     * Returns a Command's Usage
     * 
     * @return usage
     */
    public String getUsage() {
        return new StringBuilder(ChatColor.CYAN).append("Usage: ").append(ChatColor.ORANGE).append("/realms ").append(commandName).append(ChatColor.CYAN).append(usage).toString();
    }
    
    /**
     * Command Checks
     * 
     * @param command
     * @param player
     * @return weather there are the correct amount of arguments
     */
    public boolean commandargsCheck(int argumentsRequired, String[] command) {
        return (command.length >= argumentsRequired);
    }
    
    /*Multi-Used Messages*/
    private final static String NFE             = "The zone \u00A76'%s'\u00A7b could not be found!";
    private final static String NOSET           = "You cannot set \u00A7E'%s' to \u00A7D'INHERIT'\u00A7C in the \u00A76'EVERYWHERE'\u00A7C zone!";
    private final static String NOPERMWITHIN    = "You do not have permission to set \u00A7E'%s'\u00A7C within \u00A76'%s'";
    private final static String FLAGSET         = "\u00A7BSetting \u00A7e'%s'\u00A7B to '%s'\u00A7B in zone: \u00A76'%s'";
    private final static String INVALIDFLAG     = "Invaild \u00A7E'%s'\u00A7C mode!";
}
