/* 
 * Copyright 2013 Visual Illusions Entertainment.
 *  
 * This file is part of Visual Illusions Minecraft Mod Interface Library (VI-MCMIL).
 *
 * VI-MCMIL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VI-MCMIL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with VI-MCMIL.
 * If not, see http://www.gnu.org/licenses/gpl.html
 */
package net.visualillusionsent.mcmod.interfaces;

/**
 * This file is part of VI-MCMIL.
 * Copyright 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation
 * 
 * @author Jason (darkdiplomat)
 */
public final class ChatColors {

    /**
     * chat color <FONT COLOR=000000><b>BLACK</b></FONT>
     */
    public static final String BLACK = "\u00A70";

    /**
     * chat color <font color="000066"><b>DARK_BLUE</b></font>
     */
    public static final String DARK_BLUE = "\u00A71";

    /**
     * chat color <font color="006600"><b>GREEN</b></font>
     */
    public static final String GREEN = "\u00A72";

    /**
     * chat color <font color="006666"><b>TURQUOISE</b></font>
     */
    public static final String TURQUOISE = "\u00A73";

    /**
     * chat color <font color="990000"><b>RED</b></font>
     */
    public static final String RED = "\u00A74";

    /**
     * chat color <font color="540054"><b>PURPLE</b></font>
     */
    public static final String PURPLE = "\u00A75";

    /**
     * chat color <font color="FF9933"><b>ORANGE</b></font>
     */
    public static final String ORANGE = "\u00A76";

    /**
     * chat color <font color="CCCCCC"><b>LIGHT_GRAY</b></font>
     */
    public static final String LIGHT_GRAY = "\u00A77";

    /**
     * chat color <font color="333333"><b>GRAY</b></font>
     */
    public static final String GRAY = "\u00A78";

    /**
     * chat color <font color="2A2A7F"><b>BLUE</b></font>
     */
    public static final String BLUE = "\u00A79";

    /**
     * chat color <font color="33FF33"><b>LIGHT_GREEN</b></font>
     */
    public static final String LIGHT_GREEN = "\u00A7a";

    /**
     * chat color <font color="00FFFF"><b>CYAN</b></font>
     */
    public static final String CYAN = "\u00A7b";

    /**
     * chat color <font color="FF0022"><b>LIGHT_RED</b></font>
     */
    public static final String LIGHT_RED = "\u00A7c";

    /**
     * chat color <font color="FF00FF"><b>PINK</b></font>
     */
    public static final String PINK = "\u00A7d";

    /**
     * chat color <font color="FFFF00"><b>YELLOW</b></font>
     */
    public static final String YELLOW = "\u00A7e";

    /**
     * chat color <font color="000000"><b>WHITE</b></font>
     */
    public static final String WHITE = "\u00A7f";

    /**
     * chat color <b>BOLD</b>
     */
    public static final String BOLD = "\u00A7l";

    /**
     * chat color <s>STRIKED</s>
     */
    public static final String STRIKED = "\u00A7m";

    /**
     * chat color <u>UNDERLINED</u>
     */
    public static final String UNDERLINED = "\u00A7n";

    /**
     * chat color <i>ITALIC</i>
     */
    public static final String ITALIC = "\u00A7o";

    public static final String RESET = "\u00A7r";

    /**
     * chat color <b>MARKER §</b>
     */
    public static final String MARKER = "\u00A7";

    /**
     * removes all color formating from a line
     * 
     * @param str
     * @return str with formating removed
     */
    public static final String removeFormating(String str) {
        return str.replaceAll(MARKER.concat("[A-Ra-r0-9]"), "");
    }

    /**
     * This class should never be constructed
     */
    private ChatColors() {}
}
