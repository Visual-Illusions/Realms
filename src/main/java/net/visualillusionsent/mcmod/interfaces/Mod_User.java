/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
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
 */
package net.visualillusionsent.mcmod.interfaces;

/**
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * 
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public interface Mod_User extends Mod_Entity, Mod_Caller{

    boolean isInGroup(String group);

    int getHealth();

    int getMaxHealth();

    void heal(int amount);

    void causeDamage(int amount);

    boolean isDead();

    boolean isCreative();

    boolean isAdventure();

    boolean isDamageDisabled();

    boolean hasPermission(String perm);

    Mod_Item[] getInventoryContents();

    void setInventoryContents(Mod_Item[] items);

    void clearInventoryContents();

    <T> Object getPlayer();

    @Override
    String toString();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}
