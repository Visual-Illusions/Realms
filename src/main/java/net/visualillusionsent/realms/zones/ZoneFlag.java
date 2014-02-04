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
package net.visualillusionsent.realms.zones;

/**
 * @author Jason (darkdiplomat)
 */
public enum ZoneFlag {
    ON(true), //
    OFF(false), //
    INHERIT(false);

    private boolean value;

    private ZoneFlag(boolean value) {
        this.value = value;
    }

    public final boolean getValue() {
        return value;
    }

    public final String toString() {
        return this.name().toLowerCase();
    }

    public final boolean isInherit() {
        return this == INHERIT;
    }

    public final boolean isOn() {
        return this == ON;
    }

    public final boolean isOff() {
        return this == OFF;
    }

    public static final ZoneFlag getZoneFlag(String type) throws InvaildZoneFlagException {
        try {
            return ZoneFlag.valueOf(type.toUpperCase());
        }
        catch (IllegalArgumentException IAE) {
            throw new InvaildZoneFlagException(IAE);
        }
    }
}
