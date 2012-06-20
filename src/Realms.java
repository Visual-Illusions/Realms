import java.util.logging.Level;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.viutils.ICModServer;

/**
 * Realms v5.x  Polygonal Hierarchy Area Ownership
 * <p>
 * Copyright (C) 2012 Visual Illusions Entertainment
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * @author Jason Jones - darkdiplomat@visualillusionsent.net
 * 
 */
public class Realms extends Plugin{
    private final ICModServer serv = new CModServer(etc.getServer());
    private RHandle rhandle = new RHandle(serv);
    private RealmsListener rl;
    private RealmsHooks rh;
    
    @Override
    public void disable() {
        rhandle.terminate();
        if(rh != null){
            etc.getLoader().removeCustomListener(rh.PermissionChange.getName());
            etc.getLoader().removeCustomListener(rh.PermissionCheck.getName());
            etc.getLoader().removeCustomListener(rh.ZoneChange.getName());
            etc.getLoader().removeCustomListener(rh.ZoneCheck.getName());
            etc.getLoader().removeCustomListener(rh.ZoneFlagCheck.getName());
        }
        rhandle.log(Level.INFO, "Realms v"+rhandle.getVersion()+" disabled.");
    }

    @Override
    public void enable() {
        rhandle.log(Level.INFO, "Realms v"+rhandle.getVersion()+" by DarkDiplomat enabling...");
    }
    
    @Override
    public void initialize(){
        if(rhandle.initialize()){
            if(!rhandle.isLatest()){
                if(!RealmsProps.getAutoUpdate()){
                    rhandle.log(Level.INFO, "An update is availible! Current Version: "+rhandle.getCurrent());
                }
                else{
                    String result = rhandle.update();
                    if(result.equals("Update Successful")){
                        rhandle.log(Level.INFO, result);
                        rhandle.log(Level.INFO, "Reloading Realms...");
                        etc.getLoader().reloadPlugin("Realms");
                        return;
                    }
                    else{
                        rhandle.log(Level.WARNING, result);
                    }
                }
            }
            //Regular Hooks
            rl = new RealmsListener(rhandle);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN,             rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED,          rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PHYSICS,            rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE,              rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED,       rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.COMMAND_CHECK,            rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.COMMAND,                  rl, this, PluginListener.Priority.MEDIUM);
            etc.getLoader().addListener(PluginLoader.Hook.DAMAGE,                   rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT,               rl, this, PluginListener.Priority.LOW);
            etc.getLoader().addListener(PluginLoader.Hook.EAT,                      rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ENDERMAN_DROP,            rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ENDERMAN_PICKUP,          rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ENTITY_RIGHTCLICKED,      rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.EXPLOSION,                rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.FLOW,                     rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.FOODEXHAUSTION_CHANGE,    rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.IGNITE,                   rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ITEM_DROP,                rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ITEM_PICK_UP,             rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE,                 rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.MOB_SPAWN,                rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.MOB_TARGET,               rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PISTON_EXTEND,            rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PISTON_RETRACT,           rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE,              rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.PORTAL_USE,               rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.POTION_EFFECT,            rl, this, PluginListener.Priority.HIGH);
            etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND,            rl, this, PluginListener.Priority.MEDIUM);
            
            //Custom Hooks
            rh = new RealmsHooks(this, rhandle);
            etc.getLoader().addCustomListener(rh.PermissionChange);
            etc.getLoader().addCustomListener(rh.PermissionCheck);
            etc.getLoader().addCustomListener(rh.ZoneChange);
            etc.getLoader().addCustomListener(rh.ZoneCheck);
            etc.getLoader().addCustomListener(rh.ZoneFlagCheck);
            
        }
        else{
            etc.getLoader().disablePlugin("Realms");
        }
    }
}
