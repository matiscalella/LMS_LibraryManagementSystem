
package dao;

import entities.BibliographicRecord;
import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BibliographicRecord findById(Long id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<BibliographicRecord> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(BibliographicRecord bibliographicRecord) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Long id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
// ---------- Transactional overloads (using external Connection) ----------
    
    public void create(BibliographicRecord bibliographicRecord, Connection conn) throws SQLException {
        
    }

    public BibliographicRecord findById(Long id, Connection conn) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<BibliographicRecord> findAll(Connection conn) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void update(BibliographicRecord bibliographicRecord, Connection conn) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void delete(Long id, Connection conn) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
