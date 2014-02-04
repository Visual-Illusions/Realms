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
public class ZoneConstructException extends Exception {

    private static final long serialVersionUID = 642684322566161215L;

    public ZoneConstructException(String msg) {
        super(msg);
    }

    public ZoneConstructException(String msg, Exception e) {
        super(msg, e);
    }
}
