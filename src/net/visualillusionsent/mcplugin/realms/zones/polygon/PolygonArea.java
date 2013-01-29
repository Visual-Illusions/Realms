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
package net.visualillusionsent.mcplugin.realms.zones.polygon;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.visualillusionsent.mcmod.interfaces.Mod_Block;
import net.visualillusionsent.mcmod.interfaces.Mod_Entity;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class PolygonArea {

    private enum Mode {
        DELETED, //
        EDIT, //
        SAVED; //
    }

    private Zone zone;
    private LinkedList<Point> vertices = new LinkedList<Point>();
    private LinkedList<Point> workingVertices = new LinkedList<Point>();
    private int ceiling;
    private int floor;
    private int workingCeiling;
    private int workingFloor;
    private Mode mode = Mode.SAVED;

    private Point centroid = null;
    private double radius = 0;

    /**
     * PolygonArea Constructor
     * 
     * @param realm
     * @param zone
     */
    public PolygonArea(Zone zone) {
        this.zone = zone;
        this.ceiling = 1000;
        this.floor = 0;
    }

    /**
     * PolygonArea Constructor from DataSource
     */
    public PolygonArea(Zone zone, String... args) throws PolygonConstructException {
        try {
            this.zone = zone;
            this.ceiling = Integer.parseInt(args[0]);
            this.floor = Integer.parseInt(args[1]);
            for (int i = 2; i < args.length; i += 3) {
                vertices.add(new Point(Integer.parseInt(args[i]), Integer.parseInt(args[i + 1]), Integer.parseInt(args[i + 2])));
            }
            centroid = calculateCentroid(vertices);
            radius = calculateRadius(vertices, centroid);
            vertices = reorgPoints(vertices); //Redundancy and verification
        }
        catch (NumberFormatException nfe) {
            throw new PolygonConstructException();
        }
    }

    /*
     * Accessor Methods
     */
    public final Zone getZone() {
        return zone;
    }

    public final List<Point> getVertices() {
        return vertices;
    }

    public final int getCeiling() {
        return ceiling;
    }

    public final int getFloor() {
        return floor;
    }

    public final Point getCentroid() {
        return centroid;
    }

    public final double getRadius() {
        return radius;
    }

    public final int getArea() {
        return calculateArea(vertices);
    }

    public final String getMode() {
        return mode.toString().toLowerCase();
    }

    public final boolean isEmpty() {
        return vertices.size() < 3;
    }

    public final boolean workingVerticesCleared() {
        return workingVertices.size() == 0;
    }

    /*
     * Mutator Methods
     */
    public final void setWorkingCeiling(int ceiling) {
        this.workingCeiling = ceiling;
    }

    public final void setWorkingFloor(int floor) {
        this.workingFloor = floor;
    }

    /**
     * Saves Polygon
     */
    public final void save() {
        if (workingVertices.isEmpty()) {
            vertices = new LinkedList<Point>();
            floor = 0;
            ceiling = 1000;
            this.mode = Mode.SAVED;

            centroid = null;
            radius = 0;
        }
        else {
            vertices = new LinkedList<Point>(workingVertices);
            workingVertices = new LinkedList<Point>();
            floor = workingFloor;
            ceiling = workingCeiling;
            this.mode = Mode.SAVED;

            centroid = calculateCentroid(vertices);
            radius = calculateRadius(vertices, centroid);
        }
        zone.save();
    }

    private final Point calculateCentroid(List<Point> points) {
        double x = 0;
        double z = 0;

        for (Point p : points) {
            x += p.x;
            z += p.z;
        }

        return new Point((int) Math.floor(x / points.size()), (int) Math.floor(ceiling - (ceiling - floor) / 2), (int) Math.floor(z / points.size()));
    }

    private final double calculateRadius(List<Point> points, Point c) {
        double max = 0;

        for (Point p : points) {
            double distance = c.distance2D(p);

            if (distance > max) {
                max = distance;
            }
        }

        return max;
    }

    public final List<Point> edit() {
        this.mode = Mode.EDIT;
        workingFloor = floor;
        workingCeiling = ceiling;
        workingVertices = new LinkedList<Point>(vertices);
        return workingVertices;
    }

    public final void cancelEdit() {
        this.mode = Mode.SAVED;
        this.workingVertices.clear();
    }

    public final void delete() {
        this.mode = Mode.DELETED;
        zone.setPolygon(null);
        zone.save();
    }

    public final void removeWorkingVertex(Mod_Block block) {
        Iterator<Point> itr = workingVertices.iterator();
        while (itr.hasNext()) {
            Point p = itr.next();
            if (p.x == block.getX() && p.z == block.getZ()) {
                itr.remove();
            }
        }
    }

    public final boolean contains(Mod_Entity entity) {
        Point p = new Point((int) Math.floor(entity.getX()), (int) Math.floor(entity.getY()), (int) Math.floor(entity.getZ()));
        return this.contains(p, true);
    }

    public final boolean contains(Mod_Block block) {
        Point p = new Point(block.getX(), block.getY(), block.getZ());
        return this.contains(p, true);
    }

    public final boolean contains(Point point) {
        return this.contains(point, true);
    }

    /***************************************************************************
     * INPOLY.C
     * Copyright (c) 1995-1996 Galacticomm, Inc. Freeware source code.
     * http://www.visibone.com/inpoly/inpoly.c
     * 6/19/95 - Bob Stein & Craig Yap stein@visibone.com craig@cse.fau.edu
     ***************************************************************************/
    public final static boolean contains(List<Point> points, Point p, int floor, int ceiling) {
        if (points == null) {
            return false;
        }
        if (points.isEmpty()) {
            return false;
        }
        Point oldPoint;
        int x1, z1;
        int x2, z2;
        boolean inside = false;

        if (p.y > ceiling || p.y < floor) {
            return false;
        }

        if (points.size() < 3) {
            return false;
        }

        oldPoint = points.get(points.size() - 1);
        for (Point newPoint : points) {
            if (newPoint.x > oldPoint.x) {
                x1 = oldPoint.x;
                x2 = newPoint.x;
                z1 = oldPoint.z;
                z2 = newPoint.z;
            }
            else {
                x1 = newPoint.x;
                x2 = oldPoint.x;
                z1 = newPoint.z;
                z2 = oldPoint.z;
            }

            //if (x1 == 40 && p.x == 40) Realms.log(Level.INFO, String.format("(%d,%d),(%d,%d),(%d,%d),%d", x1,z1,p.x,p.z,x2,z2,determinant));

            if (x1 <= p.x && p.x <= x2 && Math.min(z1, z2) <= p.z && p.z <= Math.max(z1, z2)) { /* edges */
                int determinant = x1 * (p.z - z2) + p.x * (z2 - z1) + x2 * (z1 - p.z);
                if (determinant == 0) {
                    return true;
                }
            }
            if (newPoint.x < p.x == p.x <= oldPoint.x /* edge "open" at left end */
                    && (p.z - z1) * (x2 - x1) < (z2 - z1) * (p.x - x1)) {
                inside = !inside;
            }
            oldPoint = newPoint;
        }
        return inside;
    }

    /* End INPOLY */

    public final boolean contains(Point p, boolean checkRadius) {
        if (this.centroid == null) {
            return false;
        }

        if (checkRadius && this.centroid.distance2D(p) > this.radius) {
            return false;
        }
        return contains(vertices, p, this.floor, this.ceiling);
    }

    public final boolean workingVerticesContain(PolygonArea polygonArea) {
        for (Point p : polygonArea.getVertices()) {
            if (!contains(workingVertices, p, this.workingFloor, this.workingCeiling)) {
                return false;
            }
        }
        return true;
    }

    public final boolean containsWorkingVertex(Mod_Block block) {
        for (Point p : workingVertices) {
            if (p.x == block.getX() && p.z == block.getZ()) {
                return true;
            }
        }
        return false;
    }

    public final boolean verticesContain(PolygonArea polygonArea) {
        for (Point p : polygonArea.getVertices()) {
            if (contains(this.vertices, p, this.floor, this.ceiling)) {
                return true;
            }
        }
        return false;
    }

    // Adds the vertex to the working list
    // Returns a list of removed vertices
    public final List<Point> addVertex(Mod_User player, Mod_Block block) {
        List<Point> removed = new LinkedList<Point>();
        Point newVertex = new Point(block.getX(), block.getY(), block.getZ());

        // Case #1: The vertex list has less than three points
        // Just add the point to the end of the working vertices list
        if (workingVertices.size() < 3) {
            workingVertices.add(newVertex);
            return removed;
        }

        // Case #2: Reorder the points to make the polygon right
        workingVertices.add(newVertex);
        workingVertices = reorgPoints(workingVertices);
        return removed;
    }

    /**
     * Tests if the vertex is valid
     * 
     * @param block
     * @param player
     * @return whether the vertex is valid
     */
    public final boolean validVertex(Mod_User user, Mod_Block block) {
        // The vertex must be contained by the parent zone
        if (!zone.getParent().contains(block)) {
            user.sendError(RealmsTranslate.transformMessage("polygon.not.contain", zone.getParent().getName()));
            return false;
        }
        // The vertex must not be contained by sibling zones
        for (Zone sibling : zone.getParent().getChildren()) {
            if (sibling != zone && sibling.contains(block)) {
                user.sendError(RealmsTranslate.transformMessage("polygon.sibling.contain", sibling.getName()));
                return false;
            }
        }
        // The vertex must not already be in the vertex list
        if (containsWorkingVertex(block)) {
            user.sendError(RealmsTranslate.transMessage("vertex.exists"));
            return false;
        }
        // All checks passed: test vertex is valid
        return true;
    }

    /**
     * Checks whether the working vertices make a valid polygon
     * 
     * @param player
     * @return whether the polygon makes a valid polygon
     */
    public final boolean validPolygon(Mod_User user) {
        // A polygon must have a least three sides
        if (workingVertices.size() < 3) {
            user.sendError("A polygon must have a least three vertices");
            return false;
        }
        // The polygon must not intersect any other sibling zones unless completely containing them
        for (Zone sibling : zone.getParent().getChildren()) {
            if (sibling != zone && !sibling.isEmpty() && intersects(sibling.getPolygon().getVertices(), workingVertices)) {
                if (sibling.getPolygon().getFloor() < workingCeiling && sibling.getPolygon().getCeiling() > workingFloor) {
                    user.sendError(RealmsTranslate.transformMessage("block.dual.claim", sibling.getName()));
                    return false;
                }
            }
            else if (!sibling.isEmpty() && workingVerticesContain(sibling.getPolygon()) && sibling.getName() != zone.getName()) {
                user.sendMessage(RealmsTranslate.transformMessage("sibling.move", sibling.getName()));
                zone.getParent().removeChild(sibling);
                sibling.setParent(zone);
            }
        }
        // The polygon must contain all zone children
        for (Zone child : zone.getChildren()) {
            if (!workingVerticesContain(child.getPolygon())) {
                user.sendError(RealmsTranslate.transMessage("polygon.no.contain"));
                return false;
            }
        }

        // The polygon must not contain intersecting lines
        if (intersects(workingVertices, workingVertices)) {
            user.sendError(RealmsTranslate.transMessage("line.intersect"));

            workingVertices = reorgPoints(workingVertices);
            if (intersects(workingVertices, workingVertices)) {
                user.sendError(RealmsTranslate.transMessage("reorg.fail"));
                return false;
            }
        }

        // All checks passed: vertex is valid
        return true;
    }

    public final Point getClosestPoint(Point temp) {
        Iterator<Point> pointIt = vertices.iterator();
        Point closest = null;
        while (pointIt.hasNext()) {
            Point check = pointIt.next();
            if (closest == null) {
                closest = check;
            }
            else if (check.distance2D(temp) < closest.distance2D(temp)) {
                closest = check;
            }
        }
        return closest;
    }

    private final LinkedList<Point> reorgPoints(List<Point> points) {
        List<Point> tempPoints = new LinkedList<Point>(points);
        LinkedList<Point> toRet = new LinkedList<Point>();
        Collections.copy(tempPoints, points);
        Point start = null;
        Point negZ = null;

        for (Point point : tempPoints) {
            //Most Positive Z and Start point (North side)
            if (start == null) {
                start = point;
            }
            else if (point.z > start.z) {
                start = point;
            }
            else if (point.z == start.z && point.x <= centroid.x && point.x > start.x) {
                start = point;
            }

            //Most Negative Z Point (South side)
            if (negZ == null) {
                negZ = point;
            }
            else if (point.z < negZ.z) {
                negZ = point;
            }
            else if (point.z == negZ.z && point.x < negZ.x && point.x >= centroid.x) {
                negZ = point;
            }
        }

        //Remove the start point from our temp list
        tempPoints.remove(start);
        //Add the start point to the returning list
        toRet.add(start);

        //Find point nearest to Start point going counter-clockwise (-x)
        while (tempPoints.size() > 1) {
            Iterator<Point> pointIt = tempPoints.iterator();
            Point next = null;
            while (pointIt.hasNext()) {
                Point check = pointIt.next();
                if (next == null) {
                    next = check;
                }
                else if (check.distance2D(start) < next.distance2D(start) && (check.x <= centroid.x)) {
                    next = check;
                }
            }
            tempPoints.remove(next);
            toRet.add(next);
            start = next;
            RealmsLogMan.log(RLevel.GENERAL, "Point Reorg Debug - X:" + start.x + " Z:" + start.z);
            if (negZ == start) {
                //Current point is now the far side of the polygon, begin coming back around
                break;
            }
        }

        //Complete the oposite half of the polygon
        while (tempPoints.size() > 1) {
            Iterator<Point> pointIt = tempPoints.iterator();
            Point next = null;
            while (pointIt.hasNext()) {
                Point check = pointIt.next();
                if (next == null) {
                    next = check;
                }
                else if (check.distance2D(start) < next.distance2D(start) && (check.x >= centroid.x)) {
                    next = check;
                }
            }
            tempPoints.remove(next);
            toRet.add(next);
            start = next;
            RealmsLogMan.log(RLevel.GENERAL, "Point Reorg Debug - X:" + start.x + " Z:" + start.z);
        }

        toRet.add(tempPoints.get(0)); //And add the last point to our returning list
        RealmsLogMan.log(RLevel.GENERAL, "Point Reorg Debug - X:" + tempPoints.get(0).x + " Z:" + tempPoints.get(0).z);

        return toRet;
    }

    public static final int calculateArea(List<Point> points) {
        if (points.size() < 3) {
            return 0;
        }
        int areaCalc = 0;
        Point last = points.get(points.size() - 1);
        for (Point p : points) {
            areaCalc += p.x * last.z - last.x * p.z;
            last = p;
        }
        areaCalc = Math.abs(areaCalc / 2);
        return areaCalc;
    }

    public static final int calculateVolume(List<Point> points, int floor, int ceiling) {
        int height = ceiling - floor;
        return calculateArea(points) * height;
    }

    private static final boolean intersects(List<Point> list1, List<Point> list2) {
        List<Line> lines1 = getLines(list1);
        List<Line> lines2 = getLines(list2);
        for (Line line1 : lines1) {
            for (Line line2 : lines2) {
                if (line1.intersects2DIgnorePoints(line2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final List<Line> getLines(List<Point> points) {
        List<Line> results = new LinkedList<Line>();
        if (points.size() < 2) {
            return results;
        }
        Point last = points.get(points.size() - 1);
        for (Point p : points) {
            results.add(new Line(p, last));
            last = p;
        }
        return results;
    }

    @Override
    public final String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append(ceiling);
        builder.append(",");
        builder.append(floor);
        for (Point vertex : vertices) {
            builder.append(",");
            builder.append(vertex.x);
            builder.append(",");
            builder.append(vertex.y);
            builder.append(",");
            builder.append(vertex.z);
        }
        return builder.toString();
    }
}
