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
package net.visualillusionsent.minecraft.server.mod.canary.plugin.realms;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.lang.InitializationError;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Realms extends Plugin{
    private RealmsBase base;

    @Override
    public void disable(){
        if (RealmsBase.isLoaded()) {
            base.terminate();
        }
    }

    @Override
    public boolean enable(){
        try {
            base = new RealmsBase(new Canary_Server(this, Canary.getServer(), this.getLogman()));
        }
        catch (InitializationError interr) {
            RealmsLogMan.stacktrace(interr);
            return false;
        }
        RealmsCommandHandler.initialize();
        new Realms_CanaryHookHandler(this);
        Canary.commands().registerCommand("realms", new RealmsCanaryCommand(), this, false);
        return true;
    }
}
