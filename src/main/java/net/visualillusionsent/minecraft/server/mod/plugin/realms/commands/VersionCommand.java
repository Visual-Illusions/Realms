/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 */
package net.visualillusionsent.minecraft.server.mod.plugin.realms.commands;

import net.visualillusionsent.minecraft.server.mod.interfaces.MCChatForm;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Caller;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Checks the current version and if it's the latest", name = "version", usage = "")
final class VersionCommand extends RealmsCommand{

    private final String VER = MCChatForm.PURPLE.concat("----- ").concat(MCChatForm.BLUE.concat("Realms v%s by ").concat(MCChatForm.LIGHT_GREEN.concat("DarkDiplomat").concat(MCChatForm.PURPLE.concat(" -----")))), //
                    NOT_LATEST = MCChatForm.PURPLE.concat("----- ").concat(MCChatForm.LIGHT_GRAY.concat("Update Availible: v%s".concat(MCChatForm.PURPLE.concat(" -----")))), //
                    LATEST = MCChatForm.PURPLE.concat("----- ").concat(MCChatForm.BLUE.concat("Latest Version is Installed".concat(MCChatForm.PURPLE.concat(" -----"))));

    @Override
    final void execute(Mod_Caller caller, String[] args){
        caller.sendMessage(String.format(VER, RealmsBase.getVersion()));
        if(!RealmsBase.isLatest()){
            caller.sendMessage(String.format(NOT_LATEST, RealmsBase.getCurrent()));
        }
        else{
            caller.sendMessage(LATEST);
        }
    }
}
