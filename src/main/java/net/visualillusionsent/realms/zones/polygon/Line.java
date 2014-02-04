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
package net.visualillusionsent.realms.zones.polygon;

import java.awt.geom.Line2D;

/**
 * @author Jason (darkdiplomat)
 */
public final class Line {

    public final Point p1;
    public final Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public final boolean intersects2DIgnorePoints(Line line) {
        if (p1.equals2D(line.p1) || p1.equals2D(line.p2) || p2.equals2D(line.p1) || p2.equals2D(line.p2)) {
            return false;
        }
        else {
            return new Line2D.Double(p1.x, p1.z, p2.x, p2.z).intersectsLine(new Line2D.Double(line.p1.x, line.p1.z, line.p2.x, line.p2.z));
        }
    }
}
