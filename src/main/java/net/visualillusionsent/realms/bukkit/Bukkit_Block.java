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
package net.visualillusionsent.realms.bukkit;

import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_Block;
import org.bukkit.block.Block;

/**
 * @author Jason (darkdiplomat)
 */
public final class Bukkit_Block implements Mod_Block {

    private final Block block;
    private final int type, data, x, y, z, dimension;
    private final String world;

    public Bukkit_Block(Block block) {
        this.block = block;
        this.type = block.getTypeId();
        this.data = block.getData();
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.dimension = block.getWorld().getEnvironment().ordinal();
        this.world = block.getWorld().getName();
    }

    @Override
    public final int getType() {
        return type;
    }

    @Override
    public final int getData() {
        return data;
    }

    @Override
    public final int getX() {
        return x;
    }

    @Override
    public final int getY() {
        return y;
    }

    @Override
    public final int getZ() {
        return z;
    }

    @Override
    public final int getDimension() {
        return dimension;
    }

    @Override
    public final String getWorld() {
        return world;
    }

    @Override
    public final Block getBlock() {
        return block;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Bukkit_Block) {
            return block.equals(((Bukkit_Block) obj).getBlock());
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
