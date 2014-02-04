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
package net.visualillusionsent.realms.logging;

import java.util.logging.Level;

/**
 * @author Jason (darkdiplomat)
 */
public final class RLevel extends Level {

    private static final long serialVersionUID = 210434042012L;
    private static int baselvl = 6000;
    private static final String RD = "REALMS-DEBUG-";
    public static final RLevel //
            ANIMAL_DESTROY = new RLevel(RD.concat("ANIMAL_DESTROY"), genLevel()), //
            BLOCK_BREAK = new RLevel(RD.concat("BLOCK_BREAK"), genLevel()), //
            BLOCK_DESTROY = new RLevel(RD.concat("BLOCK_DESTROY"), genLevel()), //
            BLOCK_PHYSICS = new RLevel(RD.concat("BLOCK_PHYSICS"), genLevel()), //
            BLOCK_PLACE = new RLevel(RD.concat("BLOCK_PLACE"), genLevel()), //
            BLOCK_RIGHTCLICK = new RLevel(RD.concat("BLOCK_RIGHTCLICK"), genLevel()), //
            COMMAND = new RLevel(RD.concat("COMMAND"), genLevel()), //
            COMMAND_CHECK = new RLevel(RD.concat("COMMAND_CHECK"), genLevel()), //
            DAMAGE = new RLevel(RD.concat("DAMAGE"), genLevel()), //
            EAT = new RLevel(RD.concat("EAT"), genLevel()), //
            ENDERMAN = new RLevel(RD.concat("ENDERMAN"), genLevel()), //
            ENTITY_RIGHTCLICK = new RLevel(RD.concat("ENTITY_RIGHTCLICK"), genLevel()), //
            EXPLOSION = new RLevel(RD.concat("EXPLOSION"), genLevel()), //
            FLOW = new RLevel(RD.concat("FLOW"), genLevel()), //
            BURN = new RLevel(RD.concat("BURN"), genLevel()), //
            ITEM_DROP = new RLevel(RD.concat("ITEM_DROP"), genLevel()), //
            ITEM_PICKUP = new RLevel(RD.concat("ITEM_PICKUP"), genLevel()), //
            ITEM_USE = new RLevel(RD.concat("ITEM_USE"), genLevel()), //
            MOB_REMOVER = new RLevel(RD.concat("MOB_REMOVER"), genLevel()), //
            MOB_TARGET = new RLevel(RD.concat("MOB_TARGET"), genLevel()), //
            MOB_SPAWN = new RLevel(RD.concat("MOB_SPAWN"), genLevel()), //
            PISTONS = new RLevel(RD.concat("PISTONS"), genLevel()), //
            PORTAL_USE = new RLevel(RD.concat("PORTAL_USE"), genLevel()), //
            PLAYER_EXPLODE = new RLevel(RD.concat("PLAYER_EXPLODE"), genLevel()), //
            PLAYER_HEAL = new RLevel(RD.concat("PLAYER_HEAL"), genLevel()), //
            PLAYER_RESTRICT = new RLevel(RD.concat("PLAYER_RESTRICT"), genLevel()), //
            STARVATION = new RLevel(RD.concat("STARVATION"), genLevel()), //
            POTION_EFFECT = new RLevel(RD.concat("POTION_EFFECT"), genLevel()), //
            DISPENSE = new RLevel(RD.concat("DISPENSE"), genLevel()), //
            VEHICLE_MOVE = new RLevel(RD.concat("VEHICLE_MOVE"), genLevel()), //
            STACKTRACE = new RLevel(RD.concat("STACKTRACE"), genLevel()), //
            GENERAL = new RLevel(RD.concat("GENERAL"), genLevel());

    protected RLevel(String name, int intvalue) {
        super(name, intvalue);
    }

    private final static int genLevel() {
        ++baselvl;
        return baselvl;
    }
}
