/* 
 * Copyright 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Visual Illusions Minecraft Mod Interface Library (VI-MCMIL).
 *
 * VI-MCMIL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VI-MCMIL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with VI-MCMIL.
 * If not, see http://www.gnu.org/licenses/gpl.html
 */
package net.visualillusionsent.mcmod.interfaces;

import java.util.List;

/**
 * This file is part of VI-MCMIL.
 * Copyright 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * 
 * @author Jason (darkdiplomat)
 */
public interface Mod_Server {

    List<Mod_Entity> getAnimals();

    List<Mod_Entity> getMobs();

    List<Mod_User> getUsers();

    Mod_User getUser(String name);

    void setBlock(int x, int y, int z, int type, int data, int dimension, String world);

    Mod_Block getBlockAt(int x, int y, int z, int dimension, String world);

    Mod_Item constructItem(int type, int amount, int damage, String name, Mod_ItemEnchantment[] enchs, String[] lore);

    Mod_ItemEnchantment constructEnchantment(int id, int level);

    String getDefaultWorldName();

    List<String> getAdminGroups();

    String getDefaultGroupName();

    boolean isCanary();

    boolean isBukkit();

}
