package service;

import dao.BookDAO;
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
    public Book create(Book entity) throws ServiceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Book update(Book entity) throws ServiceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void delete(Long id) throws ServiceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Book findById(Long id) throws ServiceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<Book> findAll() throws ServiceException {
        throw new UnsupportedOperationException("Not implemented yet.");
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
