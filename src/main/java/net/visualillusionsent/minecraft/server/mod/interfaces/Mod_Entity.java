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
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.minecraft.server.mod.interfaces;

import net.visualillusionsent.realms.zones.polygon.Point;

/**
 * @author Jason (darkdiplomat)
 */
public interface Mod_Entity {

    String getName();

    //Location
    double getX();

    double getY();

    double getZ();

    float getRotation();

    float getPitch();

    void teleportTo(double x, double y, double z, float rotation, float pitch);

    Point getLocationPoint();

    int getDimension();

    String getWorld();

    void destroy();

    //Location
    <T> Object getEntity();
}
