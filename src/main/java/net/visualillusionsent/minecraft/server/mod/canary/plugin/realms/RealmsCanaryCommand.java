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

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.CanaryCommand;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RLevel;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;

public final class RealmsCanaryCommand extends CanaryCommand{

    public RealmsCanaryCommand(){
        super("realms", "Realms base command. Use /realms help for sub command help.");
    }

    @Override
    protected void execute(MessageReceiver msgrec, String[] args){
        try {
            Mod_Caller caller = null;
            if (msgrec instanceof Player) {
                caller = new Canary_User((Player) msgrec);
            }
            else {
                caller = new Canary_Console();
            }
            RealmsCommandHandler.parseRealmsCommand(caller, args.length > 1 ? args[1] : "INVALID", RealmsBase.commandAdjustment(args, 2));
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ COMMAND...");
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
    }
}