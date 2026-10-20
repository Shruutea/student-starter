package edu.esd.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** My own tests for Catalogue. */
class MyCatalogueTest {

    private static final String EJ_ISBN = "9780134685991";
    private static final String CC_ISBN = "9780132350884";
    private static final String UNKNOWN_ISBN = "1111111111111";

    private final Book effectiveJava = new Book(EJ_ISBN, "Effective Java", "Joshua Bloch", 2018);
    private final Book javaPuzzlers = new Book("9780321336781", "Java Puzzlers", "Joshua Bloch", 2005);
    private final Book cleanCode = new Book(CC_ISBN, "Clean Code", "Robert C. Martin", 2008);

    private Catalogue catalogue;

    @BeforeEach
    void setUp() {
        catalogue = new Catalogue();
    }

    // ---------- addBook ----------

    @Test
    void addingWithZeroCopiesFails() {
        assertThrows(IllegalArgumentException.class, () -> catalogue.addBook(effectiveJava, 0));
    }

    @Test
    void addingWithNegativeCopiesFails() {
        assertThrows(IllegalArgumentException.class, () -> catalogue.addBook(effectiveJava, -3));
    }

    @Test
    void failedAddDoesNotStoreTheBook() {
        assertThrows(IllegalArgumentException.class, () -> catalogue.addBook(effectiveJava, 0));

        assertThrows(BookNotFoundException.class, () -> catalogue.findByIsbn(EJ_ISBN));
    }

    @Test
    void addingDifferentBookWithSameIsbnFails() {
        catalogue.addBook(effectiveJava, 1);
        Book sameIsbn = new Book(EJ_ISBN, "Another Title", "Another Author", 2000);

        assertThrows(DuplicateBookException.class, () -> catalogue.addBook(sameIsbn, 1));
    }

    @Test
    void duplicateAddDoesNotChangeCopies() {
        catalogue.addBook(effectiveJava, 2);
        assertThrows(DuplicateBookException.class, () -> catalogue.addBook(effectiveJava, 5));

        assertEquals(2, catalogue.availableCopies(EJ_ISBN));
    }

    @Test
    void allCopiesStartOnTheShelf() {
        catalogue.addBook(cleanCode, 4);

        assertEquals(4, catalogue.availableCopies(CC_ISBN));
    }

    // ---------- not found ----------

    @Test
    void findUnknownIsbnFails() {
        assertThrows(BookNotFoundException.class, () -> catalogue.findByIsbn(UNKNOWN_ISBN));
    }

    @Test
    void availableCopiesOfUnknownIsbnFails() {
        assertThrows(BookNotFoundException.class, () -> catalogue.availableCopies(UNKNOWN_ISBN));
    }

    @Test
    void lendUnknownIsbnFails() {
        assertThrows(BookNotFoundException.class, () -> catalogue.lendCopy(UNKNOWN_ISBN));
    }

    @Test
    void returnUnknownIsbnFails() {
        assertThrows(BookNotFoundException.class, () -> catalogue.returnCopy(UNKNOWN_ISBN));
    }

    // ---------- searchByAuthor ----------

    @Test
    void searchWithNoMatchReturnsEmptyList() {
        catalogue.addBook(effectiveJava, 1);

        assertTrue(catalogue.searchByAuthor("Tolkien").isEmpty());
    }

    @Test
    void searchOnEmptyCatalogueReturnsEmptyList() {
        assertTrue(catalogue.searchByAuthor("Bloch").isEmpty());
    }

    @Test
    void searchIgnoresUpperCase() {
        catalogue.addBook(effectiveJava, 1);

        assertEquals(List.of(effectiveJava), catalogue.searchByAuthor("BLOCH"));
    }

    @Test
    void searchMatchesPartOfTheName() {
        catalogue.addBook(effectiveJava, 1);
        catalogue.addBook(cleanCode, 1);

        assertEquals(List.of(effectiveJava), catalogue.searchByAuthor("oshu"));
    }

    @Test
    void searchResultsAreSortedByTitle() {
        catalogue.addBook(javaPuzzlers, 1);
        catalogue.addBook(effectiveJava, 1);
        catalogue.addBook(cleanCode, 1);

        assertEquals(List.of(effectiveJava, javaPuzzlers), catalogue.searchByAuthor("bloch"));
    }

    // ---------- lendCopy / returnCopy ----------

    @Test
    void lendingReducesCopiesOneByOne() {
        catalogue.addBook(cleanCode, 3);

        catalogue.lendCopy(CC_ISBN);
        assertEquals(2, catalogue.availableCopies(CC_ISBN));

        catalogue.lendCopy(CC_ISBN);
        assertEquals(1, catalogue.availableCopies(CC_ISBN));
    }

    @Test
    void lendingWhenNoneLeftFailsAndCountStaysZero() {
        catalogue.addBook(cleanCode, 1);
        catalogue.lendCopy(CC_ISBN);

        assertThrows(NoCopiesAvailableException.class, () -> catalogue.lendCopy(CC_ISBN));
        assertEquals(0, catalogue.availableCopies(CC_ISBN));
    }

    @Test
    void returningWhenAllOnShelfFails() {
        catalogue.addBook(cleanCode, 2);

        assertThrows(IllegalStateException.class, () -> catalogue.returnCopy(CC_ISBN));
        assertEquals(2, catalogue.availableCopies(CC_ISBN));
    }

    @Test
    void cannotReturnMoreThanWasLent() {
        catalogue.addBook(cleanCode, 2);
        catalogue.lendCopy(CC_ISBN);
        catalogue.returnCopy(CC_ISBN);

        assertThrows(IllegalStateException.class, () -> catalogue.returnCopy(CC_ISBN));
    }

    @Test
    void lendingOneBookDoesNotAffectAnother() {
        catalogue.addBook(cleanCode, 1);
        catalogue.addBook(effectiveJava, 1);

        catalogue.lendCopy(CC_ISBN);

        assertEquals(0, catalogue.availableCopies(CC_ISBN));
        assertEquals(1, catalogue.availableCopies(EJ_ISBN));
    }
}