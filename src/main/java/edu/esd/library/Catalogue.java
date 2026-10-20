package edu.esd.library;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The library catalogue: the books the library owns and how many copies of each.
 *
 * <p>Guidelines:
 * <ul>
 *   <li>Store books in a {@code Map<String, Book>} keyed by ISBN.</li>
 *   <li>Store copy counts in two maps keyed by ISBN:
 *       {@code Map<String, Integer> totalCopies} and {@code Map<String, Integer> availableCopies}.</li>
 *   <li>Use {@code HashMap} for all three.</li>
 *   <li>Never return null to mean "not found" -- throw the exception instead.</li>
 * </ul>
 */
public class Catalogue {

    // TODO: declare the three maps described above
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Integer> totalCopies = new HashMap<>();
    private final Map<String, Integer> availableCopies = new HashMap<>();

    /**
     * Adds a new book with the given number of copies. All copies start on the shelf.
     *
     * @throws IllegalArgumentException if copies is less than 1
     * @throws DuplicateBookException   if a book with the same ISBN already exists
     */
    public void addBook(Book book, int copies) {
        if (copies < 1) {
            throw new IllegalArgumentException("Copies must be at least 1");
        }
        String isbn = book.getIsbn();
        if (books.containsKey(isbn)) {
            throw new DuplicateBookException(isbn);
        }
        books.put(isbn, book);
        totalCopies.put(isbn, copies);
        availableCopies.put(isbn, copies);
    }

    /**
     * @throws BookNotFoundException if there is no book with this ISBN
     */
    public Book findByIsbn(String isbn) {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);
        }
        return book;
    }

    /**
     * Finds books whose author contains {@code name}, ignoring upper/lower case.
     * For example "bloch" matches "Joshua Bloch".
     *
     * <p>Use a stream: filter, then sorted(Comparator.comparing(Book::getTitle)), then toList().
     *
     * @return matching books sorted by title; an empty list if none match
     */
    public List<Book> searchByAuthor(String name) {
        String target = name.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(target))
                .sorted(Comparator.comparing(Book::getTitle))
                .toList();
    }

    /**
     * Lends one copy: one fewer copy on the shelf.
     *
     * @throws BookNotFoundException       if there is no book with this ISBN
     * @throws NoCopiesAvailableException  if no copy is on the shelf
     */
    public void lendCopy(String isbn) {
        int available = availableCopies(isbn);
        if (available == 0) {
            throw new NoCopiesAvailableException(isbn);
        }
        availableCopies.put(isbn, available - 1);
    }

    /**
     * Takes a copy back: one more copy on the shelf.
     *
     * @throws BookNotFoundException  if there is no book with this ISBN
     * @throws IllegalStateException  if all copies are already on the shelf
     */
    public void returnCopy(String isbn) {
        int available = availableCopies(isbn);
        if (available == totalCopies.get(isbn)) {
            throw new IllegalStateException("All copies are already on the shelf");
        }
        availableCopies.put(isbn, available + 1);
    }

    /**
     * @return the number of copies on the shelf right now
     * @throws BookNotFoundException if there is no book with this ISBN
     */
    public int availableCopies(String isbn) {
        findByIsbn(isbn);
        return availableCopies.get(isbn);
    }
}