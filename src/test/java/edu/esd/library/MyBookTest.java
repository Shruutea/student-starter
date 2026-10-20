package edu.esd.library;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** My own tests for Book. */
class MyBookTest {

    private static final String ISBN = "9780134685991";

    // ---------- valid books ----------

    @Test
    void validBookKeepsItsValues() {
        Book book = new Book(ISBN, "Effective Java", "Joshua Bloch", 2018);

        assertEquals(ISBN, book.getIsbn());
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals(2018, book.getYear());
    }

    @Test
    void currentYearIsAllowed() {
        int thisYear = Year.now().getValue();
        Book book = new Book(ISBN, "New Book", "Someone", thisYear);

        assertEquals(thisYear, book.getYear());
    }

    // ---------- invalid ISBN ----------

    @Test
    void nullIsbnIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(null, "Title", "Author", 2000));
    }

    @Test
    void isbnWith12DigitsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("978013468599", "Title", "Author", 2000));
    }

    @Test
    void isbnWith14DigitsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("97801346859912", "Title", "Author", 2000));
    }

    @Test
    void isbnWithLettersIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("97801346859AB", "Title", "Author", 2000));
    }

    @Test
    void isbnWithDashesIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("978-0134685991", "Title", "Author", 2000));
    }

    // ---------- invalid title / author ----------

    @Test
    void nullTitleIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(ISBN, null, "Author", 2000));
    }

    @Test
    void emptyTitleIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(ISBN, "", "Author", 2000));
    }

    @Test
    void blankTitleIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(ISBN, "   ", "Author", 2000));
    }

    @Test
    void nullAuthorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(ISBN, "Title", null, 2000));
    }

    @Test
    void blankAuthorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(ISBN, "Title", "   ", 2000));
    }

    // ---------- invalid year ----------

    @Test
    void futureYearIsRejected() {
        int nextYear = Year.now().getValue() + 1;
        assertThrows(IllegalArgumentException.class,
                () -> new Book(ISBN, "Title", "Author", nextYear));
    }

    // ---------- equals / hashCode / toString ----------

    @Test
    void booksWithSameIsbnAreEqual() {
        Book a = new Book(ISBN, "Effective Java", "Joshua Bloch", 2018);
        Book b = new Book(ISBN, "Different Title", "Different Author", 2000);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void booksWithDifferentIsbnAreNotEqual() {
        Book a = new Book(ISBN, "Same Title", "Same Author", 2018);
        Book b = new Book("9780132350884", "Same Title", "Same Author", 2018);

        assertNotEquals(a, b);
    }

    @Test
    void bookIsNotEqualToNullOrOtherType() {
        Book book = new Book(ISBN, "Effective Java", "Joshua Bloch", 2018);

        assertFalse(book.equals(null));
        assertFalse(book.equals(ISBN));
    }

    @Test
    void toStringShowsIsbnAndTitle() {
        Book book = new Book(ISBN, "Effective Java", "Joshua Bloch", 2018);
        String text = book.toString();

        assertTrue(text.contains(ISBN));
        assertTrue(text.contains("Effective Java"));
    }
}