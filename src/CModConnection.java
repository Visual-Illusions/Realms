import java.sql.Connection;
import java.sql.SQLException;

import net.visualillusionsent.viutils.ICModConnection;

public class CModConnection implements ICModConnection {
    private CanaryConnection cconn;
    private Connection conn;

    public CModConnection(CanaryConnection cconn) {
        this.cconn = cconn;
    }

    public CModConnection(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Connection getConnection() {
        if (cconn != null) {
            return cconn.getConnection();
        }
        else {
            return conn;
        }
    }

    @Override
    public void release() throws SQLException {
        if (cconn != null) {
            cconn.release();
        }
        else if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
