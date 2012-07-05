import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.viutils.ICModServer;

/**
 * Realms v5.x Polygonal Hierarchy Area Ownership
 * <p>
 * Copyright (C) 2012 Visual Illusions Entertainment
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * @author Jason Jones - darkdiplomat@visualillusionsent.net
 * 
 */
public class Realms extends Plugin {
    private final ICModServer serv = new CModServer(etc.getServer());
    private RHandle rhandle = new RHandle(serv);
    private RealmsListener rl;
    private RealmsHooks rh;

    @Override
    public final void disable() {
        rhandle.terminate();
        if (rh != null) {
            etc.getLoader().removeCustomListener(rh.PermissionChange.getName());
            etc.getLoader().removeCustomListener(rh.PermissionCheck.getName());
            etc.getLoader().removeCustomListener(rh.ZoneChange.getName());
            etc.getLoader().removeCustomListener(rh.ZoneCheck.getName());
            etc.getLoader().removeCustomListener(rh.ZoneFlagCheck.getName());
        }

        etc.getInstance().removeCommand("/realms deletezone");
        etc.getInstance().removeCommand("/realms setgreeting");
        etc.getInstance().removeCommand("/realms setfarewell");
        etc.getInstance().removeCommand("/realms permission");
        etc.getInstance().removeCommand("/realms delete");
        etc.getInstance().removeCommand("/realms grant");
        etc.getInstance().removeCommand("/realms deny");
        etc.getInstance().removeCommand("/realms createzone");
        etc.getInstance().removeCommand("/realms pvp");
        etc.getInstance().removeCommand("/realms sanctuary");
        etc.getInstance().removeCommand("/realms creeper");
        etc.getInstance().removeCommand("/realms potion");
        etc.getInstance().removeCommand("/realms ghast");
        etc.getInstance().removeCommand("/realms fall");
        etc.getInstance().removeCommand("/realms suffocate");
        etc.getInstance().removeCommand("/realms fire");
        etc.getInstance().removeCommand("/realms animals");
        etc.getInstance().removeCommand("/realms physics");
        etc.getInstance().removeCommand("/realms creative");
        etc.getInstance().removeCommand("/realms pistons");
        etc.getInstance().removeCommand("/realms enderman");
        etc.getInstance().removeCommand("/realms flow");
        etc.getInstance().removeCommand("/realms spread");
        etc.getInstance().removeCommand("/realms healing");
        etc.getInstance().removeCommand("/realms tnt");
        etc.getInstance().removeCommand("/realms starve");
        etc.getInstance().removeCommand("/realms restricted");
        etc.getInstance().removeCommand("/realms combat");
        etc.getInstance().removeCommand("/realms environment");
        etc.getInstance().removeCommand("/realms version");
        etc.getInstance().removeCommand("/realms list");
        etc.getInstance().removeCommand("/realms reloadzone");
        rhandle.log(Level.INFO, "Realms v" + rhandle.getVersion() + " disabled.");
    }

    @Override
    public final void enable() {
        rhandle.log(Level.INFO, "Realms v" + rhandle.getVersion() + " by DarkDiplomat enabling...");
    }

    @Override
    public final void initialize() {
        if (rhandle.initialize()) {
            rhandle.log(Level.INFO, "Checking for latest version...");
            if (!rhandle.isLatest()) {
                if (!RealmsProps.getAutoUpdate()) {
                    rhandle.log(Level.INFO, "An update is availible! Current Version: " + rhandle.getCurrent());
                }
                else {
                    String result = rhandle.update();
                    if (result.equals("Update Successful")) {
                        rhandle.log(Level.INFO, result);
                        rhandle.log(Level.INFO, "Reloading Realms...");
                        etc.getLoader().reloadPlugin("Realms");
                        return;
                    }
                    else {
                        rhandle.log(Level.WARNING, result);
                    }
                }
            }

            //Regular Hooks
            rl = new RealmsListener(rhandle);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PHYSICS, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.COMMAND_CHECK, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.COMMAND, rl, this, PluginListener.Priority.MEDIUM);
            etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, rl, this, PluginListener.Priority.LOW);
            etc.getLoader().addListener(PluginLoader.Hook.EAT, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ENDERMAN_DROP, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ENDERMAN_PICKUP, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ENTITY_RIGHTCLICKED, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.EXPLOSION, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.FLOW, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.FOODEXHAUSTION_CHANGE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.IGNITE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ITEM_DROP, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ITEM_PICK_UP, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.MOB_SPAWN, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.MOB_TARGET, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PISTON_EXTEND, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PISTON_RETRACT, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PORTAL_USE, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.POTION_EFFECT, rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, rl, this, PluginListener.Priority.MEDIUM);

            //Custom Hooks
            rh = new RealmsHooks(this, rhandle);
            etc.getLoader().addCustomListener(rh.PermissionChange);
            etc.getLoader().addCustomListener(rh.PermissionCheck);
            etc.getLoader().addCustomListener(rh.ZoneChange);
            etc.getLoader().addCustomListener(rh.ZoneCheck);
            etc.getLoader().addCustomListener(rh.ZoneFlagCheck);

            //Add Commands
            etc.getInstance().addCommand("/realms deletezone", "<zone> - Deletes a zone");
            etc.getInstance().addCommand("/realms setgreeting", "<zone> [message] - Sets the zone's greeting");
            etc.getInstance().addCommand("/realms setfarewell", "<zone> [message] - Sets the zone's farewell");
            etc.getInstance().addCommand("/realms permission", "<zone> [page#] - Displays a list of permissions of a zone");
            etc.getInstance().addCommand("/realms delete", "<player> <perm> [zone] - deletes a permission");
            etc.getInstance().addCommand("/realms grant", "<player> <perm> <zone> [override] - grants a player permission");
            etc.getInstance().addCommand("/realms deny", "<player> <perm> <zone> [override] - deny's a player permission");
            etc.getInstance().addCommand("/realms createzone", "<zone> [parent] - creates a zone");
            etc.getInstance().addCommand("/realms pvp", "[zone] <on|off|inherit> - sets the PVP setting of a zone");
            etc.getInstance().addCommand("/realms sanctuary", "[zone] <on|off|inherit> - sets the SANCTUARY setting of a zone");
            etc.getInstance().addCommand("/realms creeper", "[zone] <on|off|inherit> - sets the CREEPER setting of a zone");
            etc.getInstance().addCommand("/realms potion", "[zone] <on|off|inherit> - sets the POTION setting of a zone");
            etc.getInstance().addCommand("/realms ghast", "[zone] <on|off|inherit> - sets the GHAST setting of a zone");
            etc.getInstance().addCommand("/realms fall", "[zone] <on|off|inherit> - sets the FALL setting of a zone");
            etc.getInstance().addCommand("/realms suffocate", "[zone] <on|off|inherit> - sets the SUFFOCATE setting of a zone");
            etc.getInstance().addCommand("/realms fire", "[zone] <on|off|inherit> - sets the FIRE setting of a zone");
            etc.getInstance().addCommand("/realms animals", "[zone] <on|off|inherit> - sets the ANIMALS setting of a zone");
            etc.getInstance().addCommand("/realms physics", "[zone] <on|off|inherit> - sets the PHYSICS setting of a zone");
            etc.getInstance().addCommand("/realms creative", "[zone] <on|off|inherit> - sets the CREATIVE setting of a zone");
            etc.getInstance().addCommand("/realms pistons", "[zone] <on|off|inherit> - sets the PISTONS setting of a zone");
            etc.getInstance().addCommand("/realms enderman", "[zone] <on|off|inherit> - sets the ENDERMAN setting of a zone");
            etc.getInstance().addCommand("/realms flow", "[zone] <on|off|inherit> - sets the FLOW setting of a zone");
            etc.getInstance().addCommand("/realms spread", "[zone] <on|off|inherit> - sets the SPREAD setting of a zone");
            etc.getInstance().addCommand("/realms healing", "[zone] <on|off|inherit> - sets the HEALING setting of a zone");
            etc.getInstance().addCommand("/realms tnt", "[zone] <on|off|inherit> - sets the TNT zone flag");
            etc.getInstance().addCommand("/realms starve", "[zone] <on|off|inherit> - sets the STARVE setting of a zone");
            etc.getInstance().addCommand("/realms restricted", "[zone] <on|off|inherit> - sets the RESTRICTED setting of a zone");
            etc.getInstance().addCommand("/realms combat", "<zone> - ");
            etc.getInstance().addCommand("/realms environment", "<zone> -");
            etc.getInstance().addCommand("/realms version", "- displays Realms version");
            etc.getInstance().addCommand("/realms list", "[page#] - displays a list of zones");
            etc.getInstance().addCommand("/realms reloadzone", "<zone> - Reloads a zone");

        }
        else {
            etc.getLoader().disablePlugin("Realms");
        }
    }
}
