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
package net.visualillusionsent.realms.tasks;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.realms.zones.permission.PermissionType;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * @author Jason (darkdiplomat)
 */
public final class RestrictionDamager implements Runnable {

    private final String debug = "Player - Name: '%s' @ Location: X: '%.2f' Y: '%.2f' Z: '%.2f' World: '%s' Dimension: '%d'";

    public RestrictionDamager(RealmsBase base) {
    }

    @Override
    public final void run() {
        try {
            List<String> restricted = ZoneLists.getInRestricted();
            /* Check Player List */
            if (!restricted.isEmpty()) {
                synchronized (restricted) {
                    for (String userName : restricted) {
                        Mod_User theUser = RealmsBase.getServer().getUser(userName);
                        if (theUser != null) {
                            Zone zone = ZoneLists.getInZone(theUser);
                            //Double Checks for Player can be hurt by the zone
                            if (!zone.permissionCheck(theUser, PermissionType.AUTHED) && !theUser.isCreative() && !theUser.isDamageDisabled() && zone.getRestricted()) {
                                //check that we can kill the player
                                if (!RealmsBase.getProperties().getBooleanVal("restrict.kill") && (theUser.getHealth() - 1 <= 0)) {
                                    continue;
                                }
                                //Hurt Player
                                theUser.causeDamage(1);
                                //Debugging message
                                RealmsLogMan.log(RLevel.PLAYER_EXPLODE, String.format(debug, theUser.getName(), theUser.getX(), theUser.getY(), theUser.getZ(), theUser.getWorld(), theUser.getDimension()));
                            }
                        }
                    }
                }
            }
        }
        catch (ConcurrentModificationException CME) {
            RealmsLogMan.log(RLevel.GENERAL, "Concurrent Modification Exception in RestrictionDamager thread. (Non-Issue)");
        }
        catch (Exception ex) {
            RealmsLogMan.log(RLevel.GENERAL, "Unhandled Exception occured in in RestrictionDamager thread. (Non-Issue)");
        }
    }
}
