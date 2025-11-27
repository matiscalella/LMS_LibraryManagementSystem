/**
 * Generic business-level service interface that defines the standard CRUD
 * operations for all domain entities in the application.
 * <p>
 * This interface represents the Service Layer in a classical layered
 * architecture (Entity → DAO → Service → UI/Menu). Implementations of this
 * interface are responsible for:
 * </p>
 *
 * <ul>
 *     <li>Applying business validations before delegating to the DAO layer.</li>
 *     <li>Ensuring entity integrity (required fields, formats, ranges, etc.).</li>
 *     <li>Handling and translating persistence errors into {@link ServiceException}.</li>
 *     <li>Exposing a clean, domain-oriented API to the presentation layer.</li>
 * </ul>
 *
 * <p>
 * The type parameter {@code T} is constrained to extend {@link entities.Base},
 * ensuring that all services operate over entities with a common structure
 * (identifier and logical deletion flag).
 * </p>
 *
 * @param <T> The concrete entity type extending {@link entities.Base}.
 */
package service;

import model.Base;
import java.util.List;


public interface GenericService<T extends Base> {
    /**
     * Persists a new entity after validating its business rules.
     *
     * @param entity the entity to be created; must not be {@code null}.
     * @return the newly created entity, including its generated identifier.
     * @throws ServiceException if the entity is invalid, violates business rules,
     *                          or if a persistence error occurs.
     */
    T create(T entity) throws ServiceException;
    
    /**
     * Updates an existing entity after checking its validity and existence.
     *
     * @param entity the entity with updated values; must not be {@code null},
     *               and its ID must refer to an existing record.
     * @return the updated entity.
     * @throws ServiceException if the entity is invalid, does not exist,
     *                          or if a persistence error occurs.
     */
    T update(T entity) throws ServiceException;

    /**
     * Performs a logical deletion on an existing entity identified by its ID.
     * <p>
     * This method should validate the ID and ensure the referenced entity exists.
     * </p>
     *
     * @param id the identifier of the entity to be logically deleted.
     * @throws ServiceException if the ID is invalid, the entity does not exist,
     *                          or a persistence error occurs.
     */
    void delete(Long id) throws ServiceException;

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity to retrieve; must not be {@code null}.
     * @return the matching entity, or {@code null} if not found.
     * @throws ServiceException if the ID is invalid or if a persistence error occurs.
     */
    T findById(Long id) throws ServiceException;

    /**
     * Retrieves all non-deleted entities.
     *
     * @return a list of all active entities; never {@code null}.
     * @throws ServiceException if a persistence error occurs.
     */
    List<T> findAll() throws ServiceException;
}
