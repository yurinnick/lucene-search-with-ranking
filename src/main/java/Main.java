import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        SearchEngine se = new SearchEngine(new File("/tmp/luceneIndex"), Boolean.FALSE);
        se.createIndex("product_catalog.json");
        List<SearchRecord> records = se.processQuery("animal", 10);
        for (SearchRecord record : records) {
            System.out.println(record.toString());
        }
    }
}