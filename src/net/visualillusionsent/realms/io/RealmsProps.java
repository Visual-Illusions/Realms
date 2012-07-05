package net.visualillusionsent.realms.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.viutils.PropsFile;

/**
 * Realms properties handler
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class RealmsProps {
    private final String StoDir = "plugins/config/Realms/";
    private final String PropsFile = "Realms.ini";
    private final String BckStoDir = StoDir + "BackUp/";
    private final File Props = new File(StoDir + PropsFile);
    private final File Storage = new File(StoDir);
    private final File BackStorage = new File(BckStoDir);
    private RHandle rhandle;

    private PropsFile Properties;

    private static int wandItem, pylontype, pylonheight;
    private static long sanctuarytimeout, healingtimeout, animalstimeout, savetimeout, restricttimeout;
    private static boolean grantbyDefault, grantOverrules, useMySQL, useCModMySQL, autoupdate,
            creepersallowed, ghastsallowed, sancmobsallowed, pedallowed, t2t, restrictcausedeath,

            //DEBUG CHECKS
            command_check, animal_destory, block_break, block_destroy, block_physics,
            block_place, block_rightclicked, command, damage, eat, enderman_drop,
            enderman_pickup, entity_rightclick, explosion, flow, ignite, item_drop,
            item_pickup, item_use, mob_destroy, mob_spawn, mob_target, piston_extend,
            piston_retract, portal_use, player_explode, player_heal, player_restricted,
            food_exhaustionchange, debugother;

    private static String sqlUser, sqlPassword, sqlDatabase, sqlDriver;
    private static String restricted_message = "WARNING: YOU HAVE ENTERED A RESTRICTED ZONE!";
    private final String defOpBlocks = "23,25,54,61,62,64,69,77,84,93,96,107,117,118", defOpItems = "383", defCommandOverride = "/home,/spawn,/help";

    private static ArrayList<Integer> OpBlock = new ArrayList<Integer>();
    private static ArrayList<Integer> OpItem = new ArrayList<Integer>();
    private static ArrayList<String> CommandOverride = new ArrayList<String>();
    private boolean isInitialized = false;

    /**
     * class constructor
     * 
     * @param rhandle
     */
    public RealmsProps(RHandle rhandle) {
        this.rhandle = rhandle;
    }

    public boolean initialize() {
        if (isInitialized) {
            return false;
        }
        /* Check Storage Directory */
        if (!Storage.exists()) {
            Storage.mkdirs();
        }

        /* Check Backup Storage */
        if (!BackStorage.exists()) {
            BackStorage.mkdirs();
        }
        /* Check Properties File */
        if (!Props.exists()) { //Create a new one if non-existent
            InputStream in = null;
            FileWriter out = null;
            try {
                File outputFile = new File(StoDir + PropsFile);
                in = getClass().getClassLoader().getResourceAsStream("DefaultProp.ini");
                out = new FileWriter(outputFile);
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            catch (IOException e) {
                rhandle.log(Level.SEVERE, "Unable to create properties file!");
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
                catch (IOException e2) {}
                return false;
            }
            finally {
                try {
                    in.close();
                    out.close();
                }
                catch (IOException ioe) {}
            }
        }

        try {
            Properties = new PropsFile(StoDir + PropsFile); //Initialize Properties
        }
        catch (IOException e) {
            rhandle.log(Level.SEVERE, "[Realms] Failed to load Properties...");
            return false;
        }

        wandItem = parseInteger(283, "Wand-Item");
        pylontype = parseInteger(85, "Pylon-Type");
        pylonheight = parseInteger(4, "Pylon-Height");

        sanctuarytimeout = parseLong(500, "Sanctuary-TimeOut");
        healingtimeout = parseLong(500, "Healing-TimeOut");
        animalstimeout = parseLong(500, "Animals-TimeOut");
        savetimeout = parseLong(30, "Save-TimeOut");
        restricttimeout = parseLong(2000, "Restrict-TimeOut");

        grantbyDefault = parseBoolean(true, "GrantByDefault");
        grantOverrules = parseBoolean(true, "GrantOverrulesDeny");

        autoupdate = parseBoolean(false, "Auto-Update");

        //Start DEBUGGING Checks
        debugother = parseBoolean(false, "Debug-Other-StackTrace");
        command_check = parseBoolean(false, "COMMAND_CHECK");
        animal_destory = parseBoolean(false, "ANIMAL_DESTROY");
        block_break = parseBoolean(false, "BLOCK_BREAK");
        block_destroy = parseBoolean(false, "BLOCK_DESTORY");
        block_physics = parseBoolean(false, "BLOCK_PHYSICS");
        block_place = parseBoolean(false, "BLOCK_PLACE");
        block_rightclicked = parseBoolean(false, "BLOCK_RIGHTCLICKED");
        command = parseBoolean(false, "COMMAND");
        damage = parseBoolean(false, "DAMAGE");
        eat = parseBoolean(false, "EAT");
        enderman_drop = parseBoolean(false, "ENDERMAN_DROP");
        enderman_pickup = parseBoolean(false, "ENDERMAN_PICKUP");
        entity_rightclick = parseBoolean(false, "ENTITY_RIGHTCLICKED");
        explosion = parseBoolean(false, "EXPLOSION");
        flow = parseBoolean(false, "FLOW");
        ignite = parseBoolean(false, "IGNITE");
        item_drop = parseBoolean(false, "ITEM_DROP");
        item_pickup = parseBoolean(false, "ITEM_PICKUP");
        item_use = parseBoolean(false, "ITEM_USE");
        mob_destroy = parseBoolean(false, "MOB_DESTROY");
        mob_spawn = parseBoolean(false, "MOB_SPAWN");
        mob_target = parseBoolean(false, "MOB_TARGET");
        piston_extend = parseBoolean(false, "PISTON_EXTEND");
        piston_retract = parseBoolean(false, "PISTON_RETRACT");
        portal_use = parseBoolean(false, "PORTAL_USE");
        player_explode = parseBoolean(false, "PLAYER_EXPLODE");
        player_heal = parseBoolean(false, "PLAYER_HEAL");
        player_restricted = parseBoolean(false, "PLAYER_RESTRICTED");
        food_exhaustionchange = parseBoolean(false, "FOOD_EXHAUSTIONCHANGE");
        //End DEBUGGING Checks

        useMySQL = parseBoolean(false, "Use-MySQL");
        useCModMySQL = parseBoolean(false, "Use-CanaryMySQL");
        creepersallowed = parseBoolean(false, "CreepersAllowed");
        ghastsallowed = parseBoolean(false, "GhastsAllowed");
        sancmobsallowed = parseBoolean(false, "AllowSancMobs");
        pedallowed = parseBoolean(false, "AllowPlayerExplodeDamage");
        t2t = parseBoolean(false, "TNTtoTNT");
        restrictcausedeath = parseBoolean(false, "RestrictedCanKill");
        restricted_message = parseString(restricted_message, "RestrictedMessage");

        sqlUser = Properties.getString("SQLUsername");
        sqlPassword = Properties.getString("SQLPassword");
        sqlDatabase = Properties.getString("SQLDatabase");
        sqlDriver = Properties.getString("SQLDriver");

        String opBlocks = parseString(defOpBlocks, "OpBlocks");
        addOpBlock(opBlocks.split(","));

        String opItem = parseString(defOpItems, "OpItems");
        addOpItem(opItem.split(","));

        String WC = parseString(defCommandOverride, "CommandOverride");
        addWhiteCommands(WC.split(","));

        return true;
    }

    /**
     * parse integers
     * 
     * @param defval
     *            default integer value
     * @param prop
     *            property to get value for
     * @return value the value of the property if found
     */
    private int parseInteger(int defval, String prop) {
        int value = defval;
        if (Properties.containsKey(prop)) {
            try {
                value = Properties.getInt(prop);
            }
            catch (NumberFormatException NFE) {
                rhandle.log(Level.WARNING, PropsFile + " - NumberFormatException @ " + prop + " Using default value of " + defval);
                value = defval;
            }
        }
        else {
            rhandle.log(Level.WARNING, PropsFile + " - Value Not Found: " + prop + ", Using default value of " + defval);
        }
        return value;
    }

    /**
     * Parse Booleans
     * 
     * @param defval
     * @param prop
     * @return value
     */
    private boolean parseBoolean(boolean defval, String prop) {
        boolean value = defval;
        if (Properties.containsKey(prop)) {
            value = Properties.getBoolean(prop);
        }
        else {
            rhandle.log(Level.WARNING, PropsFile + " - Value Not Found: " + prop + ", Using default value of " + defval);
        }
        return value;
    }

    /**
     * Parses Longs
     * 
     * @param defval
     * @param prop
     * @return value
     */
    private long parseLong(long defval, String prop) {
        long value = defval;
        if (Properties.containsKey(prop)) {
            try {
                value = Properties.getLong(prop);
            }
            catch (NumberFormatException NFE) {
                rhandle.log(Level.WARNING, PropsFile + " - NumberFormatException @ " + prop + ", Using default value of " + defval);
                value = defval;
            }
            if (value < 500 && !prop.equals("Save-TimeOut")) {
                rhandle.log(Level.WARNING, PropsFile + " - Value Too Low @ " + prop + ", Using default value of " + defval);
                value = defval;
            }
            else if (value < 5) {
                rhandle.log(Level.WARNING, PropsFile + " - Value Too Low @ " + prop + ", Using default value of " + defval);
                value = defval;
            }
        }
        else {
            rhandle.log(Level.WARNING, PropsFile + " - Value Not Found: " + prop + ", Using default value of " + defval);
        }
        return value;
    }

    /**
     * Parses Strings
     * 
     * @param defval
     * @param prop
     * @return value
     */
    private String parseString(String defval, String prop) {
        String value = defval;
        if (Properties.containsKey(prop)) {
            value = Properties.getString(prop);
            if (value == null || value.equals("") || value.equals(" ")) {
                rhandle.log(Level.WARNING, PropsFile + " - Null Value @ " + prop + ", Using default value of " + defval);
                value = defval;
            }
        }
        else {
            rhandle.log(Level.WARNING, PropsFile + " - Value Not Found: " + prop + ", Using default value of " + defval);
        }
        return value;
    }

    /**
     * Adds operable Blocks to list
     * 
     * @param ids
     */
    private void addOpBlock(String[] ids) {
        if (ids != null) {
            int bid = 0;
            for (String id : ids) {
                try {
                    bid = Integer.parseInt(id.trim());
                }
                catch (NumberFormatException NFE) {
                    rhandle.log(Level.WARNING, "OpBlock ID: " + id + " is invalid!");
                    continue;
                }
                if (bid > 255 || bid < 0) {
                    rhandle.log(Level.WARNING, "OpBlock ID: " + id + " is invalid!");
                    continue;
                }
                OpBlock.add(bid);
            }
        }
    }

    /**
     * Adds operable Items to list
     * 
     * @param ids
     */
    private void addOpItem(String[] ids) {
        if (ids != null) {
            int bid = 0;
            for (String id : ids) {
                try {
                    bid = Integer.parseInt(id.trim());
                }
                catch (NumberFormatException NFE) {
                    rhandle.log(Level.WARNING, "OpBlock ID: " + id + " is invalid!");
                    continue;
                }
                if (bid < 255) {
                    rhandle.log(Level.WARNING, "OpBlock ID: " + id + " is invalid!");
                    continue;
                }
                OpItem.add(bid);
            }
        }
    }

    /**
     * Adds Commands to Override Command Blocking list
     * 
     * @param cmds
     */
    private void addWhiteCommands(String[] cmds) {
        if (cmds != null) {
            for (String cmd : cmds) {
                CommandOverride.add(cmd.trim());
            }
        }
    }

    /**
     * Gets Wand Type setting
     * 
     * @return wandItem
     */
    public static int getWandType() {
        return wandItem;
    }

    /**
     * Gets Pylon Type setting
     * 
     * @return pylontype
     */
    public static int getpylontype() {
        return pylontype;
    }

    /**
     * Gets Pylon Height setting
     * 
     * @return pylonheight
     */
    public static int getpylonheight() {
        return pylonheight;
    }

    /**
     * Gets Sanctuary Timeout setting
     * 
     * @return sanctuarytimeout
     */
    public static long getSanctuaryTimeOut() {
        return sanctuarytimeout;
    }

    /**
     * Gets Healing Timeout setting
     * 
     * @return healingtimeout
     */
    public static long getHealingTimeOut() {
        return healingtimeout;
    }

    /**
     * Gets Animals Timeout setting
     * 
     * @return animalstimeout
     */
    public static long getAnimalsTimeOut() {
        return animalstimeout;
    }

    /**
     * Gets Save Timeout setting
     * 
     * @return savetimeout
     */
    public static long getSaveTimeOut() {
        return savetimeout;
    }

    /**
     * Gets Grant By Default setting
     * 
     * @return grantbyDefault
     */
    public static boolean getGrantByDefault() {
        return grantbyDefault;
    }

    /**
     * Gets Grant Overrules Deny setting
     * 
     * @return grantOverrules
     */
    public static boolean getGrantOverrules() {
        return grantOverrules;
    }

    /**
     * Gets DebugOther Debug setting
     * 
     * @return debugother
     */
    public static boolean getDebugOther() {
        return debugother;
    }

    /**
     * Gets onAnimalDestroy Debug setting
     * 
     * @return onAnimalDestroy
     */
    public static boolean getDebugANIMAL_DESTROY() {
        return animal_destory;
    }

    /**
     * Gets onBlockBreak Debug setting
     * 
     * @return onBlockBreak
     */
    public static boolean getDebugBLOCK_BREAK() {
        return block_break;
    }

    /**
     * Gets onBlockDestroy Debug setting
     * 
     * @return onBlockDestroy
     */
    public static boolean getDebugBLOCK_DESTROY() {
        return block_destroy;
    }

    /**
     * Gets onBlockPhysics Debug setting
     * 
     * @return onBlockPhysics
     */
    public static boolean getDebugBLOCK_PHYSICS() {
        return block_physics;
    }

    /**
     * Gets onBlockPlace Debug setting
     * 
     * @return onBlockPlace
     */
    public static boolean getDebugBLOCK_PLACE() {
        return block_place;
    }

    /**
     * Gets onBlockRightClick Debug setting
     * 
     * @return onBlockRightClick
     */
    public static boolean getDebugBLOCK_RIGHTCLICKED() {
        return block_rightclicked;
    }

    /**
     * Gets onCommand Debug setting
     * 
     * @return onCommand
     */
    public static boolean getDebugCOMMAND() {
        return command;
    }

    /**
     * Gets canPlayerUseCommand Debug setting
     * 
     * @return canPlayerUseCommand
     */
    public static boolean getDebugCOMMAND_CHECK() {
        return command_check;
    }

    /**
     * Gets onDamage Debug setting
     * 
     * @return onDamage
     */
    public static boolean getDebugDAMAGE() {
        return damage;
    }

    /**
     * Gets onEat Debug setting
     * 
     * @return onEat
     */
    public static boolean getDebugEAT() {
        return eat;
    }

    /**
     * Gets onEndermanDrop Debug setting
     * 
     * @return onEndermanDrop
     */
    public static boolean getDebugENDERMAN_DROP() {
        return enderman_drop;
    }

    /**
     * Gets onEndermanPickUp Debug setting
     * 
     * @return onEndermanPickUp
     */
    public static boolean getDebugENDERMAN_PICKUP() {
        return enderman_pickup;
    }

    /**
     * Gets onEntityRightClick Debug setting
     * 
     * @return onEntityRightClick
     */
    public static boolean getDebugENTITY_RIGHTCLICKED() {
        return entity_rightclick;
    }

    /**
     * Gets onExplosion Debug setting
     * 
     * @return onExplosion
     */
    public static boolean getDebugEXPLOSION() {
        return explosion;
    }

    /**
     * Gets onFlow Debug setting
     * 
     * @return onFlow
     */
    public static boolean getDebugFLOW() {
        return flow;
    }

    /**
     * Gets onIgnite Debug setting
     * 
     * @return onIgnite
     */
    public static boolean getDebugIGNITE() {
        return ignite;
    }

    //TODO: Fix docs below
    public static boolean getDebugITEM_DROP() {
        return item_drop;
    }

    public static boolean getDebugITEM_PICKUP() {
        return item_pickup;
    }

    public static boolean getDebugITEM_USE() {
        return item_use;
    }

    public static boolean getDebugMOB_DESTROY() {
        return mob_destroy;
    }

    public static boolean getDebugMOB_SPAWN() {
        return mob_spawn;
    }

    public static boolean getDebugMOB_TARGET() {
        return mob_target;
    }

    public static boolean getDebugPISTON_EXTEND() {
        return piston_extend;
    }

    public static boolean getDebugPISTON_RETRACT() {
        return piston_retract;
    }

    public static boolean getDebugPORTAL_USE() {
        return portal_use;
    }

    public static boolean getDebugPLAYER_EXPLODE() {
        return player_explode;
    }

    public static boolean getDebugPLAYER_HEAL() {
        return player_heal;
    }

    public static boolean getDebugPLAYER_RESTRICT() {
        return player_restricted;
    }

    public static boolean getDebugFOOD_EXHAUSTIONCHANGE() {
        return food_exhaustionchange;
    }

    /**
     * Gets Use MySQL setting
     * 
     * @return MySQL
     */
    public static boolean getMySQL() {
        return useMySQL;
    }

    /**
     * Gets Use Canary MySQL Connection setting
     * 
     * @return CMySQL
     */
    public static boolean getCMySQL() {
        return useCModMySQL;
    }

    /**
     * Gets Allow Creepers in Sanctuary setting
     * 
     * @return AllowCreepers
     */
    public static boolean getAC() {
        return creepersallowed;
    }

    /**
     * Gets Allow Ghasts in Sanctuary setting
     * 
     * @return AllowGhasts
     */
    public static boolean getAG() {
        return ghastsallowed;
    }

    /**
     * Gets Allow Mobs in Sanctuary setting
     * 
     * @return AllowSancMobs
     */
    public static boolean getSM() {
        return sancmobsallowed;
    }

    /**
     * Gets MySQL DataBase URL
     * 
     * @return SQLDatabase
     */
    public static String getDataBase() {
        return sqlDatabase;
    }

    /**
     * Gets MySQL Username
     * 
     * @return SQLUser
     */
    public static String getUsername() {
        return sqlUser;
    }

    /**
     * Gets MySQL Password
     * 
     * @return SQLPassword (maybe I should think about encryptions?)
     */
    public static String getPassword(RHandle rhandle) {
        return sqlPassword;
    }

    /**
     * Gets MySQL Driver
     * 
     * @return SQLDriver
     */
    public static String getDriver() {
        return sqlDriver;
    }

    /**
     * Disables MySQL
     */
    public void disableSQL() {
        useMySQL = false;
        useCModMySQL = false;
    }

    /**
     * Checks if Block ID is an Operable Block
     * 
     * @param id
     * @return true if it is, false if not
     */
    public static boolean isOpBlock(int id) {
        return OpBlock.contains(id);
    }

    /**
     * Checks if Item ID is an Operable Item
     * 
     * @param id
     * @return true if it is, false if not
     */
    public static boolean isOpItem(int id) {
        return OpItem.contains(id);
    }

    /**
     * Checks if a command is in the Override List
     * 
     * @param cmd
     * @return true if it is, false if not
     */
    public static boolean isCO(String cmd) {
        return CommandOverride.contains(cmd);
    }

    /**
     * Checks if Player Explosion Damage is enabled
     * 
     * @return true if it is, false if not
     */
    public static boolean getPED() {
        return pedallowed;
    }

    /**
     * Checks if TNT to TNT is enabled
     * 
     * @return true if it is, false if not
     */
    public static boolean getTNTtoTNT() {
        return t2t;
    }

    /**
     * Checks if Restricted Zone can kill a player
     * 
     * @return true if it can, false if not
     */
    public static boolean getRestrictKills() {
        return restrictcausedeath;
    }

    public static Long getRestrictTimeOut() {
        return restricttimeout;
    }

    public static boolean getAutoUpdate() {
        return autoupdate;
    }

    public static String getRestrictMess() {
        return restricted_message;
    }
}
