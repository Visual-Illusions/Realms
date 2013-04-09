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
package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.realms;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsTranslate;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.commands.RealmsCommandHandler;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RLevel;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.logging.RealmsLogMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class Realms extends JavaPlugin{

    private RealmsBase base;

    @Override
    public final void onEnable(){
        base = new RealmsBase(new Bukkit_Server(getServer(), getLogger()));
        RealmsCommandHandler.initialize();
        RealmsTranslate.initialize();
        new Realms_BukkitListener(this);
    }

    @Override
    public final void onDisable(){
        HandlerList.unregisterAll(this);
        if (RealmsBase.isLoaded()) {
            base.terminate();
        }
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String mainCmd, String[] args){
        try {
            if (cmd.getName().equals("realms")) {
                Mod_Caller caller = null;
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
        catch (Exception ex) {
            RealmsLogMan.severe("An unexpected exception occured @ COMMAND...");
            RealmsLogMan.log(RLevel.STACKTRACE, "StackTrace: ", ex);
        }
        return false;
    }
}
