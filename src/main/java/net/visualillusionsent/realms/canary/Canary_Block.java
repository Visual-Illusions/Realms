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
package net.visualillusionsent.realms.canary;

import net.canarymod.api.world.blocks.Block;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;

/**
 * @author Jason (darkdiplomat)
 */
public final class Canary_Block implements Mod_Block{

    private final Block block;

    public Canary_Block(Block block){
        this.block = block;
    }

    @Override
    public final int getType(){
        return block.getTypeId();
    }

    @Override
    public final int getData(){
        return block.getData();
    }

    @Override
    public final int getX(){
        return block.getX();
    }

    @Override
    public final int getY(){
        return block.getY();
    }

    @Override
    public final int getZ(){
        return block.getZ();
    }

    @Override
    public final int getDimension(){
        int dim = block.getWorld().getType().getId();
        return dim == -1 ? 1 : dim == 1 ? 2 : 0;
    }

    @Override
    public final String getWorld(){
        return block.getWorld().getName();
    }

    @Override
    public final Block getBlock(){
        return block;
    }

    @Override
    public final boolean equals(Object obj){
        if (obj instanceof Canary_Block) {
            return block.equals(((Canary_Block) obj).getBlock());
        }
        else if (obj instanceof Block) {
            return block.equals(obj);
        }
        return false;
    }

    @Override
    public final int hashCode(){
        return block.hashCode();
    }
}
