import java.util.*;


abstract class Book {
    protected String isbn;
    protected String title;
    protected int year;
    protected double price;
    protected String author;

    public Book(String isbn, String title, int year, double price, String author) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.price = price;
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public abstract boolean isPurchasable();
    public abstract double buy(int quantity, String email, String address) throws BookNotAvailableException;
}

// PaperBook class
class PaperBook extends Book {
    private int stock;

    public PaperBook(String isbn, String title, int year, double price, String author, int stock) {
        super(isbn, title, year, price, author);
        this.stock = stock;
    }

    @Override
    public boolean isPurchasable() {
        return true;
    }

    @Override
    public double buy(int quantity, String email, String address) throws BookNotAvailableException {
        if (stock < quantity) {
            throw new BookNotAvailableException("Not enough stock for ISBN: " + isbn);
        }
        stock -= quantity;
        ShippingService.send(address);
        return price * quantity;
    }
}

// EBook class
class EBook extends Book {
    private String filetype;

    public EBook(String isbn, String title, int year, double price, String author, String filetype) {
        super(isbn, title, year, price, author);
        this.filetype = filetype;
    }

    @Override
    public boolean isPurchasable() {
        return true;
    }

    @Override
    public double buy(int quantity, String email, String address) {
        MailService.send(email);
        return price * quantity;
    }
}

// ShowcaseBook class
class ShowcaseBook extends Book {
    public ShowcaseBook(String isbn, String title, int year, double price, String author) {
        super(isbn, title, year, price, author);
    }

    @Override
    public boolean isPurchasable() {
        return false;
    }

    @Override
    public double buy(int quantity, String email, String address) throws BookNotAvailableException {
        throw new BookNotAvailableException("Showcase books are not for sale.");
    }
}

// Exception for unavailable books
class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}


class ShippingService {
    public static void send(String address) {
        System.out.println("Quantum book store: Shipping to " + address);
    }
}


class MailService {
    public static void send(String email) {
        System.out.println("Quantum book store: Email sent to " + email);
    }
}


class BookStore {
    private List<Book> inventory = new ArrayList<>();

    public void addBook(Book book) {
        inventory.add(book);
        System.out.println("Quantum book store: Book added: " + book.title);
    }

    public void removeOutdatedBooks(int maxYearsOld) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        inventory.removeIf(book -> currentYear - book.getYear() > maxYearsOld);
        System.out.println("Quantum book store: Outdated books removed.");
    }

    public double buyBook(String isbn, int quantity, String email, String address) throws BookNotAvailableException {
        for (Book book : inventory) {
            if (book.getIsbn().equals(isbn)) {
                return book.buy(quantity, email, address);
            }
        }
        throw new BookNotAvailableException("Book not found.");
    }
}


public class QuantumBookstoreFullTest {
    public static void main(String[] args) {
        BookStore store = new BookStore();

        store.addBook(new PaperBook("111", "Java 101", 2015, 100, "James", 10));
        store.addBook(new EBook("222", "Python Guide", 2020, 50, "Guido", "pdf"));
        store.addBook(new ShowcaseBook("333", "Rare Book", 1980, 999, "Unknown"));

        try {
            double total = store.buyBook("111", 2, "buyer@example.com", "123 Cairo St");
            System.out.println("Quantum book store: Paid amount = " + total);
        } catch (BookNotAvailableException e) {
            System.out.println("Quantum book store: " + e.getMessage());
        }

        try {
            double total = store.buyBook("222", 1, "user@example.com", "");
            System.out.println("Quantum book store: Paid amount = " + total);
        } catch (BookNotAvailableException e) {
            System.out.println("Quantum book store: " + e.getMessage());
        }

        try {
            store.buyBook("333", 1, "", "");
        } catch (BookNotAvailableException e) {
            System.out.println("Quantum book store: " + e.getMessage());
        }

        store.removeOutdatedBooks(10);
    }
}
