package main;

import java.util.List;
import java.util.Scanner;

import model.Book;
import model.BibliographicRecord;
import service.BookServiceImpl;
import service.BibliographicRecordServiceImpl;
import service.ServiceException;

/**
 * Console-based menu to manually debug the Service Layer of the
 * Library Management System.
 *
 * <p>
 * This class acts as a simple CLI UI and must interact ONLY with
 * the Service Layer (no DAO, no SQL).
 * </p>
 */
public class MenuHandler {

    private final Scanner scanner;
    private final BookServiceImpl bookService;
    private final BibliographicRecordServiceImpl bibliographicRecordService;

    public MenuHandler() {
        this.scanner = new Scanner(System.in);
        this.bookService = new BookServiceImpl();
        this.bibliographicRecordService = new BibliographicRecordServiceImpl();
    }

    /**
     * Starts the main menu loop.
     */
    public void start() {
        boolean exit = false;

        while (!exit) {
            printMainMenu();
            int option = readInt("Select an option: ");

            switch (option) {
                case 1 -> createBook();
                case 2 -> listAllBooks();
                case 3 -> findBookById();
                case 4 -> updateBook();
                case 5 -> deleteBook();

                case 6 -> createBibliographicRecord();
                case 7 -> listAllBibliographicRecords();
                case 8 -> findBibliographicRecordById();
                case 9 -> updateBibliographicRecord();
                case 10 -> deleteBibliographicRecord();

                case 11 -> assignRecordToBook();

                case 0 -> {
                    System.out.println("Exiting Library Management System debug menu...");
                    exit = true;
                }

                default -> System.out.println("Invalid option. Please try again.");
            }

            System.out.println();
        }

        scanner.close();
    }

    // =====================================================
    //                     MAIN MENU
    // =====================================================

    private void printMainMenu() {
        System.out.println("================================================");
        System.out.println("        LIBRARY MANAGEMENT SYSTEM - DEBUG");
        System.out.println("================================================");
        System.out.println(" 1. Create Book");
        System.out.println(" 2. List all Books");
        System.out.println(" 3. Find Book by ID");
        System.out.println(" 4. Update Book");
        System.out.println(" 5. Delete Book (logical)");
        System.out.println("------------------------------------------------");
        System.out.println(" 6. Create BibliographicRecord");
        System.out.println(" 7. List all BibliographicRecords");
        System.out.println(" 8. Find BibliographicRecord by ID");
        System.out.println(" 9. Update BibliographicRecord");
        System.out.println("10. Delete BibliographicRecord (logical)");
        System.out.println("------------------------------------------------");
        System.out.println("11. Assign BibliographicRecord → Book");
        System.out.println("------------------------------------------------");
        System.out.println(" 0. Exit");
        System.out.println("================================================");
    }

    // =====================================================
    //                INPUT HELPERS (Scanner)
    // =====================================================

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer number.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid numeric ID.");
            }
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("This value is required. Please enter a non-empty value.");
        }
    }

    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    private Integer readRequiredInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer value.");
            }
        }
    }

    private Integer readOptionalInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer or leave it empty to skip.");
            }
        }
    }

    // =====================================================
    //                      BOOK CRUD
    // =====================================================

    private void createBook() {
        System.out.println("=== Create Book ===");

        String title = readNonEmptyString("Title: ");
        String author = readNonEmptyString("Author: ");
        String publisher = readOptionalString("Publisher (optional): ");
        Integer publicationYear = readRequiredInteger("Publication year (e.g. 2020): ");

        Book book = new Book();
        book.setDeleted(false);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPublicationYear(publicationYear);

        try {
            Book created = bookService.create(book);
            System.out.println("Book created successfully:");
            System.out.println(created);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listAllBooks() {
        System.out.println("=== List of Books ===");
        try {
            List<Book> books = bookService.findAll();
            if (books == null || books.isEmpty()) {
                System.out.println("No books found.");
                return;
            }
            books.forEach(System.out::println);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void findBookById() {
        System.out.println("=== Find Book by ID ===");
        long id = readLong("Book ID: ");

        try {
            Book book = bookService.findById(id);
            if (book == null) {
                System.out.println("Book not found with ID: " + id);
            } else {
                System.out.println("Book found:");
                System.out.println(book);
            }
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBook() {
        System.out.println("=== Update Book ===");
        long id = readLong("Book ID to update: ");

        try {
            Book book = bookService.findById(id);
            if (book == null) {
                System.out.println("Book not found with ID: " + id);
                return;
            }

            System.out.println("Current data:");
            System.out.println(book);
            System.out.println("Leave fields empty to keep current value.");

            String newTitle = readOptionalString("New title (" + book.getTitle() + "): ");
            String newAuthor = readOptionalString("New author (" + book.getAuthor() + "): ");
            String newPublisher = readOptionalString("New publisher (" + book.getPublisher() + "): ");
            Integer newYear = readOptionalInteger("New publication year (" + book.getPublicationYear() + "): ");

            if (newTitle != null) book.setTitle(newTitle);
            if (newAuthor != null) book.setAuthor(newAuthor);
            if (newPublisher != null) book.setPublisher(newPublisher);
            if (newYear != null) book.setPublicationYear(newYear);

            Book updated = bookService.update(book);
            System.out.println("Book updated successfully:");
            System.out.println(updated);

        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        System.out.println("=== Delete Book (logical) ===");
        long id = readLong("Book ID to delete: ");

        try {
            bookService.delete(id);
            System.out.println("Book deleted (logical) if it existed.");
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    //             BIBLIOGRAPHIC RECORD CRUD
    // =====================================================

    private void createBibliographicRecord() {
        System.out.println("=== Create BibliographicRecord ===");

        String isbn = readOptionalString("ISBN (optional, max 17 chars): ");
        String deweyClass = readOptionalString("Dewey class (optional): ");
        String shelfLocation = readOptionalString("Shelf location (optional): ");
        String language = readOptionalString("Language (optional): ");

        BibliographicRecord record = new BibliographicRecord();
        record.setDeleted(false);
        record.setIsbn(isbn);
        record.setDeweyClass(deweyClass);
        record.setShelfLocation(shelfLocation);
        record.setLanguage(language);

        try {
            BibliographicRecord created = bibliographicRecordService.create(record);
            System.out.println("BibliographicRecord created successfully:");
            System.out.println(created);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listAllBibliographicRecords() {
        System.out.println("=== List of BibliographicRecords ===");
        try {
            List<BibliographicRecord> records = bibliographicRecordService.findAll();
            if (records == null || records.isEmpty()) {
                System.out.println("No bibliographic records found.");
                return;
            }
            records.forEach(System.out::println);
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void findBibliographicRecordById() {
        System.out.println("=== Find BibliographicRecord by ID ===");
        long id = readLong("BibliographicRecord ID: ");

        try {
            BibliographicRecord record = bibliographicRecordService.findById(id);
            if (record == null) {
                System.out.println("BibliographicRecord not found with ID: " + id);
            } else {
                System.out.println("BibliographicRecord found:");
                System.out.println(record);
            }
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBibliographicRecord() {
        System.out.println("=== Update BibliographicRecord ===");
        long id = readLong("BibliographicRecord ID to update: ");

        try {
            BibliographicRecord record = bibliographicRecordService.findById(id);
            if (record == null) {
                System.out.println("BibliographicRecord not found with ID: " + id);
                return;
            }

            System.out.println("Current data:");
            System.out.println(record);
            System.out.println("Leave fields empty to keep current value.");

            String newIsbn = readOptionalString("New ISBN (" + record.getIsbn() + "): ");
            String newDewey = readOptionalString("New Dewey class (" + record.getDeweyClass() + "): ");
            String newShelf = readOptionalString("New shelf location (" + record.getShelfLocation() + "): ");
            String newLanguage = readOptionalString("New language (" + record.getLanguage() + "): ");

            if (newIsbn != null) record.setIsbn(newIsbn);
            if (newDewey != null) record.setDeweyClass(newDewey);
            if (newShelf != null) record.setShelfLocation(newShelf);
            if (newLanguage != null) record.setLanguage(newLanguage);

            BibliographicRecord updated = bibliographicRecordService.update(record);
            System.out.println("BibliographicRecord updated successfully:");
            System.out.println(updated);

        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteBibliographicRecord() {
        System.out.println("=== Delete BibliographicRecord (logical) ===");
        long id = readLong("BibliographicRecord ID to delete: ");

        try {
            bibliographicRecordService.delete(id);
            System.out.println("BibliographicRecord deleted (logical) if it existed.");
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    //       OPTION 11 — ASSIGN RECORD → BOOK
    // =====================================================

    private void assignRecordToBook() {
        System.out.println("=== Assign BibliographicRecord → Book ===");

        long recordId = readLong("BibliographicRecord ID: ");
        long bookId = readLong("Book ID to associate: ");

        try {
            bibliographicRecordService.assignRecordToBook(recordId, bookId);
            System.out.println("Record " + recordId + " successfully assigned to Book " + bookId + ".");
        } catch (ServiceException e) {
            System.out.println("[SERVICE ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }
}
