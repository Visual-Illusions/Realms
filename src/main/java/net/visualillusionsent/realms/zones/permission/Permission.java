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
package net.visualillusionsent.realms.zones.permission;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsBase;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Permission{

    private final String owner;
    private final PermissionType type;
    private final boolean allowed;
    private final boolean override;

    public Permission(String owner, PermissionType type, boolean allowed, boolean override){
        this.owner = owner;
        this.type = type;
        this.allowed = allowed;
        this.override = override;
    }

    public Permission(String owner, String type, boolean allowed, boolean override){
        this(owner, PermissionType.valueOf(type.toUpperCase()), allowed, override);
    }

    public Permission(String[] args) throws PermissionConstructException{
        try{
            this.owner = args[0];
            this.type = PermissionType.valueOf(args[1].toUpperCase());
            this.allowed = Boolean.parseBoolean(args[2]);
            this.override = Boolean.parseBoolean(args[3]);
        }
        catch(Exception e){
            throw new PermissionConstructException();
        }
    }

    public final String getOwnerName(){
        return owner;
    }

    public final PermissionType getType(){
        return type;
    }

    public final boolean getAllowed(){
        return allowed;
    }

    public final boolean getOverride(){
        return override;
    }

    public final boolean applicableToPlayer(Mod_User user){
        if(owner.startsWith("p-")){
            return owner.replaceAll("p\\-", "").equalsIgnoreCase(user.getName());
        }
        else if(owner.startsWith("g-")){
            return user.isInGroup(owner.replaceAll("g\\-", ""));
        }
        else if(owner.equalsIgnoreCase(user.getName())){
            return true;
        }
        else if(user.isInGroup(owner)){
            return true;
        }
        else if(owner.equalsIgnoreCase("everyone")){
            return true;
        }
        return false;
    }

    public final boolean applicableToType(PermissionType type){
        if(this.type.equals(PermissionType.ALL) || this.type.equals(type)){
            return true;
        }
        else{
            return false;
        }
    }

    public final boolean applicable(Mod_User player, PermissionType type){
        return applicableToPlayer(player) && applicableToType(type);
    }

    public final Permission battle(Permission p1, Permission p2){
        // Override permissions always win
        if(p1.getOverride() && !p2.getOverride()){
            return p1;
        }
        else if(!p1.getOverride() && p2.getOverride()){
            return p2;
        }
        // Otherwise, return whichever permission overrules the other
        // If both permissions agree, it doesn't matter which we return
        if(RealmsBase.getProperties().getBooleanVal("grant.override") && p2.getAllowed()){
            return p2;
        }
        else if(!RealmsBase.getProperties().getBooleanVal("grant.override") && !p2.getAllowed()){
            return p2;
        }
        else{
            return p1;
        }
    }

    @Override
    public final String toString(){
        StringBuffer builder = new StringBuffer();
        builder.append(owner);
        builder.append(',');
        builder.append(type.toString());
        builder.append(',');
        builder.append(String.valueOf(allowed));
        builder.append(',');
        builder.append(String.valueOf(override));
        return builder.toString();
    }
}
