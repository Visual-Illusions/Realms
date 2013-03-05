/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 */
package net.visualillusionsent.bukkitplugin.realms;

import java.util.ArrayList;
import java.util.Iterator;

import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RLevel;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.Zone;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.ZoneLists;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.permission.PermissionType;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.polygon.Point;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Realms_BukkitListener implements Listener{

    private final ArrayList<Player> moded = new ArrayList<Player>();

    public Realms_BukkitListener(Realms plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onBlockBreak(BlockBreakEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block block = new Bukkit_Block(event.getBlock());
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            Zone zone = ZoneLists.getInZone(block);
            deny = !zone.permissionCheck(user, PermissionType.DESTROY);
            RealmsLogMan.log(RLevel.BLOCK_BREAK, "Player: '" + event.getPlayer().getName() + "' Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_BREAK. Caused by: " + ex.getMessage());
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onBlockDestroy(BlockDamageEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block block = new Bukkit_Block(event.getBlock());
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            Zone zone = ZoneLists.getInZone(block);
            if(block != null){
                if(RealmsBase.getProperties().isInteractBlock(block.getType())){
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                }
            }
            RealmsLogMan.log(RLevel.BLOCK_DESTROY, "Player: '" + event.getPlayer().getName() + "' Block: '" + (block != null ? block.toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_DESTORY. Caused by: " + ex.getMessage());
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onBlockPhysics(BlockPhysicsEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(event.getChangedTypeId() == 12 || event.getChangedTypeId() == 13){
                Bukkit_Block block = new Bukkit_Block(event.getBlock());
                Zone zone = ZoneLists.getInZone(block);
                deny = !zone.getPhysics();
                RealmsLogMan.log(RLevel.BLOCK_PHYSICS, "Block: '" + block.toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                event.setCancelled(deny);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_PHYSICS. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onBlockPlace(BlockPlaceEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Block blockP = event.getBlockPlaced();
            Block blockC = event.getBlockAgainst();
            Zone zone;
            Bukkit_Block block;
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            if(event.getBlockPlaced() != null){
                block = new Bukkit_Block(blockP);
            }
            else{
                block = new Bukkit_Block(blockC);
            }
            zone = ZoneLists.getInZone(block);
            deny = !zone.permissionCheck(user, PermissionType.CREATE);
            RealmsLogMan.log(RLevel.BLOCK_PLACE, "Player: '" + event.getPlayer().getName() + "'" + " BlockPlaced: '" + (blockP != null ? blockP.toString() : "NULL") + "'" + " BlockClicked: '" + (blockC != null ? blockC.toString() : "NULL") + "'" + " ItemInHand: '" + (event.getItemInHand() != null ? event.getItemInHand().toString() : "NULL") + "'" + " Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_PLACE. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onBlockRightClick(PlayerInteractEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        try{
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                Bukkit_User user = new Bukkit_User(event.getPlayer());
                Bukkit_Block block = new Bukkit_Block(event.getClickedBlock());
                if(event.getItem() != null && event.getItem().getTypeId() == RealmsBase.getProperties().getIntVal("wand.type")){
                    RealmsBase.getPlayerWand(user).wandClick(block);
                }
                else{
                    boolean deny = false, interactBlock = RealmsBase.getProperties().isInteractBlock(block.getType());
                    Zone zone = ZoneLists.getInZone(block);
                    if(interactBlock){
                        deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                    }
                    else if(event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST || event.getClickedBlock().getType() == Material.FURNACE || event.getClickedBlock().getType() == Material.DISPENSER){
                        Zone pZone = ZoneLists.getInZone(user);
                        if(!pZone.equals(zone) && (zone.getCreative() || pZone.getCreative())){
                            deny = true;
                        }
                    }
                    RealmsLogMan.log(RLevel.BLOCK_RIGHTCLICK, "Player: '" + user.getName() + "'" + " BlockClicked: '" + (block != null ? block.toString() : "NULL") + "'" + " ItemInHand: '" + (event.getItem() != null ? event.getItem().toString() : "NULL") + "'" + " Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                    event.setCancelled(deny);
                }
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_RIGHTCLICK. Caused by: " + ex.getMessage());
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void canPlayerUseCommand(PlayerCommandPreprocessEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean allow = true;
        try{
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            Zone zone = ZoneLists.getInZone(user);
            String[] cmd = null;
            if(event.getMessage() != null){
                cmd = event.getMessage().split(" ");
            }
            if(cmd != null){
                if(!cmd[0].matches("/realms") || RealmsBase.getProperties().isCommandAllowed(cmd)){
                    if(!zone.permissionCheck(user, PermissionType.COMMAND)){
                        event.getPlayer().sendMessage(MCChatForm.LIGHT_RED.concat("You are not allowed to execute commands in this area!"));
                        allow = false;
                    }
                }
            }
            RealmsLogMan.log(RLevel.COMMAND_CHECK, "Player: '" + event.getPlayer().getName() + "' Command: '" + (cmd != null ? cmd[0] : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (allow ? "'Allowed'" : "'Denied'"));
            event.setCancelled(!allow);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ COMMAND_CHECK. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onDamageFromEntity(EntityDamageByEntityEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(event.getEntityType() == EntityType.PLAYER){
                Bukkit_User user = new Bukkit_User((Player)event.getEntity());
                Zone zone = ZoneLists.getInZone(user);
                switch(event.getCause()){
                    case ENTITY_EXPLOSION:
                        if(!RealmsBase.getProperties().getBooleanVal("player.explode") && !zone.getExplode()){
                            deny = true;
                        }
                        break;
                    default:
                        if(event.getDamager().getType() == EntityType.PLAYER || (event.getDamager().getType() == EntityType.WOLF && ((Wolf)event.getDamager()).isTamed()) || (event.getDamager().getType() == EntityType.ARROW && ((Arrow)event.getDamager()).getShooter().getType() == EntityType.PLAYER)){
                            deny = !zone.getPVP() || zone.getSanctuary();
                        }
                        else{
                            deny = zone.getSanctuary();
                        }
                }
                RealmsLogMan.log(RLevel.DAMAGE, "Player: " + ((Player)event.getEntity()).getName() + "Damage: " + event.getCause().name() + " Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                event.setCancelled(deny);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ DAMAGE_FROMENTITY. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onDamageFromEnvironment(EntityDamageEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(event.getEntityType() == EntityType.PLAYER){
                Bukkit_User user = new Bukkit_User((Player)event.getEntity());
                Zone zone = ZoneLists.getInZone(user);
                switch(event.getCause()){
                    case FALL:
                        deny = !zone.getFall() || zone.getSanctuary();
                        break;
                    case STARVATION:
                        deny = !zone.getStarve() || zone.getSanctuary();
                        break;
                    case SUFFOCATION:
                    case DROWNING:
                        deny = !zone.getSuffocate() || zone.getSanctuary();
                        break;
                    case FIRE:
                    case FIRE_TICK:
                    case LAVA:
                        deny = !zone.getFire() || zone.getSanctuary();
                        break;
                    case MAGIC:
                    case POISON:
                        deny = !zone.getPotion() || zone.getSanctuary();
                        break;
                    case BLOCK_EXPLOSION:
                        if(!RealmsBase.getProperties().getBooleanVal("player.explode") && !zone.getExplode()){
                            deny = true;
                        }
                        break;
                    default:
                        break;
                }
                RealmsLogMan.log(RLevel.DAMAGE, "Player: " + ((Player)event.getEntity()).getName() + "Damage: " + event.getCause().name() + " Zone: '" + zone.getName() + "' Result: '" + (deny ? "Took Damage'" : "Didn't Take Damage'"));
                event.setCancelled(deny);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ DAMAGE_FROMENVIRONMENT. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler
    public final void onDeath(EntityDeathEvent event){
        if(!RealmsBase.isLoaded()){
            return;
        }
        try{
            if(event.getEntityType() == EntityType.PLAYER){
                Bukkit_User user = new Bukkit_User((Player)event.getEntity());
                RealmsBase.removePlayerWand(user);
                RealmsBase.handleInventory(user, false);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ DEATH. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler
    public final void onDisconnect(PlayerQuitEvent event){
        if(!RealmsBase.isLoaded()){
            return;
        }
        try{
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            RealmsBase.removePlayerWand(user);
            RealmsBase.handleInventory(user, false);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ DISCONNECT. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onDispense(BlockDispenseEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block block = new Bukkit_Block(event.getBlock());
            Zone zone = ZoneLists.getInZone(block);
            deny = !zone.getDispensers();
            RealmsLogMan.log(RLevel.DISPENSE, "Block: '" + block.toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ DISPENSE. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onEat(PlayerInteractEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(event.getItem() != null){
                if((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)){
                    if(event.getItem().getType().isEdible()){
                        Bukkit_User user = new Bukkit_User(event.getPlayer());
                        Zone zone = ZoneLists.getInZone(user);
                        deny = !zone.permissionCheck(user, PermissionType.EAT);
                        RealmsLogMan.log(RLevel.EAT, "Player: '" + event.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
                        event.setCancelled(deny);
                    }
                }
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ EAT. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onEndermanMoveBlock(EntityChangeBlockEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(event.getEntityType() == EntityType.ENDERMAN){
                Bukkit_Entity bent = new Bukkit_Entity((LivingEntity)event.getEntity());
                Zone zone = ZoneLists.getInZone(bent);
                deny = !zone.getEnderman();
                RealmsLogMan.log(RLevel.ENDERMAN, "Block: '" + (event.getBlock() != null ? event.getBlock().toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
                event.setCancelled(deny);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ ENDERMAN. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onEntityRightClick(PlayerInteractEntityEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            Zone zone = ZoneLists.getInZone(user);
            deny = !zone.permissionCheck(user, PermissionType.INTERACT);
            if(!deny){
                if(event.getRightClicked().getType() == EntityType.MINECART){
                    Minecart minecart = (Minecart)event.getRightClicked();
                    if(minecart instanceof StorageMinecart){
                        Zone mZone = ZoneLists.getInZone(new Bukkit_Entity(minecart));
                        if(!zone.equals(mZone) && (zone.getCreative() || mZone.getCreative())){
                            if(!event.getPlayer().hasPermission("bukkit.command.gamemode") && !moded.contains(event.getPlayer())){
                                deny = true;
                            }
                        }
                    }
                }
            }
            RealmsLogMan.log(RLevel.ENTITY_RIGHTCLICK, "Player: '" + event.getPlayer().getName() + "' Entity: '" + event.getRightClicked().getType().name() + "' Zone: '" + zone.getName() + "' Result: " + (!deny ? "Allowed" : "Denied"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ ENTITY_RIGHTCLICK. Cause by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onExplosion(EntityExplodeEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        try{
            Bukkit_Block block;
            Iterator<Block> blown = event.blockList().iterator();
            while(blown.hasNext()){
                block = new Bukkit_Block(blown.next());
                Zone zone = ZoneLists.getInZone(block);
                if(!zone.getExplode()){
                    blown.remove();
                }
            }
            RealmsLogMan.log(RLevel.EXPLOSION, "Base Location: " + event.getLocation().toString() + " Entity: " + event.getEntityType().name());
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ EXPLOSION. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onFlow(BlockFromToEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block from = new Bukkit_Block(event.getBlock());
            Bukkit_Block to = new Bukkit_Block(event.getToBlock());
            Zone zone1 = ZoneLists.getInZone(from);
            Zone zone2 = ZoneLists.getInZone(to);
            if(!zone1.getFlow()){
                deny = true;
            }
            else if(!zone2.getFlow()){
                deny = true;
            }
            RealmsLogMan.log(RLevel.FLOW, "Zone From: " + zone1.getName() + " Result: '" + (!zone1.getFlow() ? "Denied'" : "Allowed' Zone To: " + zone2.getName() + " Result: '" + (zone2.getFlow() ? "Allowed'" : "Denied'")));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ FLOW. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onFoodLevelChange(FoodLevelChangeEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        if(event.getFoodLevel() < ((Player)event.getEntity()).getFoodLevel()){
            try{
                Bukkit_User user = new Bukkit_User((Player)event.getEntity());
                Zone zone = ZoneLists.getInZone(user);
                if(!zone.getStarve()){
                    deny = true;
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + event.getEntity().getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
                event.setCancelled(deny);
            }
            catch(Exception ex){
                RealmsLogMan.severe("An unexpected exception occured @ FOOD_LEVELCHANGE. Caused by: " + ex.getMessage());
                RealmsLogMan.stacktrace(ex);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onIgnite(BlockIgniteEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block block = new Bukkit_Block(event.getBlock());
            Zone zone = ZoneLists.getInZone(block);
            switch(event.getCause()){
                case FIREBALL:
                    if(event.getPlayer() != null){
                        deny = !zone.permissionCheck(new Bukkit_User(event.getPlayer()), PermissionType.IGNITE);
                        RealmsLogMan.log(RLevel.BURN, "Type: 'IGNITE' Player: '" + event.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                    }
                    else{
                        deny = !zone.getBurn();
                        RealmsLogMan.log(RLevel.BURN, "Type: 'BURN' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                    }
                    break;
                case FLINT_AND_STEEL:
                    deny = !zone.permissionCheck(new Bukkit_User(event.getPlayer()), PermissionType.IGNITE);
                    RealmsLogMan.log(RLevel.BURN, "Type: 'IGNITE' Player: '" + event.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                    break;
                case LAVA:
                case LIGHTNING:
                case SPREAD:
                    deny = !zone.getBurn();
                    RealmsLogMan.log(RLevel.BURN, "Type: 'BURN' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                    break;
            }
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ IGNITE. Caused by: " + ex.getMessage());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onItemDrop(PlayerDropItemEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_User user = new Bukkit_User(event.getPlayer());
            Zone zone = ZoneLists.getInZone(user);
            deny = (zone.getCreative() && (event.getPlayer().getGameMode() == GameMode.CREATIVE));
            RealmsLogMan.log(RLevel.ITEM_DROP, "Player: '" + event.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Drop Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ ITEM_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onItemUse(PlayerInteractEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(event.getItem() != null){
                if(RealmsBase.getProperties().isInteractItem(event.getItem().getTypeId())){
                    Zone zone = null;
                    Bukkit_User user = new Bukkit_User(event.getPlayer());
                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                        zone = ZoneLists.getInZone(new Bukkit_Block(event.getClickedBlock()));
                    }
                    else{
                        zone = ZoneLists.getInZone(user);
                    }
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                    RealmsLogMan.log(RLevel.ITEM_USE, "Player: '" + event.getPlayer().getName() + "' Zone: '" + zone.getName() + "' PermType: 'INTERACT' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                    event.setCancelled(deny);
                }
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ ITEM_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onMobSpawn(CreatureSpawnEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Entity entity = new Bukkit_Entity(event.getEntity());
            Zone zone = ZoneLists.getInZone(entity);
            if(isMonster(event.getEntityType()) && zone.getSanctuary() && !RealmsBase.getProperties().getBooleanVal("sanctuary.mobs")){
                deny = true;
            }
            else if(isAnimal(event.getEntityType()) && !zone.getAnimals()){
                deny = true;
            }
            RealmsLogMan.log(RLevel.MOB_SPAWN, "Mob: '" + event.getEntityType().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ MOB_SPAWN. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onMobTarget(EntityTargetLivingEntityEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            if(isMonster(event.getEntityType()) && event.getTarget().getType() == EntityType.PLAYER){
                Bukkit_User user = new Bukkit_User((Player)event.getTarget());
                Zone zone = ZoneLists.getInZone(user);
                deny = zone.getSanctuary();
                RealmsLogMan.log(RLevel.MOB_TARGET, "Mob: '" + event.getEntityType().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                event.setCancelled(deny);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ MOB_TARGET. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPistonExtend(BlockPistonExtendEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block piston = new Bukkit_Block(event.getBlock());
            Bukkit_Block pushing = new Bukkit_Block(getPistonTouch(event.getBlock(), event.getDirection()));
            Zone piszone = ZoneLists.getInZone(piston);
            Zone pushzone = ZoneLists.getInZone(pushing);
            deny = !piszone.getPistons() || !pushzone.getPistons();
            RealmsLogMan.log(RLevel.PISTONS, "Zone: '" + piszone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ PISTON_EXTEND. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPistonRetract(BlockPistonRetractEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Bukkit_Block piston = new Bukkit_Block(event.getBlock());
            Bukkit_Block pushing = new Bukkit_Block(getPistonTouch(event.getBlock(), event.getDirection()));
            Zone piszone = ZoneLists.getInZone(piston);
            Zone pushzone = ZoneLists.getInZone(pushing);
            deny = !piszone.getPistons() || !pushzone.getPistons();
            RealmsLogMan.log(RLevel.PISTONS, "Zone: '" + piszone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ PISTON_RETRACT Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlayerGameModeChange(PlayerGameModeChangeEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        try{
            if(!event.getPlayer().hasPermission("bukkit.command.gamemode")){
                if(event.getNewGameMode() != GameMode.SURVIVAL){
                    if(!moded.contains(event.getPlayer())){
                        moded.add(event.getPlayer());
                    }
                }
                else if(moded.contains(event.getPlayer())){
                    moded.remove(event.getPlayer());
                }
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ PLAYER_GAMEMODE_CHANGE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlayerMove(PlayerMoveEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        try{
            Player player = event.getPlayer();
            Bukkit_User user = new Bukkit_User(player);
            Zone zone = ZoneLists.getInZone(user);
            //Start Enter Zone Checks
            if(!zone.permissionCheck(user, PermissionType.ENTER)){
                player.sendMessage(ChatColor.RED + "You do not have permission to enter that zone!");
                Point thrown = RealmsBase.throwBack(zone, user.getLocationPoint());
                Location loc = player.getLocation();
                loc.setX(thrown.x);
                loc.setY(thrown.y);
                loc.setZ(thrown.z);
                player.teleport(loc);
                return;
            }
            //End Enter Zone Checks
            //Start Creative/Adventure Zone Checks
            if(!player.hasPermission("bukkit.command.gamemode") && !moded.contains(player)){
                if(zone.getCreative()){
                    if(player.getGameMode() != GameMode.CREATIVE){
                        RealmsBase.handleInventory(user, true);
                        player.setGameMode(GameMode.CREATIVE);
                    }
                    if(!ZoneLists.isInCreative(user)){
                        ZoneLists.addInCreative(user);
                    }
                }
                else if(zone.getAdventure()){
                    if(player.getGameMode() != GameMode.ADVENTURE){
                        player.setGameMode(GameMode.ADVENTURE);
                    }
                    if(!ZoneLists.isInAdventure(user)){
                        ZoneLists.addInAdventure(user);
                    }
                }
                else if(player.getGameMode() != GameMode.SURVIVAL){
                    if(player.getGameMode() == GameMode.CREATIVE && ZoneLists.isInCreative(user)){
                        ZoneLists.removeInCreative(user);
                        RealmsBase.handleInventory(user, false);
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    else if(player.getGameMode() == GameMode.ADVENTURE && ZoneLists.isInAdventure(user)){
                        ZoneLists.removeInAdventure(user);
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                }
            }
            //End Creative/Adventure Zone Checks
            //Start Healing Zone Checks
            if(zone.getHealing()){
                ZoneLists.addInHealing(user);
            }
            else{
                ZoneLists.removeInHealing(user);
            }
            //End Healing Zone Checks
            //Start Restricted Zone Checks
            if(zone.getRestricted()){
                if(!zone.permissionCheck(user, PermissionType.AUTHED)){
                    if(!ZoneLists.isInRestricted(user)){
                        ZoneLists.addInRestricted(user);
                        player.sendMessage(ChatColor.RED + RealmsBase.getProperties().getStringVal("restrict.message"));
                    }
                }
            }
            else if(ZoneLists.isInRestricted(user)){
                ZoneLists.removeInRestricted(user);
            }
            //End Restricted Zone Checks
            //Check if player should receive Welcome/Farewell Messages
            RealmsBase.playerMessage(user);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ PLAYER_MOVE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPortalUse(PlayerPortalEvent event){
        if(!RealmsBase.isLoaded() || event.isCancelled()){
            return;
        }
        boolean deny = false;
        try{
            Player player = event.getPlayer();
            Bukkit_User user = new Bukkit_User(player);
            Bukkit_Block block = new Bukkit_Block(event.getTo().getBlock());
            Zone zFrom = ZoneLists.getInZone(user);
            Zone zTo = ZoneLists.getInZone(block);
            deny = !zFrom.permissionCheck(user, PermissionType.TELEPORT);
            if(deny){
                player.sendMessage(MCChatForm.RED.concat("You do not have permission to teleport out of this zone!"));
            }
            else{
                deny = !zTo.permissionCheck(user, PermissionType.TELEPORT);
                if(deny){
                    player.sendMessage(MCChatForm.RED.concat("You do not have permission to teleport into that zone!"));
                }
                else{
                    deny = !zTo.permissionCheck(user, PermissionType.ENTER);
                    if(deny){
                        player.sendMessage(MCChatForm.RED.concat("You do not have permission to enter that zone!"));
                    }
                }
            }
            RealmsLogMan.log(RLevel.PORTAL_USE, "Player: '" + player.getName() + "' Zone From: '" + zFrom.getName() + "' Zone To: '" + zTo.getName() + "' Result: " + (!deny ? "'Allowed'" : "'Denied'"));
            event.setCancelled(deny);
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ PORTAL_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    //public final void onPotionEffect(IfBukkitHadAnEvent itWouldGoHere){
    //}
    @EventHandler(priority = EventPriority.HIGH)
    public final void onVehiclePositionChange(VehicleMoveEvent event){
        if(!RealmsBase.isLoaded()){
            return;
        }
        try{
            Vehicle vehicle = event.getVehicle();
            if(vehicle.getPassenger() != null && vehicle.getPassenger() instanceof Player){
                Player player = (Player)vehicle.getPassenger();
                Bukkit_User user = new Bukkit_User(player);
                Zone zone = ZoneLists.getInZone(user);
                //Start Enter Zone Checks
                if(!zone.permissionCheck(user, PermissionType.ENTER)){
                    vehicle.eject();
                    Point thrown = RealmsBase.throwBack(zone, user.getLocationPoint());
                    Location toThrow = new Location(player.getWorld(), thrown.x, thrown.y, thrown.z, player.getLocation().getYaw(), player.getLocation().getPitch());
                    player.teleport(toThrow);
                    if(vehicle.getType() == EntityType.BOAT){
                        player.sendMessage("[\u00A77Clippy\u00A7F]\u00A7C Looks like you fell out of your boat! Need some help?");
                    }
                    else{
                        player.sendMessage("[\u00A77Clippy\u00A7F]\u00A7C Looks like you fell out of your minecart! Need some help?");
                    }
                    return;
                }
                //End Enter Zone Checks
                //Start Creative/Adventure Zone Checks
                if(!player.hasPermission("bukkit.command.gamemode") && !moded.contains(player)){
                    if(zone.getCreative()){
                        if(player.getGameMode() != GameMode.CREATIVE){
                            RealmsBase.handleInventory(user, true);
                            player.setGameMode(GameMode.CREATIVE);
                        }
                        if(!ZoneLists.isInCreative(user)){
                            ZoneLists.addInCreative(user);
                        }
                    }
                    else if(zone.getAdventure()){
                        if(player.getGameMode() != GameMode.ADVENTURE){
                            player.setGameMode(GameMode.ADVENTURE);
                        }
                        if(!ZoneLists.isInAdventure(user)){
                            ZoneLists.addInAdventure(user);
                        }
                    }
                    else if(player.getGameMode() != GameMode.SURVIVAL){
                        if(player.getGameMode() == GameMode.CREATIVE && ZoneLists.isInCreative(user)){
                            ZoneLists.removeInCreative(user);
                            RealmsBase.handleInventory(user, false);
                            player.setGameMode(GameMode.SURVIVAL);
                        }
                        else if(player.getGameMode() == GameMode.ADVENTURE && ZoneLists.isInAdventure(user)){
                            ZoneLists.removeInAdventure(user);
                            player.setGameMode(GameMode.SURVIVAL);
                        }
                    }
                }
                //End Creative/Adventure Zone Checks
                //Start Healing Zone Checks
                if(zone.getHealing()){
                    ZoneLists.addInHealing(user);
                }
                else{
                    ZoneLists.removeInHealing(user);
                }
                //End Healing Zone Checks
                //Start Restricted Zone Checks
                if(zone.getRestricted()){
                    if(!zone.permissionCheck(user, PermissionType.AUTHED)){
                        if(!ZoneLists.isInRestricted(user)){
                            ZoneLists.addInRestricted(user);
                            player.sendMessage(ChatColor.RED + RealmsBase.getProperties().getStringVal("restrict.message"));
                        }
                    }
                }
                else if(ZoneLists.isInRestricted(user)){
                    ZoneLists.removeInRestricted(user);
                }
                //End Restricted Zone Checks
                //Check if player should receive Welcome/Farewell Messages
                RealmsBase.playerMessage(user);
            }
        }
        catch(Exception ex){
            RealmsLogMan.severe("An unexpected exception occured @ VEHICLE_MOVE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    private final Block getPistonTouch(Block piston, BlockFace facing){
        Block pushing = null;
        int x = piston.getX(), y = piston.getY(), z = piston.getZ();
        switch(facing){
            case DOWN: //DOWN
                pushing = piston.getWorld().getBlockAt(x, (y - 1), z);
            case UP: //UP
                pushing = piston.getWorld().getBlockAt(x, (y + 1), z);
            case NORTH: //NORTH (-Z)
                pushing = piston.getWorld().getBlockAt(x, y, (z - 1));
            case SOUTH: //SOUTH (+Z)
                pushing = piston.getWorld().getBlockAt(x, y, (z + 1));
            case WEST: //WEST (-X)
                pushing = piston.getWorld().getBlockAt((x - 1), y, z);
            case EAST: //EAST (+X)
                pushing = piston.getWorld().getBlockAt((x + 1), y, z);
            default:
                break;
        }
        return pushing;
    }

    private final boolean isAnimal(EntityType type){
        switch(type){
            case BAT:
            case CHICKEN:
            case COW:
            case MUSHROOM_COW:
            case OCELOT:
            case PIG:
            case SHEEP:
            case SQUID:
            case WOLF:
                return true;
            default:
                return false;
        }
    }

    private final boolean isMonster(EntityType type){
        switch(type){
            case BLAZE:
            case CAVE_SPIDER:
            case CREEPER:
            case ENDERMAN:
            case GHAST:
            case GIANT:
            case MAGMA_CUBE:
            case SILVERFISH:
            case SKELETON:
            case SLIME:
            case SPIDER:
            case WITHER:
            case ZOMBIE:
                return true;
            default:
                return false;
        }
    }
}
