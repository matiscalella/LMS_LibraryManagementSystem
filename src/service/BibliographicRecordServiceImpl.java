package service;

import dao.BibliographicRecordDAO;
import model.BibliographicRecord;
import java.sql.SQLException;
import java.util.List;

/**
 * Service implementation responsible for managing {@link BibliographicRecord}
 * entities within the Library Management System.
 *
 * <p>This class represents the Service Layer for the bibliographic_records table.
 * It applies business validations, enforces 1→1 relationship rules, and delegates
 * low-level persistence operations to the {@link dao.BibliographicRecordDAO}.
 * </p>
 *
 * <p>Main responsibilities of this service include:</p>
 *
 * <ul>
 *     <li>Validating bibliographic data such as ISBN, Dewey classification,
 *         shelf location and language.</li>
 *
 *     <li>Ensuring integrity of the 1→1 relationship between
 *         {@link model.Book} and {@link BibliographicRecord}, where the
 *         foreign key <code>book_id</code> lives in the bibliographic record.</li>
 *
 *     <li>Preventing invalid operations, such as updating a record without ID,
 *         creating a record with a predefined ID, or reassigning a record to a
 *         different book during updates.</li>
 *
 *     <li>Converting low-level {@link java.sql.SQLException} into
 *         {@link service.ServiceException} to maintain a clean separation
 *         between the DAO and the presentation layer.</li>
 * </ul>
 *
 * <p>This service does <strong>not</strong> manage transactions. Transactional
 * operations involving both Book and BibliographicRecord — such as creating a
 * Book with its associated record in a single atomic operation — must be
 * implemented in higher-level service methods or in dedicated transactional
 * service classes.</p>
 */
public class BibliographicRecordServiceImpl implements GenericService<BibliographicRecord> {

    private final BibliographicRecordDAO bibliographicRecordDAO;

    /**
     * Default constructor instantiating a new BibliographicRecordDAO.
     */
    public BibliographicRecordServiceImpl() {
        this.bibliographicRecordDAO = new BibliographicRecordDAO();
    }

    /**
     * Constructor with dependency injection support.
     *
     * @param bibliographicRecordDAO custom DAO instance to use; must not be null.
     */
    public BibliographicRecordServiceImpl(BibliographicRecordDAO bibliographicRecordDAO) {
        this.bibliographicRecordDAO = bibliographicRecordDAO;
    }

    @Override
    public BibliographicRecord create(BibliographicRecord bibliographicRecord) throws ServiceException {

        // Validate fields
        validateBibliographicRecord(bibliographicRecord);

        // New records cannot have an ID
        if (bibliographicRecord.getId() != null) {
            throw new ServiceException("New bibliographic records cannot have a predefined ID.");
        }

        // bookId must never be assigned manually during create()
        if (bibliographicRecord.getBookId() != null) {
            throw new ServiceException("bookId must not be manually assigned when creating a bibliographic record.");
        }

        bibliographicRecord.setDeleted(false);

        try {
            bibliographicRecordDAO.create(bibliographicRecord);
            return bibliographicRecord;

        } catch (SQLException e) {
            throw new ServiceException("Error creating bibliographic record: " + e.getMessage(), e);
        }
    }

    @Override
    public BibliographicRecord update(BibliographicRecord bibliographicRecord) throws ServiceException {

        // Validate general fields
        validateBibliographicRecord(bibliographicRecord);

        // Validate ID
        if (bibliographicRecord.getId() == null) {
            throw new ServiceException("BibliographicRecord ID is required for update.");
        }
        if (bibliographicRecord.getId() <= 0) {
            throw new ServiceException("BibliographicRecord ID must be a positive value.");
        }

        // Validate existence
        BibliographicRecord existing = findById(bibliographicRecord.getId());
        if (existing == null) {
            throw new ServiceException(
                "Cannot update: BibliographicRecord with ID " + bibliographicRecord.getId() + " does not exist."
            );
        }

        // Cannot update deleted records
        if (existing.isDeleted()) {
            throw new ServiceException(
                "Cannot update: BibliographicRecord with ID " + bibliographicRecord.getId() + " is deleted."
            );
        }

        // Ensure bookId cannot be reassigned (1→1 strict rule)
        if (!java.util.Objects.equals(bibliographicRecord.getBookId(), existing.getBookId())) {
            throw new ServiceException(
                "Reassigning this bibliographic record to a different book is not permitted."
            );
        }

        try {
            bibliographicRecordDAO.update(bibliographicRecord);
            return bibliographicRecord;

        } catch (SQLException e) {
            throw new ServiceException(
                "Error updating bibliographic record with ID " + bibliographicRecord.getId() + ": " + e.getMessage(),
                e
            );
        }
    }


    @Override
    public void delete(Long id) throws ServiceException {

        // Validate ID
        if (id == null) {
            throw new ServiceException("BibliographicRecord ID cannot be null.");
        }
        if (id <= 0) {
            throw new ServiceException("BibliographicRecord ID must be a positive value.");
        }

        // Validate existence
        BibliographicRecord existing = findById(id);
        if (existing == null) {
            throw new ServiceException(
                "Cannot delete: BibliographicRecord with ID " + id + " does not exist."
            );
        }

        // Validate not already deleted
        if (existing.isDeleted()) {
            throw new ServiceException(
                "Cannot delete: BibliographicRecord with ID " + id + " is already deleted."
            );
        }

        try {
            // Perform logical delete
            bibliographicRecordDAO.delete(id);

        } catch (SQLException e) {
            throw new ServiceException(
                "Error deleting bibliographic record with ID " + id + ": " + e.getMessage(),
                e
            );
        }
    }


    @Override
    public BibliographicRecord findById(Long id) throws ServiceException {

        // Validate ID
        if (id == null) {
            throw new ServiceException("BibliographicRecord ID cannot be null.");
        }
        if (id <= 0) {
            throw new ServiceException("BibliographicRecord ID must be a positive value.");
        }

        try {
            return bibliographicRecordDAO.findById(id);

        } catch (SQLException e) {
            throw new ServiceException(
                "Error retrieving bibliographic record with ID " + id + ": " + e.getMessage(),
                e
            );
        }
    }

    @Override
    public List<BibliographicRecord> findAll() throws ServiceException {
        try {
            return bibliographicRecordDAO.findAll();

        } catch (SQLException e) {
            throw new ServiceException(
                "Error retrieving bibliographic record list: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Validates the contents and business rules of a {@link BibliographicRecord}.
     *
     * @param bibliographicRecord the bibliographic bibliographicRecord to validate
     * @throws ServiceException if any value is invalid or violates business constraints
     */
    private void validateBibliographicRecord(BibliographicRecord bibliographicRecord) throws ServiceException {

        if (bibliographicRecord == null) {
            throw new ServiceException("BibliographicRecord cannot be null.");
        }

        // ISBN (optional)
        if (bibliographicRecord.getIsbn() != null) {
            if (bibliographicRecord.getIsbn().isBlank()) {
                throw new ServiceException("ISBN cannot be blank.");
            }
            if (bibliographicRecord.getIsbn().length() > 17) {
                throw new ServiceException("ISBN exceeds maximum length (17).");
            }
        }

        // Dewey Class (optional)
        if (bibliographicRecord.getDeweyClass() != null && bibliographicRecord.getDeweyClass().length() > 20) {
            throw new ServiceException("Dewey Class exceeds maximum length (20).");
        }

        // Shelf Location (optional)
        if (bibliographicRecord.getShelfLocation() != null && bibliographicRecord.getShelfLocation().length() > 50) {
            throw new ServiceException("Shelf Location exceeds maximum length (50).");
        }

        // Language (optional)
        if (bibliographicRecord.getLanguage() != null && bibliographicRecord.getLanguage().length() > 30) {
            throw new ServiceException("Language exceeds maximum length (30).");
        }
    }
}
