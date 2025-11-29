package config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database transactions using a single JDBC {@link Connection}.
 *
 * <p>This class centralizes the typical transaction workflow:
 * begin → execute operations → commit or rollback → close.</p>
 *
 * <p>It belongs to the infrastructure layer (config) because it deals
 * exclusively with database transaction mechanics, not with business rules.</p>
 *
 * <p>Usage example:</p>
 *
 * <pre>
 *     TransactionManager tm = new TransactionManager();
 *
 *     try {
 *         tm.begin();
 *         Connection conn = tm.getConnection();
 *
 *         // DAO operations using conn...
 *
 *         tm.commit();
 *     } catch (Exception e) {
 *         tm.rollback();
 *     } finally {
 *         tm.close();
 *     }
 * </pre>
 */
public class TransactionManager {

    private Connection conn;

    /**
     * Begins a new transaction by obtaining a fresh {@link Connection}
     * and disabling auto-commit mode.
     *
     * @throws SQLException if obtaining the connection fails
     */
    public void begin() throws SQLException {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
    }

    /**
     * Commits the current transaction.
     *
     * @throws SQLException if committing fails
     */
    public void commit() throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     * Rolls back the current transaction.
     * Does not throw exceptions, since rollback is best-effort.
     */
    public void rollback() {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                System.err.println("Rollback failed: " + ignored.getMessage());
            }
        }
    }

    /**
     * Restores auto-commit mode and closes the underlying connection.
     */
    public void close() {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
                System.err.println("Connection close failed: " + ignored.getMessage());
            }
        }
    }

    /**
     * Returns the active JDBC {@link Connection} for DAO operations.
     *
     * @return the active connection, or null if the transaction has not begun
     */
    public Connection getConnection() {
        return conn;
    }
}
