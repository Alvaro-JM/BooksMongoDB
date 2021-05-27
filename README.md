Proyecto MongoDB - Books
---
#### 1 
- ¿Cómo se llaman las "carpetas" en MongoDB?  
Collections (colecciones)  

- ¿Qué tipo de BD es MongoDB?  
Es un gestor de base de datos orientado a documentos  

- ¿Cuál es la extensión de los ficheros/objetos que almacena MongoDB?  
.bson, que es una especificación similar a .json 
  
- Siguiendo la notación JSON, ¿cuál es el caracter delimitador de campo?  
La coma `,` 
 
- Siguiendo la notación JSON, ¿cuál es el caracter que separa el identificador del campo de su valor?  
Los dos puntos `:`

- ¿Cuál es la diferencia entre una base de datos relacional y una base de datos no relacional?  
Una base de datos **relacional** está **estructurada**, y las tablas están
relacionadas mediante claves primarias, y en las que los datos siguen una
estructura fija.
En la base de datos **no relacional** no están estructuradas de esa forma,
tienen un **esquema dinámico**. Eso nos permite que podamos guardar en la
misma colección *objetos* diferentes, una base de datos de personas podría
contener animales.  
---
  
  
#### 2
- Indica el enlace de descarga de la herramienta utilizada para MongoDB  
https://www.mongodb.com/try/download/community

- ¿Cuál es la dependencia necesaria para trabajar con Mongo DB en Java?  
~~~
<groupId>org.mongodb</groupId>
<artifactId>mongo-java-driver</artifactId>
<version>3.12.8</version>
~~~  
- ¿Cuál es el número de puerto por donde se comunica la aplicación de Mongo?  
  localhost:27017
  
  --- 
  
#### 3
- ¿Qué botón es necesario pulsar en la interfaz para crear una nueva base de datos?  
El botón + de abajo a la izquierda, o en el botón verde CREATE DATABASE
que solo aparece si no tienes nada seleccionado.  
![](/pics/1.png)

- ¿Y una colección?  
El botón + que está en la base de datos, o en el botón verde CREATE
COLLECTION que solo aparece si tienes una base de datos seleccionada.  
![](/pics/2.png)

- ¿Cómo se añade un nuevo objeto a la colección usando la interfaz gráfica de MongoDB?  
  En el botón verde ADD DATA, opción Insert Document. En la pantalla que
aparece se añade el nuevo objeto escribiendo sus atributos.
![](/pics/3.png)
![](/pics/4.png)
---
  
#### 4
Crea una aplicación en Java que se conecte a MongoDB. Crea una nueva base de datos llamada "Biblioteca". Dentro de esta base de datos crea una colección llamada "Libros".  

La base de datos se puede crear en MongoDB siguiendo los pasos antes
descritos, o desde la misma aplicación Java se creará al conectarse si esta
no existe.
~~~
try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")){
MongoDatabase libraryDB = mongoClient.getDatabase("library");
MongoCollection<Document> booksCollection = libraryDB.getCollection("books");
}
~~~  

---  
  
#### 5
Inserta 200 libros en la colección. Los libros son unas entidades que tienen los siguientes atributos: ISBN (es el identificador), título, autora y año de publicación. Tu aplicación debe generar libros de forma aleatoria utilizando la librería Faker que dispone de un método book() que permite generar los atributos mencionados, el año puedes generarlo con Random, debe ser un entero entre 1900 y 2021.  

- Clase Book:
~~~
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
~~~  
  
- Clase CreateInsertBook:
~~~
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

~~~
  ---
#### 6 
Consulta los títulos que se han publicado después del año 2000.  
Consulta los títulos escritos por autores que comienzan por la letra 'P'.
Consulta el primer libro publicado en 2021.

- Clase ReadBooks:
~~~
/**
 * Search examples for MongoDB.
 * @author Álvaro Jiménez
 * @version Date: 26/05/2021
 */
public class ReadBooks {

    /**
     * Find books with publication year greater than year given as parameter.
     * @param booksCollection target collection
     * @param year publication year
     */
    private static void findByYear(MongoCollection<Document> booksCollection, int year){
        List<Document> booksList = booksCollection.find(gte("year", year)).into(new ArrayList<>());
        for (Document d : booksList) {
            System.out.println(d.toJson());
        }
    }
    
    /**
     * Find books whose author name has initial the character given as parameter.
     * @param booksCollection target collection
     * @param c initial
     */
    private static void findByNameInitial(MongoCollection<Document> booksCollection, char c) {
        Pattern pattern = Pattern.compile("^" + c + ".*$", Pattern.CASE_INSENSITIVE);
        
        FindIterable<Document> iterable = booksCollection.find(eq("author", pattern));
        MongoCursor<Document> cursor = iterable.iterator();
        
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
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
            findByYear(booksCollection, 2000);
            
            System.out.println("\nFind all books whose author name has initial P:");
            findByNameInitial(booksCollection, 'P');
            
            System.out.println("\nFind the first book published in 2021:");
            findFirstOfYear(booksCollection, 2021);
        }    
    }
}

~~~
