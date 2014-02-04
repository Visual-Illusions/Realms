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
/* 
 * Copyright 2013 Visual Illusions Entertainment.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html
 */
package net.visualillusionsent.realms.lang;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * @author Jason (darkdiplomat)
 */
public enum DataSourceType {
    XML("org.jdom2.JDOMException"), //
    MYSQL("com.mysql.jdbc.Driver"), //
    SQLITE("org.sqlite.JDBC"), //
    POSTGRESQL("org.postgresql.Driver"); //

    private final String driverClass;

    private DataSourceType(String driverClass) {
        this.driverClass = driverClass;
    }

    private final boolean canFindSQLDriver(String driver) {
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            Driver drive = en.nextElement();
            if (drive.getClass().getName().equals(driver)) {
                return true;
            }
        }
        return false;
    }

    public final void testDriver() {
        if (this != XML) {
            if (canFindSQLDriver(driverClass)) {
                return;
            }
        }
        try {
            Class.forName(driverClass);
        }
        catch (ClassNotFoundException cnfe) {
            throw new DataSourceError(cnfe, this);
        }
    }
}
