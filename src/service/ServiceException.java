
package service;
/**
 * Exception type used by the Service Layer to signal business rule violations,
 * invalid operations, or errors originating from the persistence layer.
 * <p>
 * This checked exception allows service implementations to:
 * </p>
 *
 * <ul>
 *     <li>Validate incoming data and reject invalid entities.</li>
 *     <li>Translate {@link java.sql.SQLException} and other low-level exceptions
 *         into meaningful, domain-oriented error messages.</li>
 *     <li>Maintain a clean separation between business logic and persistence concerns.</li>
 * </ul>
 *
 * <p>
 * Unlike runtime exceptions, {@code ServiceException} enforces explicit handling
 * by callers (such as menu controllers or UI layers), ensuring a predictable
 * control flow and clearer error reporting.
 * </p>
 */
public class ServiceException extends Exception {

    /**
     * Creates a new {@code ServiceException} with a descriptive message.
     *
     * @param message the detail message explaining the business or service error.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Creates a new {@code ServiceException} with a message and a root cause.
     *
     * @param message the detail message explaining the error.
     * @param cause   the underlying exception that triggered this error.
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
