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
package net.visualillusionsent.mcplugin.realms.zones.permission;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
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
    NULL;

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
