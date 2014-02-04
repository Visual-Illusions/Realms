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
package net.visualillusionsent.realms.canary;

import net.canarymod.api.inventory.Enchantment;
import net.visualillusionsent.minecraft.server.mod.interfaces.Mod_ItemEnchantment;

/**
 * @author Jason (darkdiplomat)
 */
public final class Canary_ItemEnchantment implements Mod_ItemEnchantment {

    private final Enchantment enchantment;

    public Canary_ItemEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @Override
    public final int getId() {
        return enchantment.getType().getId();
    }

    @Override
    public final int getLevel() {
        return enchantment.getLevel();
    }

    @Override
    public final Enchantment getBaseEnchantment() {
        return enchantment;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Canary_ItemEnchantment) {
            return enchantment.equals(((Canary_ItemEnchantment) obj).getBaseEnchantment());
        }
        else if (obj instanceof Enchantment) {
            return enchantment.equals(obj);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return enchantment.hashCode();
    }
}
