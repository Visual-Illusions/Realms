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
package net.visualillusionsent.realms.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_User;
import net.visualillusionsent.realms.logging.RLevel;
import net.visualillusionsent.realms.logging.RealmsLogMan;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public class RealmsCommandHandler{

    private final static LinkedHashMap<String, RealmsCommand> commands = new LinkedHashMap<String, RealmsCommand>();
    static {
        new ConfigReloadCommand();
        new CreateZoneCommand();
        new DeletePermissionCommand();
        new DeleteZoneCommand();
        new DenyPermissionCommand();
        new GiveMePermissionCommand();
        new GrantPermissionCommand();
        new HelpCommand();
        new PermissionsListCommand();
        new ReloadZoneCommand();
        new SetAdventureFlagCommand();
        new SetAnimalsFlagCommand();
        new SetBurnFlagCommand();
        new SetCreativeFlagCommand();
        new SetDispenseFlagCommand();
        new SetEndermanFlagCommand();
        new SetExplodeFlagCommand();
        new SetFallFlagCommand();
        new SetFarewellCommand();
        new SetFireFlagCommand();
        new SetFlowFlagCommand();
        new SetGreetingCommand();
        new SetHealingFlagCommand();
        new SetPhysicsFlagCommand();
        new SetPistonsFlagCommand();
        new SetPotionFlagCommand();
        new SetPVPFlagCommand();
        new SetRestrictedFlagCommand();
        new SetSanctuaryFlagCommand();
        new SetStarveFlagCommand();
        new SetSuffocateFlagCommand();
        new VersionCommand();
        new WandCommand();
        new ZoneListCommand();
    }

    private RealmsCommandHandler(){}

    static RealmsCommand getCommand(String cmd){
        return commands.get(cmd);
    }

    static Collection<RealmsCommand> getRealmsSubCommands(){
        return Collections.unmodifiableCollection(commands.values());
    }

    static void register(RealmsCommand cmd){
        if (cmd != null) {
            try {
                RCommand rcmd = cmd.getClass().getAnnotation(RCommand.class);
                if (!commands.containsValue(cmd)) {
                    commands.put(rcmd.name(), cmd);
                    RealmsLogMan.log(RLevel.GENERAL, "Registered subcommand: ".concat(rcmd.name()));
                }
                else {
                    RealmsLogMan.log(RLevel.GENERAL, "Failed to register SubCommand: ".concat(rcmd.name()));
                }
            }
            catch (NullPointerException npe) {
                RealmsLogMan.log(RLevel.GENERAL, "Realms Command was missing it's annotation: ".concat(cmd.getClass().getName()), npe);
            }
        }
    }

    private static String getRealmsCommandsList(){
        StringBuilder builder = new StringBuilder();
        for (String rc : commands.keySet()) {
            builder.append(MCChatForm.BLUE);
            builder.append(rc);
            builder.append(" ");
        }
        return builder.toString();
    }

    public static final void parseRealmsCommand(Mod_Caller caller, String command, String[] args){
        RealmsCommand cmd = commands.get(command);
        if (cmd != null) {
            RCommand rcmd = cmd.getClass().getAnnotation(RCommand.class);
            if (rcmd.noConsole() && caller.isConsole()) {
                caller.sendError("no.console");
                return;
            }
            else if (rcmd.bukkitOnly() && !caller.isBukkit()) {
                caller.sendError("no.bukkit");
                return;
            }
            else if (rcmd.canaryOnly() && !caller.isCanary()) {
                caller.sendError("no.canary");
                return;
            }
            else if (rcmd.adminReq() && !caller.isConsole() && !((Mod_User) caller).hasPermission("realms.admin")) {
                caller.sendError("realms.admin.fail");
                return;
            }
            cmd.parseCommand(caller, command, args);
            return;
        }
        caller.sendError("cmd.unknown");
        caller.sendError("cmd.specify", getRealmsCommandsList());
    }

    public static void initialize(){
        ;
    }
}
