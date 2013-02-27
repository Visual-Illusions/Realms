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
package net.visualillusionsent.mcplugin.realms.data;

import net.visualillusionsent.lang.DataSourceType;
import net.visualillusionsent.mcmod.interfaces.Mod_Item;
import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
interface DataSource{

    final Object lock = new Object();

    DataSourceType getType();

    void load();

    boolean reloadZone(Zone zone);

    boolean saveZone(Zone zone);

    boolean deleteZone(Zone zone);

    void loadInventories();

    boolean saveInventory(Mod_User user, Mod_Item[] items);

    boolean deleteInventory(Mod_User user);
}
