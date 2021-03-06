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
package net.visualillusionsent.realms.zones.permission;

/**
 * @author Jason (darkdiplomat)
 */
public enum PermissionType {
    ALL, //
    AUTHED, //
    COMBAT, //
    COMMAND, //
    CREATE, //
    DELEGATE, //
    DESTROY, //
    EAT, //
    ENTER, //
    ENVIRONMENT, //
    IGNITE, //
    INTERACT, //
    MESSAGE, //
    TELEPORT, //
    ZONING, //
    ;

    @Override
    public final String toString() {
        return this.name().toLowerCase();
    }

    public static final PermissionType getTypeFromString(String myType) throws InvaildPermissionTypeException {
        PermissionType rValue = null;
        try {
            rValue = PermissionType.valueOf(myType.toUpperCase());
        }
        catch (IllegalArgumentException IAE) {
            throw new InvaildPermissionTypeException(myType);
        }
        return rValue;
    }
}
