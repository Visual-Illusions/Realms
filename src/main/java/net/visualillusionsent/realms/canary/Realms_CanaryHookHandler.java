/*
 * This file is part of Realms.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms.canary;

import java.util.ArrayList;
import java.util.Iterator;
import net.canarymod.Canary;
import net.canarymod.ToolBox;
import net.canarymod.api.DamageType;
import net.canarymod.api.GameMode;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.EntityLiving;
import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Location;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.entity.DamageHook;
import net.canarymod.hook.entity.EndermanDropBlockHook;
import net.canarymod.hook.entity.EndermanPickupBlockHook;
import net.canarymod.hook.entity.EntitySpawnHook;
import net.canarymod.hook.entity.HangingEntityDestroyHook;
import net.canarymod.hook.entity.MobTargetHook;
import net.canarymod.hook.entity.PotionEffectAppliedHook;
import net.canarymod.hook.entity.VehicleMoveHook;
import net.canarymod.hook.player.*;
import net.canarymod.hook.world.BlockPhysicsHook;
import net.canarymod.hook.world.DispenseHook;
import net.canarymod.hook.world.ExplosionHook;
import net.canarymod.hook.world.FlowHook;
import net.canarymod.hook.world.IgnitionHook;
import net.canarymod.hook.world.PistonExtendHook;
import net.canarymod.hook.world.PistonRetractHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.plugin.Priority;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.permission.PermissionType;
import net.visualillusionsent.realms.zones.polygon.Point;

/**
 * @author Jason (darkdiplomat)
 */
public final class Realms_CanaryHookHandler implements PluginListener {

    private final ArrayList<Player> moded = new ArrayList<Player>();

    public Realms_CanaryHookHandler(CanaryRealms canaryRealms) {
        Canary.hooks().registerListener(this, canaryRealms);
    }

    @HookHandler(priority = Priority.HIGH)
    public final void blockDestroy(BlockDestroyHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getBlock() != null) {
                Canary_Block cBlock = new Canary_Block(hook.getBlock());
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(cBlock);
                deny = !zone.permissionCheck(user, PermissionType.DESTROY);
                RealmsLogMan.log(RLevel.BLOCK_BREAK, "Player: '" + hook.getPlayer().getName() + "' Block: '" + (hook.getBlock() != null ? hook.getBlock().toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_DESTROY. Caused by: " + ex.getClass().getName());
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void blockLeftClick(BlockLeftClickHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getBlock() != null) {
                Canary_Block cBlock = new Canary_Block(hook.getBlock());
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(cBlock);
                if (RealmsBase.getProperties().isInteractBlock(hook.getBlock().getTypeId())) {
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                }
                RealmsLogMan.log(RLevel.BLOCK_DESTROY, "Player: '" + hook.getPlayer().getName() + "' Block: '" + (hook.getBlock() != null ? hook.getBlock().toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_LEFTCLICK. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void blockPhysics(BlockPhysicsHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getBlock().getType() == BlockType.Sand || hook.getBlock().getType() == BlockType.Gravel) {
                Canary_Block cBlock = new Canary_Block(hook.getBlock());
                Zone zone = ZoneLists.getInZone(cBlock);
                deny = !zone.getPhysics();
                RealmsLogMan.log(RLevel.BLOCK_PHYSICS, "Block: '" + hook.getBlock().toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_PHYSICS. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void onBlockPlace(BlockPlaceHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Zone zone;
            Canary_Block cBlock;
            Canary_User user = new Canary_User(hook.getPlayer());
            if (hook.getBlockPlaced() != null) {
                cBlock = new Canary_Block(hook.getBlockPlaced());
                zone = ZoneLists.getInZone(cBlock);
                deny = !zone.permissionCheck(user, PermissionType.CREATE);
            }
            else {
                cBlock = new Canary_Block(hook.getBlockClicked());
                zone = ZoneLists.getInZone(cBlock);
                deny = !zone.permissionCheck(user, PermissionType.CREATE);
            }
            RealmsLogMan.log(RLevel.BLOCK_PLACE, "Player: '" + hook.getPlayer().getName() + "'" + " BlockPlaced: '" + (hook.getBlockPlaced() != null ? hook.getBlockPlaced().toString() : "NULL") + "'" + " BlockClicked: '" + (hook.getBlockClicked() != null ? hook.getBlockClicked().toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: "
                + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_PLACE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void blockRightClick(BlockRightClickHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getBlockClicked() != null) {
                Canary_Block cBlock = new Canary_Block(hook.getBlockClicked());
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(cBlock);
                if (hook.getPlayer().getItemHeld() != null && hook.getPlayer().getItemHeld().getId() == RealmsBase.getProperties().getIntVal("wand.type")) {
                    RealmsBase.getPlayerWand(user).wandClick(cBlock);
                    return;
                }
                else if (RealmsBase.getProperties().isInteractBlock(hook.getBlockClicked().getTypeId())) {
                    deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                }
                else if (hook.getBlockClicked().getType() == BlockType.Chest || hook.getBlockClicked().getType() == BlockType.EnderChest || hook.getBlockClicked().getType() == BlockType.Furnace || hook.getBlockClicked().getType() == BlockType.Dispenser) {
                    Zone pZone = ZoneLists.getInZone(user);
                    if (!pZone.equals(zone) && (zone.getCreative() || pZone.getCreative())) {
                        if (!hook.getPlayer().hasPermission("canary.commands.mode") && !moded.contains(hook.getPlayer())) {
                            deny = true;
                        }
                    }
                }
                RealmsLogMan.log(RLevel.BLOCK_RIGHTCLICK, "Player: '" + hook.getPlayer().getName() + "'" + " BlockClicked: '" + (hook.getBlockClicked() != null ? hook.getBlockClicked().toString() : "NULL") + "'" + " ItemInHand: '" + (hook.getPlayer().getItemHeld() != null ? hook.getPlayer().getItemHeld().toString() : "NULL") + "'" + " Zone: '"
                    + (zone == null ? "Not Checked" : zone.getName()) + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ BLOCK_RIGHTCLICK. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void damage(DamageHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }

        boolean deny = false;
        Entity defender = hook.getDefender();
        Entity attacker = hook.getAttacker();
        DamageType type = hook.getDamageSource().getDamagetype();

        try {
            if (defender instanceof Player) {
                Canary_User user = new Canary_User((Player) defender);
                Zone zone = ZoneLists.getInZone(user);
                switch (type) {
                    case PLAYER:
                        if (attacker != null) {
                            if (attacker instanceof Player) {
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
                        deny = (!RealmsBase.getProperties().getBooleanVal("player.explode") && !zone.getExplode()) || zone.getSanctuary();
                        break;
                    default:
                        deny = zone.getSanctuary();
                        break;
                }
                RealmsLogMan.log(RLevel.DAMAGE, "Player: " + defender.getName() + " Damage: " + type.name() + " Attacker: " + (attacker != null ? attacker.getName() : "No Attacker") + " Zone: '" + zone.getName() + "' Result: " + (deny ? "'No Damage'" : "'Damaged'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DAMAGE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler
    public final void playerDeath(PlayerDeathHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            RealmsBase.removePlayerWand(user);
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DEATH. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @HookHandler
    public final void disconnection(DisconnectionHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            RealmsBase.removePlayerWand(user);
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DISCONNECT. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final boolean dispense(DispenseHook hook) {
        if (!RealmsBase.isLoaded()) {
            return false;
        }
        boolean deny = false;
        try {
            Canary_Block cBlock = new Canary_Block(hook.getDispenser().getBlock());
            Zone zone = ZoneLists.getInZone(cBlock);
            deny = !zone.getDispensers();
            RealmsLogMan.log(RLevel.DISPENSE, "Block: '" + hook.getDispenser().toString() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ DISPENSE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        return deny;
    }

    @HookHandler(priority = Priority.HIGH)
    public final void eat(EatHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            Zone zone = ZoneLists.getInZone(user);
            deny = !zone.permissionCheck(user, PermissionType.EAT);
            RealmsLogMan.log(RLevel.EAT, "Player: '" + hook.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ EAT. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void endermanDropBlock(EndermanDropBlockHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_Entity cent = new Canary_Entity(hook.getEnderman());
            Zone zone = ZoneLists.getInZone(cent);
            deny = !zone.getEnderman();
            RealmsLogMan.log(RLevel.ENDERMAN, "'Enderman' attempted to place Block: '" + (hook.getBlock() != null ? hook.getBlock().toString() : "NULL") + "' in Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ENDERMAN_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void endermanPickupBlock(EndermanPickupBlockHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_Entity cent = new Canary_Entity(hook.getEnderman());
            Zone zone = ZoneLists.getInZone(cent);
            deny = !zone.getEnderman();
            RealmsLogMan.log(RLevel.ENDERMAN, "Block: '" + (hook.getBlock() != null ? hook.getBlock().toString() : "NULL") + "' Zone: '" + zone.getName() + "' Result: '" + (deny ? "Denied'" : "Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ENDERMAN_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void entityRightClick(EntityRightClickHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getEntity() != null) {
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                RealmsLogMan.log(RLevel.ENTITY_RIGHTCLICK, "Player: '" + hook.getPlayer().getName() + "' Entity: '" + hook.getEntity().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "Denied" : "Allow"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ENTITY_RIGHTCLICK. Cause by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void explosion(ExplosionHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            Canary_Block cBlock;
            Iterator<Block> blown = hook.getAffectedBlocks().iterator();
            while (blown.hasNext()) {
                cBlock = new Canary_Block(blown.next());
                Zone zone = ZoneLists.getInZone(cBlock);
                if (!zone.getExplode()) {
                    blown.remove();
                }
            }
            RealmsLogMan.log(RLevel.EXPLOSION, "BaseBlock: " + hook.getBlock().toString() + " Entity: " + (hook.getEntity() != null ? hook.getEntity().getName() : "UNKNOWN"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ EXPLOSION. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void flow(FlowHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_Block cFrom = new Canary_Block(hook.getBlockFrom());
            Canary_Block cTo = new Canary_Block(hook.getBlockTo());
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
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public void foodExhaustion(FoodExhaustionHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        float result = hook.getNewValue();
        try {
            if (hook.getOldValue() < hook.getNewValue()) {
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                if (!zone.getStarve()) {
                    result = hook.getOldValue();
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + hook.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        hook.setNewValue(result);
    }

    @HookHandler(priority = Priority.HIGH)
    public void foodLevel(FoodLevelHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        int result = hook.getNewValue();
        if (hook.getOldValue() > hook.getNewValue()) {
            try {
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                if (!zone.getStarve()) {
                    result = hook.getOldValue();
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + hook.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
            }
            catch (Exception ex) {
                RealmsLogMan.severe("An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE. Caused by: " + ex.getClass().getName());
                RealmsLogMan.stacktrace(ex);
            }
        }
        hook.setNewValue(result);
    }

    @HookHandler(priority = Priority.HIGH)
    public void foodSaturation(FoodSaturationHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        Float result = hook.getNewValue();
        if (hook.getOldValue() > hook.getNewValue()) {
            try {
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(user);
                if (!zone.getStarve()) {
                    result = hook.getOldValue();
                }
                RealmsLogMan.log(RLevel.STARVATION, "Player: '" + hook.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Result: " + (zone.getStarve() ? "'Allowed'" : "'Denied'"));
            }
            catch (Exception ex) {
                RealmsLogMan.severe("An unexpected exception occured @ FOOD_EXHAUSTIONCHANGE. Caused by: " + ex.getClass().getName());
                RealmsLogMan.stacktrace(ex);
            }
        }
        hook.setNewValue(result);
    }

    @HookHandler(priority = Priority.HIGH)
    public final void hangingEntityDestroy(HangingEntityDestroyHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            Canary_Entity entity = new Canary_Entity(hook.getPainting());
            Zone zone = ZoneLists.getInZone(entity);
            deny = !zone.permissionCheck(user, PermissionType.DESTROY);
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ HANGINGENTITY_DESTROY. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void ignition(IgnitionHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getBlock() != null) {
                Canary_Block cBlock = new Canary_Block(hook.getBlock());
                Zone zone = ZoneLists.getInZone(cBlock);
                byte bs = hook.getBlock().getStatus();
                if (bs == 1 || bs == 3 || bs == 4 || bs == 5 || (bs == 6 && hook.getPlayer() == null)) {
                    deny = !zone.getBurn();
                    RealmsLogMan.log(RLevel.BURN, "Type: 'BURN' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                }
                else if (hook.getPlayer() != null && (bs == 2 || bs == 6)) {
                    deny = !zone.permissionCheck(new Canary_User(hook.getPlayer()), PermissionType.IGNITE) || !zone.getBurn();
                    // RealmsLogMan.log(RLevel.BURN, "Type: 'IGNITE' Player: '" + player.getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ IGNITE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void itemDrop(ItemDropHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            Zone zone = ZoneLists.getInZone(user);
            deny = (zone.getCreative() && (hook.getPlayer().getMode() == GameMode.CREATIVE));
            RealmsLogMan.log(RLevel.ITEM_DROP, "Player: '" + hook.getPlayer().getName() + "' Zone: '" + zone.getName() + "' Drop Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ITEM_DROP. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void itemUse(ItemUseHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (RealmsBase.getProperties().isInteractItem(hook.getPlayer().getItemHeld().getId())) {
                Canary_User user = new Canary_User(hook.getPlayer());
                Zone zone = ZoneLists.getInZone(new Canary_Block(hook.getBlockClicked()));
                deny = !zone.permissionCheck(user, PermissionType.INTERACT);
                RealmsLogMan.log(RLevel.ITEM_USE, "Player: '" + hook.getPlayer().getName() + "' Zone: '" + zone.getName() + "' PermType: 'INTERACT' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ ITEM_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void entitySpawn(EntitySpawnHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getEntity().isLiving()) {
                Canary_Entity entity = new Canary_Entity(hook.getEntity());
                Zone zone = ZoneLists.getInZone(entity);
                if (((EntityLiving) hook.getEntity()).isMob() && zone.getSanctuary() && !RealmsBase.getProperties().getBooleanVal("sanctuary.mobs")) {
                    deny = true;
                }
                else if (((EntityLiving) hook.getEntity()).isAnimal() && !zone.getAnimals()) {
                    deny = true;
                }
                RealmsLogMan.log(RLevel.MOB_SPAWN, "Mob: '" + hook.getEntity().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ MOB_SPAWN. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void mobTarget(MobTargetHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getEntity().isMob() && hook.getTarget().isPlayer()) {
                Canary_User user = new Canary_User((Player) hook.getTarget());
                Zone zone = ZoneLists.getInZone(user);
                deny = zone.getSanctuary();
                RealmsLogMan.log(RLevel.MOB_TARGET, "Mob: '" + hook.getEntity().getName() + "' Zone: '" + zone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ MOB_TARGET. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void pistonExtend(PistonExtendHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_Block piston = new Canary_Block(hook.getPiston());
            Canary_Block pushing = new Canary_Block(hook.getMoving());
            Zone piszone = ZoneLists.getInZone(piston);
            Zone pushzone = ZoneLists.getInZone(pushing);
            deny = !piszone.getPistons() || !pushzone.getPistons();
            RealmsLogMan.log(RLevel.PISTONS, "Zone: '" + piszone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PISTON_EXTEND. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void pistonRetract(PistonRetractHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_Block piston = new Canary_Block(hook.getPiston());
            Canary_Block pushing = new Canary_Block(hook.getMoving());
            Zone piszone = ZoneLists.getInZone(piston);
            Zone pushzone = ZoneLists.getInZone(pushing);
            deny = !piszone.getPistons() || !pushzone.getPistons();
            RealmsLogMan.log(RLevel.PISTONS, "Zone: '" + piszone.getName() + "' Result: " + (deny ? "'Denied'" : "'Allowed'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PISTON_RETRACT. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void playerMove(PlayerMoveHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            Zone zone = ZoneLists.getInZone(user);
            // Start Enter Zone Checks
            if (!zone.permissionCheck(user, PermissionType.ENTER)) {
                hook.getPlayer().notice("You do not have permission to enter that zone!");
                hook.setCanceled();
                return;
            }
            // End Enter Zone Checks
            // Start Creative/Adventure Zone Checks
            if (!hook.getPlayer().hasPermission("canary.command.mode") && !moded.contains(hook.getPlayer())) {
                if (zone.getCreative()) {
                    if (hook.getPlayer().getMode() != GameMode.CREATIVE) {
                        RealmsBase.handleInventory(user, true);
                        hook.getPlayer().setMode(GameMode.CREATIVE);
                    }
                    if (!ZoneLists.isInCreative(user)) {
                        ZoneLists.addInCreative(user);
                    }
                }
                else if (zone.getAdventure()) {
                    if (hook.getPlayer().getMode() != GameMode.ADVENTURE) {
                        hook.getPlayer().setMode(GameMode.ADVENTURE);
                    }
                    if (!ZoneLists.isInAdventure(user)) {
                        ZoneLists.addInAdventure(user);
                    }
                }
                else if (hook.getPlayer().getMode() != GameMode.SURVIVAL) {
                    if (hook.getPlayer().getMode() == GameMode.CREATIVE && ZoneLists.isInCreative(user)) {
                        ZoneLists.removeInCreative(user);
                        RealmsBase.handleInventory(user, false);
                        hook.getPlayer().setMode(GameMode.SURVIVAL);
                    }
                    else if (hook.getPlayer().getMode() == GameMode.ADVENTURE && ZoneLists.isInAdventure(user)) {
                        ZoneLists.removeInAdventure(user);
                        hook.getPlayer().setMode(GameMode.SURVIVAL);
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
                        hook.getPlayer().notice(RealmsBase.getProperties().getStringVal("restrict.message"));
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

    @HookHandler(priority = Priority.HIGH)
    public final void portalUse(PortalUseHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            Canary_User user = new Canary_User(hook.getPlayer());
            Location to = hook.getTo();
            Canary_Block block = new Canary_Block(to.getWorld().getBlockAt(ToolBox.floorToBlock(to.getX()), ToolBox.floorToBlock(to.getY()), ToolBox.floorToBlock(to.getZ())));
            Zone zFrom = ZoneLists.getInZone(user);
            Zone zTo = ZoneLists.getInZone(block);
            deny = !zFrom.permissionCheck(user, PermissionType.TELEPORT);
            if (deny) {
                hook.getPlayer().notice("You do not have permission to teleport out of this zone!");
            }
            else {
                deny = !zTo.permissionCheck(user, PermissionType.TELEPORT);
                if (deny) {
                    hook.getPlayer().notice("You do not have permission to teleport into that zone!");
                }
                else {
                    deny = !zTo.permissionCheck(user, PermissionType.ENTER);
                    if (deny) {
                        hook.getPlayer().notice("You do not have permission to enter that zone!");
                    }
                }
            }
            RealmsLogMan.log(RLevel.PORTAL_USE, "Player: '" + hook.getPlayer().getName() + "' Zone From: '" + zFrom.getName() + "' Zone To: '" + zTo.getName() + "' Result: " + (!deny ? "'Allowed'" : "'Denied'"));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PORTAL_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setCanceled();
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void potionEffectApplied(PotionEffectAppliedHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        boolean deny = false;
        try {
            if (hook.getEntity().isPlayer()) {
                Canary_User user = new Canary_User((Player) hook.getEntity());
                Zone zone = ZoneLists.getInZone(user);
                deny = !zone.getPotion();
                RealmsLogMan.log(RLevel.POTION_EFFECT, "Player: '" + hook.getEntity().getName() + "' Zone: '" + zone.getName() + "' Result: " + (!deny ? "'Allowed'" : "'Denied'"));
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ PORTAL_USE. Caused by: " + ex.getClass().getName());
            RealmsLogMan.stacktrace(ex);
        }
        if (deny) {
            hook.setPotionEffect(null);
        }
    }

    @HookHandler(priority = Priority.HIGH)
    public final void vehicleMove(VehicleMoveHook hook) {
        if (!RealmsBase.isLoaded()) {
            return;
        }
        try {
            if (hook.getVehicle().getPassenger() != null && hook.getVehicle().getPassenger().isPlayer()) {
                Player player = (Player) hook.getVehicle().getPassenger();
                Canary_User user = new Canary_User(player);
                Zone zone = ZoneLists.getInZone(user);
                // Start Enter Zone Checks
                if (!zone.permissionCheck(user, PermissionType.ENTER)) {
                    Point thrown = RealmsBase.throwBack(zone, user.getLocationPoint());
                    player.teleportTo(thrown.x, thrown.y, thrown.z, player.getRotation(), player.getPitch());
                    player.message(String.format("[\u00A77Clippy\u00A7F]\u00A7C Looks like you fell out of your %s! Need some help?", hook.getVehicle().getName()));
                    return;
                }
                // End Enter Zone Checks
                // Start Creative Zone Checks
                if (zone.getCreative()) {
                    if (player.getMode() != GameMode.CREATIVE && !player.hasPermission("canary.command.mode") && !moded.contains(player) && !ZoneLists.isInCreative(user)) {
                        player.setMode(GameMode.CREATIVE);
                        RealmsBase.handleInventory(user, true);
                    }
                }
                else if (zone.getAdventure()) {
                    if (player.getMode() != GameMode.ADVENTURE && !player.hasPermission("canary.command.mode") && !moded.contains(player) && !ZoneLists.isInAdventure(user)) {
                        player.setMode(GameMode.ADVENTURE);
                    }
                }
                else if (player.getMode() != GameMode.SURVIVAL && !player.hasPermission("canary.command.mode") && (ZoneLists.isInCreative(user) || ZoneLists.isInAdventure(user))) {
                    player.setMode(GameMode.SURVIVAL);
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
                        if (!ZoneLists.isInRestricted(user) && player.getMode() != GameMode.CREATIVE && !player.getCapabilities().isInvulnerable()) {
                            ZoneLists.addInRestricted(user);
                            player.notice(RealmsBase.getProperties().getStringVal("restrict.message"));
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
}
