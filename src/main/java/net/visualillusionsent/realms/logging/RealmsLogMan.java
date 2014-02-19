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
package net.visualillusionsent.realms.logging;

import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.data.RealmsProps;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jason (darkdiplomat)
 */
public final class RealmsLogMan {

    private static Logger logger;
    private static final String logRec = "[%s] %s";

    static {
        logger = RealmsBase.getServer().getLogger();
    }

    private RealmsLogMan() {
    }

    public static void info(String msg) {
        if (logger == null)
            return;
        logger.info(msg);
    }

    public static void info(String msg, Throwable thrown) {
        if (logger == null)
            return;
        logger.log(Level.INFO, msg, thrown);
    }

    public static void warning(String msg) {
        if (logger == null)
            return;
        logger.warning(msg);
    }

    public static void severe(String msg) {
        if (logger == null)
            return;
        logger.severe(msg);
    }

    public static void stacktrace(Throwable thrown) {
        if (logger == null)
            return;
        if (canLog(RLevel.STACKTRACE)) {
            logger.log(Level.SEVERE, String.format(logRec, "[REALMS-DEBUG-STACKTRACE]", ""), thrown);
        }
        else {
            logger.warning("*** Set \"debug.stacktrace\" to yes in the Realms.ini to view stacktraces ***");
        }
    }

    public static void log(RLevel lvl, String msg) {
        if (logger == null)
            return;
        if (canLog(lvl))
            logger.log(Level.INFO, String.format(logRec, lvl.getName(), msg));
    }

    public static void log(RLevel lvl, String msg, Throwable thrown) {
        if (logger == null)
            return;
        if (canLog(lvl))
            logger.log(Level.INFO, String.format(logRec, lvl.getName(), msg));
    }

    public static void killLogger() {
        if (logger == null)
            return;
        logger = null;
    }

    private static boolean canLog(RLevel lvl) {
        RealmsProps rprop = RealmsBase.getProperties();
        if (rprop == null) {
            return false;
        }
        Boolean all = rprop.getBooleanVal("debug.all");
        if (all == null || !all) {
            Boolean prop = rprop.getBooleanVal(lvl.getName().replace('-', '.').replace("REALMS.", "").toLowerCase());
            if (prop == null || !prop) {
                return false;
            }
        }
        return true;
    }
}
