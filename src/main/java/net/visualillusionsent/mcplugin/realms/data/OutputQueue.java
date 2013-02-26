/* 
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Realms.
 *
 * Realms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Realms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Realms.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * 
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 */
package net.visualillusionsent.mcplugin.realms.data;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import net.visualillusionsent.mcplugin.realms.logging.RLevel;
import net.visualillusionsent.mcplugin.realms.logging.RealmsLogMan;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
final class OutputQueue{

    private LinkedList<DataSourceActionContainer> queue;

    public OutputQueue(){
        queue = new LinkedList<DataSourceActionContainer>();
    }

    public final void add(DataSourceActionContainer dsac){
        synchronized(queue){
            queue.add(dsac);
            queue.notify();
        }
    }

    public final DataSourceActionContainer next(){
        DataSourceActionContainer dsac = null;
        if(queue.isEmpty()){
            synchronized(queue){
                try{
                    queue.wait();
                }
                catch(InterruptedException iex){
                    // Interrupted
                    RealmsLogMan.log(RLevel.GENERAL, "InterruptedException occured in OutputQueue");
                    return null;
                }
            }
        }
        try{
            dsac = queue.getFirst();
            queue.removeFirst();
        }
        catch(NoSuchElementException nseex){
            throw new InternalError("Race hazard in LinkedList object.");
        }
        return dsac;
    }

    public final void clear(){
        synchronized(queue){
            queue.clear();
        }
    }
}
