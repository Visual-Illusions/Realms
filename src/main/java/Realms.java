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
import net.visualillusionsent.mcplugin.realms.RealmsBase;
import net.visualillusionsent.mcplugin.realms.RealmsTranslate;
import net.visualillusionsent.mcplugin.realms.commands.RealmsCommandHandler;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Realms extends Plugin{

    private RealmsBase base;

    @Override
    public final void enable(){
        base = new RealmsBase(new Canary_Server(etc.getServer()));
    }

    @Override
    public final void disable(){
        if(RealmsBase.isLoaded()){
            etc.getLoader().removeCustomListener("Realms-API");
            base.terminate();
        }
    }

    @Override
    public final void initialize(){
        RealmsCommandHandler.initialize();
        RealmsTranslate.initialize();
        new Realms_CanaryListener(this);
        new Realms_CanaryPluginInterface(this);
        etc.getInstance().addCommand("/realms", "- Realms base command. Use /realms help for sub command help.");
    }
}
