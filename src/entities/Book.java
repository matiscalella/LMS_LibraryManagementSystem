/**
 * Represents a Book entity in the Library Management System.
 * Contains basic metadata and a 1→1 reference to a BibliographicRecord.
 */
package entities;

public class Book extends Base {
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private BibliographicRecord bibliographicRecord;

    public Book(Long id, String title, String author, String publisher, Integer publicationYear, BibliographicRecord bibliographicRecord) {
        super(id, false);
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.bibliographicRecord = bibliographicRecord;
    }
    
    public Book() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public BibliographicRecord getBibliographicRecord() {
        return bibliographicRecord;
    }

    public void setBibliographicRecord(BibliographicRecord bibliographicRecord) {
        this.bibliographicRecord = bibliographicRecord;
    }
    

    @Override
    public String toString() {
        return "Book { " +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publicationYear=" + publicationYear +
                " }";
    }
}
