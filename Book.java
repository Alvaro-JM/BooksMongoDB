
import com.github.javafaker.Faker;
import java.util.Random;

/**
 * Book represented by title, author and publication year.
 * @author Álvaro Jiménez
 * @version Date: 26/05/2021
 */
public class Book {
    private String title;
    private String author;
    private int year;

    private Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }
    
    /**
     * Generates a random book using Faker library.
     * @return Book
     */
    public static Book randomBook(){
        Faker faker = new Faker();
        Random r = new Random();
        String title = faker.book().title();
        String author = faker.book().author();
        int year = r.nextInt(122)+1900;
        return new Book(title, author, year);
    }
}
