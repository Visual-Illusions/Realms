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
package net.visualillusionsent.realms.commands;

import net.visualillusionsent.minecraft.plugin.ChatFormat;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;

import java.util.Collection;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Displays Realms subcommand help", name = "help", usage = "[subname|page#|all]", maxParam = 1)
final class HelpCommand extends RealmsCommand {

    private final String HELP_TITLE = ChatFormat.BLUE + "Realms Subcommand Help Page " + ChatFormat.YELLOW + "%d" + ChatFormat.BLUE + " of " + ChatFormat.ORANGE + "%d";
    private final String HELP_FORM_1 = ChatFormat.CYAN.concat("/realms %s%s %s");
    private final String HELP_FORM_2 = ChatFormat.CYAN + "             \\- " + ChatFormat.YELLOW + "%s";

    @Override
    final void execute(Mod_Caller caller, String[] args) {
        if (args.length > 0) {
            RealmsCommand cmd = RealmsCommandHandler.getCommand(args[0]);
            if (cmd != null) {
                RCommand rcmd = cmd.getClass().getAnnotation(RCommand.class);
                caller.sendMessage(String.format(HELP_FORM_1, ChatFormat.ORANGE, rcmd.name(), rcmd.usage()));
                caller.sendMessage(String.format(HELP_FORM_2, rcmd.desc()));
            }
            else if (args[0].toLowerCase().equals("all")) {
                Collection<RealmsCommand> cmds = RealmsCommandHandler.getRealmsSubCommands();
                for (RealmsCommand command : cmds) {
                    RCommand rcmd = command.getClass().getAnnotation(RCommand.class);
                    caller.sendMessage(String.format(HELP_FORM_1, ChatFormat.ORANGE, rcmd.name(), rcmd.usage()));
                    caller.sendMessage(String.format(HELP_FORM_2, rcmd.desc()));
                }
            }
            else {
                try {
                    int show = Integer.parseInt(args[0]);
                    printHelpPage(caller, show);
                }
                catch (NumberFormatException nfe) {
                    caller.sendError("No Realms subcommand with the Name: ".concat(ChatFormat.ORANGE.toString()).concat(args[0]));
                }
            }
        }
        else {
            printHelpPage(caller, 1);
        }
    }

    private final void printHelpPage(Mod_Caller caller, int show) {
        RealmsCommand[] cmdarray = RealmsCommandHandler.getRealmsSubCommands().toArray(new RealmsCommand[0]);
        int total = (int) Math.ceil(cmdarray.length / 5.0F);
        if (total < 1) {
            total = 1;
        }
        if (show > total || show < 1) {
            show = 1;
        }
        int line = 5 * show;
        int start = 5 * show - 5;
        caller.sendMessage(String.format(HELP_TITLE, show, total));
        for (int index = start; index < line && index < cmdarray.length; index++) {
            RCommand rcmd = cmdarray[index].getClass().getAnnotation(RCommand.class);
            caller.sendMessage(String.format(HELP_FORM_1, ChatFormat.ORANGE, rcmd.name(), rcmd.usage()));
            caller.sendMessage(String.format(HELP_FORM_2, rcmd.desc()));
        }
    }
}
