import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.runnables.RealmsPlayerExplosionDamage;
import net.visualillusionsent.realms.zones.Wand;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.ZoneLists;

/**
 * Realms listener class
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class RealmsListener extends PluginListener{
    private RHandle rhand;
    
    public RealmsListener(RHandle rhand){
        this.rhand = rhand;
    }
      
    private ArrayList<Player> Creative = new ArrayList<Player>();
      
    @Override
    public PluginLoader.HookResult canPlayerUseCommand(Player player, String command){
        boolean allow = true;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
            String[] cmd = null;
            if (command != null){
                cmd = command.split(" ");
            }
            if (cmd != null){
                if (!(cmd[0].equalsIgnoreCase("/realms") || cmd[0].equalsIgnoreCase("/wand") || RealmsProps.isCO(cmd[0]))){
                    if (!zone.permissionCheck(cPlayer, Permission.PermType.COMMAND)){
                        player.notify("You are not allowed to execute commands in this area!");
                        allow = false;
                    }
                }
            }
            rhand.log(RLevel.DEBUGINFO, "canPlayerUseCommand: Player: '"+player.getName()+"' Command: '"+(cmd != null ? cmd[0] : "NULL")+"' Zone: '"+zone.getName()+"' Result: "+(allow ? "'Allowed'" : "'Denied'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ canPlayerUseCommand... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ canPlayerUseCommand: ", e);
        }
        return (allow ? PluginLoader.HookResult.DEFAULT_ACTION : PluginLoader.HookResult.PREVENT_ACTION);  
    }
    
    @Override
    public boolean onBlockBreak(Player player, Block block) {
        boolean deny = false;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cBlock);
          
            if(block != null){
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.DESTROY);
            }
            rhand.log(RLevel.DEBUGINFO, "onBlockBreak: Player: '"+player.getName()+"' Block: '"+(block != null ? block.toString() : "NULL")+"' Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onBlockBreak... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onBlockBreak: ", e);
        }
        return deny;
    }
  
    @Override
    public boolean onBlockDestroy(Player player, Block block){
        boolean deny = false;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cBlock);
            if(block != null){
                if (RealmsProps.isOpBlock(block.getType())){
                    deny = !zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
                }
            }
            rhand.log(RLevel.DEBUGINFO, "onBlockDestroy: Player: '"+player.getName()+"' Block: '"+(block != null ? block.toString() : "NULL")+"' Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onBlockDestroy... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onBlockDestroy: ", e);
        }
        return deny;
    }
  
    @Override
    public boolean onBlockPhysics(Block block, boolean placed){
        boolean deny = false;
        try{
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            if ((block.getType() == 12) || (block.getType() == 13)){
                deny = !zone.getPhysics();
                //rhand.log(RLevel.DEBUGINFO, "onBlockPhysics: Block: '"+block.toString()+"' Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onBlockBreak... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onBlockBreak: ", e);
        }
        return deny;
    }
  
    @Override
    public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
        boolean deny = false;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock;
            Zone zone;
            if(blockPlaced != null) {
                cBlock = new CModBlock(blockPlaced);
                zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cBlock);
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.CREATE);
            }
            else{
                cBlock = new CModBlock(blockClicked);
                zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cBlock);
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.CREATE);
            }
            rhand.log(RLevel.DEBUGINFO, "onBlockDestroy: Player: '"+player.getName()+"'" +
                                        " BlockPlaced: '"+(blockPlaced != null ? blockPlaced.toString() : "NULL")+"'" +
                                        " BlockClicked: '"+(blockClicked != null ? blockClicked.toString() : "NULL")+"'"+
                                        " ItemInHand: '"+(itemInHand != null ? itemInHand.toString() : "NULL")+"'"+
                                        " Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onBlockPlace... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onBlockPlace: ", e);
        }
        return deny;
    }
  
    @Override
    public boolean onBlockRightClick(Player player, Block blockClicked, Item itemInHand) {
        boolean deny = false, isOpBlock = RealmsProps.isOpBlock(blockClicked.getType());
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = new CModBlock(blockClicked);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            if(itemInHand.getItemId() == RealmsProps.getWandType()){
                return rhand.getPlayerWand(cPlayer).wandClick(cPlayer, cBlock);
            }
            else if (isOpBlock){
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
            }
            rhand.log(RLevel.DEBUGINFO, "onBlockRightClick: Player: '"+player.getName()+"'" +
                    " BlockClicked: '"+(blockClicked != null ? blockClicked.toString() : "NULL")+"'"+
                    " ItemInHand: '"+(itemInHand != null ? itemInHand.toString() : "NULL")+"'"+
                    " Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onBlockRightClick... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onBlockRightClick: ", e);
        }
        return deny;
    }
  
    @Override
    public boolean onCommand(Player player, String[] args) {
        boolean toRet = false, deny = false;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
            if(args[0].toLowerCase().startsWith("/realms")){
                toRet = rhand.executeCommand(args, cPlayer);
            }
            else if(args[0].equalsIgnoreCase("/wand") && player.canUseCommand("/wand")){
                toRet = rhand.getPlayerWand(cPlayer).wandCommand(cPlayer, args);
            }
            else if(args[0].equals("/mode") && args.length > 2){
                if(args[1].equals("0")){
                    Player p = etc.getServer().matchPlayer(args[2]);
                    if(p != null && Creative.contains(p)){
                        Creative.remove(p);
                    }
                }
                else if(args[1].equals("1")){
                    Player p = etc.getServer().matchPlayer(args[2]);
                    if(p != null && !Creative.contains(p)){
                        Creative.add(p);
                    }
                }
            } 
            else if(args[0].equals("/warp")){ //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                if(args.length == 2){
                    Warp warp = etc.getDataSource().getWarp(args[1]);
                    if(warp != null){
                        CModBlock cBlock = new CModBlock(warp.Location.getWorld().getBlockAt((int)Math.floor(warp.Location.x), (int)Math.floor(warp.Location.y), (int)Math.floor(warp.Location.z)));
                        zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                      
                        if(!zone.permissionCheck(cPlayer, Permission.PermType.ENTER)) {
                            player.notify("You do not have permission to enter that zone!");
                            toRet = true;
                            deny = true;
                        }
                        else if(!zone.permissionCheck(cPlayer, Permission.PermType.TELEPORT)){
                            player.notify("You do not have permission to teleport into that zone!");
                            toRet = true;
                            deny = true;
                        }
                    }
                }
                else if(args.length > 2){ //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                    Player telep = etc.getServer().matchPlayer(args[2]);
                    if(telep != null){
                        Warp warp = etc.getDataSource().getWarp(args[1]);
                        if(warp != null){
                            CModPlayer toTele = new CModPlayer(telep);
                            CModBlock cBlock = new CModBlock(warp.Location.getWorld().getBlockAt((int)Math.floor(warp.Location.x), (int)Math.floor(warp.Location.y), (int)Math.floor(warp.Location.z)));
                            zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                            if(!zone.permissionCheck(toTele, Permission.PermType.ENTER)) {
                                player.notify(telep.getName()+" does not have permission to enter that zone!");
                                toRet = true;
                                deny = true;
                            }
                            else if(!zone.permissionCheck(toTele, Permission.PermType.TELEPORT)){
                                player.notify(telep.getName()+" does not have permission to teleport into that zone!");
                                toRet = true;
                                deny = true;
                            }
                        }
                    }
                }
            }
            else if(args[0].equals("/tp")){ //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                if(args.length > 2){
                    Player pto = etc.getServer().matchPlayer(args[1]);
                    if(pto != null){
                        CModBlock cBlock = new CModBlock(pto.getWorld().getBlockAt((int)Math.floor(pto.getX()), (int)Math.floor(pto.getY()), (int)Math.floor(pto.getZ())));
                        zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                        if(!zone.permissionCheck(cPlayer, Permission.PermType.ENTER)) {
                            player.notify("You do not have permission to enter that zone!");
                            toRet = true;
                            deny = true;
                        }
                        else if(!zone.permissionCheck(cPlayer, Permission.PermType.TELEPORT)){
                            player.notify("You do not have permission to teleport into that zone!");
                            toRet = true;
                            deny = true;
                        }
                    }
                }
            }
            else if(args[0].equals("/tphere")){ //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                if(args.length > 2){
                    Player pto = etc.getServer().matchPlayer(args[1]);
                    if(pto != null){
                        CModPlayer toTele = new CModPlayer(pto);
                        zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
                        if(!zone.permissionCheck(toTele, Permission.PermType.ENTER)) {
                            player.notify(pto.getName()+" does not have permission to enter this zone!");
                            toRet = true;
                            deny = true;
                        }
                        else if(!zone.permissionCheck(toTele, Permission.PermType.TELEPORT)){
                            player.notify(pto.getName()+" does not have permission to teleport into that zone!");
                            toRet = true;
                            deny = true;
                        }
                    }
                }
            }
            String cmd = etc.combineSplit(0, args, " ");
            rhand.log(RLevel.DEBUGINFO, "canPlayerUseCommand: Player: '"+player.getName()+"' Command: '"+cmd+"' Zone: '"+(zone != null ? zone.getName() : "NULL")+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onCommand... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onCommand: ", e);
        }
        return toRet;
    }
  
    public boolean onConsoleCommand(String[] cmd){
        if(cmd[0].equalsIgnoreCase("realms")){
            return rhand.executeConsoleCommand(cmd);
        }
        return false;
    }
  
    @Override
    public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
        boolean deny = false;
        try{
            if (defender.isPlayer()){
                CModPlayer cPlayer = new CModPlayer(defender.getPlayer());
                Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
                if (attacker != null && type.equals(PluginLoader.DamageType.ENTITY)) {
                    if (attacker.isPlayer()){
                        deny = !zone.getPVP();
                        rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+attacker.getName()+"' tried to attack '"+defender.getName()+"' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Denied'" : "Allowed'"));
                    }else if (attacker.isMob()){
                        deny = zone.getSanctuary();
                        rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+attacker.getName()+"' tried to attack '"+defender.getName()+"' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Denied'" : "Allowed'"));
                        return deny;
                    }
                }
                else if (type.equals(PluginLoader.DamageType.FALL)){
                    deny = !zone.getFall();
                    rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+defender.getName()+"' attempted to take 'Fall Damage' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.SUFFOCATION) || type.equals(PluginLoader.DamageType.WATER)){
                    deny = !zone.getSuffocate();
                    rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+defender.getName()+"' attempted to take 'Suffocation Damage' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.FIRE) || type.equals(PluginLoader.DamageType.LAVA) || type.equals(PluginLoader.DamageType.FIRE_TICK)){
                    deny = !zone.getFire();
                    rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+defender.getName()+"' attempted to take 'Fire Damage' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if(type.equals(PluginLoader.DamageType.POTION)){
                    deny = !zone.getPotion();
                    rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+defender.getName()+"' attempted to take 'Potion Damage' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if(type.equals(PluginLoader.DamageType.EXPLOSION)){
                    if(RealmsProps.getPED()){
                        if(zone.getSanctuary()){
                            deny = true;
                        }
                    }
                    else if(zone.getSanctuary()){
                        deny = true;
                    }
                    rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+defender.getName()+"' attempted to take 'Explosion Damage' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if(type.equals(PluginLoader.DamageType.CREEPER_EXPLOSION)){
                    if(!zone.getSanctuary()){
                        if(!RealmsProps.getPED() && !zone.getCreeper()){
                            deny = true;
                        }
                    }
                    else{
                        deny = true;
                    }
                    rhand.log(RLevel.DEBUGINFO, "Realms.onDamage: '"+defender.getName()+"' attempted to take 'Creeper_Explosion Damage' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }     
            }
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onDamage... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onDamage: ", e);
        }
        return deny;
    }
  
    @Override
    public void onDisconnect(Player player) {
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            //Reset the player's wand onDisconnect
            Wand wand = rhand.getPlayerWand(cPlayer);
            wand.reset();
            rhand.removePlayerWand(cPlayer);
        }
        catch(Exception e){ 
            //Wand wasn't existent...
        }
    }
  
    @Override
    public boolean onEat(Player player, Item item){
        boolean allow = true;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
            allow = zone.permissionCheck(cPlayer, Permission.PermType.EAT);
            rhand.log(RLevel.DEBUGINFO, "Realms.onEat: '"+player.getName()+"' attempted to 'EAT' in Zone: '"+zone.getName()+"' Result: '"+(allow ? "Allowed'" : "Denied'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onEat... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onEat: ", e);
        }
        return !allow;
  }
  
    @Override
    public boolean onEndermanDrop(Enderman entity, Block block){
        boolean deny = false;
        try{
            CModBlock cBlock;
            if(block != null){
                cBlock = new CModBlock(block);
            }
            else{
                cBlock = new CModBlock(entity.getWorld().getBlockAt((int)Math.floor(entity.getX()), (int)Math.floor(entity.getY()), (int)Math.floor(entity.getZ())));
            }
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            deny = !zone.getEnderman();
            rhand.log(RLevel.DEBUGINFO, "Realms.onEndermanDrop: 'Enderman' attempted to place Block: '"+(block != null ? block.toString() : "NULL")+"' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Denied'" : "Allowed'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onEndermanDrop... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onEndermanDrop: ", e);
        }
        return deny;
    }
  
    @Override
    public boolean onEndermanPickup(Enderman entity, Block block){
        boolean deny = false;
        try{
            CModBlock cBlock;
            if(block != null){
                cBlock = new CModBlock(block);
            }
            else{
                cBlock = new CModBlock(entity.getWorld().getBlockAt((int)Math.floor(entity.getX()), (int)Math.floor(entity.getY()), (int)Math.floor(entity.getZ())));
            }
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            deny = !zone.getEnderman();
            rhand.log(RLevel.DEBUGINFO, "Realms.onEndermanPickup: 'Enderman' attempted to pickup Block: '"+(block != null ? block.toString() : "NULL")+"' in Zone: '"+zone.getName()+"' Result: '"+(deny ? "Denied'" : "Allowed'"));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onEndermanPickup... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onEndermanPickup: ", e);
        }
        return deny;
    }
  
    @Override
    public PluginLoader.HookResult onEntityRightClick(Player player, BaseEntity entityClicked, Item itemInHand) {
        boolean allow = true;
        try{
            if(entityClicked != null){
                CModPlayer cPlayer = new CModPlayer(player);
                Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
                allow = zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
                rhand.log(RLevel.DEBUGINFO, "Realms.onEntityRightClick: '"+player.getName()+"' attempted to RightClick Entity: '"+entityClicked.getName()+"' in Zone: '"+zone.getName()+"' Result: "+(allow ? "Allowed" : "Denied"));
            }
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onEntityRightClick... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onEntityRightClick: ", e);
        }
        return (allow ? PluginLoader.HookResult.DEFAULT_ACTION : PluginLoader.HookResult.PREVENT_ACTION);
    }
  
    @SuppressWarnings("rawtypes")
    @Override
    public boolean onExplosion(Block block, BaseEntity entity, List blocksaffected) {
        try{
            if (entity == null){
                return explodeScan(block, "TNT");
            }
            else if (entity.getName().equals("Creeper")){
                return explodeScan(block, "Creeper");
            }
            else if (entity.getName().equals("Ghast")){
                return explodeScan(block, "Ghast");
            }
            else{
                return explodeScan(block, "Creeper"); //Making all other explosion Creeper
            }
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onExplosion... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onExplosion: ", e);
        }
        return false;
    }
  
    @Override
    public boolean onFlow(Block blockFrom, Block blockTo){
        boolean deny = false;
        try{
            CModBlock cBlock1 = new CModBlock(blockFrom);
            CModBlock cBlock2 = new CModBlock(blockTo);
            Zone zone1 = ZoneLists.getZone(rhand.getEverywhere(cBlock1), cBlock1);
            Zone zone2 = ZoneLists.getZone(rhand.getEverywhere(cBlock2), cBlock2);
            if (!zone1.getFlow()){
                deny = true;
            }
            else if (!zone2.getFlow()){
                deny = true;
            }
            rhand.log(RLevel.DEBUGINFO, "Realms.onFlow: Zone1: "+zone1.getName()+" Result: '"+(!zone1.getFlow() ? "Denied'" : "Allowed' Zone2: "+zone2.getName()+" Result: '"+(zone2.getFlow() ? "Allowed'" : "Denied'")));
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onFlow... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onFlow: ", e);
        }
        return deny;
    }
  
    @Override
    public Float onFoodExhaustionChange(Player player, Float oldLevel, Float newLevel){
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
            rhand.log(RLevel.DEBUGINFO, "RealmsListener.onFoodExhaustionChange: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' Starve Result: "+(zone.getStarve() ? "'Allowed'" : "'Denied'"));
            return (zone.getStarve() ? newLevel : oldLevel);
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onFoodExhaustionChange... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onFoodExhaustionChange: ", e);
        }
        return oldLevel;
    }
  
    @Override
    public boolean onIgnite(Block block, Player player){
        try{
            int bs = block.getStatus();
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            if ((bs == 1) || (bs == 3) || (bs == 4) || (bs == 5)){
                boolean allow = zone.getSpread();
                //rhand.log(RLevel.DEBUGINFO, "RealmsListener.onIgnite: Type: 'SPREAD' Zone: '"+zone.getName()+"' Result: "+(allow ? "'Allowed'" : "'Denied'"));
                return !allow;
            }
            else if (bs == 2 || bs == 6){
                CModPlayer cPlayer = new CModPlayer(player);
                boolean allow = zone.permissionCheck(cPlayer, Permission.PermType.IGNITE);
                rhand.log(RLevel.DEBUGINFO, "RealmsListener.onIgnite: Type: 'IGNITE' Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' Result: "+(allow ? "'Allowed'" : "'Denied'"));
                return !allow;
            }
        }
        catch(Exception e){
            //rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onIgnite... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onIgnite: ", e);
        }
        return false;
    }
  
    @Override
    public boolean onItemDrop(Player player, ItemEntity item){
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
            boolean deny = (zone.getCreative() && player.getMode());
            rhand.log(RLevel.DEBUGINFO, "RealmsListener.onItemDrop: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' Drop Result: "+(deny ? "'Denied'" : "'Allowed'"));
            return deny;
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onItemDrop... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onItemDrop: ", e);
        }
        return false;
    }
  
    @Override
    public boolean onItemPickUp(Player player, ItemEntity item){
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
            boolean deny = (zone.getCreative() && player.getMode());
            rhand.log(RLevel.DEBUGINFO, "RealmsListener.onItemPickUp: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' Drop Result: "+(deny ? "'Denied'" : "'Allowed'"));
            return deny;
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onItemPickUp... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onItemPickUp: ", e);
        }
        return false;
    }


    @Override
    public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
        boolean deny = false;
        try{
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = null;
            Zone zone = null;
            if (item != null) {
                if (item.itemType != null){
                    switch (item.itemType){
                    case Cauldron:
                    case IronDoor:
                    case LavaBucket:
                    case Painting:
                    case RedStone:
                    case WaterBucket:
                    case WoodDoor:
                    case BrewingStand:
                    case RedstoneRepeater:
                        if (blockPlaced != null) {
                            cBlock = new CModBlock(blockPlaced);
                        } 
                        else if (blockClicked != null) { 
                            cBlock = new CModBlock(blockClicked);
                        }
                        else{
                            break;
                        }
                        zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                        deny = !zone.permissionCheck(cPlayer, Permission.PermType.CREATE);
                        zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                        rhand.log(RLevel.DEBUGINFO, "RealmsListener.onItemUse: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' PermType: 'CREATE' Result: "+(deny ? "'Denied'" : "'Allowed'"));
                        break;
                    case Bucket:
                    case WoodHoe:
                    case StoneHoe:
                    case IronHoe:
                    case DiamondHoe:
                    case GoldHoe:
                    case GlassBottle:
                        if (blockPlaced != null) {
                            cBlock = new CModBlock(blockPlaced);
                        } 
                        else if (blockClicked != null) { 
                            cBlock = new CModBlock(blockClicked);
                        }
                        else{
                            break;
                        }
                        zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                        deny = !zone.permissionCheck(cPlayer, Permission.PermType.DESTROY);
                        rhand.log(RLevel.DEBUGINFO, "RealmsListener.onItemUse: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' PermType: 'DESTROY' Result: "+(deny ? "'Denied'" : "'Allowed'"));
                        break;
                    case Minecart:
                    case StorageMinecart:
                    case PoweredMinecart:
                    case SnowBall:
                    case Boat:
                    case Egg:
                    case EnderPearl:
                    case EyeofEnder:
                    case SpawnEgg:
                    case InkSack:
                    case GreenRecord:
                    case GoldRecord:
                    case MellohiRecord:
                    case BlocksRecord:
                    case ChirpRecord:
                    case FarRecord:
                    case MallRecord:
                    case StalRecord:
                    case StradRecord:
                    case WardRecord:
                    case ElevenRecord:
                        if (blockPlaced != null) {
                            cBlock = new CModBlock(blockPlaced);
                        } 
                        else if (blockClicked != null) { 
                            cBlock = new CModBlock(blockClicked);
                        }
                        else{
                            break;
                        }
                        zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                        deny = !zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
                        rhand.log(RLevel.DEBUGINFO, "RealmsListener.onItemUse: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' PermType: 'INTERACT' Result: "+(deny ? "'Denied'" : "'Allowed'"));
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onItemUse... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onItemUse: ", e);
        }
        return deny;
    }

    @Override
    public boolean onMobSpawn(Mob mob) {
        try{
            CModMob cMob = new CModMob(mob);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cMob), cMob);
            boolean deny = false;
            if (mob.getName().equals("Creeper") && !RealmsProps.getAC() && !zone.getCreeper()) {
                deny = true;
            }
            else if (mob.getName().equals("Ghast") && !RealmsProps.getAG() && !zone.getGhast()){
                deny = true;
            }
            else if (mob.isMob() && zone.getSanctuary() && !RealmsProps.getSM()){
                deny = true;
            }
            else if (mob.isAnimal() && !zone.getAnimals()){
                deny = true;
            }
            //rhand.log(RLevel.DEBUGINFO, "RealmsListener.onMobSpawn: Mob: '"+mob.getName()+"' Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
            return deny;
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onMobSpawn... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onMobSpawn: ", e);
        }
        return false;
    }
  
    public boolean onMobTarget(Player player, LivingEntity mob){
        try{
            if(mob.isMob()){
                CModPlayer cPlayer = new CModPlayer(player);
                Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
                boolean deny = zone.getSanctuary();
                rhand.log(RLevel.DEBUGINFO, "RealmsListener.onMobTarget: Mob: '"+mob.getName()+"' Zone: '"+zone.getName()+"' Result: "+(deny ? "'Denied'" : "'Allowed'"));
                return deny;
            }
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onMobTarget... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onMobTarget: ", e);
        }
        return false;
    }

    @Override
    public boolean onPistonExtend(Block block, boolean sticky){
        try{
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            boolean allow = pistonallowedcheck(block);
            rhand.log(RLevel.DEBUGINFO, "RealmsListener.onPistonExtend: Zone: '"+zone.getName()+"' Result: "+(allow ? "'Allowed'" : "'Denied'"));
            return !allow;
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onPistonExtend... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onPistonExtend: ", e);
        }
        return false;
    }
  
    @Override
    public boolean onPistonRetract(Block block, boolean sticky){
        try{
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
            boolean allow = pistonallowedcheck(block);
            rhand.log(RLevel.DEBUGINFO, "RealmsListener.onPistonRetract: Zone: '"+zone.getName()+"' Result: "+(allow ? "'Allowed'" : "'Denied'"));
            return !allow;
        }
        catch(Exception e){
            rhand.log(Level.SEVERE, "[Realms] An unhandled exception occured @ onPistonRetract... (enable debuging for stacktraces)");
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception has occured @ onPistonRetract: ", e);
        }
        return false;
    }

    @Override
    public void onPlayerMove(Player player, Location from, Location to) {
        CModPlayer cPlayer = new CModPlayer(player);
        CModBlock cBlock = new CModBlock(player.getWorld().getBlockAt((int) Math.floor(to.x), (int) Math.floor(to.y), (int) Math.floor(to.z)));
        Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
  
        //Start Enter Zone Checks
        if(!zone.permissionCheck(cPlayer, Permission.PermType.ENTER)) {
            player.notify("You do not have permission to enter that zone!");
            player.teleportTo(from);
            return;
        }
        //End Enter Zone Checks
        //Start Creative Zone Checks
        if (zone.getCreative()){
            if ((player.getCreativeMode() != 1) && (!player.canUseCommand("/mode") && (!Creative.contains(player)))){
                player.setCreativeMode(1);
                // if(!realm.invlist.hasInv(player.getName())){
                //     Item[] contents = player.getInventory().getContents();
                //     RItem[] ritems = convertItem(contents);
                //     realm.invlist.addInv(player.getName(), ritems);
                //      player.getInventory().clearContents();
                //      player.getInventory().update();
                // }
            }
        }
        else if((player.getCreativeMode() != 0) && (!player.canUseCommand("/mode") && (Creative.contains(player)))){
            player.setCreativeMode(0);
            //if(realm.invlist.hasInv(player.getName())){
            //    Creative.remove(player);
            //    player.getInventory().clearContents();
            //    RItem[] ritems = realm.invlist.getInv(player.getName());
            //    Item[] items = convertRItem(ritems);
            //    player.getInventory().setContents(items);
            //    player.getInventory().update();
            //}
        }
        //End Creative Zone Checks
        //Start Healing Zone Checks
        if(zone.getHealing()){
            ZoneLists.addInHealing(cPlayer);
        }
        else{
            ZoneLists.removeInHealing(cPlayer);
        }
        //End Healing Zone Checks
        //Start Restricted Zone Checks
        if(zone.getRestricted()){
            if(!zone.permissionCheck(cPlayer, Permission.PermType.AUTHED)){
                if(!ZoneLists.isInRestricted(cPlayer) && !player.getMode() && !player.isDamageDisabled()){
                    ZoneLists.addInRestricted(cPlayer);
                    player.notify("WARNING: YOU HAVE ENTERED A RESTRICTED ZONE!");
                }
            }
        }
        else if (ZoneLists.isInRestricted(cPlayer)){
            ZoneLists.removeInRestricted(cPlayer);
        }
        //End Restricted Zone Checks
      
        //Check if player should receive Welcome/Farewell Messages
        rhand.playerMessage(cPlayer);
    }

    @Override
    public boolean onPortalUse(Player player, World from){
        CModPlayer cPlayer = new CModPlayer(player);
        Zone zone = ZoneLists.getZone(rhand.getEverywhere(cPlayer), cPlayer);
        boolean allow = zone.permissionCheck(cPlayer, Permission.PermType.TELEPORT);
        rhand.log(RLevel.DEBUGINFO, "RealmsListener.onPortalUse: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' Result: "+(allow ? "'Allowed'" : "'Denied'"));
        return !allow;
    }
  
//  public PotionEffect onPotionEffect(LivingEntity entity, PotionEffect potionEffect){
//      boolean allowed = true;
//      if(entity.isPlayer()){
//          Player player = entity.getPlayer();
//          RLocation loc = new RLocation(player.getLocation().dimension, (int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
//          Zone zone = realm.zlists.getZone(realm.everywhere, loc);
//          allowed = zone.getPotion();
//          realm.logging.DebugI("RealmsListener.onPotionEffect: Player: '"+player.getName()+"' Zone: '"+zone.getName()+"' Result: "+(allowed ? "'Allowed'" : "'Denied'"));
//      }
//      return allowed ? potionEffect : null;
//  }

    private boolean explodeScan(Block block, String tocheck){
        boolean damagePlayers = false, toRet = false, tnt = false;
        for(int x = block.getX()-5; x < block.getX()+6; x++){
            for(int y = block.getY()-5; y < block.getY()+6; y++){
                for(int z = block.getZ()-5; z < block.getZ()+6; z++){
                    CModBlock cBlock = new CModBlock(block.getWorld().getBlockAt(x, y, z));
                    Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                    if(tocheck.equals("Creeper")){
                        if(!zone.getCreeper()){
                            if(RealmsProps.getPED() && !zone.getSanctuary()){
                                damagePlayers = true;
                            }
                            toRet = true;
                        }
                    }
                    else if(tocheck.equals("Ghast") ){
                        if(!zone.getGhast()){
                            if(RealmsProps.getPED() && !zone.getSanctuary()){
                                damagePlayers = true;
                            }
                            toRet = true;
                        }
                    }
                    else if(tocheck.equals("TNT")){
                        if(!zone.getTNT()){
                            if(RealmsProps.getPED() && !zone.getSanctuary()){
                                damagePlayers = true;
                            }
                            if(RealmsProps.getTNTtoTNT()){
                                tnt = true;
                            }
                            toRet = true;
                        }
                    }
                }
            }
        }
        if(damagePlayers){
            CModBlock cBlocked = new CModBlock(block);
            new RealmsPlayerExplosionDamage(rhand, cBlocked).start();
        }
        if(tnt){
            tntTOtnt(block);
        }
        return toRet;
    }
      
    private void tntTOtnt(Block block){
        try{
            for(int x = block.getX()-3; x < block.getX()+3; x++){
                for(int y = block.getY()-3; y < block.getY()+3; y++){
                    for(int z = block.getZ()-3; z < block.getZ()+3; z++){
                        Block check = block.getWorld().getBlockAt(x, y, z);
                        if(check.getType() == 46){
                            OEntityTNTPrimed tntp = new OEntityTNTPrimed(block.getWorld().getWorld(), check.getX(), check.getY(), check.getZ());
                            check.setType(0);
                            check.update();
                            tntp.a = (10);
                            block.getWorld().getWorld().b(tntp);
                        }
                    }
                }
            }
        }
        catch(Exception e){
            //DERP! (Notchian probably changed)
            rhand.log(RLevel.DEBUGSEVERE, "An unhandled exception occurred @ tntTOtnt", e);
        }
    }
      
    private boolean pistonallowedcheck(Block block){
        boolean allow = true;
        for(int x = block.getX()-2; x < block.getX()+3; x++){
            for(int y = block.getY()-2; y < block.getY()+3; y++){
                for(int z = block.getZ()-2; z < block.getZ()+3; z++){
                    CModBlock cBlock = new CModBlock(block.getWorld().getBlockAt(x, y, z));
                    Zone zone = ZoneLists.getZone(rhand.getEverywhere(cBlock), cBlock);
                    if(!zone.getPistons()){
                        allow = false;
                    }
                }
            }
        }
        return allow;
    }
    
//    private RItem[] convertItem(Item[] items){
//        RItem[] ritems = new RItem[40];
//        int i = 0;
//        for(Item item : items){
//            RItem rit = null;
//            if(item != null){
//                rit = new RItem(item.getItemId(), item.getAmount(), item.getSlot(), item.getDamage());
//            }
//            ritems[i] = rit;
//            i++;
//       }
//        return ritems;
//    }
      
//    private Item[] convertRItem(RItem[] ritems){
//        Item[] items = new Item[40];
//        int i = 0;
//        for(RItem ritem : ritems){
//            Item it = null;
//            if(ritem != null){
//                it = new Item(ritem.ID, ritem.amount, ritem.slot, ritem.damage);
//            }
//            items[i] = it;
//            i++;
//       }
//      return items;
//    }
}
