package net.visualillusionsent.realms;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.visualillusionsent.realms.io.LogFormat;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.io.RealmsData;
import net.visualillusionsent.realms.io.RealmsFlatFile;
import net.visualillusionsent.realms.io.RealmsMySQL;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.exception.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.exception.ZoneNotFoundException;
import net.visualillusionsent.realms.io.threads.AnimalDestructor;
import net.visualillusionsent.realms.io.threads.Healer;
import net.visualillusionsent.realms.io.threads.MobDestructor;
import net.visualillusionsent.realms.io.threads.RestrictionDamager;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Wand;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModItem;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ICModServer;
import net.visualillusionsent.viutils.ChatColor;
import net.visualillusionsent.viutils.UpdateException;
import net.visualillusionsent.viutils.Updater;
import net.visualillusionsent.viutils.VersionCheck;

/**
 * Realms handler class
 * <p>
 * This file is part of Realms
 * 
 * @author Jason Jones
 */
public class RHandle {
    private Logger mclog = Logger.getLogger("Minecraft");
    private Logger rlog = Logger.getLogger("Realms");
    
    private final String version = "5.5_1",
                            name = "Realms",
                        checkurl = "http://visualillusionsent.net/cmod_plugins/versions.php?plugin=Realms",
                         downurl = "http://dl.dropbox.com/u/25586491/CanaryPlugins/Realms.jar",
                          jarloc = "plugins/Realms.jar";
    private final VersionCheck vc = new VersionCheck(version, checkurl);
    private final Updater update = new Updater(downurl, jarloc, name, Logger.getLogger("Minecraft"));
    
    private List<Zone> everywhere = new ArrayList<Zone>();
    private HashMap<ICModPlayer, Wand> wands = new HashMap<ICModPlayer, Wand>();
    private HashMap<ICModPlayer, ICModItem[]> inventories = new HashMap<ICModPlayer, ICModItem[]>();
    private ICModServer serv;
    protected ZoneLists zl;
    protected RealmsProps rprop;
    private RealmsData datasource;
    private ScheduledThreadPoolExecutor threadhandle;
    private FileHandler fhandle;
    
    private Connection conn;
    
    /**
     * class constructor
     * @param serv
     */
    public RHandle(ICModServer serv){
        this.serv = serv;
        setUpLogger();
    }
    
    /**
     * initializes Realms
     * @return true if success
     */
    public boolean initialize(){
        this.rprop = new RealmsProps(this);
        if(!rprop.initialize()){
            return false;
        }
        this.zl = new ZoneLists(this);
        if(RealmsProps.getMySQL()){
            this.datasource = new RealmsMySQL(this);
        }
        else{
            this.datasource = new RealmsFlatFile(this);
        }
        threadhandle = new ScheduledThreadPoolExecutor(3);
        threadhandle.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        if(!RealmsProps.getAC() && !RealmsProps.getAG() && !RealmsProps.getSM() && (RealmsProps.getSanctuaryTimeOut() > 0)){ //If allowing all mobs into Sanctuary area don't bother scheduling
            threadhandle.scheduleAtFixedRate(new MobDestructor(this), RealmsProps.getSanctuaryTimeOut(), RealmsProps.getSanctuaryTimeOut(), TimeUnit.MILLISECONDS); //Sanctuary Runnable
        }
        if(RealmsProps.getAnimalsTimeOut() > 200){
            threadhandle.scheduleAtFixedRate(new AnimalDestructor(this), RealmsProps.getAnimalsTimeOut(), RealmsProps.getAnimalsTimeOut(), TimeUnit.MILLISECONDS); //Animals Runnable
        }
        if(RealmsProps.getHealingTimeOut() > 200){
            threadhandle.scheduleAtFixedRate(new Healer(this), RealmsProps.getHealingTimeOut(), RealmsProps.getHealingTimeOut(), TimeUnit.MILLISECONDS); //Healing Runnable
        }
        if(RealmsProps.getRestrictTimeOut() > 200){
            threadhandle.scheduleAtFixedRate(new RestrictionDamager(this), RealmsProps.getRestrictTimeOut(), RealmsProps.getRestrictTimeOut(), TimeUnit.MILLISECONDS);
        }
        long save = 30;
        if(RealmsProps.getSaveTimeOut() > 5){
            save = RealmsProps.getSaveTimeOut();
        }
        threadhandle.scheduleAtFixedRate(new RSave(this), save, save, TimeUnit.MINUTES);
        return true;
    }
    
    /**
     * logger method
     * 
     * @param lvl
     * @param toLog
     */
    public void log(Level lvl, String toLog){
        if(lvl instanceof RLevel){
            boolean log = false;
            switch(lvl.intValue()){
                case 6001: if(RealmsProps.getDebugANIMAL_DESTROY()){        log = true; } break;
                case 6002: if(RealmsProps.getDebugBLOCK_BREAK()){           log = true; } break;
                case 6003: if(RealmsProps.getDebugBLOCK_DESTROY()){         log = true; } break;
                case 6004: if(RealmsProps.getDebugBLOCK_PHYSICS()){         log = true; } break;
                case 6005: if(RealmsProps.getDebugBLOCK_PLACE()){           log = true; } break;
                case 6006: if(RealmsProps.getDebugBLOCK_RIGHTCLICKED()){    log = true; } break; 
                case 6007: if(RealmsProps.getDebugCOMMAND()){               log = true; } break; 
                case 6008: if(RealmsProps.getDebugCOMMAND_CHECK()){         log = true; } break;
                case 6009: if(RealmsProps.getDebugDAMAGE()){                log = true; } break; 
                case 6010: if(RealmsProps.getDebugEAT()){                   log = true; } break; 
                case 6011: if(RealmsProps.getDebugENDERMAN_DROP()){         log = true; } break; 
                case 6012: if(RealmsProps.getDebugENDERMAN_PICKUP()){       log = true; } break; 
                case 6013: if(RealmsProps.getDebugENTITY_RIGHTCLICKED()){   log = true; } break; 
                case 6014: if(RealmsProps.getDebugEXPLOSION()){             log = true; } break; 
                case 6015: if(RealmsProps.getDebugFLOW()){                  log = true; } break;
                case 6016: if(RealmsProps.getDebugIGNITE()){                log = true; } break;
                case 6017: if(RealmsProps.getDebugITEM_DROP()){             log = true; } break;
                case 6018: if(RealmsProps.getDebugITEM_PICKUP()){           log = true; } break;
                case 6019: if(RealmsProps.getDebugITEM_USE()){              log = true; } break;
                case 6020: if(RealmsProps.getDebugMOB_DESTROY()){           log = true; } break;
                case 6021: if(RealmsProps.getDebugMOB_SPAWN()){             log = true; } break;
                case 6022: if(RealmsProps.getDebugMOB_TARGET()){            log = true; } break;
                case 6023: if(RealmsProps.getDebugPISTON_EXTEND()){         log = true; } break;
                case 6024: if(RealmsProps.getDebugPISTON_RETRACT()){        log = true; } break;
                case 6025: if(RealmsProps.getDebugPORTAL_USE()){            log = true; } break;
                case 6026: if(RealmsProps.getDebugPLAYER_EXPLODE()){        log = true; } break;
                case 6027: if(RealmsProps.getDebugPLAYER_HEAL()){           log = true; } break;
                case 6028: if(RealmsProps.getDebugPLAYER_RESTRICT()){       log = true; } break;
                case 6029: if(RealmsProps.getDebugFOOD_EXHAUSTIONCHANGE()){ log = true; } break;
                case 6100:
                case 6200:
                case 6300:
                    if(RealmsProps.getDebugOther()){ log = true; } break;
                default: break;
            }
            if(log){
                rlog.log(lvl, toLog);
            }
        }
        else{
            mclog.log(lvl, "[Realms] "+toLog);
        }
    }
    
    /**
     * logger method
     * 
     * @param lvl
     * @param toLog
     * @param exeception
     */
    public void log(Level lvl, String toLog, Exception e) {
        if(RealmsProps.getDebugOther()){
            rlog.log(lvl, toLog, e);
        }
    }
    
    /**
     * adds an everywhere zone
     * @param zone
     */
    public void addEverywhere(Zone zone){
        everywhere.add(zone);
    }
    
    /**
     * gets an everywhere zone
     * @param player
     * @return everywhere zone
     * @see #getEverywhere(String, Integer)
     */
    public Zone getEverywhere(ICModPlayer player){
        return getEverywhere(player.getWorldName(), player.getDimIndex());
    }
    
    /**
     * gets an everywhere zone
     * 
     * @param mob
     * @return everywhere zone
     * @see #getEverywhere(String, Integer)
     */
    public Zone getEverywhere(ICModMob mob){
        return getEverywhere(mob.getWorldName(), mob.getDimIndex());
    }
    
    /**
     * gets an everywhere zone
     * 
     * @param block
     * @return everywhere zone
     * @see #getEverywhere(String, Integer)
     */
    public Zone getEverywhere(ICModBlock block){
        return getEverywhere(block.getWorldName(), block.getDimIndex());
    }
    
    /**
     * gets an everywhere zone
     * 
     * @param world
     * @param dim
     * @return everywhere zone
     */
    public Zone getEverywhere(String world, int dim){
        if(world == null){
            return getEverywhere(serv.getDefaultWorldName(), 0);
        }
        synchronized(everywhere){
            for(Zone zone : everywhere){
                if(zone.isInWorld(world, dim)){
                    return zone;
                }
            }
        }
        Zone evr = new Zone(this, "EVERYWHERE-"+world.toUpperCase().replace("/", "+")+"-DIM"+String.valueOf(dim), null, world, dim);
        createDefaultPerms(evr);
        return evr;
    }
    
    /**
     * creates default permissions for the zone
     * @param zone
     */
    private void createDefaultPerms(Zone zone){
        
        //Grant admins all access
        for(String admin : serv.getAdminGroups()){
            zone.setPermission("g:"+admin, Permission.PermType.DELEGATE, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.ZONING, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.MESSAGE, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.COMBAT, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.ENVIRONMENT, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.ENTER, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.EAT, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.CREATE, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.DESTROY, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.INTERACT, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.COMMAND, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.TELEPORT, true, true);
            zone.setPermission("g:"+admin, Permission.PermType.AUTHED, true, true);
        }
        
        //Deny everyone else DELEGATE, ZONING, MESSAGE, COMBAT, ENVIRONMENT
        zone.setPermission("everyone", Permission.PermType.DELEGATE, false, false);
        zone.setPermission("everyone", Permission.PermType.ZONING, false, false);
        zone.setPermission("everyone", Permission.PermType.MESSAGE, false, false);
        zone.setPermission("everyone", Permission.PermType.COMBAT, false, false);
        zone.setPermission("everyone", Permission.PermType.ENVIRONMENT, false, false);
        
        //Grant everyone else ENTER, EAT, CREATE, DESTROY, INTERACT, COMMAND, TELEPORT, and AUTHED
        zone.setPermission("everyone", Permission.PermType.ENTER, true, false);
        zone.setPermission("everyone", Permission.PermType.EAT, true, false);
        zone.setPermission("everyone", Permission.PermType.CREATE, true, false);
        zone.setPermission("everyone", Permission.PermType.DESTROY, true, false);
        zone.setPermission("everyone", Permission.PermType.INTERACT, true, false);
        zone.setPermission("everyone", Permission.PermType.COMMAND, true, false);
        zone.setPermission("everyone", Permission.PermType.TELEPORT, true, false);
        zone.setPermission("everyone", Permission.PermType.AUTHED, true, false);
        
        zone.save();
    }
    
    /**
     * gets Realms data source
     * @return data source
     */
    public RealmsData getDataSource(){
        return datasource;
    }
    
    /**
     * gets the server wrapper
     * @return serv
     */
    public ICModServer getServer(){
        return serv;
    }
    
    /**
     * gets a player's wand
     * 
     * @param player
     * @return wand
     */
    public Wand getPlayerWand(ICModPlayer player){
        if(wands.containsKey(player)){
            return wands.get(player);
        }
        Wand wand = new Wand(this, player);
        wands.put(player, wand);
        return wand;
    }
    
    public void removePlayerWand(ICModPlayer player){
        if(wands.containsKey(player)){
            wands.remove(player);
        }
    }
    
    /**
     * Command Checks
     * 
     * @param command
     * @param player
     * @return The Command executer
     */
    public boolean executeCommand(String[] command, ICModPlayer player) {
        if (command.length == 1) {
            StringBuilder builder = new StringBuilder("Please specify one of: "+ChatColor.ORANGE);
            for(RealmsCommands rc : RealmsCommands.values()) {
                builder.append(rc.getCommandName());
                builder.append(" ");
            }
            player.notify(builder.toString());
            return true;
        }
        
        if (!player.canUseCommand(command[0] + command[1])) {
            player.notify("You do not have rights to /realms " + command[1]);
            return true;
        }
        

        String[] newCommands = new String[command.length-2];
        for(int index = 0; index < command.length-2; index++){
            newCommands[index] = command[index+2];
        }
        try{
            RealmsCommands myCommand = RealmsCommands.valueOf(command[1].toUpperCase());
            return myCommand.execute(player, newCommands, this);
        }
        catch(IllegalArgumentException IAE){
            return RealmsCommands.INVALID.execute(player, null, this);
        }
    }
    
    /**
     * Console Command Checks
     * 
     * @param command
     * @param player
     * @return The Command executer
     */
    public boolean executeConsoleCommand(String[] command) {    
        String[] newCommands;
        
        if (command[0].equalsIgnoreCase("realms")) {
            if (command.length == 1) {
                StringBuilder builder = new StringBuilder("Please specify one of: ");
                for(RealmsCommands rc : RealmsCommands.values()) {
                    builder.append(rc.getCommandName()+" ");
                }
                System.out.println(builder.toString());
                return true;
            }
            
            newCommands = command;
        } else {
            String theCommand = command[0].substring(7);
            
            newCommands = new String[command.length+1];
            newCommands[0] = "realms";
            newCommands[1] = theCommand;
            for (int i = 1; i < command.length; i++) {
                newCommands[i+1] = command[i];
            }
        }
        try{
            RealmsConsole CCommand = RealmsConsole.valueOf(newCommands[1].toUpperCase());
        
            return CCommand.execute(newCommands, this);
        }
        catch(IllegalArgumentException IAE){
            return RealmsConsole.INVALID.execute(newCommands, this);
        }
    }
    
    /**
     * Does the Grant/Deny Checks
     * 
     * @param player
     * @param command
     * @param realm
     * @param Usage
     * @return
     */
    public boolean doGrantDeny(ICModPlayer player, String[] command, boolean allowed) {
        
        String playerName = command[0];
        String zoneName = command[2];
        
        try {
            Permission.PermType type = Permission.PermType.getTypeFromString(command[1]);
            Zone zone = ZoneLists.getZoneByName(zoneName);
            
            if(!zone.delegateCheck(player, type)){
                player.notify("You do not have permission to §eDELEGATE §6" + type + "§c permissions in the zone " + zone.getName());
                return true;
            }
            
            boolean override = (command.length == 4 && command[3].equalsIgnoreCase("override"));
            if (playerName.contains(",")){
                player.notify("Player names cannot contain commas!");
                return true;
            }
        
            // Give warning message for default group permissions
            String defaultGroupName = serv.getDefaultGroup();
            String tempString = "";
            if (playerName.startsWith("g:")){
                tempString = playerName.replaceAll("g:", "");
            }
            if (playerName.equalsIgnoreCase(defaultGroupName) || defaultGroupName.equalsIgnoreCase(tempString)) {
                player.sendMessage("§4WARNING! §eEVERYBODY (including admins) §cis in the default group.");
                player.sendMessage("§cDid you really want to do this?");
                player.sendMessage("§cFor exceptions, you probably want to use the OVERRIDE keyword. Example:");
                player.sendMessage("§6/realms deny <player/group name> " + command[1] + " " + command[2] + " OVERRIDE");
            }
        
            // Made it past all the checks!
            zone.setPermission(playerName, type, allowed, override);
            String p = "";
            if(allowed){
                p = "§2Granted ";
            }
            else{
                p = "§5Denied ";
            }
            player.sendMessage(p + "§6" + playerName + " §e" + type + "§a permission within zone §6" + command[2]);
        }
        catch (ZoneNotFoundException e) {
                player.notify("The zone §6'" + zoneName + "'§c could not be found!");
                return true;
        }
        catch (InvaildPermissionTypeException IPTE) {
            player.notify("The PermType §6'" + command[1] + "'§c is not valid!");
            return true;
        }
        return true;
    }
    
    /**
     * Does the Grant/Deny Checks (Console)
     * 
     * @param command
     * @param realm
     * @param Usage
     * @return
     */
    public boolean ConsoledoGrantDeny(String[] command, String Usage) {
        
        boolean allowed = command[1].equalsIgnoreCase("grant");
        String playerName = command[2];
        String zoneName = command[4];
        
        try {
            Permission.PermType type = Permission.PermType.getTypeFromString(command[3]);
            Zone zone = ZoneLists.getZoneByName(zoneName);
            
            boolean override = (command.length == 6 && command[5].equalsIgnoreCase("override"));
            if (playerName.contains(",")){
                System.out.println("Error: Player names cannot contain commas!");
                return true;
            }
        
            // Give warning message for default group permissions
            String defaultGroupName = serv.getDefaultGroup();
            String tempString = "";
            if (playerName.startsWith("g:")){
                tempString = playerName.replaceAll("g:", "");
            }
            if (playerName.equalsIgnoreCase(defaultGroupName) || defaultGroupName.equalsIgnoreCase(tempString)) {
                System.out.println("WARNING! EVERYBODY (including admins) is in the default group.");
                System.out.println("Did you really want to do this?");
                System.out.println("For exceptions, you probably want to use the OVERRIDE keyword. Example:");
                System.out.println("/realms " + command[1] + " <player/group name> " + command[3] + " " + command[4] + " OVERRIDE");
            }
        
            // Made it past all the checks!
            zone.setPermission(playerName, type, allowed, override);
            String p = "";
            if(allowed){
                p = "Granted ";
            }
            else{
                p = "Denied ";
            }
            System.out.println(p + " " + playerName + " " + type + " permission within zone " + command[4]);
        }
        catch (ZoneNotFoundException e) {
            System.out.println("Error: The zone '" + zoneName + "' could not be found!");
        }
        catch (InvaildPermissionTypeException IPTE) {
            System.out.println("Error: The PermType '" + command[3] + "' is not valid!");
        }
        return true;
    }
    
    /**
     * gets version number
     * 
     * @return version
     */
    public String getVersion(){
        return version;
    }
    
    /**
     * gets current release version
     * 
     * @return current version
     */
    public String getCurrent(){
        return vc.getCurrentVersion();
    }
    
    /**
     * checks if is latest release
     * 
     * @return true if it is
     */
    public boolean isLatest(){
        return vc.isLatest();
    }
    
    /**
     * preforms an update
     * 
     * @return return message
     */
    public String update(){
        try{
            update.performUpdate();
        }
        catch(UpdateException ue){
            return ue.getMessage();
        }
        return "Update Successful!";
    }
    
    /**
     * sets up the debug logger
     */
    private void setUpLogger(){
        File LogDir = new File("plugins/config/Realms/Log/");
        try {
            if(!LogDir.exists()){
                LogDir.mkdirs();
            }
            rlog.setUseParentHandlers(false);
            LogFormat lf = new LogFormat();
            ConsoleHandler chand = new ConsoleHandler();
            chand.setFormatter(lf);
            // Create an appending file handler
            fhandle = new FileHandler("plugins/config/Realms/Log/RealmsDebug%g.log", 2621440, 21, true); //2621440 == 20 MegaBytes - Max File Size & max 21 files (0 - 20)
            fhandle.setFormatter(lf);
            fhandle.setEncoding("UTF-8");
            // Add to the desired logger if not there already
            if(rlog.getHandlers().length < 1){
                rlog.addHandler(chand);
                rlog.addHandler(fhandle);
            }
        } catch (IOException e) {
            log(Level.WARNING, "Fail to create Realms DebugLogging File");
        }
    }
    
    public void handleInventory(ICModPlayer player, boolean store){
        if(store){
            if(!inventories.containsKey(player)){
               inventories.put(player, player.getInvContents());
               player.clearInventory();
            }
        }
        else{
            if(inventories.containsKey(player)){
                player.setInvContents(inventories.get(player));
                inventories.remove(player);
            }
        }
    }
    
    /**
     * sends welcome/farewell messages to a player
     * 
     * @param player
     */
    public void playerMessage(ICModPlayer player) {
        List<Zone> oldZoneList = ZoneLists.getplayerZones(player);
        Zone everywhere = getEverywhere(player);
        List<Zone> newZoneList = ZoneLists.getZonesPlayerIsIn(everywhere, player);
        if(oldZoneList.isEmpty()){
            ZoneLists.addplayerzones(player, newZoneList);
            return;
        }
        else if (oldZoneList.hashCode() != newZoneList.hashCode()) {
            for(Zone zone : oldZoneList){
                if(!newZoneList.contains(zone)){
                    zone.farewell(player);
                }
            }
            for(Zone zone : newZoneList){
                if(!oldZoneList.contains(zone)){
                    zone.greet(player);
                }
            }
            ZoneLists.addplayerzones(player, newZoneList);
        }
    }
    
    public Connection getSQLConnection() throws SQLException{
        if(conn != null){
            return conn;
        }
        else if(RealmsProps.getCMySQL()){
            conn = serv.getCanarySQLConnection();
            return conn;
        }
        else{
            conn = DriverManager.getConnection(RealmsProps.getDataBase(), RealmsProps.getUsername(), RealmsProps.getPassword(this));
            return conn;
        }
    }
    
    public void releaseConn() throws SQLException{
        if(RealmsProps.getCMySQL()){
            serv.releaseConn();
        }
        else{
            if(conn != null && !conn.isClosed()){
                conn.close();
            }
        }
        conn = null;
    }

    public void storeInventory(ICModPlayer player, ICModItem[] items) {
        if(!inventories.containsKey(player)){
            inventories.put(player, items);
         }
    }

    public HashMap<ICModPlayer, ICModItem[]> getInvMap() {
        HashMap<ICModPlayer, ICModItem[]> invmap = new HashMap<ICModPlayer, ICModItem[]>(inventories);
        inventories.putAll(inventories);
        return invmap;
    }
    
    public void forceSave(){
        threadhandle.execute(new RSave(this));
    }
    
    //TempFix for saving :/
    public class RSave implements Runnable{
        
        public RSave(RHandle rhandle){
        }
        
        /**
         * Runs the saving of the data
         */
        public void run(){
            /*Saving Data*/
            log(Level.INFO, "Saving...");
            while(datasource.dumpzone()){/*waiting*/if(!datasource.dumpzone()){ break;}else{ continue; }}
            while(datasource.dumppoly()){/*waiting*/if(!datasource.dumppoly()){ break;}else{ continue; }}
            while(datasource.dumpperm()){/*waiting*/if(!datasource.dumpperm()){ break;}else{ continue; }}
            while(datasource.dumpinv()){/*waiting*/if(!datasource.dumpinv()){ break;}else{ continue;}}
            log(Level.INFO, "Save complete!");
        }
    }
    
    public void terminate(){
        threadhandle.shutdown(); //Terminate Threads
        fhandle.close(); //Close out logger
    }
}
