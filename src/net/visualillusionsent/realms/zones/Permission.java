package net.visualillusionsent.realms.zones;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.FlatFileDeleter;
import net.visualillusionsent.realms.io.FlatFileSaver;
import net.visualillusionsent.realms.io.MySQLDeleter;
import net.visualillusionsent.realms.io.MySQLSaver;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.exception.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Permission Class
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class Permission {
    private final String owner;
    private final PermType type;
    private final String zonename;
    private final boolean allowed;
    private final boolean override;
    private final RHandle rhandle = RHandle.getInstance();

    /**
     * Permission Types
     */
    public enum PermType {
        DELEGATE("delegate"),
        ZONING("zoning"),
        ENTER("enter"),
        ALL("all"),
        CREATE("create"),
        DESTROY("destroy"),
        TELEPORT("teleport"),
        MESSAGE("message"),
        COMBAT("combat"),
        INTERACT("interact"),
        COMMAND("command"),
        ENVIRONMENT("environment"),
        EAT("eat"),
        AUTHED("authed"),
        IGNITE("ignite"),
        NULL("");

        private String type;

        private PermType(String type) {
            this.type = type;
        }

        /**
         * Returns the Permission Type to a string
         * 
         * @return type
         */
        @Override
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
        public static PermType getTypeFromString(String myType) throws InvaildPermissionTypeException {
            PermType rValue = null;
            try {
                rValue = PermType.valueOf(myType.toUpperCase());
            }
            catch (IllegalArgumentException IAE) {
                throw new InvaildPermissionTypeException();
            }

            return rValue;
        }
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
    public Permission(String owner, PermType type, String zonename, boolean allowed, boolean override) {
        this.owner = owner;
        this.type = type;
        this.zonename = zonename;
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
    public Permission(String owner, String type, String zonename, boolean allowed, boolean override) {
        this(owner, PermType.valueOf(type.toUpperCase()), zonename, allowed, override);
    }

    /**
     * CSV File Constructor
     * 
     * @param realm
     * @param split
     * @throws ZoneNotFoundException
     */
    public Permission(String[] args) {
        this.owner = args[0];
        this.type = PermType.valueOf(args[1].toUpperCase());
        this.zonename = args[2];
        this.allowed = Integer.parseInt(args[3]) == 1;
        this.override = Integer.parseInt(args[4]) == 1;
    }

    /**
     * Gets this permission's owner's name
     * 
     * @return owner
     */
    public String getOwnerName() {
        return owner;
    }

    public PermType getType() {
        return type;
    }

    public boolean getAllowed() {
        return allowed;
    }

    public boolean getOverride() {
        return override;
    }

    public String getZoneName() {
        return zonename;
    }

    /**
     * Checks if permission is applicable to the player
     * 
     * @param player
     * @return true if it is, false if not
     */
    public boolean applicableToPlayer(ICModPlayer player) {
        if (owner.startsWith("p:")) {
            return owner.replaceAll("p:", "").equalsIgnoreCase(player.getName());
        }
        else if (owner.startsWith("g:")) {
            return player.isInGroup(owner.replaceAll("g:", ""));
        }
        else if (owner.equalsIgnoreCase(player.getName())) {
            return true;
        }
        else if (player.isInGroup(owner)) {
            return true;
        }
        else if (owner.equalsIgnoreCase("everyone")) {
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
        if (this.type.equals(PermType.ALL) || this.type.equals(type)) {
            return true;
        }
        else {
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
        if (p1.getOverride() && !p2.getOverride()) {
            return p1;
        }
        else if (!p1.getOverride() && p2.getOverride()) {
            return p2;
        }
        // Otherwise, return whichever permission overrules the other
        // If both permissions agree, it doesn't matter which we return
        if (RealmsProps.getGrantOverrules() && p2.getAllowed()) {
            return p2;
        }
        else if (!RealmsProps.getGrantOverrules() && !p2.getAllowed()) {
            return p2;
        }
        else {
            return p1;
        }
    }

    public final void delete() {
        if (RealmsProps.getMySQL()) {
            rhandle.executeTask(new MySQLDeleter(rhandle, this));
        }
        else {
            rhandle.executeTask(new FlatFileDeleter(rhandle, this));
        }
    }

    public final void save() {
        if (RealmsProps.getMySQL()) {
            rhandle.executeTask(new MySQLSaver(rhandle, this));
        }
        else {
            rhandle.executeTask(new FlatFileSaver(rhandle, this));
        }
    }

    /**
     * Permission to String
     * 
     * @return permission as string
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append(owner);
        builder.append(',');
        builder.append(type.toString());
        builder.append(',');
        builder.append(zonename);
        builder.append(',');
        builder.append(allowed ? "1" : "0");
        builder.append(',');
        builder.append(override ? "1" : "0");
        return builder.toString();
    }
}
