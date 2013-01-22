/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 */
import net.visualillusionsent.mcmod.interfaces.Mod_Entity;
import net.visualillusionsent.mcplugin.realms.zones.polygon.Point;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class Canary_Entity implements Mod_Entity {
    private final BaseEntity entity;

    public Canary_Entity(BaseEntity entity) {
        this.entity = entity;
    }

    @Override
    public final void destroy() {
        entity.destroy();
    }

    @Override
    public final String getName() {
        return entity.getName();
    }

    @Override
    public final double getX() {
        return entity.getX();
    }

    @Override
    public final double getY() {
        return entity.getY();
    }

    @Override
    public final double getZ() {
        return entity.getZ();
    }

    @Override
    public final Point getLocationPoint() {
        return new Point((int) Math.floor(getX()), (int) Math.floor(getY()), (int) Math.floor(getZ()));
    }

    @Override
    public final int getDimension() {
        return entity.getWorld().getType().toIndex();
    }

    @Override
    public final String getWorld() {
        return entity.getWorld().getName();
    }

    @Override
    public final BaseEntity getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Canary_Entity) {
            return entity.equals(((Canary_Entity) obj).getEntity());
        }
        else if (obj instanceof LivingEntity) {
            return entity.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

}
