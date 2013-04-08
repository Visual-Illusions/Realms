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
package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.realms;

import java.util.ArrayList;
import java.util.List;
import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsTranslate;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Bukkit_User extends Bukkit_Entity implements Mod_User{

    private final Player player;
    private final net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item[] itemArray = new net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item[] {};

    public Bukkit_User(Player player){
        super(player);
        this.player = player;
    }

    @Override
    public final String getName(){
        return player.getName();
    }

    @Override
    public final void destroy(){}

    @Override
    public final void sendError(String transKey, Object... args){
        if (args == null) {
            player.sendMessage(MCChatForm.LIGHT_RED.concat(RealmsTranslate.transMessage(transKey)));
        }
        else {
            player.sendMessage(MCChatForm.LIGHT_RED.concat(RealmsTranslate.transformMessage(transKey, args)));
        }
    }

    @Override
    public final void sendMessage(String transKey, Object... args){
        if (args == null) {
            player.sendMessage(RealmsTranslate.transMessage(transKey));
        }
        else {
            player.sendMessage(RealmsTranslate.transformMessage(transKey, args));
        }
    }

    @Override
    public final boolean isInGroup(String group){
        if (group.equals("NO_GROUP")) {
            return true;
        }
        return false;
    }

    @Override
    public final int getHealth(){
        return player.getHealth();
    }

    @Override
    public final int getMaxHealth(){
        return player.getMaxHealth();
    }

    @Override
    public final void heal(int amount){
        int newHealth = getHealth() + amount;
        if (newHealth > player.getMaxHealth()) {
            newHealth = player.getMaxHealth();
        }
        player.setHealth(newHealth);
    }

    @Override
    public final void causeDamage(int amount){
        player.damage(amount);
    }

    @Override
    public final boolean isDead(){
        return player.isDead() || player.getHealth() <= 0;
    }

    @Override
    public final boolean isCreative(){
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public final boolean isAdventure(){
        return player.getGameMode() == GameMode.ADVENTURE;
    }

    @Override
    public final boolean isDamageDisabled(){
        return false;
    }

    @Override
    public final boolean hasPermission(String perm){
        return player.hasPermission(perm);
    }

    @Override
    public final Mod_Item[] getInventoryContents(){
        List<Mod_Item> items = new ArrayList<Mod_Item>();
        for (ItemStack item : player.getInventory().getContents()) {
            items.add(new Bukkit_Item(item));
        }
        return items.toArray(itemArray);
    }

    @Override
    public final void setInventoryContents(Mod_Item[] items){
        for (Mod_Item item : items) {
            player.getInventory().addItem(((Bukkit_Item) item).getBaseItem());
        }
    }

    @Override
    public final void clearInventoryContents(){
        player.getInventory().clear();
    }

    @Override
    public final boolean isConsole(){
        return false;
    }

    @Override
    public final boolean isBukkit(){
        return true;
    }

    @Override
    public final boolean isCanary(){
        return false;
    }

    @Override
    public final Player getPlayer(){
        return player;
    }

    @Override
    public final int hashCode(){
        return player.hashCode();
    }

    @Override
    public final boolean equals(Object obj){
        if (obj instanceof Bukkit_User) {
            return player.equals(((Bukkit_User) obj).getPlayer());
        }
        else if (obj instanceof Player) {
            return player.equals((Player) obj);
        }
        return false;
    }
}
