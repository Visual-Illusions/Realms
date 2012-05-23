package net.visualillusionsent.realms.zones;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Zone/Permission Lists
 * 
 * @author darkdiplomat
 */
public class ZoneLists {
    private List<Zone> zones;
    private Hashtable<ICModPlayer, List<Zone>> playerZoneList;
    private List<ICModPlayer> inRestricted;
    private List<ICModPlayer> inHealing;
    private static ZoneLists inst;
    
    public ZoneLists(RHandle rhand){
        zones = new ArrayList<Zone>();
        playerZoneList = new Hashtable<ICModPlayer, List<Zone>>();
        inRestricted = new ArrayList<ICModPlayer>();
        inHealing = new ArrayList<ICModPlayer>();
        inst = this;
     }
    
    /**
     * Add a Zone to list
     * 
     * @param zone
     */
    public static void addZone(Zone zone){
        inst.zones.add(zone);
    }
    
    /**
     * Removes a Zone from the list
     * 
     * @param zone
     */
    public static void removeZone(Zone zone){
        inst.zones.remove(zone);
    }
    
    /**
     * Returns the list of Zones
     * 
     * @return List<Zone> zones
     */
    public static List<Zone> getZones(){
        return inst.zones;
    }
    
    /**
     * Checks if Zone is in Zone
     * 
     * @param zone
     * @return true if yes, false if not
     */
    public boolean isZone(Zone zone){
        return zones.contains(zone);
    }
    
    /**
     * Gets a Zone from a name
     * 
     * @param name
     * @return zone
     * @throws ZoneNotFoundException
     */
    public static Zone getZoneByName(String name) throws ZoneNotFoundException {
        for(Zone zone : inst.zones){
            if(zone.getName().equalsIgnoreCase(name)){
                return zone;
            }
        }
        throw new ZoneNotFoundException();
    }
    
    /** 
     * Finds the smallest zone that the block is contained by
     * 
     * @param zone
     * @param mob
     * @return zone
     */
    public static Zone getZone(Zone zone, ICModMob mob) {
        if(!zone.getChildren().isEmpty()){
            for(Zone child : zone.getChildren()) {
                if(child.contains(mob)) {
                    return getZone(child, mob);
                }
            }
        }
        return zone;
    }
    
    public static Zone getZone(Zone zone, ICModPlayer player) {
        if(!zone.getChildren().isEmpty()){
            for(Zone child : zone.getChildren()) {
                if(child.contains(player)) {
                    return getZone(child, player);
                }
            }
        }
        return zone;
    }
    
    public static Zone getZone(Zone zone, ICModBlock block) {
        if(!zone.getChildren().isEmpty()){
            for(Zone child : zone.getChildren()) {
                if(child.contains(block)) {
                    return getZone(child, block);
                }
            }
        }
        return zone;
    }
    
    /**
     * Returns a list of Zones a Player is in
     * 
     * @param zone
     * @param player
     * @return List<Zone> Zones Player is In
     */
    public static List<Zone> getZonesPlayerIsIn(Zone zone, ICModPlayer player) {
        List<Zone> newZoneList = new ArrayList<Zone>();
        newZoneList.add(zone);
        for(Zone child : zone.getChildren()) {
            if(child.contains(player)) {
                newZoneList.addAll(getZonesPlayerIsIn(child, player));
                return newZoneList;
            }
        }
        return newZoneList;
    }
    
    /**
     * Returns a list of Zones a Player is in
     * 
     * @param player
     * @return List<Zone> Zones Player is In
     */
    public static List<Zone> getplayerZones(ICModPlayer player){
        if(inst.playerZoneList.containsKey(player.getName())){
            List<Zone> pzone = new ArrayList<Zone>();
            pzone.addAll(inst.playerZoneList.get(player.getName()));
            return pzone;
        }
        return null;
    }
    
    /**
     * Removes a Zone from a Player's Zone list
     * 
     * @param zone
     */
    public static void removeZonefromPlayerZoneList(Zone zone){
        for(ICModPlayer key : inst.playerZoneList.keySet()){
            if(inst.playerZoneList.get(key).contains(zone)){
                inst.playerZoneList.get(key).remove(zone);
            }
        }
    }
    
    /**
     * Adds a Zone to a Player's Zone list
     * 
     * @param player
     * @param zones
     */
    public static void addplayerzones(ICModPlayer player, List<Zone> zones){
        inst.playerZoneList.put(player, zones);
    }
    
    public static boolean isInRestricted(ICModPlayer cPlayer){
        return inst.inRestricted.contains(cPlayer);
    }
    
    public static void addInRestricted(ICModPlayer player){
        inst.inRestricted.add(player);
    }
    
    public static void removeInRestricted(ICModPlayer player){
        if(inst.inRestricted.contains(player)){
            inst.inRestricted.remove(player);
        }
    }
    
    public static List<ICModPlayer> getInRestricted(){
        return inst.inRestricted;
    }
    
    public static void addInHealing(ICModPlayer player){
        inst.inHealing.add(player);
    }
    
    public static void removeInHealing(ICModPlayer player){
        if(inst.inHealing.contains(player)){
            inst.inHealing.remove(player);
        }
    }
    
    public static List<ICModPlayer> getInHealing(){
        return inst.inHealing;
    }
}
