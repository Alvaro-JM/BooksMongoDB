
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.bson.Document;

/**
 * Search examples for MongoDB.
 * @author Álvaro Jiménez
 * @version Date: 26/05/2021
 */
public class ReadBooks {

    /**
     * Find titles with publication year greater than year given as parameter.
     * @param booksCollection target collection
     * @param year publication year
     */
    private static void findTitleByYear(MongoCollection<Document> booksCollection, int year){
        List<Document> booksList = booksCollection.find(gte("year", year))
                .projection(fields(excludeId(), include("title", "year")))
                .into(new ArrayList<>());
        for (Document d : booksList) {
            System.out.println(d.toJson());
        }
    }
    
    /**
     * Find books whose author name has initial the character given as parameter.
     * This method use a pattern.
     * @param booksCollection target collection
     * @param c initial
     */
    private static void findByNameInitialPattern(MongoCollection<Document> booksCollection, char c) {
        Pattern pattern = Pattern.compile("^" + c + ".*$", Pattern.CASE_INSENSITIVE);
        
        FindIterable<Document> iterable = booksCollection.find(eq("author", pattern))
                .projection(fields(excludeId(), include("title", "author")));
        MongoCursor<Document> cursor = iterable.iterator();
        
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
    }
    
    /**
     * Find titles whose author name has initial the character given as parameter.
     * This method use a pattern.
     * @param booksCollection target collection
     * @param c initial
     */
    private static void findByNameInitial(MongoCollection<Document> booksCollection, char c) {
        List<Document> booksList = booksCollection.find(and(gte("author", c), lte("author", (char)(c+1))))
                .projection(fields(excludeId(), include("title", "author")))
                .into(new ArrayList<>());
        
        for (Document d : booksList) {
            System.out.println(d.toJson());
        }
    }
    
    /**
     * Find first book published in year given as parameter.
     * @param booksCollection target collection
     * @param year publication year
     */
    private static void findFirstOfYear(MongoCollection<Document> booksCollection, int year){
        Document d = booksCollection.find(eq("year", year)).first();
        System.out.println(d.toJson());
    }
    
    
    /**
     * Main to run.
     * @param args the command line arguments, not needed
     */
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {

            MongoDatabase libraryDB = mongoClient.getDatabase("library");
            MongoCollection<Document> booksCollection = libraryDB.getCollection("books");

            System.out.println("\nFind all books with a publication year after 2000:");
            findTitleByYear(booksCollection, 2000);
            
            System.out.println("\nFind all books whose author name has initial P:");
            findByNameInitial(booksCollection, 'P');
            
            System.out.println("\nFind all books whose author name has initial P (with PATTERN):");
            findByNameInitialPattern(booksCollection, 'P');
            
            System.out.println("\nFind the first book published in 2021:");
            findFirstOfYear(booksCollection, 2021);
        
        }    
    }
    
}
