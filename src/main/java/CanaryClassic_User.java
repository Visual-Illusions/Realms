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
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsTranslate;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class CanaryClassic_User extends CanaryClassic_Entity implements Mod_User{

    private final Player player;

    public CanaryClassic_User(Player player){
        super(player);
        this.player = player;
    }

    @Override
    public final void sendError(String transKey, Object... args){
        if (args == null) {
            player.notify(RealmsTranslate.transMessage(transKey));
        }
        else {
            player.notify(RealmsTranslate.transformMessage(transKey, args));
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
        player.applyDamage(DamageType.SUFFOCATION, amount);
    }

    @Override
    public final boolean isDead(){
        return player.isDead() || player.getHealth() <= 0;
    }

    @Override
    public final boolean isCreative(){
        return player.getCreativeMode() == 1;
    }

    @Override
    public final boolean isAdventure(){
        return player.getCreativeMode() == 2;
    }

    @Override
    public final boolean isDamageDisabled(){
        return player.isDamageDisabled();
    }

    @Override
    public final boolean isInGroup(String group){
        return player.isInGroup(group);
    }

    @Override
    public final boolean hasPermission(String perm){
        return player.canUseCommand(perm);
    }

    @Override
    public final Mod_Item[] getInventoryContents(){
        Item[] items = player.getInventory().getContents();
        Mod_Item[] its = new Mod_Item[40];
        for (int index = 0; index < 40; index++) {
            if (items[index] != null) {
                its[index] = new CanaryClassic_Item(items[index].clone());
            }
        }
        return its;
    }

    @Override
    public final void setInventoryContents(Mod_Item[] items){
        Item[] cIt = new Item[40];
        for (int index = 0; index < 40; index++) {
            cIt[index] = items[index] != null ? ((CanaryClassic_Item) items[index]).getBaseItem() : null;
        }
        player.getInventory().setContents(cIt);
    }

    @Override
    public final void clearInventoryContents(){
        player.getInventory().clearContents();
    }

    @Override
    public final boolean isConsole(){
        return false;
    }

    @Override
    public final boolean isBukkit(){
        return false;
    }

    @Override
    public final boolean isCanary(){
        return true;
    }

    @Override
    public final Player getPlayer(){
        return player;
    }

    @Override
    public final boolean equals(Object obj){
        if (obj instanceof CanaryClassic_User) {
            return ((CanaryClassic_User) obj).getPlayer().equals(player);
        }
        else if (obj instanceof Player) {
            return player.equals(obj);
        }
        return false;
    }

    @Override
    public final int hashCode(){
        return player.hashCode();
    }
}