/* Copyright 2012 - 2013 Visual Illusions Entertainment.
 * This file is part of Realms.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * Source Code availible @ https://github.com/Visual-Illusions/Realms */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RLevel;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.Zone;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.ZoneLists;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.permission.PermissionType;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.polygon.Point;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Realms_CanaryClassicListener extends PluginListener{

    private final PluginLoader.HookResult ALLOW = PluginLoader.HookResult.DEFAULT_ACTION;
    private final PluginLoader.HookResult DENY = PluginLoader.HookResult.PREVENT_ACTION;
    private final ArrayList<Player> moded = new ArrayList<Player>();
    private final HashMap<Player, Minecart> cartInstance = new HashMap<Player, Minecart>(); // Cause FUCKING CANARY IS DERP IN THIS DEPT...!

    public Realms_CanaryClassicListener(Realms plugin){
        PluginListener.Priority HIGH = PluginListener.Priority.HIGH;
        PluginListener.Priority NORMAL = PluginListener.Priority.MEDIUM;
        PluginLoader loader = etc.getLoader();
        loader.addListener(PluginLoader.Hook.BLOCK_BROKEN, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.BLOCK_DESTROYED, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.BLOCK_PHYSICS, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.BLOCK_PLACE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.COMMAND, this, plugin, NORMAL);
        loader.addListener(PluginLoader.Hook.SERVERCOMMAND, this, plugin, NORMAL);
        loader.addListener(PluginLoader.Hook.DAMAGE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.DEATH, this, plugin, NORMAL);
        loader.addListener(PluginLoader.Hook.DISCONNECT, this, plugin, NORMAL);
        loader.addListener(PluginLoader.Hook.DISPENSE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.EAT, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.ENDERMAN_DROP, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.ENDERMAN_PICKUP, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.ENTITY_RIGHTCLICKED, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.EXPLOSION, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.FOODEXHAUSTION_CHANGE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.FOODLEVEL_CHANGE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.FOODSATURATION_CHANGE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.HANGING_ENTITY_DESTROYED, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.IGNITE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.ITEM_DROP, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.ITEM_USE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.MOB_SPAWN, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.MOB_TARGET, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.OPEN_INVENTORY, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.PISTON_EXTEND, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.PISTON_RETRACT, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.PLAYER_MOVE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.PORTAL_USE, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.POTION_EFFECT, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.VEHICLE_ENTERED, this, plugin, HIGH);
        loader.addListener(PluginLoader.Hook.VEHICLE_POSITIONCHANGE, this, plugin, HIGH);
    }

    @Override
    public final boolean onBlockBreak(Player player, Block block){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (block != null) {
                CanaryClassic_Block cBlock = new CanaryClassic_Block(block);
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(cBlock);
                deny = !zone.permissionCheck(user, PermissionType.DESTROY);
                RealmsLogMan.log(RLevel.BLOCK_BREAK, "Player: '" + player.getName() + "' Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_BREAK. Caused by: " + ex.getClass().getName());
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
        return deny;
    }

    @Override
    public final boolean onBlockDestroy(Player player, Block block){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (block != null) {
                CanaryClassic_Block cBlock = new CanaryClassic_Block(block);
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(cBlock);
                if (RealmsBase.getProperties().isInteractBlock(block.getType())) {
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                }
                RealmsLogMan.log(RLevel.BLOCK_DESTROY, "Player: '" + player.getName() + "' Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_DESTROY. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onBlockPhysics(Block block, boolean placed){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (block.blockType == Block.Type.Sand || block.blockType == Block.Type.Gravel) {
                CanaryClassic_Block cBlock = new CanaryClassic_Block(block);
                Zone zone = ZoneLists.getInZone(cBlock);
                deny = !zone.getPhysics();
                RealmsLogMan.log(RLevel.BLOCK_PHYSICS, "Block: '" + block.toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_PHYSICS. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onBlockPlace(Player player, Block blockP, Block blockC, Item item){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            Zone zone;
            CanaryClassic_Block cBlock;
            CanaryClassic_User user = new CanaryClassic_User(player);
            if (blockP != null) {
                cBlock = new CanaryClassic_Block(blockP);
                zone = ZoneLists.getInZone(cBlock);
                deny = !zone.permissionCheck(user, PermissionType.CREATE);
            }
            else {
                cBlock = new CanaryClassic_Block(blockC);
                zone = ZoneLists.getInZone(cBlock);
                deny = !zone.permissionCheck(user, PermissionType.CREATE);
            }
            RealmsLogMan.log(RLevel.BLOCK_PLACE, "Player: '" + player.getName() + "'" + " BlockPlaced: '" + (blockP != null ? blockP.toString() : "NULL") + "'" + " BlockClicked: '" + (blockC != null ? blockC.toString() : "NULL") + "'" + " ItemInHand: '" + (item != null ? item.toString() : "NULL") + "'" + " Zone: '" + zone.getName() + "' Result: "
                    + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_PLACE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onBlockRightClick(Player player, Block block, Item item){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (block != null) {
                CanaryClassic_Block cBlock = new CanaryClassic_Block(block);
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(cBlock);
                if (item.getItemId() == RealmsBase.getProperties().getIntVal("wand.type")) {
                    return RealmsBase.getPlayerWand(user).wandClick(cBlock);
                }
                else if (RealmsBase.getProperties().isInteractBlock(block.getType())) {
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                }
                else if (block.blockType == Block.Type.Chest || block.blockType == Block.Type.EnderChest || block.blockType == Block.Type.Furnace || block.blockType == Block.Type.Dispenser) {
                    Zone pZone = ZoneLists.getInZone(user);
                    if (!pZone.equals(zone) && (zone.getCreative() || pZone.getCreative())) {
                        if (!player.canUseCommandByDefault("/mode") && !moded.contains(player)) {
                            deny = true;
                        }
                    }
                }
                RealmsLogMan.log(RLevel.BLOCK_RIGHTCLICK, "Player: '" + player.getName() + "'" + " BlockClicked: '" + (block != null ? block.toString() : "NULL") + "'" + " ItemInHand: '" + (item != null ? item.toString() : "NULL") + "'" + " Zone: '" + (zone == null ? "Not Checked" : zone.getName()) + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_RIGHTCLICK. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final PluginLoader.HookResult canPlayerUseCommand(Player player, String command){
        if (!RealmsBase.isLoaded()) {
            return ALLOW;
        }
        boolean allow = player.canUseCommandByDefault(command);
        try {
            CanaryClassic_User user = new CanaryClassic_User(player);
            Zone zone = ZoneLists.getInZone(user);
            String[] cmd = null;
            if (command != null) {
                cmd = command.split(" ");
            }
            if (cmd != null) {
                if (!cmd[0].matches("/realms") || RealmsBase.getProperties().isCommandAllowed(cmd) && allow) {
                    if (!zone.permissionCheck(user, PermissionType.COMMAND)) {
                        player.notify("You are not allowed to execute commands in this area!");
                        allow = false;
                    }
                }
            }
            RealmsLogMan.log(RLevel.COMMAND_CHECK, "Player: '" + player.getName() + "' Command: '" + (command != null ? command : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ COMMAND_CHECK. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return allow ? ALLOW : DENY;
    }

    @Override
    public final boolean onCommand(Player player, String[] args){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean toRet = false;
        CanaryClassic_User user = new CanaryClassic_User(player);
        Zone zone = ZoneLists.getInZone(user);
        try {
            if (args[0].equals("/realms")) {
                RealmsCommandHandler.parseRealmsCommand(user, args.length > 1 ? args[1] : "INVALID", RealmsBase.commandAdjustment(args, 2));
                return true;
            }
            else if (!RealmsBase.getProperties().isCommandAllowed(args) && !zone.permissionCheck(user, PermissionType.COMMAND)) {
                player.notify("You are not allowed to execute commands in this area!");
                return true;
            }
            if (args[0].equals("/mode") && args.length > 2) {
                Player p = etc.getServer().matchPlayer(args[2]);
                CanaryClassic_User mUser = new CanaryClassic_User(p);
                if (args[1].equals("0")) {
                    if (p != null && moded.contains(p)) {
                        moded.remove(p);
                        RealmsBase.handleInventory(mUser, false);
                    }
                }
                else if (args[1].equals("1")) {
                    if (p != null && !moded.contains(p)) {
                        moded.add(p);
                        RealmsBase.handleInventory(mUser, false);
                    }
                }
                else if (args[1].equals("2")) {
                    if (p != null && !moded.contains(p)) {
                        moded.add(p);
                        RealmsBase.handleInventory(mUser, false);
                    }
                }
            }
            else if (args[0].equals("/warp")) { // Fix for onTeleport being derp -- Let Canary Handle it if permchecks are passed
                if (args.length == 2) {
                    Warp warp = etc.getDataSource().getWarp(args[1]);
                    if (warp != null) {
                        CanaryClassic_Block block = new CanaryClassic_Block(new Block(0, (int) Math.floor(warp.Location.x), (int) Math.floor(warp.Location.y), (int) Math.floor(warp.Location.z)));
                        zone = ZoneLists.getInZone(block);
                        if (!zone.permissionCheck(user, PermissionType.ENTER)) {
                            player.notify("You do not have permission to enter that zone!");
                            toRet = true;
                        }
                        else if (!zone.permissionCheck(user, PermissionType.TELEPORT)) {
                            player.notify("You do not have permission to teleport into that zone!");
                            toRet = true;
                        }
                    }
                }
                else if (args.length > 2) { // Fix for onTeleport being derp -- Let VIMod Handle it if permchecks are passed
                    Player telep = etc.getServer().matchPlayer(args[2]);
                    if (telep != null) {
                        CanaryClassic_User teleUser = new CanaryClassic_User(telep);
                        Warp warp = etc.getDataSource().getWarp(args[1]);
                        if (warp != null) {
                            CanaryClassic_Block block = new CanaryClassic_Block(new Block(0, (int) Math.floor(warp.Location.x), (int) Math.floor(warp.Location.y), (int) Math.floor(warp.Location.z)));
                            zone = ZoneLists.getInZone(block);
                            if (!zone.permissionCheck(teleUser, PermissionType.ENTER)) {
                                player.notify(telep.getName() + " does not have permission to enter that zone!");
                                toRet = true;
                            }
                            else if (!zone.permissionCheck(teleUser, PermissionType.TELEPORT)) {
                                player.notify(telep.getName() + " does not have permission to teleport into that zone!");
                                toRet = true;
                            }
                        }
                    }
                }
            }
            else if (args[0].equals("/tp")) { // Fix for onTeleport being derp -- Let Canary Handle it if permchecks are passed
                if (args.length > 2) {
                    Player pto = etc.getServer().matchPlayer(args[1]);
                    if (pto != null) {
                        CanaryClassic_User teleTo = new CanaryClassic_User(pto);
                        zone = ZoneLists.getInZone(teleTo);
                        if (!zone.permissionCheck(teleTo, PermissionType.ENTER)) {
                            player.notify("You do not have permission to enter that zone!");
                            toRet = true;
                        }
                        else if (!zone.permissionCheck(teleTo, PermissionType.TELEPORT)) {
                            player.notify("You do not have permission to teleport into that zone!");
                            toRet = true;
                        }
                    }
                }
            }
            else if (args[0].equals("/tphere")) { // Fix for onTeleport being derp -- Let Canary Handle it if permchecks are passed
                if (args.length > 2) {
                    Player pto = etc.getServer().matchPlayer(args[1]);
                    if (pto != null) {
                        CanaryClassic_User teleTo = new CanaryClassic_User(pto);
                        zone = ZoneLists.getInZone(teleTo);
                        if (!zone.permissionCheck(teleTo, PermissionType.ENTER)) {
                            player.notify(pto.getName() + " does not have permission to enter this zone!");
                            toRet = true;
                        }
                        else if (!zone.permissionCheck(teleTo, PermissionType.TELEPORT)) {
                            player.notify(pto.getName() + " does not have permission to teleport into that zone!");
                            toRet = true;
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ COMMAND. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return toRet;
    }

    @Override
    public final boolean onConsoleCommand(String[] args){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        try {
            if (args[0].equals("realms")) {
                RealmsCommandHandler.parseRealmsCommand(new CanaryClassic_Console(), args.length > 1 ? args[1] : "INVALID", RealmsBase.commandAdjustment(args, 2));
                return true;
            }
        }
        catch (Exception ex) {
            try {
                RealmsLogMan.severe("An unexpected exception occured @ CONSOLE_COMMAND. Caused by: " + ex.getClass().getName());
                RealmsLogMan.stacktrace(ex);
            }
            catch (Exception exc) {
                // FAIL...
            }
        }
        return false;
    }

    @Override
    public final HookParametersDamage onDamage(HookParametersDamage hpDamage){
        if (!RealmsBase.isLoaded()) {
            return hpDamage;
        }

        boolean deny = false;
        BaseEntity defender = hpDamage.getDefender();
        BaseEntity attacker = hpDamage.getAttacker();
        DamageType type = hpDamage.getDamageSource().getDamageType();

        try {
            if (defender.isPlayer()) {
                CanaryClassic_User user = new CanaryClassic_User(defender.getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                switch (type) {
                    case ENTITY:
                        if (attacker != null) {
                            if (attacker.isPlayer() || (attacker.getName().equals("Wolf") && (new Wolf((OEntityWolf) attacker.getEntity()).isTame()))) {
                                deny = (!zone.getPVP()) || zone.getSanctuary();
                            }
                        }
                        break;
                    case FALL:
                        deny = (!zone.getFall()) || zone.getSanctuary();
                        break;
                    case STARVATION:
                        deny = (!zone.getStarve()) || zone.getSanctuary();
                        break;
                    case SUFFOCATION:
                    case WATER:
                        deny = (!zone.getSuffocate()) || zone.getSanctuary();
                        break;
                    case FIRE:
                    case LAVA:
                    case FIRE_TICK:
                        deny = (!zone.getFire()) || zone.getSanctuary();
                        break;
                    case POTION:
                        deny = (!zone.getPotion()) || zone.getSanctuary();
                        break;
                    case EXPLOSION:
                    case CREEPER_EXPLOSION:
                        deny = (!RealmsBase.getProperties().getBooleanVal("player.explode") && !zone.getExplode()) || zone.getSanctuary();
                        break;
                    default:
                        deny = zone.getSanctuary();
                        break;
                }
                RealmsLogMan.log(RLevel.DAMAGE, "Player: " + defender.getPlayer().getName() + " Damage: " + type.name() + " Attacker: " + (attacker != null ? attacker.getName() : "No Attacker") + " Zone: '" + zone.getName() + "' Result: " + (deny ? "'No Damage'" : "'Damaged'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DAMAGE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hpDamage.setCanceled();
        }
        return hpDamage;
    }

    @Override
    public final void onDeath(LivingEntity entity){
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            if (entity.isPlayer()) {
                CanaryClassic_User user = new CanaryClassic_User(entity.getPlayer());
                RealmsBase.removePlayerWand(user);
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DEATH. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @Override
    public final void onDisconnect(Player player){
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            CanaryClassic_User user = new CanaryClassic_User(player);
            RealmsBase.removePlayerWand(user);
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DISCONNECT. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @Override
    public final boolean onDispense(Dispenser dispenser, BaseEntity tobedispensed){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Block cBlock = new CanaryClassic_Block(dispenser.getBlock());
            Zone zone = ZoneLists.getInZone(cBlock);
            deny = !zone.getDispensers();
            RealmsLogMan.log(RLevel.DISPENSE, "Block: '" + dispenser.toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DISPENSE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onEat(Player player, Item item){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_User user = new CanaryClassic_User(player);
            Zone zone = ZoneLists.getInZone(user);
            deny = !zone.permissionCheck(user, PermissionType.EAT);
            RealmsLogMan.log(RLevel.EAT, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ EAT. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onEndermanDrop(Enderman entity, Block block){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Entity cent = new CanaryClassic_Entity(entity);
            Zone zone = ZoneLists.getInZone(cent);
            deny = !zone.getEnderman();
            RealmsLogMan.log(RLevel.ENDERMAN, "'Enderman' attempted to place Block: '" + (block != null ? block.toString() : "NULL") + "' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ENDERMAN_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onEndermanPickup(Enderman entity, Block block){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Entity cent = new CanaryClassic_Entity(entity);
            Zone zone = ZoneLists.getInZone(cent);
            deny = !zone.getEnderman();
            RealmsLogMan.log(RLevel.ENDERMAN, "Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ENDERMAN_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final PluginLoader.HookResult onEntityRightClick(Player player, BaseEntity entityClicked, Item itemInHand){
        if (!RealmsBase.isLoaded()) {
            return ALLOW;
        }
        boolean deny = false;
        try {
            if (entityClicked != null) {
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(user);
                deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                RealmsLogMan.log(RLevel.ENTITY_RIGHTCLICK, "Player: '" + player.getName() + "' Entity: '" + entityClicked.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "Denied" : "Allow"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ENTITY_RIGHTCLICK. Cause by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny ? DENY : ALLOW;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public final boolean onExplosion(Block block, BaseEntity entity, List blocksaffected){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        try {
            CanaryClassic_Block cBlock;
            Iterator<Block> blown = ((List<Block>) blocksaffected).iterator();
            while (blown.hasNext()) {
                cBlock = new CanaryClassic_Block(blown.next());
                Zone zone = ZoneLists.getInZone(cBlock);
                if (!zone.getExplode()) {
                    blown.remove();
                }
            }
            RealmsLogMan.log(RLevel.EXPLOSION, "BaseBlock: " + block.toString() + " Entity: " + (entity != null ? entity.getName() : "TNT"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ EXPLOSION. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return false;
    }

    @Override
    public final boolean onFlow(Block from, Block to){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Block cFrom = new CanaryClassic_Block(from);
            CanaryClassic_Block cTo = new CanaryClassic_Block(to);
            Zone zone1 = ZoneLists.getInZone(cFrom);
            Zone zone2 = ZoneLists.getInZone(cTo);
            if (!zone1.getFlow()) {
                deny = true;
            }
            else if (!zone2.getFlow()) {
                deny = true;
            }
            RealmsLogMan.log(RLevel.FLOW, "Zone1: " + zone1.getName() + " Result: '" + (!zone1.getFlow() ? "Denied'" : "Allowed' Zone2: " + zone2.getName() + " Result: '" + (zone2.getFlow() ? "Allowed'" : "Denied'")));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ FLOW. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public Float onFoodExhaustionChange(Player player, Float oldLevel, Float newLevel){
        if (!RealmsBase.isLoaded()) {
            return newLevel;
        }
        Float result = newLevel;
        try {
            if (oldLevel.floatValue() > newLevel.floatValue()) {
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(user);
                if (!zone.getStarve()) {
                    result = oldLevel;
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return result;
    }

    @Override
    public int onFoodLevelChange(Player player, int oldFoodLevel, int newFoodLevel){
        if (!RealmsBase.isLoaded()) {
            return newFoodLevel;
        }
        int result = newFoodLevel;
        if (oldFoodLevel > newFoodLevel) {
            try {
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(user);
                if (!zone.getStarve()) {
                    result = oldFoodLevel;
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
            }
            catch (Exception ex) {
                RealmsLogMan.severe("An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE. Caused by: " + ex.getClass().getName());
                RealmsLogMan.stacktrace(ex);
            }
        }
        return result;
    }

    @Override
    public Float onFoodSaturationChange(Player player, Float oldLevel, Float newLevel){
        if (!RealmsBase.isLoaded()) {
            return newLevel;
        }
        Float result = newLevel;
        if (oldLevel.floatValue() > newLevel.floatValue()) {
            try {
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(user);
                if (!zone.getStarve()) {
                    result = oldLevel;
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
            }
            catch (Exception ex) {
                RealmsLogMan.severe("An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE. Caused by: " + ex.getClass().getName());
                RealmsLogMan.stacktrace(ex);
            }
        }
        return result;
    }

    @Override
    public final boolean onHangingEntityDestroyed(HangingEntity baseEntity, DamageSource source){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (source.getDamagingEntity() != null && source.getDamagingEntity().isPlayer()) {
                CanaryClassic_User user = new CanaryClassic_User(source.getDamagingEntity().getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                deny = !zone.permissionCheck(user, PermissionType.DESTROY);
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ HANGINGENTITY_DESTROY. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onIgnite(Block block, Player player){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (block != null) {
                CanaryClassic_Block cBlock = new CanaryClassic_Block(block);
                Zone zone = ZoneLists.getInZone(cBlock);
                int bs = block.getStatus();
                if (bs == 1 || bs == 3 || bs == 4 || bs == 5 || (bs == 6 && player == null)) {
                    deny = !zone.getBurn();
                    RealmsLogMan.log(RLevel.BURN, "Type: 'BURN' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                }
                else if (player != null && (bs == 2 || bs == 6)) {
                    deny = !zone.permissionCheck(new CanaryClassic_User(player), PermissionType.IGNITE) || !zone.getBurn();
                    // RealmsLogMan.log(RLevel.BURN, "Type: 'IGNITE' Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ IGNITE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onItemDrop(Player player, ItemEntity item){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_User user = new CanaryClassic_User(player);
            Zone zone = ZoneLists.getInZone(user);
            deny = (zone.getCreative() && (player.getCreativeMode() == 1));
            RealmsLogMan.log(RLevel.ITEM_DROP, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Drop Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ITEM_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (item != null) {
                if (RealmsBase.getProperties().isInteractItem(item.getItemId())) {
                    CanaryClassic_User user = new CanaryClassic_User(player);
                    Zone zone = null;
                    if (blockClicked != null) {
                        zone = ZoneLists.getInZone(new CanaryClassic_Block(blockClicked));
                    }
                    else {
                        zone = ZoneLists.getInZone(user);
                    }
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                    RealmsLogMan.log(RLevel.ITEM_USE, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' PermType: 'INTERACT' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ITEM_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onMobSpawn(Mob mob){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Entity entity = new CanaryClassic_Entity(mob);
            Zone zone = ZoneLists.getInZone(entity);
            if (mob.isMob() && zone.getSanctuary() && !RealmsBase.getProperties().getBooleanVal("sanctuary.mobs")) {
                deny = true;
            }
            else if (mob.isAnimal() && !zone.getAnimals()) {
                deny = true;
            }
            RealmsLogMan.log(RLevel.MOB_SPAWN, "Mob: '" + mob.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ MOB_SPAWN. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onMobTarget(Player player, LivingEntity entity){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (entity.isMob()) {
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(user);
                deny = zone.getSanctuary();
                RealmsLogMan.log(RLevel.MOB_TARGET, "Mob: '" + entity.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ MOB_TARGET. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onOpenInventory(HookParametersOpenInventory openInventory){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            if (openInventory.getInventory().getName().equals("container.minecart")) {
                Player player = openInventory.getPlayer();
                Minecart minecart = cartInstance.get(player);
                if (minecart != null) {
                    Zone zone = ZoneLists.getInZone(new CanaryClassic_User((Player) player));
                    Zone mZone = ZoneLists.getInZone(new CanaryClassic_Entity(minecart));
                    if (!zone.equals(mZone) && (zone.getCreative() || mZone.getCreative())) {
                        if (!player.canUseCommandByDefault("/mode") && !moded.contains(player)) {
                            deny = true;
                        }
                    }
                    cartInstance.remove(player);
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ OPEN_INVENTORY. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onPistonExtend(Block block, boolean isSticky){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Block piston = new CanaryClassic_Block(block);
            CanaryClassic_Block pushing = new CanaryClassic_Block(getPistonTouch(block));
            Zone piszone = ZoneLists.getInZone(piston);
            Zone pushzone = ZoneLists.getInZone(pushing);
            deny = !piszone.getPistons() || !pushzone.getPistons();
            RealmsLogMan.log(RLevel.PISTONS, "Zone: '" + piszone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PISTON_EXTEND. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final boolean onPistonRetract(Block block, boolean isSticky){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_Block piston = new CanaryClassic_Block(block);
            CanaryClassic_Block pushing = new CanaryClassic_Block(getPistonTouch(block));
            Zone piszone = ZoneLists.getInZone(piston);
            Zone pushzone = ZoneLists.getInZone(pushing);
            deny = !piszone.getPistons() || !pushzone.getPistons();
            RealmsLogMan.log(RLevel.PISTONS, "Zone: '" + piszone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PISTON_RETRACT. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final void onPlayerMove(Player player, Location from, Location to){
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            CanaryClassic_User user = new CanaryClassic_User(player);
            Zone zone = ZoneLists.getInZone(user);
            // Start Enter Zone Checks
            if (!zone.permissionCheck(user, PermissionType.ENTER)) {
                player.notify("You do not have permission to enter that zone!");
                Point thrown = RealmsBase.throwBack(zone, user.getLocationPoint());
                player.teleportTo(thrown.x + 0.5D, thrown.y + 0.5D, thrown.z + 0.5D, player.getRotation(), player.getPitch());
                return;
            }
            // End Enter Zone Checks
            // Start Creative/Adventure Zone Checks
            if (!player.canUseCommandByDefault("/mode") && !moded.contains(player)) {
                if (zone.getCreative()) {
                    if (player.getCreativeMode() != 1) {
                        RealmsBase.handleInventory(user, true);
                        player.setCreativeMode(1);
                    }
                    if (!ZoneLists.isInCreative(user)) {
                        ZoneLists.addInCreative(user);
                    }
                }
                else if (zone.getAdventure()) {
                    if (player.getCreativeMode() != 2) {
                        player.setCreativeMode(2);
                    }
                    if (!ZoneLists.isInAdventure(user)) {
                        ZoneLists.addInAdventure(user);
                    }
                }
                else if (player.getCreativeMode() != 0) {
                    if (player.getCreativeMode() == 1 && ZoneLists.isInCreative(user)) {
                        ZoneLists.removeInCreative(user);
                        RealmsBase.handleInventory(user, false);
                        player.setCreativeMode(0);
                    }
                    else if (player.getCreativeMode() == 2 && ZoneLists.isInAdventure(user)) {
                        ZoneLists.removeInAdventure(user);
                        player.setCreativeMode(0);
                    }
                }
            }
            // End Creative/Adventure Zone Checks
            // Start Healing Zone Checks
            if (zone.getHealing()) {
                ZoneLists.addInHealing(user);
            }
            else {
                ZoneLists.removeInHealing(user);
            }
            // End Healing Zone Checks
            // Start Restricted Zone Checks
            if (zone.getRestricted()) {
                if (!zone.permissionCheck(user, PermissionType.AUTHED)) {
                    if (!ZoneLists.isInRestricted(user)) {
                        ZoneLists.addInRestricted(user);
                        player.notify(RealmsBase.getProperties().getStringVal("restrict.message"));
                    }
                }
            }
            else if (ZoneLists.isInRestricted(user)) {
                ZoneLists.removeInRestricted(user);
            }
            // End Restricted Zone Checks
            // Check if player should receive Welcome/Farewell Messages
            RealmsBase.playerMessage(user);
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PLAYER_MOVE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @Override
    public final boolean onPortalUse(Player player, Location to){
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            CanaryClassic_User user = new CanaryClassic_User(player);
            CanaryClassic_Block block = new CanaryClassic_Block(to.getWorld().getBlockAt((int) to.x, (int) to.y, (int) to.z));
            Zone zFrom = ZoneLists.getInZone(user);
            Zone zTo = ZoneLists.getInZone(block);
            deny = !zFrom.permissionCheck(user, PermissionType.TELEPORT);
            if (deny) {
                player.notify("You do not have permission to teleport out of this zone!");
            }
            else {
                deny = !zTo.permissionCheck(user, PermissionType.TELEPORT);
                if (deny) {
                    player.notify("You do not have permission to teleport into that zone!");
                }
                else {
                    deny = !zTo.permissionCheck(user, PermissionType.ENTER);
                    if (deny) {
                        player.notify("You do not have permission to enter that zone!");
                    }
                }
            }
            RealmsLogMan.log(RLevel.PORTAL_USE, "Player: '" + player.getName() + "' Zone From: '" + zFrom.getName() + "' Zone To: '" + zTo.getName() + "' Result: " + (!deny ? "'Allowed'" : "'Denied'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PORTAL_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @Override
    public final PotionEffect onPotionEffect(LivingEntity entity, PotionEffect potionEffect){
        if (!RealmsBase.isLoaded()) {
            return potionEffect;
        }
        boolean deny = false;
        try {
            if (entity.isPlayer()) {
                CanaryClassic_User user = new CanaryClassic_User(entity.getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                deny = !zone.getPotion();
                RealmsLogMan.log(RLevel.POTION_EFFECT, "Player: '" + entity.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: " + (!deny ? "'Allowed'" : "'Denied'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PORTAL_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny ? null : potionEffect;
    }

    @Override
    public final void onVehicleEnter(BaseVehicle vehicle, HumanEntity humEnt){ // For somereason this is called when opening a StorageCart
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            if (vehicle.getName().equals("Minecart")) {
                Minecart minecart = new Minecart((OEntityMinecart) vehicle.getEntity());
                if (minecart.getType() == Minecart.Type.StorageCart) {
                    cartInstance.put(humEnt.getPlayer(), minecart);
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ VEHICLE_ENTER. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @Override
    public final void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z){
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            if (vehicle.getRiddenByEntity() != null && vehicle.getRiddenByEntity().isPlayer()) {
                Player player = vehicle.getRiddenByEntity().getPlayer();
                CanaryClassic_User user = new CanaryClassic_User(player);
                Zone zone = ZoneLists.getInZone(user);
                // Start Enter Zone Checks
                if (!zone.permissionCheck(user, PermissionType.ENTER)) {
                    player.dismount();
                    Point thrown = RealmsBase.throwBack(zone, user.getLocationPoint());
                    player.teleportTo(thrown.x, thrown.y, thrown.z, player.getRotation(), player.getPitch());
                    if (vehicle.getName().equals("Boat")) {
                        player.sendMessage("[\u00A77Clippy\u00A7F]\u00A7C Looks like you fell out of your boat! Need some help?");
                    }
                    else {
                        player.sendMessage("[\u00A77Clippy\u00A7F]\u00A7C Looks like you fell out of your minecart! Need some help?");
                    }
                    return;
                }
                // End Enter Zone Checks
                // Start Creative Zone Checks
                if (zone.getCreative()) {
                    if (player.getCreativeMode() != 1 && !player.canUseCommandByDefault("/mode") && !moded.contains(player) && !ZoneLists.isInCreative(user)) {
                        player.setCreativeMode(1);
                        RealmsBase.handleInventory(user, true);
                    }
                }
                else if (zone.getAdventure()) {
                    if (player.getCreativeMode() != 2 && !player.canUseCommandByDefault("/mode") && !moded.contains(player) && !ZoneLists.isInAdventure(user)) {
                        player.setCreativeMode(2);
                    }
                }
                else if (player.getCreativeMode() != 0 && !player.canUseCommandByDefault("/mode") && (ZoneLists.isInCreative(user) || ZoneLists.isInAdventure(user))) {
                    player.setCreativeMode(0);
                    ZoneLists.removeInCreative(user);
                    ZoneLists.removeInAdventure(user);
                    RealmsBase.handleInventory(user, false);
                }
                // End Creative Zone Checks
                // Start Healing Zone Checks
                if (zone.getHealing()) {
                    ZoneLists.addInHealing(user);
                }
                else {
                    ZoneLists.removeInHealing(user);
                }
                // End Healing Zone Checks
                // Start Restricted Zone Checks
                if (zone.getRestricted()) {
                    if (!zone.permissionCheck(user, PermissionType.AUTHED)) {
                        if (!ZoneLists.isInRestricted(user) && player.getCreativeMode() != 1 && !player.isDamageDisabled()) {
                            ZoneLists.addInRestricted(user);
                            player.notify(RealmsBase.getProperties().getStringVal("restrict.message"));
                        }
                    }
                }
                else if (ZoneLists.isInRestricted(user)) {
                    ZoneLists.removeInRestricted(user);
                }
                // End Restricted Zone Checks
                // Check if player should receive Welcome/Farewell Messages
                RealmsBase.playerMessage(user);
                RealmsLogMan.log(RLevel.VEHICLE_MOVE, "Player: '" + player.getName() + "' Zone: '" + zone.getName() + "'");
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ VEHICLE_MOVE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    private final Block getPistonTouch(Block piston){
        Block pushing = null;
        int x = piston.getX(), y = piston.getY(), z = piston.getZ();
        RealmsLogMan.log(RLevel.GENERAL, String.valueOf(piston.getData()));
        switch (piston.getData()) {
            case 0: // DOWN
            case 8:
                pushing = piston.getWorld().getBlockAt(x, (y - 1), z);
                break;
            case 1: // UP
            case 9:
                pushing = piston.getWorld().getBlockAt(x, (y + 1), z);
                break;
            case 2: // NORTH (-Z)
            case 10:
                pushing = piston.getWorld().getBlockAt(x, y, (z - 1));
                break;
            case 3: // SOUTH (+Z)
            case 11:
                pushing = piston.getWorld().getBlockAt(x, y, (z + 1));
                break;
            case 4: // WEST (-X)
            case 12:
                pushing = piston.getWorld().getBlockAt((x - 1), y, z);
                break;
            case 5: // EAST (+X)
            case 13:
                pushing = piston.getWorld().getBlockAt((x + 1), y, z);
                break;
            default:
                break;
        }
        return pushing;
    }
}
