package net.visualillusionsent.mcplugin.realms.zones.polygon;

import java.util.Comparator;

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
