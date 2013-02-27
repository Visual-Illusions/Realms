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
import java.util.List;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Realms_CanaryPluginInterface implements PluginInterface{

    private final String PARAMETER_INVALID = "[Realms] [API] A Plugin did not specify a proper parameter for '%s'";
    private final String PARAMETER_LENGTH = "[Realms] [API] A Plugin did not specify enough parameters for '%s'";

    public Realms_CanaryPluginInterface(Realms plugin){
        etc.getLoader().addCustomListener(this);
    }

    @Override
    public final String getName(){
        return "Realms-API";
    }

    @Override
    public final int getNumParameters(){
        return -1;
    }

    @Override
    public final String checkParameters(Object[] args){
        if(args == null){
            return "[Realms] [API] A Plugin did not specify an API Command to process with the Realms-API!";
        }
        if(args[0] instanceof String){
            String cmd = (String)args[0];
            if(cmd.toUpperCase().equals("PLAYER-ZONES")){
                if(args.length < 2){
                    return String.format(PARAMETER_LENGTH, "PLAYER-ZONES");
                }
                else if(!(args[1] instanceof Player)){
                    return String.format(PARAMETER_INVALID, "PLAYER-ZONES");
                }
            }
            else if(cmd.toUpperCase().equals("ZONE-CHECK")){
                if(args.length < 2){
                    return String.format(PARAMETER_LENGTH, "ZONE-CHECK");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-CHECK");
                }
            }
            else if(cmd.toUpperCase().equals("CREATE-ZONE")){
                if(args.length < 5){
                    return String.format(PARAMETER_LENGTH, "CREATE-ZONE");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "CREATE-ZONE");
                }
                else if(!(args[2] instanceof String)){
                    return String.format(PARAMETER_INVALID, "CREATE-ZONE");
                }
                else if(!(args[3] instanceof String)){
                    return String.format(PARAMETER_INVALID, "CREATE-ZONE");
                }
                else if(!(args[4] instanceof Integer)){
                    return String.format(PARAMETER_INVALID, "CREATE-ZONE");
                }
            }
            else if(cmd.toUpperCase().equals("DELETE-ZONE")){
                if(args.length < 2){
                    return String.format(PARAMETER_LENGTH, "DELETE-ZONE");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "DELETE-ZONE");
                }
            }
            else if(cmd.toUpperCase().equals("ZONE-FLAGCHECK")){
                if(args.length < 4){
                    return String.format(PARAMETER_LENGTH, "ZONE-FLAGCHECK");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-FLAGCHECK");
                }
                else if(!(args[2] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-FLAGCHECK");
                }
                else if(!(args[3] instanceof Boolean)){
                    return String.format(PARAMETER_INVALID, "ZONE-FLAGCHECK");
                }
            }
            else if(cmd.toUpperCase().equals("ZONE-FLAGCHANGE")){
                if(args.length < 4){
                    return String.format(PARAMETER_LENGTH, "ZONE-FLAGCHANGE");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-FLAGCHANGE");
                }
                else if(!(args[2] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-FLAGCHANGE");
                }
                else if(!(args[3] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-FLAGCHANGE");
                }
            }
            else if(cmd.toUpperCase().equals("ZONE-SETPOLYGON")){
                if(args.length < 5){
                    return String.format(PARAMETER_LENGTH, "ZONE-SETPOLYGON");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPOLYGON");
                }
                else if(!(args[2] instanceof Integer)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPOLYGON");
                }
                else if(!(args[3] instanceof Integer)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPOLYGON");
                }
                else if(!(args[4] instanceof List)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPOLYGON");
                }
            }
            else if(cmd.toUpperCase().equals("ZONE-SETPERMISSION")){
                if(args.length < 5){
                    return String.format(PARAMETER_LENGTH, "ZONE-SETPERMISSION");
                }
                else if(!(args[1] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPERMISSION");
                }
                else if(!(args[2] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPERMISSION");
                }
                else if(!(args[3] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPERMISSION");
                }
                else if(!(args[4] instanceof Boolean)){
                    return String.format(PARAMETER_INVALID, "ZONE-SETPERMISSION");
                }
            }
            else if(cmd.toUpperCase().equals("ZONE-CHECKPERMISSION")){
                if(args.length < 4){
                    return String.format(PARAMETER_LENGTH, "ZONE-CHECKPERMISSION");
                }
                else if(!(args[1] instanceof Player)){
                    return String.format(PARAMETER_INVALID, "ZONE-CHECKPERMISSION");
                }
                else if(!(args[2] instanceof Block) && !(args[2] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-CHECKPERMISSION");
                }
                else if(!(args[3] instanceof String)){
                    return String.format(PARAMETER_INVALID, "ZONE-CHECKPERMISSION");
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Object run(Object[] args){
        String cmd = (String)args[0];
        if(cmd.toUpperCase().equals("PLAYER-ZONES")){
            return Canary_RealmsAPIListener.getPlayerZoneNames((Player)args[1]);
        }
        else if(cmd.toUpperCase().equals("ZONE-CHECK")){
            return Boolean.valueOf(Canary_RealmsAPIListener.nameCheck((String)args[1]));
        }
        else if(cmd.toUpperCase().equals("ZONE-FLAGCHECK")){
            return Canary_RealmsAPIListener.checkZoneFlag((String)args[1], (String)args[2], ((Boolean)args[3]).booleanValue());
        }
        else if(cmd.toUpperCase().equals("ZONE-FLAGCHANGE")){
            return Boolean.valueOf(Canary_RealmsAPIListener.changeZoneFlag((String)args[1], (String)args[2], (String)args[3]));
        }
        else if(cmd.toUpperCase().equals("CREATE-ZONE")){
            return Boolean.valueOf(Canary_RealmsAPIListener.createZone((String)args[1], (String)args[2], (String)args[3], ((Integer)args[4]).intValue()));
        }
        else if(cmd.toUpperCase().equals("DELETE-ZONE")){
            return Boolean.valueOf(Canary_RealmsAPIListener.deleteZone((String)args[1]));
        }
        else if(cmd.toUpperCase().equals("ZONE-SETPOLYGON")){
            return Boolean.valueOf(Canary_RealmsAPIListener.setZonePolygon((String)args[1], ((Integer)args[2]).intValue(), ((Integer)args[3]).intValue(), (List<Integer[]>)args[4]));
        }
        else if(cmd.toUpperCase().equals("ZONE-SETPERMISSION")){
            return Boolean.valueOf(Canary_RealmsAPIListener.setZonePermission((String)args[1], (String)args[2], (String)args[3], ((Boolean)args[4]).booleanValue()));
        }
        else if(cmd.toUpperCase().equals("ZONE-CHECKPERMISSION")){
            if(args[2] instanceof Block){
                return Canary_RealmsAPIListener.checkZonePermission((Player)args[1], (Block)args[2], (String)args[3]);
            }
            else{
                return Canary_RealmsAPIListener.checkZonePermission((Player)args[1], (String)args[2], (String)args[3]);
            }
        }
        return null;
    }
}
