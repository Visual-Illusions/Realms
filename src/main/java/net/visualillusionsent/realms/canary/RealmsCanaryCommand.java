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
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.realms.canary;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;

/**
 * @author Jason (darkdiplomat)
 */
public final class RealmsCanaryCommand implements CommandListener{

    @Command(aliases = { "realms" },
            description = "Realms base command. Use /realms help for sub command help.",
            permissions = { "realms" },
            toolTip = "/realms <subcommand> <subargs>")
    public void realmsExecute(MessageReceiver msgrec, String[] args){
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
