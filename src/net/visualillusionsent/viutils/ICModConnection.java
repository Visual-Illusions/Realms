package net.visualillusionsent.viutils;

import java.sql.Connection;
import java.sql.SQLException;

public interface ICModConnection {

    public Connection getConnection() throws SQLException;

    public void release() throws SQLException;

}
