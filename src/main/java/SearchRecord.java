import org.apache.lucene.document.Document;

public class SearchRecord implements Comparable<SearchRecord>{
    public String author;
    public String title;
    public Double price;
    public String link;
    public Double rating;
    public int id;

    public SearchRecord(int id, String author, String title, Double price, String link, Double rating) {
        this.author = author;
        this.title = title;
        this.price = price;
        this.link = link;
        this.id = id;
        this.rating = rating;
    }

    public SearchRecord(int id, Document document) {
        this.id = id;
        this.author = document.get("author");
        this.title = document.get("title");
        this.price = Double.parseDouble(document.get("price"));
        this.link = document.get("link");
        this.rating = Double.parseDouble(document.get("rating"));
    }

    @Override
    public String toString() {
        return String.format("%-5s \t $%-5s \t %-5s \t %-20s \t %s", id, price, rating, author, title);
    }

    @Override
    public int compareTo(SearchRecord o) {
        if (o.id == id) return 0;
        if (o.id > id) return -1;
        return 1;
    }
}