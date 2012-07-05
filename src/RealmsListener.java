import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.io.threads.ExplosionDamagePlayer;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;

/**
 * Realms listener class
 * <p>
 * This file is part of Realms
 * 
 * @author Jason Jones
 */
public class RealmsListener extends PluginListener {
    private RHandle rhandle;

    public RealmsListener(RHandle rhandle) {
        this.rhandle = rhandle;
    }

    private ArrayList<Player> Creative = new ArrayList<Player>();

    @Override
    public final PluginLoader.HookResult canPlayerUseCommand(Player player, String command) {
        if (!rhandle.getLoaded()) {
            return PluginLoader.HookResult.DEFAULT_ACTION;
        }
        boolean allow = true;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
            String[] cmd = null;
            if (command != null) {
                cmd = command.split(" ");
            }
            if (cmd != null) {
                if (!(cmd[0].equalsIgnoreCase("/realms") || cmd[0].equalsIgnoreCase("/wand") || RealmsProps.isCO(cmd[0]))) {
                    if (!zone.permissionCheck(cPlayer, Permission.PermType.COMMAND)) {
                        player.notify("You are not allowed to execute commands in this area!");
                        allow = false;
                    }
                }
            }
            rhandle.log(RLevel.COMMAND_CHECK, "Player: '" + player.getName() + "' Command: '" + (cmd != null ? cmd[0] : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ COMMAND_CHECK... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return allow ? PluginLoader.HookResult.DEFAULT_ACTION : PluginLoader.HookResult.PREVENT_ACTION;
    }

    @Override
    public final boolean onBlockBreak(Player player, Block block) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cBlock);

            if (block != null) {
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.DESTROY);
            }
            rhandle.log(RLevel.BLOCK_BREAK, "Player: '" + player.getName() + "' Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "[Realms] An unexpected exception occured @ BLOCK_BREAK... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onBlockDestroy(Player player, Block block) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cBlock);
            if (block != null) {
                if (RealmsProps.isOpBlock(block.getType())) {
                    deny = !zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
                }
            }
            rhandle.log(RLevel.BLOCK_DESTROY, "Player: '" + player.getName() + "' Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "[Realms] An unexpected exception occured @ BLOCK_DESTORY... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onBlockPhysics(Block block, boolean placed) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            if (block.getType() == 12 || block.getType() == 13) {
                deny = !zone.getPhysics();
                rhandle.log(RLevel.BLOCK_PHYSICS, "Block: '" + block.toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ BLOCK_PHYSICS... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock;
            Zone zone;
            if (blockPlaced != null) {
                cBlock = new CModBlock(blockPlaced);
                zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cBlock);
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.CREATE);
            }
            else {
                cBlock = new CModBlock(blockClicked);
                zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cBlock);
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.CREATE);
            }
            rhandle.log(RLevel.BLOCK_PLACE, "Player: '" + player.getName() + "'" +
                    " BlockPlaced: '" + (blockPlaced != null ? blockPlaced.toString() : "NULL") + "'" +
                    " BlockClicked: '" + (blockClicked != null ? blockClicked.toString() : "NULL") + "'" +
                    " ItemInHand: '" + (itemInHand != null ? itemInHand.toString() : "NULL") + "'" +
                    " Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ BLOCK_PLACE... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onBlockRightClick(Player player, Block blockClicked, Item itemInHand) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false, isOpBlock = RealmsProps.isOpBlock(blockClicked.getType());
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = new CModBlock(blockClicked);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            if (itemInHand.getItemId() == RealmsProps.getWandType()) {
                return rhandle.getPlayerWand(cPlayer).wandClick(cPlayer, cBlock);
            }
            else if (isOpBlock) {
                deny = !zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
            }
            rhandle.log(RLevel.BLOCK_RIGHTCLICK, "Player: '" + player.getName() + "'" +
                    " BlockClicked: '" + (blockClicked != null ? blockClicked.toString() : "NULL") + "'" +
                    " ItemInHand: '" + (itemInHand != null ? itemInHand.toString() : "NULL") + "'" +
                    " Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ BLOCK_RIGHTCLICKED... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onCommand(Player player, String[] args) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean toRet = false, deny = false;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
            if (args[0].toLowerCase().startsWith("/realms")) {
                toRet = rhandle.executeCommand(args, cPlayer);
            }
            else if (args[0].equalsIgnoreCase("/wand") && player.canUseCommand("/wand")) {
                toRet = rhandle.getPlayerWand(cPlayer).wandCommand(cPlayer, args);
            }
            else if (args[0].equals("/mode") && args.length > 2) {
                if (args[1].equals("0")) {
                    Player p = etc.getServer().matchPlayer(args[2]);
                    if (p != null && Creative.contains(p)) {
                        Creative.remove(p);
                    }
                }
                else if (args[1].equals("1")) {
                    Player p = etc.getServer().matchPlayer(args[2]);
                    if (p != null && !Creative.contains(p)) {
                        Creative.add(p);
                    }
                }
            }
            else if (args[0].equals("/warp")) { //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                if (args.length == 2) {
                    Warp warp = etc.getDataSource().getWarp(args[1]);
                    if (warp != null) {
                        CModBlock cBlock = new CModBlock(warp.Location.getWorld().getBlockAt((int) Math.floor(warp.Location.x), (int) Math.floor(warp.Location.y), (int) Math.floor(warp.Location.z)));
                        zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);

                        if (!zone.permissionCheck(cPlayer, Permission.PermType.ENTER)) {
                            player.notify("You do not have permission to enter that zone!");
                            toRet = true;
                            deny = true;
                        }
                        else if (!zone.permissionCheck(cPlayer, Permission.PermType.TELEPORT)) {
                            player.notify("You do not have permission to teleport into that zone!");
                            toRet = true;
                            deny = true;
                        }
                    }
                }
                else if (args.length > 2) { //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                    Player telep = etc.getServer().matchPlayer(args[2]);
                    if (telep != null) {
                        Warp warp = etc.getDataSource().getWarp(args[1]);
                        if (warp != null) {
                            CModPlayer toTele = new CModPlayer(telep);
                            CModBlock cBlock = new CModBlock(warp.Location.getWorld().getBlockAt((int) Math.floor(warp.Location.x), (int) Math.floor(warp.Location.y), (int) Math.floor(warp.Location.z)));
                            zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                            if (!zone.permissionCheck(toTele, Permission.PermType.ENTER)) {
                                player.notify(telep.getName() + " does not have permission to enter that zone!");
                                toRet = true;
                                deny = true;
                            }
                            else if (!zone.permissionCheck(toTele, Permission.PermType.TELEPORT)) {
                                player.notify(telep.getName() + " does not have permission to teleport into that zone!");
                                toRet = true;
                                deny = true;
                            }
                        }
                    }
                }
            }
            else if (args[0].equals("/tp")) { //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                if (args.length > 2) {
                    Player pto = etc.getServer().matchPlayer(args[1]);
                    if (pto != null) {
                        CModBlock cBlock = new CModBlock(pto.getWorld().getBlockAt((int) Math.floor(pto.getX()), (int) Math.floor(pto.getY()), (int) Math.floor(pto.getZ())));
                        zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                        if (!zone.permissionCheck(cPlayer, Permission.PermType.ENTER)) {
                            player.notify("You do not have permission to enter that zone!");
                            toRet = true;
                            deny = true;
                        }
                        else if (!zone.permissionCheck(cPlayer, Permission.PermType.TELEPORT)) {
                            player.notify("You do not have permission to teleport into that zone!");
                            toRet = true;
                            deny = true;
                        }
                    }
                }
            }
            else if (args[0].equals("/tphere")) { //Fix for onTeleport being derp -- Let CanaryMod Handle it if permchecks are passed
                if (args.length > 2) {
                    Player pto = etc.getServer().matchPlayer(args[1]);
                    if (pto != null) {
                        CModPlayer toTele = new CModPlayer(pto);
                        zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
                        if (!zone.permissionCheck(toTele, Permission.PermType.ENTER)) {
                            player.notify(pto.getName() + " does not have permission to enter this zone!");
                            toRet = true;
                            deny = true;
                        }
                        else if (!zone.permissionCheck(toTele, Permission.PermType.TELEPORT)) {
                            player.notify(pto.getName() + " does not have permission to teleport into that zone!");
                            toRet = true;
                            deny = true;
                        }
                    }
                }
            }
            String cmd = etc.combineSplit(0, args, " ");
            rhandle.log(RLevel.COMMAND, "Player: '" + player.getName() + "' Command: '" + cmd + "' Zone: '" + (zone != null ? zone.getName() : "NULL") + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ COMMAND... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return toRet;
    }

    @Override
    public final boolean onConsoleCommand(String[] cmd) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            if (cmd[0].equalsIgnoreCase("realms")) {
                return rhandle.executeConsoleCommand(cmd);
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ CONSOLE_COMMAND... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (defender.isPlayer()) {
                CModPlayer cPlayer = new CModPlayer(defender.getPlayer());
                Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
                if (attacker != null && type.equals(PluginLoader.DamageType.ENTITY)) {
                    if (attacker.isPlayer()) {
                        deny = !zone.getPVP();
                        rhandle.log(RLevel.DAMAGE, "'" + attacker.getName() + "' tried to attack '" + defender.getName() + "' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
                    }
                    else if (attacker.isMob()) {
                        deny = zone.getSanctuary();
                        rhandle.log(RLevel.DAMAGE, "'" + attacker.getName() + "' tried to attack '" + defender.getName() + "' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
                        return deny;
                    }
                }
                else if (type.equals(PluginLoader.DamageType.FALL)) {
                    deny = !zone.getFall();
                    rhandle.log(RLevel.DAMAGE, "'" + defender.getName() + "' attempted to take 'Fall Damage' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.SUFFOCATION) || type.equals(PluginLoader.DamageType.WATER)) {
                    deny = !zone.getSuffocate();
                    rhandle.log(RLevel.DAMAGE, "'" + defender.getName() + "' attempted to take 'Suffocation Damage' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.FIRE) || type.equals(PluginLoader.DamageType.LAVA) || type.equals(PluginLoader.DamageType.FIRE_TICK)) {
                    deny = !zone.getFire();
                    rhandle.log(RLevel.DAMAGE, "'" + defender.getName() + "' attempted to take 'Fire Damage' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.POTION)) {
                    deny = !zone.getPotion();
                    rhandle.log(RLevel.DAMAGE, "'" + defender.getName() + "' attempted to take 'Potion Damage' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.EXPLOSION)) {
                    if (RealmsProps.getPED()) {
                        if (zone.getSanctuary()) {
                            deny = true;
                        }
                    }
                    else if (zone.getSanctuary()) {
                        deny = true;
                    }
                    rhandle.log(RLevel.DAMAGE, "'" + defender.getName() + "' attempted to take 'Explosion Damage' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
                else if (type.equals(PluginLoader.DamageType.CREEPER_EXPLOSION)) {
                    if (!zone.getSanctuary()) {
                        if (!RealmsProps.getPED() && !zone.getCreeper()) {
                            deny = true;
                        }
                    }
                    else {
                        deny = true;
                    }
                    rhandle.log(RLevel.DAMAGE, "'" + defender.getName() + "' attempted to take 'Creeper_Explosion Damage' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                    return deny;
                }
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ DAMAGE... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final void onDisconnect(Player player) {
        if (!rhandle.getLoaded()) {
            return;
        }
        try {
            CModPlayer cPlayer = new CModPlayer(player);

            //Restore inventory if exists
            rhandle.handleInventory(cPlayer, false);

            //Reset the player's wand onDisconnect
            rhandle.removePlayerWand(cPlayer);

        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ DISCONNECT... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
    }

    @Override
    public final boolean onEat(Player player, Item item) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean allow = true;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
            allow = zone.permissionCheck(cPlayer, Permission.PermType.EAT);
            rhandle.log(RLevel.EAT, "'" + player.getName() + "' attempted to 'EAT' in Zone: '" + zone.getName() + "' Result: '" + (allow ? "Allowed'" : "Denied'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ EAT... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return !allow;
    }

    @Override
    public final boolean onEndermanDrop(Enderman entity, Block block) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModBlock cBlock;
            if (block != null) {
                cBlock = new CModBlock(block);
            }
            else {
                cBlock = new CModBlock(entity.getWorld().getBlockAt((int) Math.floor(entity.getX()), (int) Math.floor(entity.getY()), (int) Math.floor(entity.getZ())));
            }
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            deny = !zone.getEnderman();
            rhandle.log(RLevel.ENDERMAN_DROP, "'Enderman' attempted to place Block: '" + (block != null ? block.toString() : "NULL") + "' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ ENDERMAN_DROP... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onEndermanPickup(Enderman entity, Block block) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModBlock cBlock;
            if (block != null) {
                cBlock = new CModBlock(block);
            }
            else {
                cBlock = new CModBlock(entity.getWorld().getBlockAt((int) Math.floor(entity.getX()), (int) Math.floor(entity.getY()), (int) Math.floor(entity.getZ())));
            }
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            deny = !zone.getEnderman();
            rhandle.log(RLevel.ENDERMAN_PICKUP, "'Enderman' attempted to pickup Block: '" + (block != null ? block.toString() : "NULL") + "' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ ENDERMAN_PICKUP... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final PluginLoader.HookResult onEntityRightClick(Player player, BaseEntity entityClicked, Item itemInHand) {
        if (!rhandle.getLoaded()) {
            return PluginLoader.HookResult.DEFAULT_ACTION;
        }
        boolean allow = true;
        try {
            if (entityClicked != null) {
                CModPlayer cPlayer = new CModPlayer(player);
                Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
                allow = zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
                rhandle.log(RLevel.ENTITY_RIGHTCLICK, "'" + player.getName() + "' attempted to RightClick Entity: '" + entityClicked.getName() + "' in Zone: '" + zone.getName() + "' Result: " + (allow ? "Allowed" : "Denied"));
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ ENTITY_RIGHTCLICK... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return allow ? PluginLoader.HookResult.DEFAULT_ACTION : PluginLoader.HookResult.PREVENT_ACTION;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final boolean onExplosion(Block block, BaseEntity entity, List blocksaffected) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            if (entity == null) {
                return explodeScan(block, "TNT");
            }
            else if (entity.getName().equals("Creeper")) {
                return explodeScan(block, "Creeper");
            }
            else if (entity.getName().equals("Ghast")) {
                return explodeScan(block, "Ghast");
            }
            else {
                return explodeScan(block, "Creeper"); //Making all other explosions Creeper
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ EXPLOSION... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onFlow(Block blockFrom, Block blockTo) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModBlock cBlock1 = new CModBlock(blockFrom);
            CModBlock cBlock2 = new CModBlock(blockTo);
            Zone zone1 = ZoneLists.getZone(rhandle.getEverywhere(cBlock1), cBlock1);
            Zone zone2 = ZoneLists.getZone(rhandle.getEverywhere(cBlock2), cBlock2);
            if (!zone1.getFlow()) {
                deny = true;
            }
            else if (!zone2.getFlow()) {
                deny = true;
            }
            rhandle.log(RLevel.FLOW, "Zone1: " + zone1.getName() + " Result: '" + (!zone1.getFlow() ? "Denied'" : "Allowed' Zone2: " + zone2.getName() + " Result: '" + (zone2.getFlow() ? "Allowed'" : "Denied'")));
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ FLOW... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final Float onFoodExhaustionChange(Player player, Float oldLevel, Float newLevel) {
        if (!rhandle.getLoaded()) {
            return newLevel;
        }
        if (oldLevel > newLevel) {
            try {
                CModPlayer cPlayer = new CModPlayer(player);
                Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
                rhandle.log(RLevel.FOOD_EXHAUSTION, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Starve Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
                return zone.getStarve() ? newLevel : oldLevel;
            }
            catch (Exception e) {
                rhandle.log(Level.SEVERE, "An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE... (enable debuging for stacktraces)");
                rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
            }
        }
        return newLevel;
    }

    @Override
    public final boolean onIgnite(Block block, Player player) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            int bs = block.getStatus();
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            if (bs == 1 || bs == 3 || bs == 4 || bs == 5) {
                boolean allow = zone.getSpread();
                rhandle.log(RLevel.IGNITE, "Type: 'SPREAD' Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
                return !allow;
            }
            else if (bs == 2 || bs == 6) {
                CModPlayer cPlayer = new CModPlayer(player);
                boolean allow = zone.permissionCheck(cPlayer, Permission.PermType.IGNITE);
                rhandle.log(RLevel.IGNITE, "Type: 'IGNITE' Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
                return !allow;
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ IGNITE... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    public final boolean onInventoryOpen(HookParametersOpenInventory openinventory) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            Inventory inv = openinventory.getInventory();
            CModPlayer cmp = new CModPlayer(openinventory.getPlayer());
            CModBlock cmb = null;
            if (inv instanceof Chest) {
                cmb = new CModBlock(((Chest) inv).getBlock());
            }
            else if (inv instanceof DoubleChest) {
                cmb = new CModBlock(((DoubleChest) inv).getBlock());
            }
            if (cmb != null) {
                Zone pzone = ZoneLists.getZone(rhandle.getEverywhere(cmp), cmp);
                Zone bzone = ZoneLists.getZone(rhandle.getEverywhere(cmb), cmb);

                if (pzone.getCreative() && !bzone.getCreative() || !pzone.getCreative() && bzone.getCreative()) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ INVENTORY_OPEN... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onItemDrop(Player player, ItemEntity item) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
            boolean deny = zone.getCreative() && player.getMode();
            rhandle.log(RLevel.ITEM_DROP, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Drop Result: " + (deny ? "'Denied'" : "'Allowed'"));
            return deny;
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ ITEM_DROP... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onItemPickUp(Player player, ItemEntity item) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
            boolean deny = zone.getCreative() && player.getMode();
            rhandle.log(RLevel.ITEM_PICKUP, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Drop Result: " + (deny ? "'Denied'" : "'Allowed'"));
            return deny;
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ ITEM_PICKUP... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CModPlayer cPlayer = new CModPlayer(player);
            CModBlock cBlock = null;
            Zone zone = null;
            if (item != null) {
                if (item.itemType != null) {
                    switch (item.itemType) {
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
                            else {
                                break;
                            }
                            zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                            deny = !zone.permissionCheck(cPlayer, Permission.PermType.CREATE);
                            rhandle.log(RLevel.ITEM_USE, "RealmsListener.onItemUse: Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' PermType: 'CREATE' Result: " + (deny ? "'Denied'" : "'Allowed'"));
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
                            else {
                                break;
                            }
                            zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                            deny = !zone.permissionCheck(cPlayer, Permission.PermType.DESTROY);
                            rhandle.log(RLevel.ITEM_USE, "RealmsListener.onItemUse: Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' PermType: 'DESTROY' Result: " + (deny ? "'Denied'" : "'Allowed'"));
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
                            else {
                                break;
                            }
                            zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                            deny = !zone.permissionCheck(cPlayer, Permission.PermType.INTERACT);
                            rhandle.log(RLevel.ITEM_USE, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' PermType: 'INTERACT' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                            break;
                    }
                }
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ ITEM_USE... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return deny;
    }

    @Override
    public final boolean onMobSpawn(Mob mob) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            CModMob cMob = new CModMob(mob);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cMob), cMob);
            boolean deny = false;
            if (mob.getName().equals("Creeper") && !RealmsProps.getAC() && !zone.getCreeper()) {
                deny = true;
            }
            else if (mob.getName().equals("Ghast") && !RealmsProps.getAG() && !zone.getGhast()) {
                deny = true;
            }
            else if (mob.isMob() && zone.getSanctuary() && !RealmsProps.getSM()) {
                deny = true;
            }
            else if (mob.isAnimal() && !zone.getAnimals()) {
                deny = true;
            }
            rhandle.log(RLevel.MOB_SPAWN, "Mob: '" + mob.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            return deny;
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ MOB_SPAWN... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onMobTarget(Player player, LivingEntity mob) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            if (mob.isMob()) {
                CModPlayer cPlayer = new CModPlayer(player);
                Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
                boolean deny = zone.getSanctuary();
                rhandle.log(RLevel.MOB_TARGET, "Mob: '" + mob.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                return deny;
            }
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ MOB_TARGET... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onPistonExtend(Block block, boolean sticky) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            boolean allow = pistonallowedcheck(block);
            rhandle.log(RLevel.PISTON_EXTEND, "Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
            return !allow;
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ PISTON_EXTEND... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final boolean onPistonRetract(Block block, boolean sticky) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        try {
            CModBlock cBlock = new CModBlock(block);
            Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
            boolean allow = pistonallowedcheck(block);
            rhandle.log(RLevel.PISTON_RETRACT, "Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
            return !allow;
        }
        catch (Exception e) {
            rhandle.log(Level.SEVERE, "An unexpected exception occured @ PISTON_RETRACT... (enable debuging for stacktraces)");
            rhandle.log(RLevel.DEBUGSEVERE, "Debugging StackTrace: ", e);
        }
        return false;
    }

    @Override
    public final void onPlayerMove(Player player, Location from, Location to) {
        if (!rhandle.getLoaded()) {
            return;
        }
        CModPlayer cPlayer = new CModPlayer(player);
        CModBlock cBlock = new CModBlock(player.getWorld().getBlockAt((int) Math.floor(from.x), (int) Math.floor(from.y), (int) Math.floor(from.z)));
        Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);

        //Start Enter Zone Checks
        if (!zone.permissionCheck(cPlayer, Permission.PermType.ENTER)) {
            player.notify("You do not have permission to enter that zone!");
            player.teleportTo(from);
            return;
        }
        //End Enter Zone Checks
        //Start Creative Zone Checks
        if (zone.getCreative()) {
            if (player.getCreativeMode() != 1 && !player.canUseCommand("/mode") && !Creative.contains(player)) {
                player.setCreativeMode(1);
                rhandle.handleInventory(cPlayer, true);
            }
        }
        else if (player.getCreativeMode() != 0 && !player.canUseCommand("/mode") && !Creative.contains(player)) {
            player.setCreativeMode(0);
            rhandle.handleInventory(cPlayer, false);
        }
        //End Creative Zone Checks
        //Start Healing Zone Checks
        if (zone.getHealing()) {
            ZoneLists.addInHealing(cPlayer);
        }
        else {
            ZoneLists.removeInHealing(cPlayer);
        }
        //End Healing Zone Checks
        //Start Restricted Zone Checks
        if (zone.getRestricted()) {
            if (!zone.permissionCheck(cPlayer, Permission.PermType.AUTHED)) {
                if (!ZoneLists.isInRestricted(cPlayer) && !player.getMode() && !player.isDamageDisabled()) {
                    ZoneLists.addInRestricted(cPlayer);
                    player.notify(RealmsProps.getRestrictMess());
                }
            }
        }
        else if (ZoneLists.isInRestricted(cPlayer)) {
            ZoneLists.removeInRestricted(cPlayer);
        }
        //End Restricted Zone Checks

        //Check if player should receive Welcome/Farewell Messages
        rhandle.playerMessage(cPlayer);
    }

    @Override
    public final boolean onPortalUse(Player player, World from) {
        if (!rhandle.getLoaded()) {
            return false;
        }
        CModPlayer cPlayer = new CModPlayer(player);
        Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cPlayer), cPlayer);
        boolean allow = zone.permissionCheck(cPlayer, Permission.PermType.TELEPORT);
        rhandle.log(RLevel.PORTAL_USE, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
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

    private final boolean explodeScan(Block block, String tocheck) {
        boolean damagePlayers = false, toRet = false, tnt = false;
        for (int x = block.getX() - 5; x < block.getX() + 6; x++) {
            for (int y = block.getY() - 5; y < block.getY() + 6; y++) {
                for (int z = block.getZ() - 5; z < block.getZ() + 6; z++) {
                    CModBlock cBlock = new CModBlock(block.getWorld().getBlockAt(x, y, z));
                    Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                    if (tocheck.equals("Creeper")) {
                        if (!zone.getCreeper()) {
                            if (RealmsProps.getPED()) {
                                damagePlayers = true;
                            }
                            toRet = true;
                        }
                    }
                    else if (tocheck.equals("Ghast")) {
                        if (!zone.getGhast()) {
                            if (RealmsProps.getPED()) {
                                damagePlayers = true;
                            }
                            toRet = true;
                        }
                    }
                    else if (tocheck.equals("TNT")) {
                        if (!zone.getTNT()) {
                            if (RealmsProps.getPED()) {
                                damagePlayers = true;
                            }
                            if (RealmsProps.getTNTtoTNT()) {
                                tnt = true;
                            }
                            toRet = true;
                        }
                    }
                }
            }
        }
        if (damagePlayers) {
            CModBlock cBlocked = new CModBlock(block);
            rhandle.executeTask(new ExplosionDamagePlayer(rhandle, cBlocked));
        }
        if (tnt) {
            tntTOtnt(block);
        }
        return toRet;
    }

    private final void tntTOtnt(Block block) {
        try {
            for (int x = block.getX() - 3; x < block.getX() + 3; x++) {
                for (int y = block.getY() - 3; y < block.getY() + 3; y++) {
                    for (int z = block.getZ() - 3; z < block.getZ() + 3; z++) {
                        Block check = block.getWorld().getBlockAt(x, y, z);
                        if (check.getType() == 46) {
                            OEntityTNTPrimed tntp = new OEntityTNTPrimed(block.getWorld().getWorld(), check.getX(), check.getY(), check.getZ());
                            check.setType(0);
                            check.update();
                            tntp.a = 3;
                            block.getWorld().getWorld().b(tntp);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            //DERP! (Notchian probably changed)
            rhandle.log(RLevel.DEBUGSEVERE, "An unexpected exception occurred @ tntTOtnt (Outdated?)", e);
        }
    }

    private final boolean pistonallowedcheck(Block block) {
        boolean allow = true;
        for (int x = block.getX() - 2; x < block.getX() + 3; x++) {
            for (int y = block.getY() - 2; y < block.getY() + 3; y++) {
                for (int z = block.getZ() - 2; z < block.getZ() + 3; z++) {
                    CModBlock cBlock = new CModBlock(block.getWorld().getBlockAt(x, y, z));
                    Zone zone = ZoneLists.getZone(rhandle.getEverywhere(cBlock), cBlock);
                    if (!zone.getPistons()) {
                        allow = false;
                    }
                }
            }
        }
        return allow;
    }
}
