package net.visualillusionsent.mcplugin.realms.commands;

import net.visualillusionsent.mcmod.interfaces.MCChatForm;
import net.visualillusionsent.mcmod.interfaces.Mod_Caller;
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.utils.UtilityException;

@RCommand(desc = "Reloads the Realms Configuration file", name = "configreload", usage = "", adminReq = true)
public final class ConfigReloadCommand extends RealmsCommand{

    @Override
    void execute(Mod_Caller caller, String[] args){
        try{
            RealmsBase.getProperties().reload();
            caller.sendMessage(MCChatForm.LIGHT_GREEN.concat("Realms Configuration reloaded."));
        }
        catch(UtilityException ue){
            caller.sendError("Exception while reloading config: ".concat(ue.getMessage()));
        }
    }
}
