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
    private final String BckStoDir = StoDir+"BackUp/";
    private final File Props = new File(StoDir+PropsFile);
    private final File Storage = new File(StoDir);
    private final File BackStorage = new File(BckStoDir);
    private RHandle rhandle;
    
    private PropsFile Properties;
    
    private static int wandItem, pylontype, pylonheight;
    private static long sanctuarytimeout, healingtimeout, animalstimeout, savetimeout, restricttimeout;
    private static boolean grantbyDefault, grantOverrules, useMySQL, useCModMySQL, 
                           creepersallowed, ghastsallowed, sancmobsallowed, pedallowed, t2t, restrictcausedeath,
                           
                           //DEBUG CHECKS
                           CanPlayerUseCommand, onAnimalDestroy, onBlockBreak, onBlockDestroy, onBlockPhysics,
                           onBlockPlace, onBlockRightClick, onCommand, onDamage, onEat, onEndermanDrop,
                           onEndermanPickUp, onEntityRightClick, onExplosion, onFlow, onIgnite, onItemDrop,
                           onItemPickUp, onItemUse, onMobDestroy, onMobSpawn, onMobTarget, onPistonExtend, 
                           onPistonRetract, onPortalUse, onPlayerExpode, onPlayerHeal, onPlayerRestict, debugother;
    
    private static String sqlUser, sqlPassword, sqlDatabase, sqlDriver;
    
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
    public RealmsProps(RHandle rhandle){
        this.rhandle = rhandle;
    }
    
    public boolean initialize(){
        if(isInitialized){
            return false;
        }
        /*Check Storage Directory*/
        if(!Storage.exists()){
            Storage.mkdirs();
        }
        
        /*Check Backup Storage*/
        if(!BackStorage.exists()){
            BackStorage.mkdirs();
        }
        /*Check Properties File*/
        if(!Props.exists()){ //Create a new one if non-existent
            InputStream in = null;
            FileWriter out = null;
            try{
                File outputFile = new File(StoDir+PropsFile);
                in = getClass().getClassLoader().getResourceAsStream("DefaultProp.ini");
                out = new FileWriter(outputFile);
                int c;
                while ((c = in.read()) != -1){
                    out.write(c);
                }
            } 
            catch (IOException e){
                rhandle.log(Level.SEVERE, "Unable to create properties file!");
                try{
                    if(in != null){
                        in.close();
                    }
                    if(out != null){
                        out.close();
                    }
                }
                catch(IOException e2){ }
                return false;
            }
            finally{
                try{
                    in.close();
                    out.close();
                }
                catch(IOException ioe){ }
            }
        }
        
        try {
            Properties = new PropsFile(StoDir+PropsFile); //Initialize Properties
        } catch (IOException e) {
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
        
        //Start DEBUGGING Checks
        debugother          = parseBoolean(false, "DebugOther");
        CanPlayerUseCommand = parseBoolean(false, "CanPlayerUseCommand");
        onAnimalDestroy     = parseBoolean(false, "onAnimalDestroy");
        onBlockBreak        = parseBoolean(false, "onBlockBreak");
        onBlockDestroy      = parseBoolean(false, "onBlockDestroy");
        onBlockPhysics      = parseBoolean(false, "onBlockPhysics");
        onBlockPlace        = parseBoolean(false, "onBlockPlace");
        onBlockRightClick   = parseBoolean(false, "onBlockRightClick");
        onCommand           = parseBoolean(false, "onCommand");
        onDamage            = parseBoolean(false, "onDamage");
        onEat               = parseBoolean(false, "onEat");
        onEndermanDrop      = parseBoolean(false, "onEndermanDrop");
        onEndermanPickUp    = parseBoolean(false, "onEndermanPickUp");
        onEntityRightClick  = parseBoolean(false, "onEntityRightClick");
        onExplosion         = parseBoolean(false, "onExplosion");
        onFlow              = parseBoolean(false, "onFlow");
        onIgnite            = parseBoolean(false, "onIgnite");
        onItemDrop          = parseBoolean(false, "onItemDrop");
        onItemPickUp        = parseBoolean(false, "onItemPickUp");
        onItemUse           = parseBoolean(false, "onItemUse");
        onMobDestroy        = parseBoolean(false, "onMobDestroy");
        onMobSpawn          = parseBoolean(false, "onMobSpawn");
        onMobTarget         = parseBoolean(false, "onMobTarget");
        onPistonExtend      = parseBoolean(false, "onPistonExtend");
        onPistonRetract     = parseBoolean(false, "onPistonRetract");
        onPortalUse         = parseBoolean(false, "onPortalUse");
        onPlayerExpode      = parseBoolean(false, "onPlayerExpode");
        onPlayerHeal        = parseBoolean(false, "onPlayerHeal");
        onPlayerRestict     = parseBoolean(false, "onPlayerRestict");
        //End DEBUGGING Checks
        
        useMySQL = parseBoolean(false, "Use-MySQL");
        useCModMySQL = parseBoolean(false, "Use-CanaryMySQL");
        creepersallowed = parseBoolean(false, "CreepersAllowed");
        ghastsallowed = parseBoolean(false, "GhastsAllowed");
        sancmobsallowed = parseBoolean(false, "AllowSancMobs");
        pedallowed = parseBoolean(false, "AllowPlayerExplodeDamage");
        t2t = parseBoolean(false, "TNTtoTNT");
        restrictcausedeath = parseBoolean(false, "RestrictedCanKill");
        
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
     * @param defval    default integer value
     * @param prop      property to get value for
     * @return value    the value of the property if found
     */
    private int parseInteger(int defval, String prop){
        int value = defval;
        if(Properties.containsKey(prop)){
            try{
                value = Properties.getInt(prop);
            }catch(NumberFormatException NFE){
                rhandle.log(Level.WARNING, PropsFile+" - NumberFormatException @ "+prop+" Using default value of "+defval);
                value = defval;
            }
        }
        else{
            rhandle.log(Level.WARNING, PropsFile+" - Value Not Found: "+prop+", Using default value of "+defval);
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
    private boolean parseBoolean(boolean defval, String prop){
        boolean value = defval;
        if(Properties.containsKey(prop)){
            value = Properties.getBoolean(prop);
        }
        else{
            rhandle.log(Level.WARNING, PropsFile+" - Value Not Found: "+prop+", Using default value of "+defval);
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
    private long parseLong(long defval, String prop){
        long value = defval;
        if(Properties.containsKey(prop)){
            try{
                value = Properties.getLong(prop);
            }catch(NumberFormatException NFE){
                rhandle.log(Level.WARNING, PropsFile+" - NumberFormatException @ "+prop+", Using default value of "+defval);
                value = defval;
            }
            if(value < 500 && !prop.equals("Save-TimeOut")){
                rhandle.log(Level.WARNING, PropsFile+" - Value Too Low @ "+prop+", Using default value of "+defval);
                value = defval;
            }
            else if (value < 5){
                rhandle.log(Level.WARNING, PropsFile+" - Value Too Low @ "+prop+", Using default value of "+defval);
                value = defval;
            }
        }
        else{
            rhandle.log(Level.WARNING, PropsFile+" - Value Not Found: "+prop+", Using default value of "+defval);
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
    private String parseString(String defval, String prop){
        String value = defval;
        if(Properties.containsKey(prop)){
            value = Properties.getString(prop);
            if(value == null || value.equals("") || value.equals(" ")){
                rhandle.log(Level.WARNING, PropsFile+" - Null Value @ "+prop+", Using default value of "+defval);
                value = defval;
            }
        }
        else{
            rhandle.log(Level.WARNING, PropsFile+" - Value Not Found: "+prop+", Using default value of "+defval);
        }
        return value;
    }
    
    /**
     * Adds operable Blocks to list
     * 
     * @param ids
     */
    private void addOpBlock(String[] ids){
        if(ids != null){
            int bid = 0;
            for(String id : ids){
                try{
                    bid = Integer.parseInt(id.trim());
                }
                catch(NumberFormatException NFE){
                    rhandle.log(Level.WARNING, "OpBlock ID: "+id+" is invalid!");
                    continue;
                }
                if(bid > 255 || bid < 0){
                    rhandle.log(Level.WARNING, "OpBlock ID: "+id+" is invalid!");
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
    private void addOpItem(String[] ids){
        if(ids != null){
            int bid = 0;
            for(String id : ids){
                try{
                    bid = Integer.parseInt(id.trim());
                }
                catch(NumberFormatException NFE){
                    rhandle.log(Level.WARNING, "OpBlock ID: "+id+" is invalid!");
                    continue;
                }
                if(bid < 255){
                    rhandle.log(Level.WARNING, "OpBlock ID: "+id+" is invalid!");
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
    private void addWhiteCommands(String[] cmds){
        if(cmds != null){
            for(String cmd : cmds){
                CommandOverride.add(cmd.trim());
            }
        }
    }

    /**
     * Gets Wand Type setting
     * 
     * @return wandItem
     */
    public static int getWandType(){
        return wandItem;
    }
    
    /**
     * Gets Pylon Type setting
     * 
     * @return pylontype
     */
    public static int getpylontype(){
        return pylontype;
    }
    
    /**
     * Gets Pylon Height setting
     * 
     * @return pylonheight
     */
    public static int getpylonheight(){
        return pylonheight;
    }
    
    /**
     * Gets Sanctuary Timeout setting
     * 
     * @return sanctuarytimeout
     */
    public static long getSanctuaryTimeOut(){
        return sanctuarytimeout;
    }
    
    /**
     * Gets Healing Timeout setting
     * 
     * @return healingtimeout
     */
    public static long getHealingTimeOut(){
        return healingtimeout;
    }
    
    /**
     * Gets Animals Timeout setting
     * 
     * @return animalstimeout
     */
    public static long getAnimalsTimeOut(){
        return animalstimeout;
    }
    
    /**
     * Gets Save Timeout setting
     * 
     * @return savetimeout
     */
    public static long getSaveTimeOut(){
        return savetimeout;
    }
    
    /**
     * Gets Grant By Default setting
     * 
     * @return grantbyDefault
     */
    public static boolean getGrantByDefault(){
        return grantbyDefault;
    }
    
    /**
     * Gets Grant Overrules Deny setting
     * 
     * @return grantOverrules
     */
    public static boolean getGrantOverrules(){
        return grantOverrules;
    }
    
    /**
     * Gets DebugOther Debug setting
     * 
     * @return debugother
     */
    public static boolean getDebugOther(){
        return debugother;
    }
    
    /**
     * Gets canPlayerUseCommand Debug setting
     * 
     * @return canPlayerUseCommand
     */
    public static boolean getDebugCanPlayerUseCommand(){
        return CanPlayerUseCommand;
    }
    
    /**
     * Gets onAnimalDestroy Debug setting
     * 
     * @return onAnimalDestroy
     */
    public static boolean getDebugOnAnimalDestroy(){
        return onAnimalDestroy;
    }
    
    /**
     * Gets onBlockBreak Debug setting
     * 
     * @return onBlockBreak
     */
    public static boolean getDebugOnBlockBreak(){
        return onBlockBreak;
    }
    
    /**
     * Gets onBlockDestroy Debug setting
     * 
     * @return onBlockDestroy
     */
    public static boolean getDebugOnBlockDestroy(){
        return onBlockDestroy;
    }
    
    /**
     * Gets onBlockPhysics Debug setting
     * 
     * @return onBlockPhysics
     */
    public static boolean getDebugOnBlockPhysics(){
        return onBlockPhysics;
    }
    
    /**
     * Gets onBlockPlace Debug setting
     * 
     * @return onBlockPlace
     */
    public static boolean getDebugOnBlockPlace(){
        return onBlockPlace;
    }
    
    /**
     * Gets onBlockRightClick Debug setting
     * 
     * @return onBlockRightClick
     */
    public static boolean getDebugOnBlockRightClick(){
        return onBlockRightClick;
    }
    
    /**
     * Gets onCommand Debug setting
     * 
     * @return onCommand
     */
    public static boolean getDebugOnCommand(){
        return onCommand;
    }
    
    /**
     * Gets onDamage Debug setting
     * 
     * @return onDamage
     */
    public static boolean getDebugOnDamage(){
        return onDamage;
    }
    
    /**
     * Gets onEat Debug setting
     * 
     * @return onEat
     */
    public static boolean getDebugOnEat(){
         return onEat;
    }
    
    /**
     * Gets onEndermanDrop Debug setting
     * 
     * @return onEndermanDrop
     */
    public static boolean getDebugOnEndermanDrop(){
        return onEndermanDrop;
    }
    
    /**
     * Gets onEndermanPickUp Debug setting
     * 
     * @return onEndermanPickUp
     */
    public static boolean getDebugOnEndermanPickUp(){
        return onEndermanPickUp;
    }
    
    /**
     * Gets onEntityRightClick Debug setting
     * 
     * @return onEntityRightClick
     */
    public static boolean getDebugOnEntityRightClick(){
        return onEntityRightClick;
    }
    
    /**
     * Gets onExplosion Debug setting
     * 
     * @return onExplosion
     */
    public static boolean getDebugOnExplosion(){
        return onExplosion;
    }
    
    /**
     * Gets onFlow Debug setting
     * 
     * @return onFlow
     */
    public static boolean getDebugOnFlow(){
        return onFlow;
    }
    
    /**
     * Gets onIgnite Debug setting
     * 
     * @return onIgnite
     */
    public static boolean getDebugOnIgnite(){
        return onIgnite;
    }
    
    //TODO: Fix docs below
    public static boolean getDebugOnItemDrop(){
        return onItemDrop;
    }
    public static boolean getDebugOnItemPickUp(){
        return onItemPickUp;
    }
    public static boolean getDebugOnItemUse(){
        return onItemUse;
    }
    public static boolean getDebugOnMobDestroy(){
        return onMobDestroy;
    }
    public static boolean getDebugOnMobSpawn(){
        return onMobSpawn;
    }
    public static boolean getDebugOnMobTarget(){
        return onMobTarget;
    }
    public static boolean getDebugOnPistonExtend(){
        return onPistonExtend;
    }
    public static boolean getDebugOnPistonRetract(){
        return onPistonRetract;
    }
    public static boolean getDebugOnPortalUse(){
        return onPortalUse;
    }
    public static boolean getDebugOnPlayerExpode(){
        return onPlayerExpode;
    }
    public static boolean getDebugOnPlayerHeal(){
        return onPlayerHeal;
    }
    public static boolean getDebugOnPlayerRestict(){
        return onPlayerRestict;
    }
    
    
    /**
     * Gets Use MySQL setting
     * 
     * @return MySQL
     */
    public static boolean getMySQL(){
        return useMySQL;
    }
    
    /**
     * Gets Use Canary MySQL Connection setting
     * 
     * @return CMySQL
     */
    public static boolean getCMySQL(){
        return useCModMySQL;
    }
    
    /**
     * Gets Allow Creepers in Sanctuary setting
     * 
     * @return AllowCreepers
     */
    public static boolean getAC(){
        return creepersallowed;
    }
    
    /**
     * Gets Allow Ghasts in Sanctuary setting
     * 
     * @return AllowGhasts
     */
    public static boolean getAG(){
        return ghastsallowed;
    }
    
    /**
     * Gets Allow Mobs in Sanctuary setting
     * 
     * @return AllowSancMobs
     */
    public static boolean getSM(){
        return sancmobsallowed;
    }
    
    /**
     * Gets MySQL DataBase URL
     * 
     * @return SQLDatabase
     */
    public static String getDataBase(){
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
     * @return SQLPassword  (maybe I should think about encryptions?)
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
    public static boolean isOpBlock(int id){
        return OpBlock.contains(id);
    }
    
    /**
     * Checks if Item ID is an Operable Item
     * 
     * @param id
     * @return true if it is, false if not
     */
    public static boolean isOpItem(int id){
        return OpItem.contains(id);
    }
    
    /**
     * Checks if a command is in the Override List
     * 
     * @param cmd
     * @return true if it is, false if not
     */
    public static boolean isCO(String cmd){
        return CommandOverride.contains(cmd);
    }
    
    /**
     * Checks if Player Explosion Damage is enabled
     * 
     * @return true if it is, false if not
     */
    public static boolean getPED(){
        return pedallowed;
    }
    
    /**
     * Checks if TNT to TNT is enabled
     * 
     * @return true if it is, false if not
     */
    public static boolean getTNTtoTNT(){
        return t2t;
    }
    
    /**
     * Checks if Restricted Zone can kill a player
     * 
     * @return true if it can, false if not
     */
    public static boolean getRestrictKills(){
        return restrictcausedeath;
    }
    
    public static Long getRestrictTimeOut(){
        return restricttimeout;
    }
}
