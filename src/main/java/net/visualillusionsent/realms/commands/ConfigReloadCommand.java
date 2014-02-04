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
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.utils.UtilityException;

/**
 * @author Jason (darkdiplomat)
 */
@RCommand(desc = "Reloads the Realms Configuration file", name = "configreload", usage = "", adminReq = true)
final class ConfigReloadCommand extends RealmsCommand {

    @Override
    void execute(Mod_Caller caller, String[] args) {
        try {
            RealmsBase.getProperties().reload();
            caller.sendMessage(ChatFormat.LIGHT_GREEN.concat("Realms Configuration reloaded."));
        }
        catch (UtilityException ue) {
            caller.sendError("Exception while reloading config: ".concat(ue.getMessage()));
        }
    }
}
