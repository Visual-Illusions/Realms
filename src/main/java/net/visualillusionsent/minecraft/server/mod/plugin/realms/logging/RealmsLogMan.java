/* Copyright 2012 - 2013 Visual Illusions Entertainment.
 * This file is part of Realms.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 * Source Code availible @ https://github.com/Visual-Illusions/Realms */
package net.visualillusionsent.minecraft.server.mod.plugin.realms.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.visualillusionsent.minecraft.server.mod.plugin.realms.RealmsBase;

/**
 * This file is part of Realms.
 * Copyright 2012 - 2013 Visual Illusions Entertainment.
 * Licensed under the terms of the GNU General Public License Version 3 as published by the Free Software Foundation.
 * Source Code availible @ https://github.com/Visual-Illusions/Realms
 * 
 * @author Jason (darkdiplomat)
 */
public final class RealmsLogMan{

    private static Logger logger;
    static {
        logger = new RLogger();
        if (RealmsBase.getServer().isCanary() || RealmsBase.getServer().isBukkit()) {
            logger.setParent(RealmsBase.getServer().getLogger());
        }
        else {
            logger.setParent(Logger.getLogger("Minecraft-Sever"));
        }
        logger.setLevel(Level.ALL);
    }

    private static class RLogger extends Logger{

        RLogger(){
            super("Realms-Logger", null);
        }

        @Override
        public void log(LogRecord logRecord){
            Level lvl = logRecord.getLevel();
            String message = logRecord.getMessage();
            if (lvl instanceof RLevel) {
                Boolean all = RealmsBase.getProperties().getBooleanVal("debug.all");
                if (all == null || !all) {
                    Boolean prop = RealmsBase.getProperties().getBooleanVal(lvl.getName().replace('-', '.').replace("REALMS.", "").toLowerCase());
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
        logger.info(msg);
    }

    public static void info(String msg, Throwable thrown){
        logger.log(Level.INFO, msg, thrown);
    }

    public static void warning(String msg){
        logger.warning(msg);
    }

    public static void warning(String msg, Throwable thrown){
        logger.log(Level.WARNING, msg, thrown);
    }

    public static void severe(String msg){
        logger.severe(msg);
    }

    public static void severe(String msg, Throwable thrown){
        logger.log(Level.SEVERE, msg, thrown);
    }

    public static void stacktrace(Throwable thrown){
        if (RealmsBase.getProperties().getBooleanVal("debug.stacktrace") || RealmsBase.getProperties().getBooleanVal("debug.all")) {
            logger.log(RLevel.STACKTRACE, "Stacktrace: ", thrown);
        }
        else {
            logger.warning("*** Set \"debug.stacktrace\" to yes in the Realms.ini to view stacktraces ***");
        }
    }

    public static void log(RLevel lvl, String msg){
        logger.log(lvl, msg);
    }

    public static void log(RLevel lvl, String msg, Throwable thrown){
        logger.log(lvl, msg, thrown);
    }

    public static void killLogger(){
        logger.setLevel(Level.OFF);
        logger = null;
    }
}
