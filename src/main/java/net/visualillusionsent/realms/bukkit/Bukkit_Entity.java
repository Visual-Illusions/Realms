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
package net.visualillusionsent.realms.bukkit;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.realms.zones.polygon.Point;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class Bukkit_Entity implements Mod_Entity{

    private final Entity entity;

    public Bukkit_Entity(Entity entity){
        this.entity = entity;
    }

    @Override
    public String getName(){
        return entity.getType().getName();
    }

    @Override
    public final double getX(){
        return entity.getLocation().getX();
    }

    @Override
    public final double getY(){
        return entity.getLocation().getY();
    }

    @Override
    public final double getZ(){
        return entity.getLocation().getZ();
    }

    @Override
    public final float getRotation(){
        return entity.getLocation().getYaw();
    }

    @Override
    public final float getPitch(){
        return entity.getLocation().getPitch();
    }

    @Override
    public final void teleportTo(double x, double y, double z, float rotation, float pitch){
        entity.teleport(new Location(entity.getWorld(), x, y, z, rotation, pitch));
    }

    @Override
    public final Point getLocationPoint(){
        return new Point((int)Math.floor(getX()), (int)Math.floor(getY()), (int)Math.floor(getZ()));
    }

    @Override
    public final int getDimension(){
        return entity.getWorld().getEnvironment().ordinal();
    }

    @Override
    public final String getWorld(){
        return entity.getWorld().getName();
    }

    @Override
    public void destroy(){
        entity.remove();
    }

    @Override
    public Entity getEntity(){
        return entity;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Bukkit_Entity){
            return entity.equals(((Bukkit_Entity)obj).getEntity());
        }
        else if(obj instanceof Entity){
            return entity.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return entity.hashCode();
    }
}
