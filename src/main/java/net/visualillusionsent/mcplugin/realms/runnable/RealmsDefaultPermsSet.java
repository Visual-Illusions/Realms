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
package net.visualillusionsent.mcplugin.realms.runnable;

import java.util.List;

import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.zones.Zone;
import net.visualillusionsent.mcplugin.realms.zones.permission.Permission;
import net.visualillusionsent.mcplugin.realms.zones.permission.PermissionType;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class RealmsDefaultPermsSet implements Runnable{

    private final Zone zone;

    public RealmsDefaultPermsSet(Zone zone){
        this.zone = zone;
    }

    @Override
    public final void run(){
        List<String> adminGroups = RealmsBase.getServer().getAdminGroups();
        //Grant admins all access
        if(adminGroups != null){
            for(String admin : adminGroups){
                zone.setPermission(new Permission("g-".concat(admin), PermissionType.ALL, true, true));
            }
        }
        //Deny everyone else DELEGATE, ZONING, MESSAGE, COMBAT, ENVIRONMENT
        zone.setPermission(new Permission("everyone", PermissionType.DELEGATE, false, false));
        zone.setPermission(new Permission("everyone", PermissionType.ZONING, false, false));
        zone.setPermission(new Permission("everyone", PermissionType.MESSAGE, false, false));
        zone.setPermission(new Permission("everyone", PermissionType.COMBAT, false, false));
        zone.setPermission(new Permission("everyone", PermissionType.ENVIRONMENT, false, false));
        //Grant everyone else ENTER, EAT, CREATE, DESTROY, INTERACT, COMMAND, TELEPORT, and AUTHED
        zone.setPermission(new Permission("everyone", PermissionType.ENTER, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.EAT, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.CREATE, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.DESTROY, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.INTERACT, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.COMMAND, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.TELEPORT, true, false));
        zone.setPermission(new Permission("everyone", PermissionType.AUTHED, true, false));
    }
}
