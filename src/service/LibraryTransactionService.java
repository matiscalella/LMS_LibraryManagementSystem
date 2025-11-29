package service;

import config.TransactionManager;
import dao.BookDAO;
import dao.BibliographicRecordDAO;
import model.Book;
import model.BibliographicRecord;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * High-level transactional workflows involving both {@link Book} and
 * {@link BibliographicRecord}. All multi-step operations that must be
 * executed atomically (commit or rollback) are handled here.
 *
 * <p>This class uses {@link TransactionManager} to encapsulate JDBC
 * transaction mechanics, keeping this service clean and business-focused.</p>
 */
public class LibraryTransactionService {

    private final BookDAO bookDAO;
    private final BibliographicRecordDAO bibliographicRecordDAO;

    public LibraryTransactionService() {
        this.bookDAO = new BookDAO();
        this.bibliographicRecordDAO = new BibliographicRecordDAO();
    }

    public LibraryTransactionService(BookDAO bookDAO, BibliographicRecordDAO bibliographicRecordDAO) {
        this.bookDAO = bookDAO;
        this.bibliographicRecordDAO = bibliographicRecordDAO;
    }

    // ==========================================================
    // TX #1: Create Book + BibliographicRecord

    /**
     * Creates a {@link Book} and its {@link BibliographicRecord} in a single
     * atomic transaction. If any step fails, the entire operation is rolled back.
     *
     * @throws ServiceException if validation or persistence fails
     */
    public void createBookWithRecord(Book book, BibliographicRecord bibliographicRecord) throws ServiceException {

        // Simple upfront validations
        if (book == null) {
            throw new ServiceException("Book cannot be null.");
        }
        if (bibliographicRecord == null) {
            throw new ServiceException("BibliographicRecord cannot be null.");
        }
        if (book.getId() != null) {
            throw new ServiceException("New Book cannot have a predefined ID.");
        }
        if (bibliographicRecord.getId() != null) {
            throw new ServiceException("New BibliographicRecord cannot have a predefined ID.");
        }
        if (bibliographicRecord.getBookId() != null) {
            throw new ServiceException("BibliographicRecord.bookId must be null when creating Book + Record.");
        }

        TransactionManager tm = new TransactionManager();

        try {
            tm.begin();
            Connection conn = tm.getConnection();

            // 1) Create Book
            bookDAO.create(book, conn);

            if (book.getId() == null) {
                throw new ServiceException("Failed to obtain generated ID for Book.");
            }

            // 2) Link and create BibliographicRecord
            bibliographicRecord.setBookId(book.getId());
            bibliographicRecordDAO.create(bibliographicRecord, conn);

            if (bibliographicRecord.getId() == null) {
                throw new ServiceException("Failed to obtain generated ID for BibliographicRecord.");
            }

            tm.commit();

        } catch (SQLException e) {
            tm.rollback();
            throw new ServiceException("Transaction failed while creating Book + BibliographicRecord: "
                    + e.getMessage(), e);

        } finally {
            tm.close();
        }
    }

    // ==========================================================
    // TX #2: Delete Book + BibliographicRecord
    // ==========================================================

    /**
     * Deletes a {@link Book} and its linked {@link BibliographicRecord}
     * (either via ON DELETE CASCADE or manual deletion), inside a single transaction.
     */
    public void deleteBookAndRecord(Long bookId) throws ServiceException {

        if (bookId == null || bookId <= 0) {
            throw new ServiceException("Book ID must be a positive, non-null value.");
        }

        TransactionManager tm = new TransactionManager();

        try {
            tm.begin();
            Connection conn = tm.getConnection();

            // Check existence
            Book existing = bookDAO.findById(bookId, conn);

            if (existing == null) {
                throw new ServiceException("Book with ID " + bookId + " does not exist.");
            }

            // Perform deletion
            bookDAO.delete(bookId, conn);

            tm.commit();

        } catch (SQLException e) {
            tm.rollback();
            throw new ServiceException("Transaction failed while deleting Book + Record: " + e.getMessage(), e);

        } finally {
            tm.close();
        }
    }

    // ==========================================================
    // TX #3: Move BibliographicRecord → another Book
    // ==========================================================

    /**
    * Assigns or moves a BibliographicRecord to a new Book.
    *
    * <p>If the record has no current book_id (NULL), this method performs an initial assignment.</p>
    * <p>If it already has a book_id, this method reassigns it to the new Book.</p>
    * <p>All operations occur inside a single atomic transaction.</p>
    */
   public void moveBibliographicRecordToAnotherBook(Long recordId, Long newBookId)
           throws ServiceException {

       if (recordId == null || newBookId == null) {
           throw new ServiceException("recordId and newBookId cannot be null.");
       }
       if (recordId <= 0 || newBookId <= 0) {
           throw new ServiceException("recordId and newBookId must be positive values.");
       }

       TransactionManager tm = new TransactionManager();

       try {
           tm.begin();
           Connection conn = tm.getConnection();

           // Retrieve record
           BibliographicRecord bibliographicRecord =
                   bibliographicRecordDAO.findById(recordId, conn);

           if (bibliographicRecord == null) {
               throw new ServiceException("BibliographicRecord with ID " + recordId + " does not exist.");
           }

           // Check new target Book exists
           Book newBook = bookDAO.findById(newBookId, conn);
           if (newBook == null) {
               throw new ServiceException("Target Book with ID " + newBookId + " does not exist.");
           }

           Long currentBookId = bibliographicRecord.getBookId();

           // Case 1: record NOT assigned yet → assign it
           if (currentBookId == null) {
               bibliographicRecordDAO.updateBookId(recordId, newBookId, conn);
               tm.commit();
               return;
           }

           // Case 2: record already assigned → prevent no-op
           if (currentBookId.equals(newBookId)) {
               throw new ServiceException(
                   "Record is already assigned to Book ID " + newBookId + "."
               );
           }

           // Case 3: record assigned to another book → move it
           bibliographicRecordDAO.updateBookId(recordId, newBookId, conn);

           tm.commit();

       } catch (SQLException e) {
           tm.rollback();
           throw new ServiceException("Transaction failed while assigning/moving BibliographicRecord: "
                   + e.getMessage(), e);

       } finally {
           tm.close();
       }
   }
}