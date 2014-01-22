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
package net.visualillusionsent.realms.canary;

import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.EntityLiving;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.realms.zones.polygon.Point;

/**
 * @author Jason (darkdiplomat)
 */
public class Canary_Entity implements Mod_Entity{

    private final Entity entity;

    public Canary_Entity(Entity entity){
        this.entity = entity;
    }

    @Override
    public final void destroy(){
        entity.destroy();
    }

    @Override
    public final String getName(){
        return entity.getName();
    }

    @Override
    public final double getX(){
        return entity.getX();
    }

    @Override
    public final double getY(){
        return entity.getY();
    }

    @Override
    public final double getZ(){
        return entity.getZ();
    }

    @Override
    public final float getRotation(){
        return entity.getRotation();
    }

    @Override
    public final float getPitch(){
        return entity.getPitch();
    }

    @Override
    public final void teleportTo(double x, double y, double z, float rotation, float pitch){
        entity.setX(x);
        entity.setY(y);
        entity.setZ(z);
        entity.setRotation(rotation);
        entity.setPitch(pitch);
    }

    @Override
    public final Point getLocationPoint(){
        return new Point((int) Math.floor(getX()), (int) Math.floor(getY()), (int) Math.floor(getZ()));
    }

    @Override
    public final int getDimension(){
        int dim = entity.getWorld().getType().getId();
        return dim == -1 ? 1 : dim == 1 ? 2 : 0;
    }

    @Override
    public final String getWorld(){
        return entity.getWorld().getName();
    }

    @Override
    public final Entity getEntity(){
        return entity;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Canary_Entity) {
            return entity.equals(((Canary_Entity) obj).getEntity());
        }
        else if (obj instanceof EntityLiving) {
            return entity.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return entity.hashCode();
    }
}
