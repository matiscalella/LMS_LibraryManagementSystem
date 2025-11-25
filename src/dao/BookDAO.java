
package dao;

import config.DatabaseConnection;
import entities.BibliographicRecord;
import entities.Book;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class BookDAO implements GenericDAO<Book> {
    
// ----------- SQL statements
    private static final String INSERT_SQL =
        "INSERT INTO books (deleted, title, author, publisher, publication_year) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL =
        "SELECT " +
        "  b.id AS book_id, " +
        "  b.deleted AS book_deleted, " +
        "  b.title AS book_title, " +
        "  b.author AS book_author, " +
        "  b.publisher AS book_publisher, " +
        "  b.publication_year AS book_publication_year, " +
        "  br.id AS br_id, " +
        "  br.deleted AS br_deleted, " +
        "  br.isbn AS br_isbn, " +
        "  br.dewey_class AS br_dewey_class, " +
        "  br.shelf_location AS br_shelf_location, " +
        "  br.language AS br_language, " +
        "  br.book_id AS br_book_id " +
        "FROM books b " +
        "LEFT JOIN bibliographic_records br " +
        "       ON br.book_id = b.id " +
        "      AND br.deleted = FALSE " +
        "WHERE b.id = ? AND b.deleted = FALSE";
    
    private static final String SELECT_ALL_SQL =
        "SELECT " +
        "  b.id AS book_id, " +
        "  b.deleted AS book_deleted, " +
        "  b.title AS book_title, " +
        "  b.author AS book_author, " +
        "  b.publisher AS book_publisher, " +
        "  b.publication_year AS book_publication_year, " +
        "  br.id AS br_id, " +
        "  br.deleted AS br_deleted, " +
        "  br.isbn AS br_isbn, " +
        "  br.dewey_class AS br_dewey_class, " +
        "  br.shelf_location AS br_shelf_location, " +
        "  br.language AS br_language, " +
        "  br.book_id AS br_book_id " +
        "FROM books b " +
        "LEFT JOIN bibliographic_records br " +
        "       ON br.book_id = b.id " +
        "      AND br.deleted = FALSE " +
        "WHERE b.deleted = FALSE";
    
    private static final String UPDATE_SQL =
        "UPDATE books SET deleted = ?, title = ?, author = ?, publisher = ?, publication_year = ? " +
        "WHERE id = ?";
    
    private static final String LOGICAL_DELETE_SQL =
        "UPDATE books SET deleted = TRUE WHERE id = ?";
    
    @Override
    public void create(Book book) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            create(book, conn);
        }
    }

    @Override
    public Book findById(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(id, conn);
        }
    }

    @Override
    public List<Book> findAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findAll(conn);
        }
    }

    @Override
    public void update(Book book) throws SQLException {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book ID cannot be null for update.");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            update(book, conn);
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for delete.");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            delete(id, conn);
        }
    }
    
// ---------- Transactional overloads (using external Connection) ----------

    /**
     * Inserts a Book using an existing Connection.
     */
    public void create(Book book, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, book.isDeleted());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());

            if (book.getPublicationYear() != null) {
                ps.setInt(5, book.getPublicationYear());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long generatedId = rs.getLong(1);
                    book.setId(generatedId);
                }
            }
        }
    }

    /**
     * Finds a Book by ID using an existing Connection.
     */
    public Book findById(Long id, Connection conn) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildBookFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lists all Books using an existing Connection.
     */
    public List<Book> findAll(Connection conn) throws SQLException {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
        ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = buildBookFromResultSet(rs);
                books.add(book);
            }
        }
        return books;
    }

    /**
     * Updates a Book using an existing Connection.
     */
    public void update(Book book, Connection conn) throws SQLException {
        if (book.getId() == null) {
            throw new IllegalArgumentException("Book ID cannot be null for update.");
        }
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setBoolean(1, book.isDeleted());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            if (book.getPublicationYear() != null) {
                ps.setInt(5, book.getPublicationYear());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setLong(6, book.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Logical delete using an existing Connection.
     */
    public void delete(Long id, Connection conn) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for delete.");
        }
        try (PreparedStatement ps = conn.prepareStatement(LOGICAL_DELETE_SQL)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    
    /**
    * Builds a Book (and its optional BibliographicRecord)
    * from the current row of the ResultSet.
    */
    private Book buildBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();

        // ------ Book fields ------
        book.setId(rs.getLong("book_id"));
        book.setDeleted(rs.getBoolean("book_deleted"));
        book.setTitle(rs.getString("book_title"));
        book.setAuthor(rs.getString("book_author"));
        book.setPublisher(rs.getString("book_publisher"));

        int year = rs.getInt("book_publication_year");
        if (rs.wasNull()) {
            book.setPublicationYear(null);
        } else {
            book.setPublicationYear(year);
        }

        // ------ BibliographicRecord (optional) ------
        Long brId = rs.getLong("br_id");

        if (!rs.wasNull()) {
            BibliographicRecord br = new BibliographicRecord();
            br.setId(brId);
            br.setDeleted(rs.getBoolean("br_deleted"));
            br.setIsbn(rs.getString("br_isbn"));
            br.setDeweyClass(rs.getString("br_dewey_class"));
            br.setShelfLocation(rs.getString("br_shelf_location"));
            br.setLanguage(rs.getString("br_language"));
            long fk = rs.getLong("br_book_id");
            if (rs.wasNull()) {
                br.setBookId(null);
            } else {
                br.setBookId(fk);
            }

            book.setBibliographicRecord(br);
        } else {
            book.setBibliographicRecord(null);
        }

        return book;
    }
    
}
