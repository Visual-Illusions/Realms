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

/**
 * @author Jason (darkdiplomat)
 */
public class DataSourceError extends Error {

    private static final long serialVersionUID = 101800012013L;

    public DataSourceError(String msg) {
        super(msg);
    }

    public DataSourceError(Exception ex) {
        super(ex.getMessage());
        this.setStackTrace(ex.getStackTrace());
    }

    public DataSourceError(ClassNotFoundException cnfe, DataSourceType type) {
        super("Driver not found for DataSourceType: ".concat(type.name()));
        this.setStackTrace(cnfe.getStackTrace());
    }
}
