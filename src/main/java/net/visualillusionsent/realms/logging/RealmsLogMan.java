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
package net.visualillusionsent.realms.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.visualillusionsent.realms.RealmsBase;
import net.visualillusionsent.realms.data.RealmsProps;

/**
 * @author Jason (darkdiplomat)
 */
public final class RealmsLogMan{

    private static Logger logger;
    static {
        logger = new RLogger();
        logger.setParent(RealmsBase.getServer().getLogger());
        logger.setLevel(Level.ALL);
    }

    private static class RLogger extends Logger{

        RLogger(){
            super("Realms-Log", null);
        }

        @Override
        public void log(LogRecord logRecord){
            Level lvl = logRecord.getLevel();
            String message = logRecord.getMessage();
            RealmsProps rprop = RealmsBase.getProperties();
            if (lvl instanceof RLevel) {
                Boolean all = rprop.getBooleanVal("debug.all");
                if (all == null || !all) {
                    Boolean prop = rprop.getBooleanVal(lvl.getName().replace('-', '.').replace("REALMS.", "").toLowerCase());
                    if (prop == null || !prop) {
                        return;
                    }
                }
            }
            if (RealmsBase.getServer().isCanaryClassic()) {
                if (lvl instanceof RLevel) {
                    logRecord.setMessage(" [" + lvl.getName() + "] [Realms] " + message);
                }
                else {
                    logRecord.setMessage(" [Realms] ".concat(message));
                }
            }
            else {
                logRecord.setMessage("[Realms] ".concat(message));
            }
            super.log(logRecord);
        }
    }

    private RealmsLogMan(){}

    public static void info(String msg){
        if (logger == null)
            return;
        logger.info(msg);
    }

    public static void info(String msg, Throwable thrown){
        if (logger == null)
            return;
        logger.log(Level.INFO, msg, thrown);
    }

    public static void warning(String msg){
        if (logger == null)
            return;
        logger.warning(msg);
    }

    public static void warning(String msg, Throwable thrown){
        if (logger == null)
            return;
        logger.log(Level.WARNING, msg, thrown);
    }

    public static void severe(String msg){
        if (logger == null)
            return;
        logger.severe(msg);
    }

    public static void severe(String msg, Throwable thrown){
        if (logger == null)
            return;
        logger.log(Level.SEVERE, msg, thrown);
    }

    public static void stacktrace(Throwable thrown){
        if (logger == null)
            return;
        if (RealmsBase.getProperties().getBooleanVal("debug.stacktrace") || RealmsBase.getProperties().getBooleanVal("debug.all")) {
            logger.log(RLevel.STACKTRACE, "Stacktrace: ", thrown);
        }
        else {
            logger.warning("*** Set \"debug.stacktrace\" to yes in the Realms.ini to view stacktraces ***");
        }
    }

    public static void log(RLevel lvl, String msg){
        if (logger == null)
            return;
        logger.log(lvl, msg);
    }

    public static void log(RLevel lvl, String msg, Throwable thrown){
        if (logger == null)
            return;
        logger.log(lvl, msg, thrown);
    }

    public static void killLogger(){
        if (logger == null)
            return;
        logger.setLevel(Level.OFF);
        logger = null;
    }
}
