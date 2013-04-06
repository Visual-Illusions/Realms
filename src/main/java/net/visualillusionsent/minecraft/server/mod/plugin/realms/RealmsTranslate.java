/* Copyright 2012 - 2013 Visual Illusions Entertainment.
 * This file is part of Realms.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * Source Code availible @ https://github.com/Visual-Illusions/Realms */
package net.visualillusionsent.minecraft.server.mod.plugin.realms;

import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.utils.LocaleHelper;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class RealmsTranslate extends LocaleHelper{

    private final static RealmsTranslate instance;
    static {
        instance = new RealmsTranslate();
    }

    private RealmsTranslate(){
        localeCodeOverride = RealmsBase.getProperties().getStringVal("lang.locale");
    }

    public final static String transMessage(String key){
        return colorize(instance.localeTranslate(key));
    }

    public final static String transformMessage(String key, Object... args){
        return colorize(instance.localeTranslateMessage(key, args));
    }

    private final static String colorize(String msg){
        return msg.replaceAll("\\$c", MCChatForm.MARKER.stringValue());
    }

    public static void initialize(){ // Just meant to help initialize the class so there isnt a delay later
        ;
    }
}
