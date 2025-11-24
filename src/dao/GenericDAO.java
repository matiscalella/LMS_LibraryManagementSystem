/**
 * Generic DAO contract for basic CRUD operations.
 *
 * @param <T> Entity type
 */
package dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T> {
    void create(T entity) throws SQLException;
    T findById(Long id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(Long id) throws SQLException; // logical delete
}
