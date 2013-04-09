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
import java.util.ArrayList;
import java.util.List;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class CanaryClassic_Item implements net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item{

    private final Item item;

    public CanaryClassic_Item(Item item){
        this.item = item;
    }

    @Override
    public final int getId(){
        return item.getItemId();
    }

    @Override
    public final int getAmount(){
        return item.getAmount();
    }

    @Override
    public final int getDamage(){
        return item.getDamage();
    }

    @Override
    public final String getName(){
        return item.getName();
    }

    @Override
    public final Mod_ItemEnchantment[] getEnchantments(){
        if(item.getEnchantments() != null){
            List<Mod_ItemEnchantment> enchants = new ArrayList<Mod_ItemEnchantment>();
            for(Enchantment ench : item.getEnchantments()){
                enchants.add(new CanaryClassic_ItemEnchantment(ench));
            }
            return enchants.toArray(new Mod_ItemEnchantment[0]);
        }
        return null;
    }

    @Override
    public final String[] getLore(){
        return item.getLore();
    }

    @Override
    public final Item getBaseItem(){
        return item;
    }

    @Override
    public final boolean equals(Object obj){
        if(obj instanceof CanaryClassic_Item){
            return item.equals(((CanaryClassic_Item)obj).getBaseItem());
        }
        else if(obj instanceof Item){
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
        toRet.append(item.getName() != null ? item.getName() : "NO_NAME_FOR_THIS_ITEM");
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
