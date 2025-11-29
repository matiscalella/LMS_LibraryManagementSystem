package model;
/**
 * Represents the bibliographic record associated with a Book.
 * Used to store classification, ISBN and catalog information.
 */
public class BibliographicRecord extends Base {
    private String isbn;
    private String deweyClass;
    private String shelfLocation;
    private String language;
    private Long bookId;

    public BibliographicRecord(Long id, String isbn, String deweyClass, String shelfLocation, String language) {
        super(id, false);
        this.isbn = isbn;
        this.deweyClass = deweyClass;
        this.shelfLocation = shelfLocation;
        this.language = language;
    }
    
    public BibliographicRecord(Long id, boolean deleted, String isbn, String deweyClass, String shelfLocation, String language, Long bookId) {
        super(id, deleted);
        this.isbn = isbn;
        this.deweyClass = deweyClass;
        this.shelfLocation = shelfLocation;
        this.language = language;
        this.bookId = bookId;
    }
    public BibliographicRecord() {}

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDeweyClass() {
        return deweyClass;
    }

    public void setDeweyClass(String deweyClass) {
        this.deweyClass = deweyClass;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
    public Long getBookId() { return bookId; }
    
    public void setBookId(Long bookId) { this.bookId = bookId; }

    @Override
    public String toString() {
        return "BibliographicRecord { " +
                "id=" + getId() +
                ", isbn='" + isbn + '\'' +
                ", deweyClass='" + deweyClass + '\'' +
                ", shelfLocation='" + shelfLocation + '\'' +
                ", language='" + language + '\'' +
                " }";
    }   
}
