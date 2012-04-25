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
 * 
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
    private PropsFile BckNums;
    
    private static int wandItem, pylontype, pylonheight;
    private static long sanctuarytimeout, healingtimeout, animalstimeout, savetimeout, restricttimeout;
    private static boolean grantbyDefault, grantOverrules, debug, useMySQL, useCModMySQL, 
                           creepersallowed, ghastsallowed, sancmobsallowed, pedallowed, t2t, restrictcausedeath;
    private String sqlUser, sqlPassword, sqlDatabase, sqlDriver;
    
    private final String defOpBlocks = "23,25,54,61,62,64,69,77,84,93,96,107,117,118", defOpItems = "383", defCommandOverride = "/home,/spawn,/help";
    
    private static ArrayList<Integer> OpBlock = new ArrayList<Integer>();
    private static ArrayList<Integer> OpItem = new ArrayList<Integer>();
    private static ArrayList<String> CommandOverride = new ArrayList<String>();
    
    private int polybcknum = 0, zonebcknum = 0, permbcknum = 0, invbcknum = 0;
    
    /**
     * class constructor
     * 
     * @param rhandle
     */
    public RealmsProps(RHandle rhandle){
        this.rhandle = rhandle;
        initialize();
    }
    
    private void initialize(){
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
            try{
                File outputFile = new File(StoDir+PropsFile);
                InputStream in = getClass().getClassLoader().getResourceAsStream("DefaultProp.ini");
                FileWriter out = new FileWriter(outputFile);
                int c;
                while ((c = in.read()) != -1){
                    out.write(c);
                }
                in.close();
                out.close();
            } 
            catch (IOException e){
                rhandle.log(Level.SEVERE, "Unable to create properties file!");
            }
        }
        
        Properties = new PropsFile(StoDir+PropsFile); //Initialize Properties
        
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
        debug = parseBoolean(false, "Debug");
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
        
        
        BckNums = new PropsFile(BckStoDir+"BackUpNums.bak");
        if(!BckNums.containsKey("PolyNum")){
            BckNums.setInt("PolyNum", 0);
        }
        else{
            polybcknum = BckNums.getInt("PolyNum");
        }
        if(!BckNums.containsKey("ZoneNum")){
            BckNums.setInt("ZoneNum", 0);
        }
        else{
            zonebcknum = BckNums.getInt("ZoneNum");
        }
        if(!BckNums.containsKey("PermNum")){
            BckNums.setInt("PermNum", 0);
        }
        else{
            permbcknum = BckNums.getInt("PermNum");
        }
        if(!BckNums.containsKey("InvNum")){
            BckNums.setInt("InvNum", 0);
        }
        else{
            invbcknum = BckNums.getInt("InvNum");
        }
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
     * Gets Debug setting
     * 
     * @return grantOverrules
     */
    public static boolean getDebug(){
        return debug;
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
    public String getDataBase(){
        return sqlDatabase;
    }
    
    /**
     * Gets MySQL Username
     * 
     * @return SQLUser
     */
    public String getUsername() {
        return sqlUser;
    }
    
    /**
     * Gets MySQL Password
     * 
     * @return SQLPassword  (maybe I should think about encryptions?)
     */
    public String getPassword() {
        return sqlPassword;
    }
    
    /**
     * Gets MySQL Driver
     * 
     * @return SQLDriver
     */
    public String getDriver() {
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
     * Gets and Sets BackUp Number for Polygons
     */
    public int getPolyNum(){
        if(polybcknum > 10){
            polybcknum = 1;
        }
        else{
            polybcknum++;
        }
        BckNums.setInt("PolyNum", polybcknum);
        return polybcknum;
    }
    
    /**
     * Gets and Sets BackUp Number for Zones
     */
    public int getZoneNum(){
        if(zonebcknum > 10){
            zonebcknum = 1;
        }
        else{
            zonebcknum++;
        }
        BckNums.setInt("ZoneNum", zonebcknum);
        return zonebcknum;
    }
    
    /**
     * Gets and Sets BackUp Number for Permissions
     */
    public int getPermNum(){
        if(permbcknum > 10){
            permbcknum = 1;
        }
        else{
            permbcknum++;
        }
        BckNums.setInt("PermNum", permbcknum);
        return permbcknum;
    }
    
    /**
     * Gets and Sets BackUp Number for Inventories
     */
    public int getInvNum(){
        if(invbcknum > 10){
            invbcknum = 1;
        }
        else{
            invbcknum++;
        }
        BckNums.setInt("InvNum", invbcknum);
        return invbcknum;
    }
    
    /**
     * Checks if Player Explosion Damage is enabled
     * 
     * @return true if it is, false if not
     */
    public boolean getPED(){
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
