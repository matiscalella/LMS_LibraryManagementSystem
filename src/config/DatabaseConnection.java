/**
 * Provides a centralized and reusable access point to the application's
 * database connection using JDBC.
 * <p>
 * This class loads the MySQL JDBC driver and exposes a single static method
 * {@link #getConnection()} that returns a valid {@code java.sql.Connection}
 * to the <strong>library_db</strong> database.
 * <p>
 * It is used across all DAO and Service classes to ensure consistent database
 * access, including support for operations that require manual control of
 * transactions (commit/rollback) by disabling {@code autoCommit}.
 * <p>
 * <strong>Usage Notes:</strong>
 * <ul>
 *   <li>Connections must be explicitly closed by the caller.</li>
 *   <li>The returned Connection can participate in multi-step transactional
 *       operations in the Service layer.</li>
 *   <li>The configuration follows the traditional JDBC pattern 
 *       (no ORM, no connection pool), as required by the TFI.</li>
 * </ul>
 *
 * <p>
 * This class belongs to the <code>config</code> package.
 * </p>
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC";;
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private DatabaseConnection() {}
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Si no se encuentra el driver, lanza excepción en tiempo de ejecución
            throw new RuntimeException("Error: JDBC driver not found, ", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database: " + URL, e);
        }
    }
}
