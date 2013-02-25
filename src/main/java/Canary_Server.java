/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.visualillusionsent.mcmod.interfaces.Mod_Block;
import net.visualillusionsent.mcmod.interfaces.Mod_Entity;
import net.visualillusionsent.mcmod.interfaces.Mod_Item;
import net.visualillusionsent.mcmod.interfaces.Mod_ItemEnchantment;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Canary_Server implements net.visualillusionsent.mcmod.interfaces.Mod_Server{

    private final Server server;

    public Canary_Server(Server server){
        this.server = server;
    }

    @Override
    public final List<Mod_Entity> getAnimals(){
        List<Mod_Entity> animals = new ArrayList<Mod_Entity>();
        Set<World[]> worlds = new HashSet<World[]>(server.getLoadedWorld());
        for(World[] world : worlds){
            World normal = world[0];
            World nether = world[1];
            World end = world[2];
            List<Mob> normAnimals = Collections.synchronizedList(normal.getAnimalList());
            List<Mob> nethAnimals = Collections.synchronizedList(nether.getAnimalList());
            List<Mob> endAnimals = Collections.synchronizedList(end.getAnimalList());
            synchronized(normAnimals){
                for(Mob animal : normAnimals){
                    animals.add(new Canary_Entity(animal));
                }
            }
            synchronized(nethAnimals){
                for(Mob animal : nethAnimals){
                    animals.add(new Canary_Entity(animal));
                }
            }
            synchronized(endAnimals){
                for(Mob animal : endAnimals){
                    animals.add(new Canary_Entity(animal));
                }
            }
        }
        return animals;
    }

    @Override
    public final List<Mod_Entity> getMobs(){
        List<Mod_Entity> mobs = new ArrayList<Mod_Entity>();
        Set<World[]> worlds = Collections.synchronizedSet(server.getLoadedWorld());
        synchronized(worlds){
            for(World[] world : worlds){
                World normal = world[0];
                World nether = world[1];
                World end = world[2];
                List<Mob> normMobs = Collections.synchronizedList(normal.getMobList());
                List<Mob> nethMobs = Collections.synchronizedList(nether.getMobList());
                List<Mob> endMobs = Collections.synchronizedList(end.getMobList());
                try{
                    synchronized(normMobs){
                        for(Mob mob : normMobs){
                            mobs.add(new Canary_Entity(mob));
                        }
                    }
                }
                catch(ConcurrentModificationException cme){
                    RealmsLogMan.log(RLevel.GENERAL, "ConcurrentModificationException while getting MobList for World: " + normal.getName() + " Dimension: 0. Skipping list...");
                }
                try{
                    synchronized(nethMobs){
                        for(Mob mob : nethMobs){
                            mobs.add(new Canary_Entity(mob));
                        }
                    }
                }
                catch(ConcurrentModificationException cme){
                    RealmsLogMan.log(RLevel.GENERAL, "ConcurrentModificationException while getting MobList for World: " + normal.getName() + " Dimension: -1. Skipping list...");
                }
                try{
                    synchronized(endMobs){
                        for(Mob mob : endMobs){
                            mobs.add(new Canary_Entity(mob));
                        }
                    }
                }
                catch(ConcurrentModificationException cme){
                    RealmsLogMan.log(RLevel.GENERAL, "ConcurrentModificationException while getting MobList for World: " + normal.getName() + " Dimension: 1. Skipping list...");
                }
            }
        }
        return mobs;
    }

    @Override
    public final List<Mod_User> getUsers(){
        Player[] current = etc.getServer().getPlayerList().toArray(new Player[0]);
        List<Mod_User> users = new ArrayList<Mod_User>();
        for(Player player : current){
            users.add(new Canary_User(player));
        }
        return users;
    }

    @Override
    public final Mod_User getUser(String name){
        Player player = server.getPlayer(name);
        if(player != null){
            return new Canary_User(player);
        }
        return null;
    }

    @Override
    public final void setBlock(int x, int y, int z, int type, int data, int dimension, String world){
        Block block = new Block(type, x, y, z, data);
        server.getWorld(world)[dimension].setBlock(block);
    }

    @Override
    public final Mod_Block getBlockAt(int x, int y, int z, int dimension, String world){
        Block block = etc.getServer().getWorld(world)[dimension].getBlockAt(x, y, z);
        return new Canary_Block(block);
    }

    @Override
    public final Mod_Item constructItem(int type, int amount, int damage, String name, Mod_ItemEnchantment[] enchs, String[] lore){
        Item item = new Item(type, amount, damage);
        if(enchs != null){
            for(Mod_ItemEnchantment ench : enchs){
                item.addEnchantment(ench.getId(), ench.getLevel());
            }
        }
        if(lore != null){
            item.setLore(lore);
        }
        if(name != null){
            item.setName(name);
        }
        return new Canary_Item(item);
    }

    @Override
    public final Mod_ItemEnchantment constructEnchantment(int id, int level){
        return new Canary_ItemEnchantment(new Enchantment(Enchantment.Type.fromId(id), level));
    }

    @Override
    public final String getDefaultWorldName(){
        return etc.getServer().getDefaultWorld().getName();
    }

    @Override
    public final List<String> getAdminGroups(){
        List<String> adminGroups = new ArrayList<String>();
        for(Group group : (List<Group>)etc.getDataSource().getGroupList()){
            if(group.Administrator){
                adminGroups.add(group.Name);
            }
        }
        return adminGroups;
    }

    public final String getDefaultGroupName(){
        return etc.getDataSource().getDefaultGroup().Name;
    }

    @Override
    public final int getHighestY(int x, int z, String world, int dimension){
        return etc.getServer().getWorld(world)[dimension].getHighestBlockY(x, z);
    }

    @Override
    public boolean isCanary(){
        return true;
    }

    @Override
    public boolean isBukkit(){
        return false;
    }
}
