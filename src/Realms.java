import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.viutils.ICModServer;

/**
 * Realms v6.x  Polygonal Hierarchy Area Ownership
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
 * @author darkdiplomat - darkdiplomat@visualillusionsent.net
 * 
 */
public class Realms extends Plugin{
    private final ICModServer serv = new CModServer(etc.getServer());
    private RHandle rhandle = new RHandle(serv);
    private RealmsListener rl;
    
    @Override
    public void disable() {
    }

    @Override
    public void enable() {
    }
    
    @Override
    public void initialize(){
        if(rhandle.initialize()){
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
            
            //CustomHooks
        }
        else{
            etc.getLoader().disablePlugin("Realms");
        }
    }
}
