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
package net.visualillusionsent.realms.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Entity;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.tasks.SynchronizedTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Jason (darkdiplomat)
 */
public final class Bukkit_Server implements net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Server {

    private final Server server;
    private final Logger system_logger;
    private final JavaPlugin realms;

    public Bukkit_Server(JavaPlugin realms, Server server, Logger system_logger) {
        this.server = server;
        this.system_logger = system_logger;
        this.realms = realms;
    }

    @Override
    public final List<Mod_Entity> getAnimals() {
        List<World> worlds = new ArrayList<World>(server.getWorlds());
        List<Mod_Entity> animals = new ArrayList<Mod_Entity>();
        for (World world : worlds) {
            List<Animals> bAni = new ArrayList<Animals>(world.getEntitiesByClass(Animals.class));
            for (Animals anim : bAni) {
                animals.add(new Bukkit_Entity(anim));
            }
        }
        return animals;
    }

    @Override
    public final List<Mod_Entity> getMobs() {
        List<World> worlds = new ArrayList<World>(server.getWorlds());
        List<Mod_Entity> mobs = new ArrayList<Mod_Entity>();
        for (World world : worlds) {
            List<Monster> bMobs = new ArrayList<Monster>(world.getEntitiesByClass(Monster.class));
            for (Monster mon : bMobs) {
                mobs.add(new Bukkit_Entity(mon));
            }
        }
        return mobs;
    }

    @Override
    public final List<Mod_User> getUsers() {
        List<Mod_User> users = new ArrayList<Mod_User>();
        for (Player player : server.getOnlinePlayers()) {
            users.add(new Bukkit_User(player));
        }
        return users;
    }

    @Override
    public final Mod_User getUser(String name) {
        Player player = server.getPlayer(name);
        if (player != null) {
            return new Bukkit_User(player);
        }
        return null;
    }

    @Override
    public final void setBlock(int x, int y, int z, int type, int data, int dimension, String world) {
        Block block = server.getWorld(world).getBlockAt(x, y, z);
        block.setTypeId(type);
        block.setData((byte) data);
    }

    @Override
    public final Mod_Block getBlockAt(int x, int y, int z, int dimension, String world) {
        Block block = server.getWorld(world).getBlockAt(x, y, z);
        return new Bukkit_Block(block);
    }

    @Override
    public final Mod_Item constructItem(int type, int amount, int damage, String name, Mod_ItemEnchantment[] enchants, String[] lore) {
        ItemStack item = new ItemStack(type, amount, (short) damage);
        if (enchants != null) {
            for (Mod_ItemEnchantment ench : enchants) {
                item.addEnchantment(Enchantment.getById(ench.getId()), ench.getLevel());
            }
        }
        ItemMeta meta = server.getItemFactory().getItemMeta(Material.getMaterial(type));
        item.setItemMeta(meta);
        if (lore != null) {
            item.getItemMeta().setLore(Arrays.asList(new String[0]));
        }
        if (name != null) {
            item.getItemMeta().setDisplayName(name);
        }
        return new Bukkit_Item(item);
    }

    @Override
    public final Mod_ItemEnchantment constructEnchantment(int id, int level) {
        return new Bukkit_ItemEnchantment(Enchantment.getById(id), level);
    }

    @Override
    public final String getDefaultWorldName() {
        return server.getServerName();
    }

    @Override
    public final List<String> getAdminGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final String getDefaultGroupName() {
        return "NO_GROUP";
    }

    @Override
    public final int getHighestY(int x, int z, String world, int dimension) {
        return server.getWorld(world).getHighestBlockYAt(x, z);
    }

    @Override
    public boolean isCanaryClassic() {
        return false;
    }

    @Override
    public final boolean isCanary() {
        return false;
    }

    @Override
    public final boolean isBukkit() {
        return true;
    }

    @Override
    public Logger getLogger() {
        return system_logger;
    }

    @Override
    public SynchronizedTask addTaskToServer(Runnable runnable, long delay) {
        return new BukkitSyncRealmsTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(realms, runnable, delay * 20, delay * 20));
    }

    @Override
    public void removeTask(SynchronizedTask task) {
        Bukkit.getScheduler().cancelTask(((BukkitSyncRealmsTask) task).getTaskId());
    }
}
