package net.visualillusionsent.realms.zones;

import net.visualillusionsent.realms.io.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Permission Class
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class Permission {
    private String owner;
    private PermType type;
    
    /**
     * Permission Types
     */
    public enum PermType {
        DELEGATE ("delegate"),
        ZONING ("zoning"),
        ENTER ("enter"),
        ALL ("all"),
        CREATE ("create"),
        DESTROY ("destroy"),
        TELEPORT ("teleport"),
        MESSAGE ("message"),
        COMBAT ("combat"),
        INTERACT ("interact"),
        COMMAND ("command"),
        ENVIRONMENT ("environment"),
        EAT ("eat"),
        AUTHED("authed"),
        IGNITE("ignite"),
        NULL ("");
        
        private String type;
        
        private PermType (String type) {
            this.type = type;
        }
        
        /**
         * Returns the Permission Type to a string
         * 
         * @return type
         */
        public String toString() {
            return this.type;
        }
        
        /**
         * Gets Permission Type from String
         * 
         * @param myType
         * @return Permission Value
         * @throws InvaildPermissionTypeException
         */
        public static PermType getTypeFromString (String myType) throws InvaildPermissionTypeException {
            PermType rValue = null;
            try {
                rValue = PermType.valueOf(myType.toUpperCase());
            } catch (IllegalArgumentException IAE) {
                throw new InvaildPermissionTypeException();
            }
            
            return rValue;
        }
    }
    
    private boolean allowed;
    private boolean override;

    /**
     * Creates a new Permission
     * 
     * @param realm
     * @param owner
     * @param type
     * @param zone
     * @param allowed
     * @param override
     */
    public Permission(String owner, PermType type, boolean allowed, boolean override) {
        this.owner = owner;
        this.type = type;
        this.allowed = allowed;
        this.override = override;
    }
    
    /**
     * Creates a new Permission
     * 
     * @param realm
     * @param owner
     * @param type
     * @param zone
     * @param allowed
     * @param override
     */
    public Permission(String owner, String type, boolean allowed, boolean override){
        this(owner, PermType.valueOf(type.toUpperCase()), allowed, override);
    }

    /**
     * CSV File Constructor
     * 
     * @param realm
     * @param split
     * @throws ZoneNotFoundException
     */
    public Permission(String[] split){
        this.owner = split[0];
        this.type = PermType.valueOf(split[1].toUpperCase());
        this.allowed = Integer.parseInt(split[3]) == 1;
        this.override = Integer.parseInt(split[4]) == 1;
    }

    /*
     * Accessor Methods
     */
    public String getOwnerName() {return owner;}
    public PermType getType() {return type;}
    public boolean getAllowed() {return allowed;}
    public boolean getOverride() {return override;}
    

    /**
     * Checks if permission is applicable to the player
     * 
     * @param player
     * @return true if it is, false if not
     */
    public boolean applicableToPlayer(ICModPlayer player) {
        if(owner.startsWith("p:")){
            return owner.replaceAll("p:","").equalsIgnoreCase(player.getName());
        }
        else if(owner.startsWith("g:")){
            return player.isInGroup(owner.replaceAll("g:",""));
        }
        else if(owner.equalsIgnoreCase(player.getName())){
            return true;
        }
        else if(player.isInGroup(owner)){
            return true;
        }
        else if(owner.equalsIgnoreCase("everyone")){
            return true;
        }
        return false;
    }

    /**
     * Checks if Permission matches type or ALL Permission
     * 
     * @param type
     * @return true if it is, false if not
     */
    public boolean applicableToType(PermType type) {
        if(this.type.equals(PermType.ALL) || this.type.equals(type)){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Checks if Permission is applicable to player and type
     * 
     * @param player
     * @param type
     * @return true if it is, false if not
     */
    public boolean applicable(ICModPlayer player, PermType type) {
        return applicableToPlayer(player) && applicableToType(type);
    }
    
    /**
     * Battle method
     * 
     * @param p1
     * @param p2
     * @return boolean check result
     */
    public Permission battle(Permission p1, Permission p2) {
        // Override permissions always win
        if(p1.getOverride() && !p2.getOverride()){
            return p1;
        }
        else if(!p1.getOverride() && p2.getOverride()){
            return p2;
        }
        // Otherwise, return whichever permission overrules the other
        // If both permissions agree, it doesn't matter which we return
        if(RealmsProps.getGrantOverrules() && p2.getAllowed()){
            return p2;
        }
        else if(!RealmsProps.getGrantOverrules() && !p2.getAllowed()){
            return p2;
        }
        else{
            return p1;
        }
    }

    /**
     * Permission to String
     * 
     * @return permission as string
     */
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append(owner);
        builder.append(',');
        builder.append(type.toString());
        builder.append(',');
        builder.append(allowed ? "YES" : "NO"); 
        builder.append(',');
        builder.append(override ? "YES" : "NO");
        return builder.toString();
    }
}
