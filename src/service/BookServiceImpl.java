package service;

import dao.BookDAO;
import java.sql.SQLException;
import model.Book;
import java.util.List;
import java.time.LocalDate;

/**
 * Service implementation for managing {@link Book} entities.
 * <p>
 * This class belongs to the Service Layer and is responsible for
 * applying business validations, coordinating operations, and
 * delegating persistence tasks to the {@link BookDAO}.
 * </p>
 */
public class BookServiceImpl implements GenericService<Book> {

    private final BookDAO bookDAO;

    /**
     * Default constructor using a new instance of {@link BookDAO}.
     */
    public BookServiceImpl() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Allows injecting a custom {@link BookDAO} implementation.
     *
     * @param bookDAO the DAO instance to use; must not be null.
     */
    public BookServiceImpl(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    public Book create(Book book) throws ServiceException {
        validateBook(book);
        if (book.getId() != null) {
            throw new ServiceException("New books cannot have a predefined ID.");
        }
        book.setDeleted(false);
        try {
            // Delegate persistence to DAO
            bookDAO.create(book);
            // DAO populates the generated ID inside the same object
            return book;

        } catch (SQLException e) {
            throw new ServiceException("Error creating book: " + e.getMessage(), e);
        }
    }

    @Override
    public Book update(Book book) throws ServiceException {

        // Validate object content
        validateBook(book);

        // Validate ID
        if (book.getId() == null) {
            throw new ServiceException("Book ID is required for update.");
        }
        if (book.getId() <= 0) {
            throw new ServiceException("Book ID must be a positive value.");
        }

        // Validate that the book exists
        Book existing = findById(book.getId());
        if (existing == null) {
            throw new ServiceException("Cannot update: Book with ID " + book.getId() + " does not exist.");
        }

        try {
            // Delegate persistence to DAO
            bookDAO.update(book);

            // Return updated entity
            return book;

        } catch (SQLException e) {
            throw new ServiceException("Error updating book: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) throws ServiceException {
        // Validate ID
        if (id == null) {
            throw new ServiceException("Book ID cannot be null.");
        }
        if (id <= 0) {
            throw new ServiceException("Book ID must be a positive value.");
        }
        // Validate existence
        Book existing = findById(id);
        if (existing == null) {
            throw new ServiceException("Cannot delete: Book with ID " + id + " does not exist.");
        }
        try {
            // Delegate logical delete to DAO
            bookDAO.delete(id);
            
        } catch (SQLException e) {
            throw new ServiceException("Error deleting book: " + e.getMessage(), e);
        }
    }

    @Override
    public Book findById(Long id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("Book ID cannot be null.");
        }
        if (id <= 0) {
            throw new ServiceException("Book ID must be a positive value.");
        }
        try {
            return bookDAO.findById(id);
        } catch (SQLException e) {
            throw new ServiceException("Error retrieving book with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findAll() throws ServiceException {
        try {
            return bookDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error retrieving book list.", e);
        }
    }
    
    private void validateBook(Book book) throws ServiceException {
        int currentYear = LocalDate.now().getYear();

        if (book == null) {
            throw new ServiceException("Book cannot be null.");
        }

        // Title
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new ServiceException("Title is required.");
        }
        if (book.getTitle().length() > 150) {
            throw new ServiceException("Title exceeds maximum length (150).");
        }

        // Author
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new ServiceException("Author is required.");
        }
        if (book.getAuthor().length() > 120) {
            throw new ServiceException("Author exceeds maximum length (120).");
        }

        // Publisher (optional)
        if (book.getPublisher() != null && book.getPublisher().length() > 100) {
            throw new ServiceException("Publisher exceeds maximum length (100).");
        }

        // Publication Year
        if (book.getPublicationYear() < 0) {
            throw new ServiceException("Publication year must be positive.");
        }
        if (book.getPublicationYear() > currentYear) {
            throw new ServiceException("Publication year cannot exceed " + currentYear + ".");
        }
    }
}
