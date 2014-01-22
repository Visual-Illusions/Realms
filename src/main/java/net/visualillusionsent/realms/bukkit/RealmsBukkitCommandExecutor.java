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
package net.visualillusionsent.realms.bukkit;

import net.visualillusionsent.minecraft.plugin.bukkit.VisualIllusionsBukkitPluginInformationCommand;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Jason (darkdiplomat)
 */
public class RealmsBukkitCommandExecutor extends VisualIllusionsBukkitPluginInformationCommand {

    RealmsBukkitCommandExecutor(BukkitRealms plugin) {
        super(plugin);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String mainCmd, String[] args){
        try {
            if (cmd.getName().equals("realms")) {
                if(args[0].equals("info")){
                    this.sendInformation(sender);
                }
                else {
                    Mod_Caller caller;
                    if (sender instanceof Player) {
                        caller = new Bukkit_User((Player) sender);
                    }
                    else {
                        caller = new Bukkit_Console();
                    }
                    RealmsCommandHandler.parseRealmsCommand(caller, args.length > 0 ? args[0] : "INVALID", RealmsBase.commandAdjustment(args, 1));
                    return true;
                }
            }
        }
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occurred @ COMMAND...");
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
        return false;
    }
}
