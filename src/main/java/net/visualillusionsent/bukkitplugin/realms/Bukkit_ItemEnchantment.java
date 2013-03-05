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

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;

import org.bukkit.enchantments.Enchantment;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Bukkit_ItemEnchantment implements Mod_ItemEnchantment{

    private final Enchantment enchantment;
    private final int level;

    public Bukkit_ItemEnchantment(Enchantment enchantment, int level){
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public final int getId(){
        return enchantment.getId();
    }

    @Override
    public final int getLevel(){
        return level;
    }

    @Override
    public final Enchantment getBaseEnchantment(){
        return enchantment;
    }

    @Override
    public final boolean equals(Object obj){
        if(obj instanceof Bukkit_ItemEnchantment){
            Bukkit_ItemEnchantment other = (Bukkit_ItemEnchantment)obj;
            if(other.getBaseEnchantment() == this.enchantment && this.level == other.getLevel()){
                return true;
            }
        }
        return false;
    }

    @Override
    public final int hashCode(){
        int hash = 7;
        hash = hash * level + enchantment.hashCode();
        hash = hash * level + level;
        return hash;
    }
}
