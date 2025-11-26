/**
 * DAO implementation for managing {@link entities.BibliographicRecord}
 * persistence operations.
 *
 * <p>This class exposes CRUD operations for the bibliographic_records table and
 * provides transactional overloads that reuse an external
 * {@link java.sql.Connection}.</p>
 *
 * <p>The BibliographicRecordDAO handles:</p>
 * <ul>
 *     <li>Insert, update, find and logical delete of BibliographicRecord entities</li>
 *     <li>Mapping of ResultSet rows to fully constructed entity objects</li>
 *     <li>Management of the foreign key <code>book_id</code> in the 1→1 relationship</li>
 * </ul>
 *
 * <p>This DAO does not manage transactions. Responsibility for commits and rollbacks
 * belongs to the Service layer.</p>
 */
package dao;

import entities.BibliographicRecord;
import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.sql.Types;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BibliographicRecordDAO implements GenericDAO<BibliographicRecord> {

// ----------- SQL statements
    private static final String INSERT_SQL = "INSERT INTO bibliographic_records (deleted, isbn, dewey_class, shelf_location, language, book_id)"+
            " VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL =
        "SELECT " +
        "  br.id AS br_id, " +
        "  br.deleted AS br_deleted, " +
        "  br.isbn AS br_isbn, " +
        "  br.dewey_class AS br_dewey_class, " +
        "  br.shelf_location AS br_shelf_location, " +
        "  br.language AS br_language, " +
        "  br.book_id AS br_book_id " +
        "FROM bibliographic_records br " +
        "WHERE br.id = ? AND br.deleted = FALSE";
    
    private static final String SELECT_ALL_SQL =
        "SELECT " +
        "  br.id AS br_id, " +
        "  br.deleted AS br_deleted, " +
        "  br.isbn AS br_isbn, " +
        "  br.dewey_class AS br_dewey_class, " +
        "  br.shelf_location AS br_shelf_location, " +
        "  br.language AS br_language, " +
        "  br.book_id AS br_book_id " +
        "FROM bibliographic_records br " +
        "WHERE br.deleted = FALSE";
    
    private static final String UPDATE_SQL =
        "UPDATE bibliographic_records " +
        "SET deleted = ?, isbn = ?, dewey_class = ?, shelf_location = ?, language = ?, book_id = ? " +
        "WHERE id = ?";
    
    private static final String LOGICAL_DELETE_SQL =
            "UPDATE bibliographic_records SET deleted = TRUE WHERE id = ?";
    
    @Override
    public void create(BibliographicRecord bibliographicRecord) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            create(bibliographicRecord, conn);
        }
    }

    @Override
    public BibliographicRecord findById(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Bibliographic record ID cannot be null");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(id, conn);
        }        
    }

    @Override
    public List<BibliographicRecord> findAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findAll(conn);
        }
    }

    @Override
    public void update(BibliographicRecord bibliographicRecord) throws SQLException {
        if (bibliographicRecord.getId() == null) {
            throw new IllegalArgumentException("Bibliographic record ID cannot be null for update.");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            update(bibliographicRecord, conn);
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Bibliographic record ID cannot be null for delete.");
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            delete(id, conn);
        }
    }
    
// ---------- Transactional overloads (using external Connection) ----------
    /**
     * Inserts a Bibliographic Record using an existing Connection.
     */
    public void create(BibliographicRecord bibliographicRecord, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, bibliographicRecord.isDeleted());
            ps.setString(2, bibliographicRecord.getIsbn());
            ps.setString(3, bibliographicRecord.getDeweyClass());
            ps.setString(4, bibliographicRecord.getShelfLocation());
            ps.setString(5, bibliographicRecord.getLanguage());
            if (bibliographicRecord.getBookId() != null) {
                ps.setLong(6, bibliographicRecord.getBookId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    bibliographicRecord.setId(id);
                }
            }
        }
    }

    /**
     * Finds a Bibliographic Record by ID using an existing Connection.
     */
    public BibliographicRecord findById(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildBibliographicRecordFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Lists all Bibliographic Records using an existing Connection.
     */
    public List<BibliographicRecord> findAll(Connection conn) throws SQLException {
        List<BibliographicRecord> bibliographicRecords = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
        ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BibliographicRecord bibliographicRecord = buildBibliographicRecordFromResultSet(rs);
                bibliographicRecords.add(bibliographicRecord);
            }
        }
        return bibliographicRecords;
    }
    
    /**
     * Updates a Bibliographic Record using an existing Connection.
     */
    public void update(BibliographicRecord bibliographicRecord, Connection conn) throws SQLException {
        if (bibliographicRecord.getId()== null) {
            throw new IllegalArgumentException("Bibliographic Record ID cannot be null for update.");
        }
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setBoolean(1, bibliographicRecord.isDeleted());
            ps.setString(2, bibliographicRecord.getIsbn());
            ps.setString(3, bibliographicRecord.getDeweyClass());
            ps.setString(4, bibliographicRecord.getShelfLocation());
            ps.setString(5, bibliographicRecord.getLanguage());
            if (bibliographicRecord.getBookId() != null) {
                ps.setLong(6, bibliographicRecord.getBookId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            ps.setLong(7, bibliographicRecord.getId());
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
    * Builds a Bibliographic Record from the current row of the ResultSet.
    */
    private BibliographicRecord buildBibliographicRecordFromResultSet(ResultSet rs) throws SQLException {
        BibliographicRecord bibliographicRecord = new BibliographicRecord();

        // ------ Bibliographic Record fields ------
        bibliographicRecord.setId(rs.getLong("br_id"));
        bibliographicRecord.setDeleted(rs.getBoolean("br_deleted"));
        bibliographicRecord.setIsbn(rs.getString("br_isbn"));
        bibliographicRecord.setDeweyClass(rs.getString("br_dewey_class"));
        bibliographicRecord.setShelfLocation(rs.getString("br_shelf_location"));
        bibliographicRecord.setLanguage(rs.getString("br_language"));
        long fk = rs.getLong("br_book_id");
         if (rs.wasNull()) { 
             bibliographicRecord.setBookId(null); 
         } else { 
             bibliographicRecord.setBookId(fk); 
         }
        return bibliographicRecord;
    }
}
