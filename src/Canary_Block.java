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
import net.visualillusionsent.mcmod.interfaces.Mod_Block;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Canary_Block implements Mod_Block {
    private final Block block;

    public Canary_Block(Block block) {
        this.block = block;
    }

    @Override
    public final int getType() {
        return block.getType();
    }

    @Override
    public final int getData() {
        return block.getData();
    }

    @Override
    public final int getX() {
        return block.getX();
    }

    @Override
    public final int getY() {
        return block.getY();
    }

    @Override
    public final int getZ() {
        return block.getZ();
    }

    @Override
    public final int getDimension() {
        return block.getWorld().getType().toIndex();
    }

    @Override
    public final String getWorld() {
        return block.getWorld().getName();
    }

    @Override
    public final Block getBlock() {
        return block;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Canary_Block) {
            return block.equals(((Canary_Block) obj).getBlock());
        }
        else if (obj instanceof Block) {
            return block.equals(obj);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return block.hashCode();
    }
}
