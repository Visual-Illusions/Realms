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
package net.visualillusionsent.realms.canary;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;
import net.visualillusionsent.realms.Realms;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.realms.lang.InitializationError;
import net.visualillusionsent.realms.logging.RealmsLogMan;

/**
 * @author Jason (darkdiplomat)
 */
public final class CanaryRealms extends VisualIllusionsCanaryPlugin implements Realms {
    private RealmsBase base;

    @Override
    public void disable() {
        if (RealmsBase.isLoaded()) {
            base.terminate();
        }
    }

    @Override
    public boolean enable() {
        super.enable();

        try {
            base = new RealmsBase(this, new Canary_Server(this, Canary.getServer(), logger));
        }
        catch (InitializationError interr) {
            RealmsLogMan.stacktrace(interr);
            return false;
        }
        RealmsCommandHandler.initialize();
        new Realms_CanaryHookHandler(this);
        try {
            Canary.commands().registerCommands(new RealmsCanaryCommand(this), this, false);
        }
        catch (CommandDependencyException ex) {
            RealmsLogMan.stacktrace(ex);
            return false;
        }
        return true;
    }
}
