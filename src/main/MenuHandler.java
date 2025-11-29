package main;

import java.util.List;
import java.util.Scanner;

import model.Book;
import model.BibliographicRecord;

import service.BookServiceImpl;
import service.BibliographicRecordServiceImpl;
import service.LibraryTransactionService;
import service.ServiceException;

/**
 * Console-based debugging menu for the Library Management System (LMS).
 *
 * <p>This menu acts as a lightweight UI layer designed exclusively for
 * manual testing and verification of:</p>
 *
 * <ul>
 *     <li>Standard CRUD operations via {@link BookServiceImpl} and
 *         {@link BibliographicRecordServiceImpl}</li>
 *     <li>High-level transactional workflows via {@link LibraryTransactionService}</li>
 *     <li>Basic 1→1 association between Book and BibliographicRecord</li>
 *     <li>User input validation for quick inspection of the Service Layer behavior</li>
 * </ul>
 *
 * <p>It intentionally avoids DAO and database logic, delegating all business
 * logic to the Service Layer in accordance with the layered architecture.</p>
 */
public class MenuHandler {

    private final Scanner scanner;
    private final BookServiceImpl bookService;
    private final BibliographicRecordServiceImpl bibliographicRecordService;
    private final LibraryTransactionService txService;

    /**
     * Default constructor initializing required services and input scanner.
     */
    public MenuHandler() {
        this.scanner = new Scanner(System.in);
        this.bookService = new BookServiceImpl();
        this.bibliographicRecordService = new BibliographicRecordServiceImpl();
        this.txService = new LibraryTransactionService();
    }

    /**
     * Starts the main menu loop, reading user input and delegating
     * to menu-specific handler methods.
     */
    public void start() {
        boolean exit = false;

        while (!exit) {
            printMainMenu();
            int option = readInt("Select an option: ");

            switch (option) {

                // BOOK CRUD
                case 1 -> createBook();
                case 2 -> listAllBooks();
                case 3 -> findBookById();
                case 4 -> updateBook();
                case 5 -> deleteBook();

                // BIBLIOGRAPHIC RECORD CRUD
                case 6 -> createBibliographicRecord();
                case 7 -> listAllBibliographicRecords();
                case 8 -> findBibliographicRecordById();
                case 9 -> updateBibliographicRecord();
                case 10 -> deleteBibliographicRecord();

                // SIMPLE RELATION ASSIGNMENT
                case 11 -> assignBibliographicRecordToBook();

                // TRANSACTIONAL WORKFLOWS
                case 12 -> createBookWithBibliographicRecordTx();
                case 13 -> moveBibliographicRecordTx();
                case 14 -> deleteBookAndRecordTx();

                case 0 -> {
                    System.out.println("Exiting program...");
                    exit = true;
                }

                default ->
                    System.out.println("Invalid option, please try again.");
            }
            System.out.println();
        }

        scanner.close();
    }

    // =====================================================
    //                     MAIN MENU
    // =====================================================

    /**
     * Prints the menu options in a clean, structured format.
     */
    private void printMainMenu() {
        System.out.println("================================================");
        System.out.println("        LIBRARY MANAGEMENT SYSTEM - DEBUG");
        System.out.println("================================================");
        System.out.println(" BOOK CRUD");
        System.out.println(" 1. Create Book");
        System.out.println(" 2. List Books");
        System.out.println(" 3. Search Book by ID");
        System.out.println(" 4. Update Book");
        System.out.println(" 5. Delete Book (logical)");
        System.out.println("------------------------------------------------");
        System.out.println(" BIBLIOGRAPHIC RECORD CRUD");
        System.out.println(" 6. Create BibliographicRecord");
        System.out.println(" 7. List BibliographicRecords");
        System.out.println(" 8. Search BibliographicRecord by ID");
        System.out.println(" 9. Update BibliographicRecord");
        System.out.println("10. Delete BibliographicRecord (logical)");
        System.out.println("------------------------------------------------");
        System.out.println(" SIMPLE RELATION MANAGEMENT");
        System.out.println("11. Assign BibliographicRecord → Book");
        System.out.println("------------------------------------------------");
        System.out.println(" TRANSACTIONAL WORKFLOWS");
        System.out.println("12. Create Book + BibliographicRecord (TX)");
        System.out.println("13. Move BibliographicRecord → another Book (TX)");
        System.out.println("14. Delete Book + BibliographicRecord (TX)");
        System.out.println("------------------------------------------------");
        System.out.println(" 0. Exit");
        System.out.println("================================================");
    }

    // =====================================================
    //                  INPUT UTILITIES
    // =====================================================

    /**
     * Safely reads an integer from the console.
     *
     * @param prompt the message to display to the user
     * @return the parsed integer
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    /**
     * Safely reads a long value (typically an ID).
     *
     * @param prompt the message to display
     * @return the parsed long value
     */
    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric ID.");
            }
        }
    }

    /**
     * Reads a required, non-empty string. Re-prompts until valid.
     *
     * @param prompt the message to display
     * @return non-empty String
     */
    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    /**
     * Reads an optional string; returns null if empty.
     */
    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? null : value;
    }

    /**
     * Reads an optional integer; returns null if empty.
     */
    private Integer readOptionalInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer or leave blank.");
            }
        }
    }

    /**
     * Reads a required integer; re-prompts until a valid number is provided.
     */
    private Integer readRequiredInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.valueOf(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    /**
     * Basic yes/no confirmation prompt used for delete operations.
     */
    private void confirm(String message) {
        System.out.print(message + " (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            throw new RuntimeException("Operation cancelled by user.");
        }
    }

    // =====================================================
    //                    BOOK CRUD
    // =====================================================

    /**
     * Prompts the user for book details and invokes {@link BookServiceImpl#create(Book)}.
     */
    private void createBook() {
        System.out.println("=== Create Book ===");
        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        String publisher = readOptionalString("Publisher: ");
        Integer year = readRequiredInteger("Publication year: ");

        Book book = new Book(null, false, title, author, publisher, year);

        try {
            Book created = bookService.create(book);
            System.out.println("Book created:\n" + created);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Prints all Books using {@link BookServiceImpl#findAll()}.
     */
    private void listAllBooks() {
        System.out.println("=== Books ===");
        try {
            List<Book> books = bookService.findAll();
            if (books.isEmpty()) System.out.println("No books found.");
            else books.forEach(System.out::println);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Searches and displays a Book by ID.
     */
    private void findBookById() {
        long id = readLong("Book ID: ");
        try {
            Book book = bookService.findById(id);
            System.out.println(book == null ? "Book not found." : book);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Updates editable fields of a Book.
     */
    private void updateBook() {
        long id = readLong("Book ID: ");

        try {
            Book book = bookService.findById(id);
            if (book == null) {
                System.out.println("Book not found.");
                return;
            }

            System.out.println("Current Book:\n" + book);
            System.out.println("Leave empty to keep current value.");

            String t = readOptionalString("Title: ");
            String a = readOptionalString("Author: ");
            String p = readOptionalString("Publisher: ");
            Integer y = readOptionalInteger("Publication year: ");

            if (t != null) book.setTitle(t);
            if (a != null) book.setAuthor(a);
            if (p != null) book.setPublisher(p);
            if (y != null) book.setPublicationYear(y);

            Book updated = bookService.update(book);
            System.out.println("Updated:\n" + updated);

        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Logically deletes a Book.
     */
    private void deleteBook() {
        long id = readLong("Book ID to delete: ");
        try {
            confirm("Confirm delete?");
            bookService.delete(id);
            System.out.println("Book deleted.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================
    //          BIBLIOGRAPHIC RECORD CRUD
    // =====================================================

    /**
     * Prompts for BibliographicRecord details and performs creation.
     */
    private void createBibliographicRecord() {
        System.out.println("=== Create BibliographicRecord ===");

        String isbn = readOptionalString("ISBN: ");
        String dewey = readOptionalString("Dewey: ");
        String shelf = readOptionalString("Shelf: ");
        String lang = readOptionalString("Language: ");

        BibliographicRecord bibliographicRecord =
                new BibliographicRecord(null, false, isbn, dewey, shelf, lang, null);

        try {
            BibliographicRecord created = bibliographicRecordService.create(bibliographicRecord);
            System.out.println("Created:\n" + created);

        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Displays all BibliographicRecord entities.
     */
    private void listAllBibliographicRecords() {
        System.out.println("=== BibliographicRecords ===");
        try {
            List<BibliographicRecord> list = bibliographicRecordService.findAll();
            if (list.isEmpty()) System.out.println("No records found.");
            else list.forEach(System.out::println);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Searches and displays a BibliographicRecord by ID.
     */
    private void findBibliographicRecordById() {
        long id = readLong("Record ID: ");
        try {
            BibliographicRecord bibliographicRecord = bibliographicRecordService.findById(id);
            System.out.println(bibliographicRecord == null ? "Record not found." : bibliographicRecord);

        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Allows the user to modify fields of a BibliographicRecord.
     */
    private void updateBibliographicRecord() {
        long id = readLong("Record ID to update: ");

        try {
            BibliographicRecord bibliographicRecord = bibliographicRecordService.findById(id);
            if (bibliographicRecord == null) {
                System.out.println("Record not found.");
                return;
            }

            System.out.println("Current Record:\n" + bibliographicRecord);
            System.out.println("Leave empty to keep current value.");

            String isbn = readOptionalString("New ISBN: ");
            String dewey = readOptionalString("New Dewey: ");
            String shelf = readOptionalString("New Shelf: ");
            String lang = readOptionalString("New Language: ");

            if (isbn != null) bibliographicRecord.setIsbn(isbn);
            if (dewey != null) bibliographicRecord.setDeweyClass(dewey);
            if (shelf != null) bibliographicRecord.setShelfLocation(shelf);
            if (lang != null) bibliographicRecord.setLanguage(lang);

            BibliographicRecord updated = bibliographicRecordService.update(bibliographicRecord);
            System.out.println("Updated:\n" + updated);

        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    /**
     * Logically deletes a BibliographicRecord.
     */
    private void deleteBibliographicRecord() {
        long id = readLong("Record ID to delete: ");
        try {
            confirm("Confirm delete?");
            bibliographicRecordService.delete(id);
            System.out.println("Record deleted.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================
    //             SIMPLE RELATION ASSIGNMENT
    // =====================================================

    /**
     * Assigns an existing BibliographicRecord to an existing Book
     * using {@link BibliographicRecordServiceImpl#assignRecordToBook(Long, Long)}.
     */
    private void assignBibliographicRecordToBook() {
        long recordId = readLong("Record ID: ");
        long bookId = readLong("Book ID: ");

        try {
            bibliographicRecordService.assignRecordToBook(recordId, bookId);
            System.out.println("Record assigned to Book.");
        } catch (Exception e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        }
    }

    // =====================================================
    //          TRANSACTIONAL WORKFLOWS (TX)
    // =====================================================

    /**
     * Creates a Book together with its BibliographicRecord
     * as a single atomic transaction handled by {@link LibraryTransactionService}.
     */
    private void createBookWithBibliographicRecordTx() {
        System.out.println("=== Create Book + BibliographicRecord (TX) ===");

        // Book input
        String title = readNonEmptyString("Book Title: ");
        String author = readNonEmptyString("Author: ");
        String publisher = readOptionalString("Publisher: ");
        Integer year = readRequiredInteger("Publication year: ");

        Book book = new Book(null, false, title, author, publisher, year);

        // Record input
        String isbn = readOptionalString("Record ISBN: ");
        String dewey = readOptionalString("Record Dewey: ");
        String shelf = readOptionalString("Record Shelf: ");
        String lang = readOptionalString("Record Language: ");

        BibliographicRecord bibliographicRecord =
                new BibliographicRecord(null, false, isbn, dewey, shelf, lang, null);

        try {
            txService.createBookWithRecord(book, bibliographicRecord);
            System.out.println("Transaction OK: Book + Record created.");

        } catch (Exception e) {
            System.out.println("[TX ERROR] " + e.getMessage());
        }
    }

    /**
    * Assigns or moves a BibliographicRecord to another Book using a transaction.
    * <p>
    * If the record has no book_id (NULL), it will be assigned to the new Book.
    * If the record already has a book_id, it will be moved to the new Book.
    * </p>
    */
   private void moveBibliographicRecordTx() {
       System.out.println("=== Assign / Move BibliographicRecord → Book (TX) ===");

       long recordId = readLong("BibliographicRecord ID: ");
       long newBookId = readLong("Target Book ID: ");

       try {
           txService.moveBibliographicRecordToAnotherBook(recordId, newBookId);

           System.out.println("""
                   Operation successful:
                   - If the record previously had no Book, it has now been assigned.
                   - If it had a Book, it has now been moved to the new Book.
                   """);

       } catch (Exception e) {
           System.out.println("[TX ERROR] " + e.getMessage());
       }
   }

    /**
     * Deletes a Book and its associated BibliographicRecord inside a single transaction.
     */
    private void deleteBookAndRecordTx() {
        System.out.println("=== Delete Book + Record (TX) ===");

        long bookId = readLong("Book ID: ");

        try {
            confirm("Confirm delete?");
            txService.deleteBookAndRecord(bookId);
            System.out.println("Book + Record deleted.");

        } catch (Exception e) {
            System.out.println("[TX ERROR] " + e.getMessage());
        }
    }
}
