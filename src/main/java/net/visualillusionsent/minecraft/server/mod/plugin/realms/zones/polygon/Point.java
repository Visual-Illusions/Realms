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

/**
 * Polygon Point
 * 
 * @author impact
 * @author durron597
 * @author Jason (darkdiplomat)
 */
public final class Point implements Cloneable {

    public int x, y, z;

    /**
     * Constructs a new Point
     * 
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     * @param z
     *            the Z coordinate
     */
    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Checks if a Point is equal to this Point
     * 
     * @param p
     *            the Point to check
     * @return {@code true} if equal; {@code false} if not
     */
    public final boolean equals(Point point) {
        return this == point || (x == point.x && y == point.y && z == point.z);
    }

    /**
     * Checks if a Point is 2D (X/Z) equal to this Point
     * 
     * @param point
     *            the Point to check
     * @return {@code true} if equal; {@code false} if not
     */
    public final boolean equals2D(Point point) {
        return x == point.x && z == point.z;
    }

    public final double distance2D(Point p) {
        return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(z - p.z, 2));
    }

    public final static long isLeft2D(Point p1, Point p2, Point p3) {
        return (p2.x - p1.x) * (p3.z - p1.z) - (p3.x - p1.x) * (p2.z - p1.z);
    }

    /**
     * Creates and returns a new instance of this Point.
     */
    public final Point clone() {
        return new Point(x, y, z);
    }

    public final String asString() {
        return String.format("%d,%d,%d", x, y, z);
    }

    public final int[] asIntArray() {
        return new int[] { x, y, z };
    }

    public final String toString() {
        return String.format("Point[X:%d Y:%d Z:%d", x, y, z);
    }
}
