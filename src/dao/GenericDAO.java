package dao;

import java.sql.SQLException;
import java.util.List;
/**
 * Generic DAO contract defining the basic CRUD operations
 * to be implemented by all DAO classes in the system.
 *
 * <p>This interface abstracts persistence operations for
 * any entity type, isolating the rest of the application
 * from JDBC-specific details.</p>
 *
 * @param <T> the entity type managed by the DAO
 */
public interface GenericDAO<T> {
    void create(T entity) throws SQLException;
    T findById(Long id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(Long id) throws SQLException; // logical delete
}
