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
package net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.polygon;

import java.util.Comparator;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class PointComparator implements Comparator<Point>{

    private Point origin;

    public PointComparator(Point origin){
        this.origin = origin;
    }

    public int compare(Point p1, Point p2){
        double angle1 = Math.atan2(p1.x - origin.x, p1.z - origin.z);
        double angle2 = Math.atan2(p2.x - origin.x, p2.z - origin.z);
        if(angle1 < angle2){
            return 1;
        }
        else if(angle2 > angle1){
            return -1;
        }
        return 0;
    }
}
