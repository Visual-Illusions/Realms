package net.visualillusionsent.realms.zones.polygons;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.FlatFileDeleter;
import net.visualillusionsent.realms.io.FlatFileSaver;
import net.visualillusionsent.realms.io.MySQLDeleter;
import net.visualillusionsent.realms.io.MySQLSaver;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms PolygonArea Class
 * 
 * @author darkdiplomat
 */
public class PolygonArea {
    private Zone zone;
    private LinkedList<Point> vertices = new LinkedList<Point>();
    private LinkedList<Point> workingVertices = new LinkedList<Point>();
    private int ceiling;
    private int floor;
    private int workingCeiling;
    private int workingFloor;
    private String mode = "saved";

    private Point centroid = null;
    private double radius = 0;

    private RHandle rhandle;

    /**
     * PolygonArea Constructor
     * 
     * @param realm
     * @param zone
     */
    public PolygonArea(RHandle rhandle, Zone zone) {
        this.zone = zone;
        this.ceiling = 1000;
        this.floor = 0;
        this.rhandle = rhandle;
    }

    /**
     * PolygonArea Constructor from CSV Format
     * 
     * @param realm
     * @param split
     * @throws ZoneNotFoundException
     */
    public PolygonArea(RHandle rhandle, Zone zone, String[] args) {
        this.zone = zone;
        this.ceiling = Integer.parseInt(args[1]);
        this.floor = Integer.parseInt(args[2]);
        for (int i = 3; i < args.length; i += 3) {
            vertices.add(new Point(Integer.parseInt(args[i]), Integer.parseInt(args[i + 1]), Integer.parseInt(args[i + 2])));
        }
        zone.setPolygon(this);
        centroid = calculateCentroid(vertices);
        radius = calculateRadius(vertices, centroid);
        this.rhandle = rhandle;
    }

    /**
     * Returns this Polygon to a String
     * 
     * @return String CSV format Polygon
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append(zone.getName());
        builder.append(",");
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

    /*
     * Accessor Methods
     */
    public Zone getZone() {
        return zone;
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public int getCeiling() {
        return ceiling;
    }

    public int getFloor() {
        return floor;
    }

    public Point getCentroid() {
        return centroid;
    }

    public double getRadius() {
        return radius;
    }

    public int getArea() {
        return calculateArea(vertices);
    }

    public String getMode() {
        return mode;
    }

    public boolean isEmpty() {
        return vertices.size() < 3;
    }

    public boolean workingVerticesCleared() {
        return workingVertices.size() == 0;
    }

    /*
     * Mutator Methods
     */
    public void setWorkingCeiling(int ceiling) {
        this.workingCeiling = ceiling;
    }

    public void setWorkingFloor(int floor) {
        this.workingFloor = floor;
    }

    /**
     * Saves Polygon
     */
    public void save() {
        if (workingVertices.isEmpty()) {
            vertices = new LinkedList<Point>();
            floor = 0;
            ceiling = 1000;
            this.mode = "saved";

            centroid = null;
            radius = 0;
        }
        else {
            vertices = new LinkedList<Point>(workingVertices);
            workingVertices = new LinkedList<Point>();
            floor = workingFloor;
            ceiling = workingCeiling;
            this.mode = "saved";

            centroid = calculateCentroid(vertices);
            radius = calculateRadius(vertices, centroid);
        }
        if (RealmsProps.getMySQL()) {
            rhandle.executeTask(new MySQLSaver(rhandle, this));
        }
        else {
            rhandle.executeTask(new FlatFileSaver(rhandle, this));
        }
    }

    /**
     * Calculates Centroid
     * 
     * @param points
     * @return Point
     */
    public Point calculateCentroid(List<Point> points) {
        double x = 0;
        double z = 0;

        for (Point p : points) {
            x += p.x;
            z += p.z;
        }

        return new Point((int) Math.floor(x / points.size()), (int) Math.floor(ceiling - (ceiling - floor) / 2), (int) Math.floor(z / points.size()));
    }

    /**
     * Calculates Radius
     * 
     * @param points
     * @param c
     * @return max radius
     */
    public static double calculateRadius(List<Point> points, Point c) {
        double max = 0;

        for (Point p : points) {
            double distance = c.distance2D(p);

            if (distance > max) {
                max = distance;
            }
        }

        return max;
    }

    public List<Point> edit() {
        this.mode = "edit";
        workingFloor = floor;
        workingCeiling = ceiling;
        workingVertices = new LinkedList<Point>(vertices);
        return workingVertices;
    }

    /**
     * Cancels Editing
     */
    public void cancelEdit() {
        this.mode = "saved";
        this.workingVertices.clear();
    }

    /**
     * Deletes Zone
     */
    public void delete() {
        this.mode = "deleted";
        if (RealmsProps.getMySQL()) {
            rhandle.executeTask(new MySQLDeleter(rhandle, this));
        }
        else {
            rhandle.executeTask(new FlatFileDeleter(rhandle, this));
        }
    }

    /**
     * Removes Working Vertex
     * 
     * @param location
     */
    public void removeWorkingVertex(ICModBlock block) {
        Iterator<Point> itr = workingVertices.iterator();
        while (itr.hasNext()) {
            Point p = itr.next();
            if (p.x == block.getX() && p.z == block.getZ()) {
                itr.remove();
            }
        }
    }

    /**
     * Checks if the Polygon Area contains a player
     * 
     * @param player
     * @return true if it does, false if not
     */
    public boolean contains(ICModPlayer player) {
        Point p = new Point((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
        return this.contains(p, true);
    }

    /**
     * Checks if the Polygon Area contains a mob
     * 
     * @param player
     * @return true if it does, false if not
     */
    public boolean contains(ICModMob mob) {
        Point p = new Point((int) Math.floor(mob.getX()), (int) Math.floor(mob.getY()), (int) Math.floor(mob.getZ()));
        return this.contains(p, true);
    }

    /**
     * Checks if the Polygon Area contains a block
     * 
     * @param player
     * @return true if it does, false if not
     */
    public boolean contains(ICModBlock block) {
        Point p = new Point(block.getX(), block.getY(), block.getZ());
        return this.contains(p, true);
    }

    /***************************************************************************
     * INPOLY.C * * Copyright (c) 1995-1996 Galacticomm, Inc. Freeware source
     * code. * * http://www.visibone.com/inpoly/inpoly.c * * 6/19/95 - Bob Stein
     * & Craig Yap * stein@visibone.com * craig@cse.fau.edu *
     ***************************************************************************/
    public static boolean contains(List<Point> points, Point p, int floor, int ceiling) {
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
                    && (p.z - z1) * (x2 - x1)
                    < (z2 - z1) * (p.x - x1)) {
                inside = !inside;
            }
            oldPoint = newPoint;
        }
        return inside;
    }

    /* End INPOLY */

    public boolean contains(Point p, boolean checkRadius) {
        if (this.centroid == null) {
            return false;
        }

        if (checkRadius && this.centroid.distance2D(p) > this.radius) {
            return false;
        }
        return contains(vertices, p, this.floor, this.ceiling);
    }

    public boolean workingVerticesContain(PolygonArea polygonArea) {

        for (Point p : polygonArea.getVertices()) {
            if (!contains(workingVertices, p, this.workingFloor, this.workingCeiling)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsWorkingVertex(ICModBlock block) {
        for (Point p : workingVertices) {
            if (p.x == block.getX() && p.z == block.getZ()) {
                return true;
            }
        }
        return false;
    }

    // Adds the vertex to the working list
    // Returns a list of removed vertices
    public List<Point> addVertex(ICModPlayer player, ICModBlock block) {
        List<Point> removed = new LinkedList<Point>();
        Point newVertex = new Point(block.getX(), block.getY(), block.getZ());

        // Case #1: The vertex list has less than three points
        // Just add the point to the end of the working vertices list
        if (workingVertices.size() < 3) {
            workingVertices.add(newVertex);
            return removed;
        }

        // Case #2: Adding the vertex to the end of the working vertices list creates a valid polygon
        // Just add the point to the end of the working vertices list
        workingVertices.add(newVertex);
        if (validPolygon(player)) {
            return removed;
        }

        // Case #3: Adding the vertex to the end of the working vertices list does not create a valid polygon
        // Insert the polygon into place between the two nearest polygons

        // Remove the new vertex from the end of the working vertices list from case #2
        workingVertices.remove(newVertex);
        // Find the two nearest points (p1 & p2)
        Point p1 = null;
        Point p2 = null;
        for (Point p : workingVertices) {
            if (p1 == null || newVertex.distance2D(p1) > newVertex.distance2D(p)) {
                p2 = p1;
                p1 = p;
            }
            else if (p2 == null || newVertex.distance2D(p2) > newVertex.distance2D(p)) {
                p2 = p;
            }
        }
        if (p1 == null || p2 == null) {
            workingVertices.add(newVertex);
        }

        // Remove working vertices between p1 and p2
        int start = 0;
        int end = 0;
        if (workingVertices.indexOf(p1) > workingVertices.indexOf(p2)) {
            start = workingVertices.indexOf(p2) + 1;
            end = workingVertices.indexOf(p1);
        }
        else {
            start = workingVertices.indexOf(p1) + 1;
            end = workingVertices.indexOf(p2);
        }

        for (Point p : workingVertices.subList(start, end)) {
            removed.add(p);
        }
        workingVertices.subList(start, end).clear();

        // Finally, add the new vertex between p1 and p2
        workingVertices.add(start, newVertex);

        return removed;
    }

    /**
     * Tests if the vertex is valid
     * 
     * @param block
     * @param player
     * @return whether the vertex is valid
     */
    public boolean validVertex(ICModPlayer player, ICModBlock block) {
        // The vertex must be contained by the parent zone
        if (!zone.getParent().contains(block)) {
            player.notify("Block not contained within " + zone.getParent().getName());
            return false;
        }
        // The vertex must not be contained by sibling zones
        for (Zone sibling : zone.getParent().getChildren()) {
            if (sibling != zone && sibling.contains(block)) {
                player.notify("Block already claimed by a sibling zone: " + sibling.getName());
                return false;
            }
        }
        // The vertex must not already be in the vertex list
        if (containsWorkingVertex(block)) {
            player.notify("Warning: This column of blocks is already in the vertex list.");
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
    public boolean validPolygon(ICModPlayer player) {
        // A polygon must have a least three sides
        if (workingVertices.size() < 3) {
            player.notify("A polygon must have a least three vertices");
            return false;
        }
        // The polygon must not intersect any other sibling zones unless completely containing them
        for (Zone sibling : zone.getParent().getChildren()) {
            if (sibling != zone && !sibling.isEmpty() && intersects(sibling.getPolygon().getVertices(), workingVertices)) {
                if (sibling.getPolygon().getFloor() < workingCeiling && sibling.getPolygon().getCeiling() > workingFloor) {
                    player.notify("A block enclosed by this polygon is already claimed by " + sibling.getName() + ".");
                    return false;
                }
            }
            else if (!sibling.isEmpty() && workingVerticesContain(sibling.getPolygon()) && sibling.getName() != zone.getName()) {
                player.notify("Moving " + sibling.getName() + " into child list of current working zone.");
                zone.getParent().removeChild(sibling);
                sibling.setParent(zone);
            }
        }
        // The polygon must contain all zone children
        for (Zone child : zone.getChildren()) {
            if (!workingVerticesContain(child.getPolygon())) {
                player.notify("New zone boundries do not contain all zone children!");
                return false;
            }
        }

        // The polygon must not contain intersecting lines
        if (intersects(workingVertices, workingVertices)) {
            player.notify("Warning: Polygon line intersection!");
            //TODO reorder points?

            //return false; NOTE: Testing by-pass
        }

        // All checks passed: vertex is valid
        return true;
    }

    /**
     * Calculate the area of a polygon defined by a list of points
     * 
     * @param points
     *            the points to calculate the area from
     * @return the 2d area of a polygon
     */
    public static int calculateArea(List<Point> points) {
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

    /**
     * Calculate the volume of a polygon with a ceiling and floor
     * 
     * @param points
     *            the points in the polygon
     * @param floor
     * @param ceiling
     * @return The volume of the area
     */
    public static int calculateVolume(List<Point> points, int floor, int ceiling) {
        int height = ceiling - floor;
        return calculateArea(points) * height;
    }

    /**
     * Tests two lists of points for polygon intersection Should probably use
     * ANY-SEGMENTS-INTERSECT for performance, not a big deal though
     * 
     * @param list1
     *            the first list of points
     * @param list2
     *            the second list of points
     * @return whether the points intersect
     */
    private static boolean intersects(List<Point> list1, List<Point> list2) {
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

    /**
     * Gets a list of lines made by the points
     * 
     * @param points
     *            the points to get the lines from
     * @return all lines made by the list of points
     */
    private static List<Line> getLines(List<Point> points) {
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
}
