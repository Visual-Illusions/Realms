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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * @author Jason (darkdiplomat)
 */
public final class Bukkit_Item implements Mod_Item{

    private final ItemStack item;

    public Bukkit_Item(ItemStack item){
        this.item = item.clone();
    }

    @Override
    public final int getId(){
        return item.getTypeId();
    }

    @Override
    public final int getAmount(){
        return item.getAmount();
    }

    @Override
    public final int getDamage(){
        return item.getData().getData();
    }

    @Override
    public final String getName(){
        if(item.getItemMeta() != null){
            if(item.getItemMeta().getDisplayName() != null){
                return item.getItemMeta().getDisplayName();
            }
        }
        return null;
    }

    @Override
    public final Mod_ItemEnchantment[] getEnchantments(){
        if(item.getEnchantments() != null){
            List<Mod_ItemEnchantment> toRet = new LinkedList<Mod_ItemEnchantment>();
            HashMap<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>(item.getEnchantments());
            if(!enchs.isEmpty()){
                for(Enchantment ench : enchs.keySet()){
                    toRet.add(new Bukkit_ItemEnchantment(ench, enchs.get(ench).intValue()));
                }
                return toRet.toArray(new Mod_ItemEnchantment[0]);
            }
        }
        return null;
    }

    @Override
    public final String[] getLore(){
        if(item.getItemMeta() != null){
            if(item.getItemMeta().getLore() != null && !item.getItemMeta().getLore().isEmpty()){
                return item.getItemMeta().getLore().toArray(new String[0]);
            }
        }
        return null;
    }

    @Override
    public ItemStack getBaseItem(){
        return item;
    }

    @Override
    public final boolean equals(Object obj){
        if(obj instanceof Bukkit_Item){
            return item.equals(((Bukkit_Item)obj).getBaseItem());
        }
        else if(obj instanceof ItemStack){
            return item.equals(obj);
        }
        return false;
    }

    @Override
    public final int hashCode(){
        return item.hashCode();
    }

    @Override
    public final String toString(){
        StringBuilder toRet = new StringBuilder();
        toRet.append(getId());
        toRet.append(',');
        toRet.append(getAmount());
        toRet.append(',');
        toRet.append(getDamage());
        toRet.append(',');
        toRet.append(getName() != null ? getName() : "NO_NAME_FOR_THIS_ITEM");
        if(getEnchantments() != null){
            for(Mod_ItemEnchantment ench : getEnchantments()){
                toRet.append(',');
                toRet.append(ench.getId());
                toRet.append(':');
                toRet.append(ench.getLevel());
            }
        }
        if(getLore() != null){
            for(String lore : getLore()){
                toRet.append(',');
                toRet.append(lore);
            }
        }
        return toRet.toString();
    }
}
