package net.visualillusionsent.minecraft.server.mod.canary.plugin.realms;

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
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsTranslate;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Canary_Console implements Mod_Caller{

    public Canary_Console(){}

    @Override
    public final void sendError(String transKey, Object... args){
        if (args == null) {
            System.out.println("[ERROR] ".concat(RealmsTranslate.transMessage(transKey)));
        }
        else {
            System.out.println("[ERROR]".concat(RealmsTranslate.transformMessage(transKey, args)));
        }
    }

    @Override
    public final void sendMessage(String transKey, Object... args){
        if (args == null) {
            System.out.println(RealmsTranslate.transMessage(transKey));
        }
        else {
            System.out.println(RealmsTranslate.transformMessage(transKey, args));
        }
    }

    @Override
    public final boolean isConsole(){
        return true;
    }

    @Override
    public final boolean isBukkit(){
        return false;
    }

    @Override
    public final boolean isCanary(){
        return true;
    }
}
