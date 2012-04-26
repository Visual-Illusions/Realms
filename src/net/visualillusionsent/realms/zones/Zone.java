package net.visualillusionsent.realms.zones;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.InvaildZoneFlagException;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.polygons.PolygonArea;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ChatColor;

/**
 * Realms Zone Class
 * 
 * @author darkdiplomat
 */
public class Zone {
    private String name, world;
    private int dimension = 0;
    private Zone parent;
    private String greeting;
    private String farewell;
    private PolygonArea polygon;
    private RHandle rhandle;
    private List<Permission> zoneperms = new ArrayList<Permission>();
    private String flagform = "\u00A76%s\u00A7B= %s %s";
    
    private ZoneFlag pvp; // OFF = pvp disabled, ON = pvp enabled
    private ZoneFlag sanctuary; // OFF = zone is not a sanctuary, ON = zone is a sanctuary
    private ZoneFlag creeper; // OFF = creepers may not explode, ON = creepers may explode
    private ZoneFlag fall; //OFF = No Fall Damage, ON = Fall Damage
    private ZoneFlag suffocate; //OFF = No Suffocation, ON = Suffocation
    private ZoneFlag fire; //OFF = NO Fire Damage, ON = Fire Damage
    private ZoneFlag animals; //OFF = NO Animals, ON = Animals
    private ZoneFlag ghast; // OFF = Ghast fireballs may not explode, ON = Ghast fireballs may explode
    private ZoneFlag physics; // OFF = Sand/Gravel may not fall, ON = Sand/Gravel may fall
    private ZoneFlag pistons; //OFF = Pistons may not function, ON = Pistons may function
    private ZoneFlag creative; //OFF = Survival Mode, ON = Creative Mode
    private ZoneFlag enderman; //OFF = No Enderman may not PickUp/Place Blocks. ON = Enderman may PickUp/Place Blocks
    private ZoneFlag spread; //OFF = Fire may not spread. ON = Fire may spread.
    private ZoneFlag flow; //OFF = Water/Lava may not flow. ON = Water/Lava may flow.
    private ZoneFlag TNT; //OFF = TNT may not explode. ON = TNT may explode.
    private ZoneFlag healing; //Changed to boolean --  OFF = No Healing. ON = Healing.
    private ZoneFlag potion; //OFF = no potion damage, ON = potions can damage
    private ZoneFlag starve; //OFF = no Starvation, ON = Starvation
    private ZoneFlag restricted; //OFF = Allow All, ON = Those without Authed permission take damage
    private ZoneFlag respawn; //OFF = not a respawn zone, ON = is a respawn zone
    
    public enum ZoneFlag {
        ON (true), OFF (false), INHERIT (false), NULL (false);
        
        private boolean value;
        
        private ZoneFlag (boolean value) {
            this.value = value;
        }
        
        public boolean getValue() {
            return value;
        }
        
        public static ZoneFlag getZoneFlag(String type) throws InvaildZoneFlagException {
            try {
                return ZoneFlag.valueOf(type.toUpperCase());
            }catch(IllegalArgumentException IAE){
                throw new InvaildZoneFlagException();
            }
        }
    }
    
    private List<Zone> children = new ArrayList<Zone>();

    // Regular Constructor
    public Zone(RHandle rhandle, String name, Zone parent, String world, int dimension){
        this.rhandle = rhandle;
        this.name = name;
        this.parent = parent;
        this.polygon = null;
        this.children = new ArrayList<Zone>();
        this.greeting = null;
        this.farewell = null;
        
        setDefaults(parent == null);

        if(parent != null && !parent.getChildren().contains(this)){
            parent.children.add(this);
        }
        if(name.startsWith("EVERYWHERE")){
            this.rhandle.addEverywhere(this);
        }
        this.world = world;
        this.dimension = dimension;
        ZoneLists.addZone(this);
        if(name.startsWith("EVERYWHERE")){
            rhandle.addEverywhere(this);
        }
        this.rhandle.log(Level.INFO, "Zone created: " + name);
        rhandle.getDataSource().saveZone(this);
    }

    // CSV File Constructor
    public Zone(RHandle rhandle, String[] args){
        this.rhandle = rhandle;
        this.name = args[0];
        this.world = args[1];
        this.dimension = Integer.valueOf(args[2]);
        if(name.startsWith("EVERYWHERE")){
            this.parent = null;
        }
        else{
            try {
                this.parent = ZoneLists.getZoneByName(args[3]);
            }
            catch (ZoneNotFoundException ZNFE) {
                this.parent = rhandle.getEverywhere(world, dimension);
            }
        }
        
        if(args.length < 5 || args[4] == null || args[4].equalsIgnoreCase("null")){
            this.greeting = null;
        }
        else{
            this.greeting = args[4];
        }
        if(args.length < 6 || args[5] == null || args[5].equalsIgnoreCase("null")){
            this.farewell = null;
        }
        else{
            this.farewell = args[5];
        }
        
        if(args.length < 7) {
            setDefaults(name.toUpperCase().startsWith("EVERYWHERE")); 
        } 
        else {
            try {
                this.pvp = ZoneFlag.getZoneFlag(args[6]);
            }catch (Exception e) {
                this.pvp = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.pvp = ZoneFlag.ON;
                }
            }
            try{
                this.sanctuary = ZoneFlag.getZoneFlag(args[7]);
            }catch (Exception e) {
                this.sanctuary = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.sanctuary = ZoneFlag.OFF;
                }
            }
            try{
                this.creeper = ZoneFlag.getZoneFlag(args[8]);
            }catch (Exception e) {
                this.creeper = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.creeper = ZoneFlag.ON;
                }
            }
            try{
                this.ghast = ZoneFlag.getZoneFlag(args[9]);
            }catch (Exception e) {
                this.ghast = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.ghast = ZoneFlag.ON;
                }
            }
            try{
                this.fall = ZoneFlag.getZoneFlag(args[10]);
            }catch (Exception e) {
                this.fall = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.fall = ZoneFlag.ON;
                }
            }
            try{
                this.suffocate = ZoneFlag.getZoneFlag(args[11]);
            }catch (Exception e) {
                this.suffocate = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.suffocate = ZoneFlag.ON;
                }
            }
            try{
                this.fire = ZoneFlag.getZoneFlag(args[12]);
            }catch (Exception e) {
                this.fire = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.fire = ZoneFlag.ON;
                }
            }
            try{
                this.animals = ZoneFlag.getZoneFlag(args[13]);
            }catch (Exception e) {
                this.animals = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.animals = ZoneFlag.ON;
                }
            }
            try{
                this.physics = ZoneFlag.getZoneFlag(args[14]);
            }catch (Exception e) {
                this.physics = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.physics = ZoneFlag.ON;
                }
            }
            try{
                this.creative = ZoneFlag.getZoneFlag(args[15]);
            }catch (Exception e) {
                this.creative = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.creative = ZoneFlag.OFF;
                }
            }
            try{
                this.pistons = ZoneFlag.getZoneFlag(args[16]);
            }catch (Exception e) {
                this.pistons = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.pistons = ZoneFlag.ON;
                }
            }
            try{
                this.healing = ZoneFlag.getZoneFlag(args[17]);
            }catch (Exception e) {
                this.healing = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.healing = ZoneFlag.OFF;
                }
            }
            try{
                this.enderman = ZoneFlag.getZoneFlag(args[18]);
            }catch (Exception e) {
                this.enderman = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.enderman = ZoneFlag.ON;
                }
            }
            try{
                this.spread = ZoneFlag.getZoneFlag(args[19]);
            }catch (Exception e) {
                this.spread = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.spread = ZoneFlag.ON;
                }
            }
            try{
                this.flow = ZoneFlag.getZoneFlag(args[20]);
            }catch (Exception e) {
                this.flow = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.flow = ZoneFlag.ON;
                }
            }
            try{
                this.TNT = ZoneFlag.getZoneFlag(args[21]);
            }catch (Exception e) {
                this.TNT = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.TNT = ZoneFlag.ON;
                }
            }
            try{
                this.potion = ZoneFlag.getZoneFlag(args[22]);
            }catch (Exception e) {
                this.potion = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.potion = ZoneFlag.ON;
                }
            }
            try{
                this.starve = ZoneFlag.getZoneFlag(args[23]);
            }catch (Exception e) {
                this.starve = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.starve = ZoneFlag.ON;
                }
            }
            try{
                this.restricted = ZoneFlag.getZoneFlag(args[24]);
            }catch (Exception e) {
                this.restricted = ZoneFlag.INHERIT;
                if(name.toUpperCase().startsWith("EVERYWHERE")){
                    this.restricted = ZoneFlag.ON;
                }
            }
            if(name.toUpperCase().startsWith("EVERYWHERE")){
                this.respawn = ZoneFlag.OFF; //SHOULD ALWAYS BE OFF FOR EVERYWHERE
            }
            else{
                try{
                    this.respawn = ZoneFlag.getZoneFlag(args[25]);
                }catch (Exception e) {
                    this.respawn = ZoneFlag.INHERIT;
                }
            }
        }
        this.children = new ArrayList<Zone>();
        if(parent != null && !parent.getChildren().contains(this)){
            parent.children.add(this);
        }
        if(name.startsWith("EVERYWHERE")){
            rhandle.addEverywhere(this);
        }
        ZoneLists.addZone(this);
    }

    private void setDefaults(boolean isEverywhere) {
        if (isEverywhere) {
            this.pvp = ZoneFlag.ON;
            this.sanctuary = ZoneFlag.OFF;
            this.creeper = ZoneFlag.ON;
            this.ghast = ZoneFlag.ON;
            this.fall = ZoneFlag.ON;
            this.suffocate = ZoneFlag.ON;
            this.fire = ZoneFlag.ON;
            this.animals = ZoneFlag.ON;
            this.physics = ZoneFlag.ON;
            this.creative = ZoneFlag.OFF;
            this.pistons = ZoneFlag.ON;
            this.healing = ZoneFlag.OFF;
            this.enderman = ZoneFlag.ON;
            this.spread = ZoneFlag.ON;
            this.flow = ZoneFlag.ON;
            this.TNT = ZoneFlag.ON;
            this.potion = ZoneFlag.ON;
            this.starve = ZoneFlag.ON;
            this.restricted = ZoneFlag.OFF;
            this.respawn = ZoneFlag.OFF;
        } 
        else {
            this.pvp = ZoneFlag.INHERIT;
            this.sanctuary = ZoneFlag.INHERIT;
            this.creeper = ZoneFlag.INHERIT;
            this.ghast = ZoneFlag.INHERIT;
            this.fall = ZoneFlag.INHERIT;
            this.suffocate = ZoneFlag.INHERIT;
            this.fire = ZoneFlag.INHERIT;
            this.animals = ZoneFlag.INHERIT;
            this.physics = ZoneFlag.INHERIT;
            this.creative = ZoneFlag.INHERIT;
            this.pistons = ZoneFlag.INHERIT;
            this.healing = ZoneFlag.INHERIT;
            this.enderman = ZoneFlag.INHERIT;
            this.spread = ZoneFlag.INHERIT;
            this.flow = ZoneFlag.INHERIT;
            this.TNT = ZoneFlag.INHERIT;
            this.potion = ZoneFlag.INHERIT;
            this.starve = ZoneFlag.INHERIT;
            this.restricted = ZoneFlag.INHERIT;
            this.respawn = ZoneFlag.INHERIT;
        }
    }
    
    /*
     * Accessor Methods
     */

    public String getName() {return name; }
    public Zone getParent() {return parent; }
    public String getWorld() { return world; }
    public int getDimension() { return dimension; }
    public String getGreeting() { return greeting; }
    public String getFarewell() { return farewell; }
    public List<Zone> getChildren() {return children; }
    public PolygonArea getPolygon() {return polygon; }
    
    public ZoneFlag getAbsolutePVP() { return pvp; }
    public ZoneFlag getAbsoluteSanctuary() { return sanctuary; }
    public ZoneFlag getAbsoluteCreeper() { return creeper; }
    public ZoneFlag getAbsoluteGhast() { return ghast; }
    public ZoneFlag getAbsoluteFall() { return fall; }
    public ZoneFlag getAbsoluteSuffocate() { return suffocate; }
    public ZoneFlag getAbsoluteFire() { return fire; }
    public ZoneFlag getAbsoluteAnimals(){ return animals; }
    public ZoneFlag getAbsolutePhysics(){ return physics; }
    public ZoneFlag getAbsoluteCreative(){ return creative; }
    public ZoneFlag getAbsolutePistons(){ return pistons;}
    public ZoneFlag getAbsoluteHealing() { return healing;}
    public ZoneFlag getAbsoluteEnderman() { return enderman;}
    public ZoneFlag getAbsoluteSpread() { return spread;}
    public ZoneFlag getAbsoluteFlow() { return flow;}
    public ZoneFlag getAbsoluteTNT() { return TNT;}
    public ZoneFlag getAbsolutePotion() { return potion;}
    public ZoneFlag getAbsoluteStarve(){ return starve; }
    public ZoneFlag getAbsoluteRestricted(){ return restricted; }
    public ZoneFlag getAbsoluteRespawn(){ return respawn; }

    public boolean getPVP() {
        if (this.pvp.equals(ZoneFlag.INHERIT) && this.parent != null) {
            return parent.getPVP();
        } else {
            return this.pvp.getValue();
        }
    }
    
    public boolean getSanctuary() {
        if (this.sanctuary.equals(ZoneFlag.INHERIT) && this.parent != null) {
            return parent.getSanctuary();
        } else {
            return this.sanctuary.getValue();
        }
    }
    
    public boolean getCreeper() {
        if (this.creeper.equals(ZoneFlag.INHERIT) && this.parent != null) {
            return parent.getCreeper();
        } else {
            return this.creeper.getValue();
        }
    }
    
    public boolean getGhast() {
        if (this.ghast.equals(ZoneFlag.INHERIT) && this.parent != null) {
            return parent.getGhast();
        } else {
            return this.ghast.getValue();
        }
    }
    
    public boolean getFall() {
        if (this.fall.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getFall();
        }else{
            return this.fall.getValue();
        }
    }
    
    public boolean getSuffocate() {
        if (this.suffocate.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getSuffocate();
        }else{
            return this.suffocate.getValue();
        }
    }
    
    public boolean getFire() {
        if (this.fire.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getFire();
        }else{
            return this.fire.getValue();
        }
    }
    
    public boolean getAnimals() {
        if (this.animals.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getAnimals();
        }else{
            return this.animals.getValue();
        }
    }
    
    public boolean getPhysics() {
        if (this.physics.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getPhysics();
        }else{
            return this.physics.getValue();
        }
    }
    
    public boolean getCreative() {
        if (this.creative.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getCreative();
        }else{
            return this.creative.getValue();
        }
    }
    
    public boolean getPistons() {
        if (this.pistons.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getPistons();
        }else{
            return this.pistons.getValue();
        }
    }
    
    public boolean getHealing() {
        if (this.healing.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getHealing();
        }else{
            return this.healing.getValue();
        }
    }
    
    public boolean getEnderman() {
        if (this.enderman.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getEnderman();
        }else{
            return this.enderman.getValue();
        }
    }
    
    public boolean getSpread() {
        if (this.spread.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getSpread();
        }else{
            return this.spread.getValue();
        }
    }
    
    public boolean getFlow() {
        if (this.flow.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getFlow();
        }else{
            return this.flow.getValue();
        }
    }
    
    public boolean getTNT() {
        if (this.TNT.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getTNT();
        }else{
            return this.TNT.getValue();
        }
    }
    
    public boolean getPotion() {
        if (this.potion.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getPotion();
        }else{
            return this.potion.getValue();
        }
    }
    
    public boolean getStarve() {
        if (this.starve.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getStarve();
        }else{
            return this.starve.getValue();
        }
    }
    
    public boolean getRestricted() {
        if (this.restricted.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getRestricted();
        }else{
            return this.restricted.getValue();
        }
    }
    
    public boolean getRespawn() {
        if (this.respawn.equals(ZoneFlag.INHERIT) && this.parent != null){
            return parent.getRespawn();
        }else{
            return this.respawn.getValue();
        }
    }
    
    /*
     * Mutator Methods
     */

    public void removeChild(Zone child) {
        children.remove(child);
    }

    public void setPolygon(PolygonArea polygon) {
        this.polygon = polygon;
    }
    
    public void setWorld(String world){
        this.world = world;
    }
    
    public void setDimension(int dim){
        this.dimension = dim;
    }

    public void setParent(Zone newParent) {
        parent = newParent;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public void setFarewell(String farewell) {
        this.farewell = farewell;
    }
    
    public void setPVP(ZoneFlag pvp) {
        this.pvp = pvp;
    }
    
    public void setSanctuary(ZoneFlag sanctuary) {
        this.sanctuary = sanctuary;
    }
    
    public void setCreeper(ZoneFlag creeper) {
        this.creeper = creeper;
    }
    
    public void setGhast(ZoneFlag ghast) {
        this.ghast = ghast;
    }
    
    public void setFall(ZoneFlag fall){
        this.fall = fall;
    }
    
    public void setSuffocate(ZoneFlag suffocate){
        this.suffocate = suffocate;
    }
    
    public void setFire(ZoneFlag fire){
        this.fire = fire;
    }
    
    public void setAnimals(ZoneFlag animals){
        this.animals = animals;
    }
    
    public void setPhysics(ZoneFlag physics){
        this.physics = physics;
    }
    
    public void setCreative(ZoneFlag creative){
        this.creative = creative;
    }
    
    public void setPistons(ZoneFlag pistons){
        this.pistons = pistons;
    }
    
    public void setHealing(ZoneFlag healing) {
        this.healing = healing;
    }
    
    public void setEnderman(ZoneFlag enderman){
        this.enderman = enderman;
    }
    
    public void setSpread(ZoneFlag spread){
        this.spread = spread;
    }
    
    public void setFlow(ZoneFlag flow){
        this.flow = flow;
    }
    
    public void setTNT(ZoneFlag TNT){
        this.TNT = TNT;
    }
    
    public void setPotion(ZoneFlag potion){
        this.potion = potion;
    }
    
    public void setStarve(ZoneFlag starve){
        this.starve = starve;
    }
    
    public void setRestricted(ZoneFlag restricted){
        this.restricted = restricted;
    }
    
    public void setRespawn(ZoneFlag respawn){
        this.respawn = respawn;
    }

    /*
     * Other Methods
     */
    public void farewell(ICModPlayer player) {
        if(farewell != null) player.sendMessage(farewell.replace("@", "\u00A7"));
    }

    public void greet(ICModPlayer player) {
        if(greeting != null) player.sendMessage(greeting.replace("@", "\u00A7"));
    }

    // Delete the zone
    public void delete() {
        // Delete polygon
        polygon.delete();

        if(parent != null){
            parent.removeChild(this);
        }
        for(Zone child : getChildren()){
            if(parent != null){
                child.setParent(parent);
            }
        }
        ZoneLists.removeZonefromPlayerZoneList(this);
        ZoneLists.removeZone(this);
        //rhandle.getDataSource().deleteZone(this);
    }
    
    //Save Zone
    public void save(){
        rhandle.getDataSource().saveZone(this);
    }

    // Does this zone contain zero area?
    public boolean isEmpty() {
        return polygon == null || polygon.isEmpty();
    }

    public boolean contains(ICModBlock block) {
        if(name.equalsIgnoreCase("everywhere")){
            return true;
        }
        if(world.equals(block.getWorldName()) && dimension == block.getDimension()){
            return polygon.contains(block);
        }
        return false;
    }

    public boolean contains(ICModPlayer player) {
        if(name.equalsIgnoreCase("everywhere")){
            return true;
        }
        if(world.equals(player.getWorldName()) && dimension == player.getDimension()){
            return polygon.contains(player);
        }
        return false;
    }
    
    public boolean contains(ICModMob mob) {
        if(name.equalsIgnoreCase("everywhere")){
            return true;
        }
        if(world.equals(mob.getWorldName()) && dimension == mob.getDimension()){
            return polygon.contains(mob);
        }
        return false;
    }

    public Zone whichChildContains(ICModPlayer player) {
        for(Zone child : children){
            if(child.contains(player)){
                return child.whichChildContains(player);
            }
        }
        return this;
    }
    
    public Zone whichChildContains(ICModBlock block){
        for(Zone child : children){
            if(child.contains(block)){
                return child.whichChildContains(block);
            }
        }
        return this;
    }
    
    public Zone whichChildContains(ICModMob mob) {
        for(Zone child : children){
            if(child.contains(mob)){
                return child.whichChildContains(mob);
            }
        }
        return this;
    }
    
    public boolean isInWorld(String world, int dim){
        return this.world.equals(world) && this.dimension == dim;
    }
    
    /**
     * Gets a zone's flags
     * 
     * @param zone
     * @param showComb
     * @param showEnviro
     * @return flags
     */
    public String[] getFlags(boolean showComb, boolean showEnviro){
        String[] environ = new String[5];
        String[] comb = new String[2];
        StringBuilder flags = new StringBuilder();
        //Start Environment Flags
        if(showEnviro){
            flags.append(padAlign(String.format(flagform, "FALL", (getFall() ? "\u00A72ON " : "\u00A74OFF "), (getAbsoluteFall().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(padAlign(String.format(flagform, "SUFFOCATE", (getSuffocate() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteSuffocate().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "FIRE", (getFire() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteFire().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            environ[0] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(padAlign(String.format(flagform, "PHYSICS", (getPhysics() ?"\u00A72ON " : "\u00A74OFF "), (getAbsolutePhysics().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(padAlign(String.format(flagform, "ANIMALS", (getAnimals() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteAnimals().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "CREATIVE", (getCreative() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteCreative().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            environ[1] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(padAlign(String.format(flagform, "PISTONS", (getPistons() ?"\u00A72ON " : "\u00A74OFF "), (getAbsolutePistons().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(padAlign(String.format(flagform, "ENDERMAN", (getEnderman() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteEnderman().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "SPREAD", (getSpread() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteSpread().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            environ[2] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(padAlign(String.format(flagform, "FLOW", (getFlow() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteFlow().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(padAlign(String.format(flagform, "STARVE", (getStarve() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteStarve().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "TNT", (getTNT() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteTNT().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            environ[3] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(padAlign(String.format(flagform, "RESTRICTED", (getRestricted() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteRestricted().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "RESPAWN", (getRespawn() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteRespawn().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            environ[4] = flags.toString();
            flags.delete(0, flags.length());
        }
        //End Environment Flags
        //Start Combat Flags
        if(showComb){
            flags.append(padAlign(String.format(flagform, "PVP", (getPVP() ?"\u00A72ON " : "\u00A74OFF "), (getAbsolutePVP().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(padAlign(String.format(flagform, "SANCTUARY", (getSanctuary() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteSanctuary().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "CREEPER", (getCreeper() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteCreeper().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            comb[0] = flags.toString();
            flags.delete(0, flags.length());
            flags.append(padAlign(String.format(flagform, "GHAST", (getGhast() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteGhast().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(padAlign(String.format(flagform, "HEALING", (getHealing() ?"\u00A72ON " : "\u00A74OFF "), (getAbsoluteHealing().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")), 30, ' ', false));
            flags.append(String.format(flagform, "POTION", (getPotion() ?"\u00A72ON " : "\u00A74OFF "), (getAbsolutePotion().equals(Zone.ZoneFlag.INHERIT) ? ChatColor.PINK+"(I)" : "")));
            comb[1] = flags.toString();
        }
        //End Combat Flags
        if(showComb && !showEnviro){
            return comb;
        }
        else if(!showComb && showEnviro){
            return environ;
        }
        else{
            String[] allFlags = new String[environ.length+comb.length];
            for(int i = 0; i < environ.length; i++){
                if(environ[i] != null){
                    allFlags[i] = environ[i];
                }
            }
            for(int i = 0; i < comb.length; i++){
                if(comb[i] != null){
                    allFlags[environ.length+i] = comb[i];
                }
            }
            return allFlags;
        }
    }
    private String padAlign(String string, int fieldLength, char padding, boolean alignRight){
        int length = fieldLength - getRealLength(string);
        if (length <= 0){
            return string;
        }
        StringBuffer buffer = new StringBuffer(fieldLength);
        for(int i=0; i<length; i++){
            buffer.append(padding);
        }
        if (alignRight){
            buffer.append(string);
        }
        else{
            buffer.insert(0,string);
        }
        return buffer.toString();
    }
    
    private int getRealLength(String string){
        return string.replace("\u00A7[0-9A-Fa-f]", "").length();
    }
    
    //Start Permission Checks
    /**
     * Set Permissions Method
     * 
     * Overrides previous permission if it existed
     * Otherwise creates new permission
     *
     * @param String ownerName
     * @param PermType type
     * @param Zone zone
     * @param Boolean allowed
     * @param Boolean override
     */
    public void setPermission(String ownerName, Permission.PermType type, boolean allowed, boolean override) {
        Permission previous = getSpecificPermission(ownerName, type);
        if(previous != null){
            zoneperms.remove(previous);
        }
        zoneperms.add(new Permission(ownerName, type, allowed, override));
    }
    
    public void setPermission(Permission perm){
        zoneperms.add(perm);
    }
    
    /**
     * Gets Specific Permission
     * 
     * @param String ownerName
     * @param PermType type
     * @param Zone zone
     * 
     * @return Permission
     */
    public Permission getSpecificPermission(String ownerName, Permission.PermType type) {
        for(Permission p : zoneperms){
            if(p.getOwnerName().equals(ownerName) && p.getType().equals(type)){
                return p;
            }
        }
        return null;
    }
    
    /**
     * Permission Delete
     * 
     * @param ownerName
     * @param type
     * @param zone
     */
    public void deletePermission(String ownerName, Permission.PermType type) {
        Permission permission = getSpecificPermission(ownerName, type);
        if(permission != null){
            zoneperms.remove(permission);
        }
    }
    
    /**
     * Delegate check
     * 
     * @param player
     * @param type
     * @param zone
     * @return boolean check result
     */
    public boolean delegateCheck(ICModPlayer player, Permission.PermType type) {
        if(getParent() != null){
            if(getParent().permissionCheck(player, Permission.PermType.ALL)){
                return true;
            }
        }
        if(type.equals(Permission.PermType.DELEGATE)){
            return permissionCheck(player, Permission.PermType.ALL);
        }
        else{
            return permissionCheck(player, Permission.PermType.DELEGATE) && permissionCheck(player, type);
        }
    }
    
    /**
     * General Permission Check
     * 
     * @param player
     * @param type
     * @param zone
     * @return boolean Allowed
     */
    public boolean permissionCheck(ICModPlayer player, Permission.PermType type) {
        Permission result = null;
        
        for(Permission p : zoneperms) {
            if(p.applicable(player, type)) {
                if(result == null){
                    result = p;
                }
                else{
                    result = p.battle(result, p);
                }
            }
        }
        if(result == null) {
            if(getParent() != null){
                return getParent().permissionCheck(player, type);
            }
            else{
                return RealmsProps.getGrantByDefault();
            }
        } else {
            return result.getAllowed();
        }
    }
    
    public List<Permission> getPerms() {
        return zoneperms;
    }
    //End Permission checks
    
    public String toString() {
        StringBuffer toRet = new StringBuffer();
        toRet.append(name);
        toRet.append(',');
        toRet.append(world);
        toRet.append(',');
        toRet.append(dimension);
        toRet.append(',');
        toRet.append(parent == null ? "null" : parent.getName());
        toRet.append(',');
        toRet.append(greeting);
        toRet.append(',');
        toRet.append(farewell);
        toRet.append(',');
        toRet.append(pvp.toString());
        toRet.append(',');
        toRet.append(sanctuary.toString());
        toRet.append(',');
        toRet.append(creeper.toString());
        toRet.append(',');
        toRet.append(ghast.toString());
        toRet.append(',');
        toRet.append(fall.toString());
        toRet.append(',');
        toRet.append(suffocate.toString());
        toRet.append(',');
        toRet.append(fire.toString());
        toRet.append(',');
        toRet.append(animals.toString());
        toRet.append(',');
        toRet.append(physics.toString());
        toRet.append(',');
        toRet.append(creative.toString());
        toRet.append(',');
        toRet.append(pistons.toString());
        toRet.append(',');
        toRet.append(healing.toString());
        toRet.append(',');
        toRet.append(enderman.toString());
        toRet.append(',');
        toRet.append(spread.toString());
        toRet.append(',');
        toRet.append(flow.toString());
        toRet.append(',');
        toRet.append(TNT.toString());
        toRet.append(',');
        toRet.append(potion.toString());
        toRet.append(',');
        toRet.append(starve.toString());
        toRet.append(',');
        toRet.append(restricted.toString());
        toRet.append(',');
        toRet.append(respawn.toString());
        return toRet.toString();
    }
    
    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + dimension;
        hash = 31 * hash + (null == world ? 0 : world.hashCode());
        hash = 31 * hash + (null == name ? 0 : name.hashCode());
        return hash;
    }
    
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if((obj == null) || (obj.getClass() != this.getClass())){
            return false;
        }
        Zone zone = (Zone)obj;
        return dimension == zone.getDimension() && world.equals(zone.getWorld()) && name.equals(zone.getName());
    }
}
