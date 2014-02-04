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
package net.visualillusionsent.realms;

import net.visualillusionsent.minecraft.plugin.ChatFormat;
import net.visualillusionsent.utils.LocaleHelper;

/**
 * @author Jason (darkdiplomat)
 */
public final class RealmsTranslate extends LocaleHelper {

    private static final RealmsTranslate instance;

    static {
        instance = new RealmsTranslate();
    }

    private RealmsTranslate() {
        super(false, null, RealmsBase.getProperties().getStringVal("lang.locale"));
    }

    public final static String transMessage(String key) {
        return colorize(instance.systemTranslate(key));
    }

    public final static String transformMessage(String key, Object... args) {
        return colorize(instance.systemTranslate(key, args));
    }

    private final static String colorize(String msg) {
        return msg.replaceAll("~", ChatFormat.MARKER.stringValue());
    }

    public static void initialize() { // Just meant to help initialize the class so there isnt a delay later
    }
}
