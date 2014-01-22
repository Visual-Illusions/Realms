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
package net.visualillusionsent.realms.commands;

import net.visualillusionsent.minecraft.plugin.ChatFormat;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;

/**
 * @author Jason (darkdiplomat)
 */
abstract class RealmsCommand{

    private final String USAGE = ChatFormat.CYAN.concat("Usage: /realms %s %s");

    protected RealmsCommand(){
        RealmsCommandHandler.register(this);
    }

    abstract void execute(Mod_Caller caller, String[] args);

    /**
     * Parses the command
     * 
     * @param caller
     *            - caller of the command
     * @param command
     *            - the Realms command to execute
     * @param args
     *            - the command arguments
     * @return true if executed
     */
    public final boolean parseCommand(Mod_Caller caller, String command, String[] args){
        if(args.length < getClass().getAnnotation(RCommand.class).minParam() || (args.length > getClass().getAnnotation(RCommand.class).maxParam())){
            onBadSyntax(caller, command);
            return false;
        }
        execute(caller, args);
        return true;
    }

    public final void onBadSyntax(Mod_Caller caller, String command){
        caller.sendError(String.format(USAGE, command, getClass().getAnnotation(RCommand.class).usage()));
    }
}
