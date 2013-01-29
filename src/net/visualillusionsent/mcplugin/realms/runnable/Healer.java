/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 */
package net.visualillusionsent.mcplugin.realms.runnable;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.mcmod.interfaces.Mod_User;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;
import net.visualillusionsent.mcplugin.realms.zones.ZoneLists;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Healer implements Runnable {
    private final String debug = "Healing User - Name: '%s' in World: '%s' Dimension: '%d' @ Location: X: '%.2f' Y: '%.2f' Z: '%.2f'";

    public Healer(RealmsBase base) {}

    @Override
    public final void run() {
        try {
            List<String> userList = ZoneLists.getInHealing();

            /* Check Player List */
            if (!userList.isEmpty()) {
                for (String userName : userList) {
                    Mod_User theUser = RealmsBase.getServer().getUser(userName);
                    if (theUser != null) {
                        theUser.heal(1); //Heal
                        RealmsLogMan.log(RLevel.PLAYER_HEAL, String.format(debug, userName, theUser.getWorld(), theUser.getDimension(), theUser.getX(), theUser.getY(), theUser.getZ())); //Debugging
                    }
                }
            }
        }
        catch (ConcurrentModificationException CME) {
            RealmsLogMan.log(RLevel.GENERAL, "Concurrent Modification Exception in Healer. (Don't worry Not a major issue)");
        }
        catch (Exception ex) {

        }
    }
}
