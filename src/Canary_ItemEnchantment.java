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
import net.visualillusionsent.mcmod.interfaces.Mod_ItemEnchantment;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * Source Code availible @ https://github.com/darkdiplomat/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class Canary_ItemEnchantment implements Mod_ItemEnchantment {

    private final Enchantment enchantment;

    public Canary_ItemEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @Override
    public final int getId() {
        return enchantment.getType().getType();
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