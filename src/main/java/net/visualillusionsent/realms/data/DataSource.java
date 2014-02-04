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
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms.data;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.lang.DataSourceType;
import net.visualillusionsent.realms.zones.Zone;

/**
 * @author Jason (darkdiplomat)
 */
interface DataSource {

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
