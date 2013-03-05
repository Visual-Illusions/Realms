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
package net.visualillusionsent.minecraft.server.mod.plugin.realms.data;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Item;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.zones.Zone;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
final class DataSourceActionContainer{

    private final OutputAction action;
    private final Zone zone;
    private final Mod_User user;
    private final Mod_Item[] items;

    public DataSourceActionContainer(OutputAction action, Zone zone, Mod_User user, Mod_Item[] items){
        this.action = action;
        this.zone = zone;
        this.user = user;
        this.items = items;
    }

    public final OutputAction getAction(){
        return action;
    }

    public final Zone getZone(){
        return zone;
    }

    public final Mod_User getUser(){
        return user;
    }

    public final Mod_Item[] getItems(){
        return items;
    }
}
