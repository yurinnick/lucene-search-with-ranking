import org.apache.lucene.document.Document;

public class SearchRecord {
    public String author;
    public String title;
    public String price;
    public String link;

    public SearchRecord(String author, String title, String price, String link) {
        this.author = author;
        this.title = title;
        this.price = price;
        this.link = link;
    }

    public SearchRecord(Document document) {
        this.author = document.get("author");
        this.title = document.get("title");
        this.price = document.get("price");
        this.link = document.get("link");
    }

    @Override
    public String toString() {
        return String.format("$%-5s \t %-20s \t %s", price, author, title);
    }
}