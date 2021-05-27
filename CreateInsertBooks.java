
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Creates and inserts books in a MongoDB database.
 * @author Álvaro Jiménez
 * @version Date: 26/05/2021
 */
public class CreateInsertBooks {

    /**
     * Generates books documents.
     * @return document
     */
    private static Document generateNewBook() {
        Book b = Book.randomBook();
        return new Document("_id", new ObjectId()).append("title", b.getTitle())
                .append("author", b.getAuthor())
                .append("year", b.getYear());
    }
    
    /**
     * Inserts a hundred books into a collection.
     * @param booksCollection target collection
     */
    private static void insertManyDocuments(MongoCollection<Document> booksCollection) {
        List<Document> books = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            books.add(generateNewBook());
        }
        booksCollection.insertMany(books, new InsertManyOptions().ordered(false));
        System.out.println("100 books have been inserted.");
    }
    
    /**
     * Main to run.
     * @param args the command line arguments, not needed
     */
    public static void main(String[] args) {
        
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase libraryDB = mongoClient.getDatabase("library");
            MongoCollection<Document> booksCollection = libraryDB.getCollection("books");
            
            insertManyDocuments(booksCollection);
            insertManyDocuments(booksCollection);
        }
        
    }
    
}
