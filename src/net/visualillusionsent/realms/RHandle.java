package net.visualillusionsent.realms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.visualillusionsent.realms.io.AnimalDestructor;
import net.visualillusionsent.realms.io.Healer;
import net.visualillusionsent.realms.io.InvaildPermissionTypeException;
import net.visualillusionsent.realms.io.LogFormat;
import net.visualillusionsent.realms.io.MobDestructor;
import net.visualillusionsent.realms.io.RealmsData;
import net.visualillusionsent.realms.io.RealmsFlatFile;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.RestrictionDamager;
import net.visualillusionsent.realms.io.ZoneNotFoundException;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Wand;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ICModServer;
import net.visualillusionsent.viutils.ChatColor;
import net.visualillusionsent.viutils.Updater;
import net.visualillusionsent.viutils.VersionCheck;

public class RHandle {
    private Logger mclog = Logger.getLogger("Minecraft");
    private Logger rlog = Logger.getLogger("Realms");
    
    private final String version = "6.0",
                            name = "Realms",
                        checkurl = "http://visualillusionsent.net/cmod_plugins/versions.php?plugin="+name,
                         downurl = "http://dl.dropbox.com/u/25586491/CanaryPlugins/Realms.jar",
                          jarloc = "plugins/Realms.jar";
    private final VersionCheck vc = new VersionCheck(version, checkurl);
    private final Updater update = new Updater(downurl, jarloc, name, Logger.getLogger("Minecraft"));
    
    private List<Zone> everywhere = new ArrayList<Zone>();
    private HashMap<ICModPlayer, Wand> wands = new HashMap<ICModPlayer, Wand>();
    private ICModServer serv;
    protected ZoneLists zl;
    protected RealmsProps rprop;
    private RealmsData datasource;
    private ScheduledThreadPoolExecutor threadhandle;
    
    public RHandle(ICModServer serv){
        this.serv = serv;
        setUpLogger();
    }
    
    public boolean initialize(){
        this.rprop = new RealmsProps(this);
        this.zl = new ZoneLists(this);
        this.datasource = new RealmsFlatFile(this);
        if(!this.datasource.loadZones()){
            return false;
        }
        threadhandle = new ScheduledThreadPoolExecutor(3);
        threadhandle.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        threadhandle.setKeepAliveTime(1, TimeUnit.MINUTES);
        if(!RealmsProps.getAC() && !RealmsProps.getAG() && !RealmsProps.getSM()){ //If allowing all mobs into Sanctuary area don't bother scheduling
            threadhandle.scheduleAtFixedRate(new MobDestructor(this), RealmsProps.getSanctuaryTimeOut(), RealmsProps.getSanctuaryTimeOut(), TimeUnit.MILLISECONDS); //Sanctuary Runnable
        }
        threadhandle.scheduleAtFixedRate(new AnimalDestructor(this), RealmsProps.getAnimalsTimeOut(), RealmsProps.getAnimalsTimeOut(), TimeUnit.MILLISECONDS); //Animals Runnable
        threadhandle.scheduleAtFixedRate(new Healer(this), RealmsProps.getHealingTimeOut(), RealmsProps.getHealingTimeOut(), TimeUnit.MILLISECONDS); //Healing Runnable
        threadhandle.scheduleAtFixedRate(new RestrictionDamager(this), RealmsProps.getRestrictTimeOut(), RealmsProps.getRestrictTimeOut(), TimeUnit.MILLISECONDS);
        return true;
    }
    
    public void log(Level lvl, String toLog){
        if(lvl.getName().startsWith("DEBUG") && RealmsProps.getDebug()){
            rlog.log(lvl, toLog);
        }
        else{
            mclog.log(lvl, toLog);
        }
    }
    
    public void log(Level lvl, String toLog, Exception e) {
        if(lvl.getName().startsWith("DEBUG") && RealmsProps.getDebug()){
            rlog.log(lvl, toLog, e);
        }
    }
    
    public void addEverywhere(Zone zone){
        everywhere.add(zone);
    }
    
    public Zone getEverywhere(ICModPlayer player){
        return getEverywhere(player.getWorldName(), player.getDimIndex());
    }
    
    public Zone getEverywhere(ICModMob mob){
        return getEverywhere(mob.getWorldName(), mob.getDimIndex());
    }
    
    public Zone getEverywhere(ICModBlock block){
        return getEverywhere(block.getWorldName(), block.getDimIndex());
    }
    
    public Zone getEverywhere(String world, int dim){
        synchronized(everywhere){
            for(Zone zone : everywhere){
                if(zone.isInWorld(world, dim)){
                    return zone;
                }
            }
        }
        Zone evr = new Zone(this, "EVERYWHERE-"+world.toUpperCase()+"-DIM"+String.valueOf(dim), null, world, dim);
        createDefaultPerms(evr);
        return evr;
    }
    
    private void createDefaultPerms(Zone zone){
        
        
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
    }
    
    public RealmsData getDataSource(){
        return datasource;
    }
    
    public ICModServer getServer(){
        return serv;
    }
    
    public Zone getNearestRespawn(ICModPlayer player){
        
        return null;
    }
    
    public Wand getPlayerWand(ICModPlayer player){
        if(wands.containsKey(player)){
            return wands.get(player);
        }
        Wand wand = new Wand(this, player);
        wands.put(player, wand);
        return wand;
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
    public boolean doGrantDeny(ICModPlayer player, String[] command) {
        
        boolean allowed = command[1].equalsIgnoreCase("grant");
        String playerName = command[2];
        String zoneName = command[4];
        
        try {
            Permission.PermType type = Permission.PermType.getTypeFromString(command[3]);
            Zone zone = ZoneLists.getZoneByName(zoneName);
            
            if(!zone.delegateCheck(player, type)){
                player.notify("You do not have permission to §eDELEGATE §6" + type + "§c permissions in the zone " + zone.getName());
                return true;
            }
            
            boolean override = (command.length == 6 && command[5].equalsIgnoreCase("override"));
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
                player.sendMessage("§6/realms " + command[1] + " <player/group name> " + command[3] + " " + command[4] + " OVERRIDE");
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
            player.sendMessage(p + "§6" + playerName + " §e" + type + "§a permission within zone §6" + command[4]);
        }
        catch (ZoneNotFoundException e) {
                player.notify("The zone §6'" + zoneName + "'§c could not be found!");
                return true;
        }
        catch (InvaildPermissionTypeException IPTE) {
            player.notify("The PermType §6'" + command[3] + "'§c is not valid!");
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
    
    public String getVersion(){
        return version;
    }
    
    public String getCurrent(){
        return vc.getCurrentVersion();
    }
    
    public boolean isLatest(){
        return vc.isLatest();
    }
    
    public String update(){
        return update.performUpdate();
    }
    
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
            FileHandler fhand = new FileHandler("plugins/config/Realms/Log/RealmsDebug%g.log", 52428800, 50, true); //52428800 == 50 MegaBytes - Max File Size
            fhand.setFormatter(lf);
            fhand.setEncoding("UTF-8");
            // Add to the desired logger if not there already
            if(rlog.getHandlers().length < 1){
                rlog.addHandler(chand);
                rlog.addHandler(fhand);
            }
        } catch (IOException e) {
            log(Level.WARNING, "Fail to create Realms DebugLogging File");
        }
    }
}
