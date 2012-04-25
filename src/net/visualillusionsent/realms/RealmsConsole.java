package net.visualillusionsent.realms;

import net.visualillusionsent.realms.io.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.InvaildZoneFlagException;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;

/**
 * Realms Console Commands
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public enum RealmsConsole {
    
    /**
     * Deletes a Zone
     */
    DELETEZONE ("deletezone", "<zone name>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
            if(zoneName.equalsIgnoreCase("everywhere")){
                output("Error: You cannot delete the 'everywhere' zone!");
                return true;
            }
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                if(zone.getParent() == null){
                    output("Error: The zone '" + zoneName + "' does not have a parent zone!");
                    return true;
                }
                zone.delete();
                output("Deleted " + zoneName + ", moved all children to parent zone.");
            }
            catch(ZoneNotFoundException ZNFE){
                output("Error: The zone '" + zoneName + "' could not be found!");
            }
            return true;
        }
    },
    
    /**
     * Sets the Greeting Message
     */
    SETGREETING ("setgreeting", "<zone> [greeting]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                StringBuilder greeting = new StringBuilder();
                for(int i = 3; i < command.length; i++) {
                    if(command[i].contains(",")){
                       output("Error: GREETINGS cannot contain COMMAS!");
                       return true;
                    }
                    greeting.append(" "+command[i]);
                }
                zone.setGreeting(greeting.toString().trim());
                output("GREETING set to " + greeting.toString().trim());
                zone.save();
            } 
            catch (ZoneNotFoundException ZNFE) {
                output("Error: The zone '" + zoneName + "' could not be found!");
            }
            return true;
        }
    },
    
    /**
     * Sets the Farewell Message
     */
    SETFAREWELL ("setfarewell", "<zone> [greeting]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                StringBuilder farewell = new StringBuilder();
                for(int i = 3; i < command.length; i++) {
                    if(command[i].contains(",")){
                        output("Error: FAREWELLS cannot contain commas!");
                        return true;
                    }
                    farewell.append(" " + command[i]);
                }
                zone.setFarewell(farewell.toString().trim());
                output("FAREWELL set to " + farewell.toString().trim());
                zone.save();
            } 
            catch (ZoneNotFoundException ZNFE) {
                output("Error: The zone '" + zoneName + "' could not be found!");
            }
            return true;
        }
    },
    
    /**
     * Displays a list of Permissions for the zone
     */
    PERMISSION ("permission", "<zone> [page#]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
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
                    show = Integer.parseInt(command[3]);
                }
                catch (NumberFormatException nfe){
                    show = 1;
                }
                catch (ArrayIndexOutOfBoundsException AIOOBE){
                    show = 1;
                }
                if (show > total || show < 1){
                    show = 1;
                }
                int page = 5 * show;
                int start = 5 * show - 5;
                output("List all permissions attached to " + zoneName + " Page " + show + " of " + total);
                output("PLAYER/GROUP PERMISSION GRANTED/DENIED OVERRIDDEN");
                for (int i = start; i < page && i < permsplit.length; i++){
                    String grantdeny = "GRANTED";
                    String override = "";
                    String[] permission = permsplit[i].split(",");
                    if (permission[3].equals("0")) grantdeny = "DENIED";
                    if (permission[4].equals("1")) override = "TRUE";
                    output(permission[0] + " " + permission[1].toUpperCase() + " " + grantdeny + " " + override);
                }
            } 
            catch (ZoneNotFoundException ZNFE) {
                output("Error: The zone '" + zoneName + "' could not be found!");
            }
            return true;
        }
    },
    /**
     * Deletes a Player Permission
     */
    DELETE ("delete", "<playername> <PermType> <zone>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(5, command)){
                output(getUsage());
                return true;
            }
            String playerName = command[2];
            
            Permission.PermType type = null;
            try {
                type = Permission.PermType.getTypeFromString(command[3]);
            } catch (InvaildPermissionTypeException IPTE) {
                output("Error: The type '" + command[3] + "' is not valid!");
                return true;
            }
            String zoneName = command[4];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                zone.deletePermission(playerName, type);
                output("Deleted all of " + playerName + "'s permissions to " + type + " in " + zone.getName());
            } 
            catch (ZoneNotFoundException ZNFE) {
                output("Error: The zone '" + zoneName + "' could not be found!");
            }
            return true;
        }
    },
    /**
     * Grants a Player Permission
     */
    GRANT ("grant", "<playername> <permissiontype> <zone> [override]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(6, command)){
                output(getUsage());
                return true;
            }
            return rhandle.ConsoledoGrantDeny(command, getUsage());
        }
    },
    /**
     * Denies a Player Permission
     */
    DENY ("deny", "<playername> <permissiontype> <zone> [override]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(6, command)){
                output(getUsage());
                return true;
            }
            return rhandle.ConsoledoGrantDeny(command, getUsage());
        }
    },
    /**
     * Creates a new Zone
     */
    CREATEZONE ("createzone", "<zone> [parentzone]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(4, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
            String parentZoneName = command[3];
            try{
                Zone parentZone = ZoneLists.getZoneByName(parentZoneName);
                for(Zone zone : ZoneLists.getZones()){
                    if(zone.getName().equalsIgnoreCase(zoneName)){
                        output("Error: A zone with the name '" + zoneName + "' already exists!");
                        return true;
                    }
                }
                if (zoneName.toUpperCase().startsWith("EVERYWHERE")){
                    output("Error: ZONE NAMES cannot start with EVERYWHERE");
                    return true;
                }
                if (zoneName.contains(",")){
                    output("Error: ZONE NAMES cannot contain COMMAS!");
                    return true;
                }
                if (zoneName.equalsIgnoreCase("null")){
                    output("Error: Zones cannot be named NULL!");
                    return true;
                }
                
                // Made it past all the checks!
                Zone zone = new Zone(rhandle, zoneName, parentZone, parentZone.getWorld(), parentZone.getDimension());
                //rhandle.setDefaultPerm(zone); //FIXME
                ZoneLists.addZone(zone);
                
                output("Created zone " + zoneName + " within " + parentZoneName);
                output("Have an admin edit the zone!");
            } 
            catch (ZoneNotFoundException ZNFE) {
                output("Error: The Parent Zone '" + zoneName + "' could not be found!");
            }
            return true;
        }
    },
    /**
     * Sets PVP Zone Flag
     */
    PVP ("pvp", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setPVP(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getPVP() ? "ON " : "OFF ")+(zone.getAbsolutePVP().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets SANCTUARY Zone Flag
     */
    SANCTUARY ("sanctuary", "<zone> <on|off|inherit>") {        
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setSanctuary(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getSanctuary() ? "ON " : "OFF ")+(zone.getAbsoluteSanctuary().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets CREEPER Zone Flag
     */
    CREEPER ("creeper", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setCreeper(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getCreeper() ? "ON " : "OFF ")+(zone.getAbsoluteCreeper().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets POTION Zone Flag
     */
    POTION ("potion", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setPotion(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getPotion() ? "ON " : "OFF ")+(zone.getAbsolutePotion().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets GHAST Zone Flag
     */
    GHAST ("ghast", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setGhast(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getGhast() ? "ON " : "OFF ")+(zone.getAbsoluteGhast().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets FLAG Zone Flag
     */
    FALL ("fall", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setFall(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getFall() ? "ON " : "OFF ")+(zone.getAbsoluteFall().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets SUFFOCATE Zone Flag
     */
    SUFFOCATE ("suffocate", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setSuffocate(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getSuffocate() ? "ON " : "OFF ")+(zone.getAbsoluteSuffocate().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    
    /**
     * Sets FIRE Zone Flag
     */
    FIRE ("fire", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setFire(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getFire() ? "ON " : "OFF ")+(zone.getAbsoluteFire().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets ANIMALS Zone Flag
     */
    ANIMALS ("animals", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setAnimals(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getAnimals() ? "ON " : "OFF ")+(zone.getAbsoluteAnimals().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets PHYSICS Zone Flag
     */
    PHYSICS ("physics", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setPhysics(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getPhysics() ? "ON " : "OFF ")+(zone.getAbsolutePhysics().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets CREATIVE Zone Flag
     */
    CREATIVE ("creative", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setCreative(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getCreative() ? "ON " : "OFF ")+(zone.getAbsoluteCreative().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets PISTONS Zone Flag
     */
    PISTONS ("pistons", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setPistons(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getPistons() ? "ON " : "OFF ")+(zone.getAbsolutePistons().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets ENDERMAN Zone Flag
     */
    ENDERMAN ("enderman", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setEnderman(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getEnderman() ? "ON " : "OFF ")+(zone.getAbsoluteEnderman().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets FLOW Zone Flag
     */
    FLOW ("flow", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setFlow(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getFlow() ? "ON " : "OFF ")+(zone.getAbsoluteFlow().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets SPREAD Zone Flag
     */
    SPREAD ("spread", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setSpread(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getSpread() ? "ON " : "OFF ")+(zone.getAbsoluteSpread().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets HEALING Zone Flag
     */
    HEALING ("healing", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setHealing(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getHealing() ? "ON " : "OFF ")+(zone.getAbsoluteHealing().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets TNT Zone Flag
     */
    TNT ("tnt", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setTNT(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getTNT() ? "ON " : "OFF ")+(zone.getAbsoluteTNT().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets STARVE Zone Flag
     */
    STARVE ("starve", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setStarve(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getStarve() ? "ON " : "OFF ")+(zone.getAbsoluteStarve().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Sets RESTRICTED Zone Flag
     */
    RESTRICTED ("restricted", "<zone> <on|off|inherit>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName  = command[2];
            try{
                Zone.ZoneFlag theFlag = Zone.ZoneFlag.getZoneFlag(command[3].toUpperCase());
                Zone zone = ZoneLists.getZoneByName(zoneName);
                    
                if (zone.getName().toUpperCase().startsWith("EVERYWHERE") && theFlag.equals(Zone.ZoneFlag.INHERIT)) {
                    output(String.format(NOSET, command[1].toUpperCase()));
                    return true;
                }
                zone.setRestricted(theFlag);
                output(String.format(FLAGSET, command[1].toUpperCase(), (zone.getRestricted() ? "ON " : "OFF ")+(zone.getAbsoluteRestricted().equals(Zone.ZoneFlag.INHERIT) ? "(INHERITED)" : ""), zone.getName() ));
                zone.save();
            } 
            catch (InvaildZoneFlagException ife) {
                output(String.format(INVALIDFLAG, command[1].toUpperCase()));
            } 
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Displays Zone's COMBAT Settings
     */
    COMBAT ("combat", "<zone>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                System.out.println("COMBAT settings for Zone: "+zoneName);
                String[] comb = zone.getFlags(true, false);
                for(String mess : comb){
                    output(mess);
                }
            }
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Displays Zone's ENVIRONMENT Settings
     */
    ENVIRONMENT ("environment", "<zone>") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            if(!commandargsCheck(3, command)){
                output(getUsage());
                return true;
            }
            String zoneName = command[2];
            try{
                Zone zone = ZoneLists.getZoneByName(zoneName);
                String[] enviro = zone.getFlags(false, true);
                for(String mess : enviro){
                    output(mess);
                }
            }
            catch (ZoneNotFoundException ZNFE) {
                output(String.format(NFE, zoneName));
            }
            return true;
        }
    },
    /**
     * Check Realms' Version
     */
    VERSION ("version", "--checks version #--") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
            System.out.println("----- Realms v"+rhandle.getVersion()+" by DarkDiplomat -----");
            if(!rhandle.isLatest()){
                System.out.println("----- Update Availible: "+rhandle.getCurrent()+" -----");
            }
            else{
                System.out.println("----- Latest Version is Installed -----");
            }
            return true;
        }
    },
    /**
     * Displays list of All Zones
     */
    LIST ("list", "[page#]") {
        @Override
        public boolean execute(String[] command, RHandle rhandle) {
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
            System.out.println("List all ZONES Page " + show + " of " + total);
            for (int i = start; i < page && i < zonesplit.length; i++){
                System.out.println(zonesplit[i]);
            }
            return true;
        }
    },
    /**
     * Invalid Command
     */
    INVALID ("", "<command> <arguments>");
    
    private String commandName;
    protected final String usage;
    
    /**
     * Command Object
     * 
     * @param commandName
     * @param usage
     */
    private RealmsConsole(String commandName, String usage) {
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
    public boolean execute( String[] command, RHandle rhandle) {
        System.out.println("Realms command not understood.");
        return true;
    }
    
    /**
     * Returns a Command's Usage
     * 
     * @return usage
     */
    public String getUsage() {
        return "Usage: realms " + commandName + " " + usage;
    }
    
    private static void output(String message){
        System.out.println(message);
    }
    
    /**
     * Command Checks
     * 
     * @param command
     * @return weather there are the correct amount of arguments
     */
    public boolean commandargsCheck(int argumentsRequired, String[] command) {
        return (command.length >= argumentsRequired);
    }
    
    /*Multi-Used Messages*/
    private final static String NFE             = "The zone '%s' could not be found!";
    private final static String NOSET           = "You cannot set '%s' to 'INHERIT' in the 'EVERYWHERE' zone!";
    private final static String FLAGSET         = "Setting '%s' to '%s' in zone: '%s'";
    private final static String INVALIDFLAG     = "Invaild '%s' mode!";
}
