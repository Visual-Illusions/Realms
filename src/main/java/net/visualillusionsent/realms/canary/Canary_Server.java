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
package net.visualillusionsent.realms.canary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import net.canarymod.Canary;
import net.canarymod.api.Server;
import net.canarymod.api.entity.living.animal.EntityAnimal;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.entity.living.monster.EntityMob;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.plugin.Plugin;
import net.canarymod.tasks.ServerTask;
import net.canarymod.tasks.ServerTaskManager;
import net.canarymod.user.Group;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Server;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.interfaces.SynchronizedTask;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class Canary_Server implements Mod_Server{
    private final Server server;
    private final Logger system_logger;
    private final Plugin realms;

    public Canary_Server(Plugin realms, Server server, Logger system_logger){
        this.server = server;
        this.system_logger = system_logger;
        this.realms = realms;
    }

    @Override
    public List<Mod_Entity> getAnimals(){
        Collection<World> worlds = server.getWorldManager().getAllWorlds();
        List<Mod_Entity> animals = new ArrayList<Mod_Entity>();
        synchronized (worlds) {
            for (World world : worlds) {
                Collection<EntityAnimal> cAni = world.getAnimalList();
                for (EntityAnimal anim : cAni) {
                    animals.add(new Canary_Entity(anim));
                }
            }
        }
        return animals;
    }

    @Override
    public List<Mod_Entity> getMobs(){
        Collection<World> worlds = server.getWorldManager().getAllWorlds();
        List<Mod_Entity> mobs = new ArrayList<Mod_Entity>();
        synchronized (worlds) {
            for (World world : worlds) {
                Collection<EntityMob> cMob = world.getMobList();
                for (EntityMob mob : cMob) {
                    mobs.add(new Canary_Entity(mob));
                }
            }
        }
        return mobs;
    }

    @Override
    public List<Mod_User> getUsers(){
        Player[] current = server.getPlayerList().toArray(new Player[0]);
        List<Mod_User> users = new ArrayList<Mod_User>();
        for (Player player : current) {
            users.add(new Canary_User(player));
        }
        return users;
    }

    @Override
    public Mod_User getUser(String name){
        Player player = server.getPlayer(name);
        if (player != null) {
            return new Canary_User(player);
        }
        return null;
    }

    @Override
    public final void setBlock(int x, int y, int z, int type, int data, int dimension, String world){
        Block block = server.getWorldManager().getWorld(world, DimensionType.fromId(dimension == 1 ? -1 : dimension == 2 ? 1 : 0), false).getBlockAt(x, y, z);
        block.setTypeId((short) type);
        block.setData((short) data);
        server.getWorldManager().getWorld(world, DimensionType.fromId(dimension == 1 ? -1 : dimension == 2 ? 1 : 0), false).setBlock(block);

    }

    @Override
    public final Mod_Block getBlockAt(int x, int y, int z, int dimension, String world){
        return new Canary_Block(server.getWorldManager().getWorld(world, DimensionType.fromId(dimension == 1 ? -1 : dimension == 2 ? 1 : 0), false).getBlockAt(x, y, z));
    }

    @Override
    public final Mod_Item constructItem(int type, int amount, int damage, String name, Mod_ItemEnchantment[] enchs, String[] lore){
        Item item = Canary.factory().getItemFactory().newItem(type, amount, damage);
        if (enchs != null) {
            for (Mod_ItemEnchantment ench : enchs) {
                item.addEnchantments(Canary.factory().getItemFactory().newEnchantment((short) ench.getId(), (short) ench.getLevel()));
            }
        }
        if (lore != null) {
            item.setLore(lore);
        }
        if (name != null) {
            item.setDisplayName(name);
        }
        return new Canary_Item(item);
    }

    @Override
    public final Mod_ItemEnchantment constructEnchantment(int id, int level){
        return new Canary_ItemEnchantment(Canary.factory().getItemFactory().newEnchantment((short) id, (short) level));
    }

    @Override
    public final String getDefaultWorldName(){
        return Canary.getServer().getDefaultWorld().getName();
    }

    @Override
    public final List<String> getAdminGroups(){
        List<String> adminGroups = new ArrayList<String>();
        for (Group group : Canary.usersAndGroups().getGroups()) {
            if (group.isAdministratorGroup()) {
                adminGroups.add(group.getName());
            }
        }
        return adminGroups;
    }

    public final String getDefaultGroupName(){
        return Canary.usersAndGroups().getDefaultGroup().getName();
    }

    @Override
    public final int getHighestY(int x, int z, String world, int dimension){
        return server.getWorldManager().getWorld(world, DimensionType.fromId(dimension == 1 ? -1 : dimension == 2 ? 1 : 0), false).getHighestBlockAt(x, z);
    }

    @Override
    public boolean isCanaryClassic(){
        return false;
    }

    @Override
    public boolean isCanary(){
        return true;
    }

    @Override
    public boolean isBukkit(){
        return false;
    }

    @Override
    public Logger getLogger(){
        return system_logger;
    }

    @Override
    public SynchronizedTask addTaskToServer(Runnable runnable, long delay){
        CanarySyncRealmsTask csrt = new CanarySyncRealmsTask(realms, runnable, delay);
        ServerTaskManager.addTask(csrt);
        return csrt;
    }

    @Override
    public void removeTask(SynchronizedTask task){
        ServerTaskManager.removeTask((ServerTask) task);
    }
}
