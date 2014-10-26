import com.google.gson.Gson;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

    private SynonymAnalyzer analyzer;
    private IndexWriterConfig config;
    private SimpleFSDirectory index;

    public SearchEngine(File indexDir, String dataPath) throws IOException {
        this(indexDir, dataPath, true);
    }

    public SearchEngine(File indexDir, String dataPath, Boolean override) throws IOException {
        this.analyzer = new SynonymAnalyzer();
        this.config = new IndexWriterConfig(Version.LUCENE_4_9, this.analyzer);
        if (override) for (File f : indexDir.listFiles()) f.delete();
        this.index = new SimpleFSDirectory(indexDir);
        if (override) addToIndex(dataPath);
    }

    public void addToIndex(String dataPath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/" + dataPath)));
        IndexWriter indexWriter = new IndexWriter(this.index, this.config);
        String line;
        int counter = 0;
        while ((line = br.readLine()) != null) {
            SearchRecord metadata = new Gson().fromJson(line, SearchRecord.class);
            addDoc(indexWriter, metadata);
            counter += 1;
        }
        indexWriter.close();
        br.close();
        System.out.printf("Index %d records%n", counter);
    }

    public List<SearchRecord> processQuery(String searchQuery) throws ParseException, IOException {
        return processQuery(searchQuery, 5);
    }

    public List<SearchRecord> processQuery(String searchQuery, int hitsCount) throws ParseException, IOException {
        ArrayList<SearchRecord> result = new ArrayList<SearchRecord>();
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                Version.LUCENE_4_9,
                new String[]{"author", "title"},
                this.analyzer);
        queryParser.setDefaultOperator(QueryParser.OR_OPERATOR);
        queryParser.setAllowLeadingWildcard(true);
        Query query = queryParser.parse(searchQuery);
        IndexReader reader = IndexReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsCount, true);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        for(int i=0; i<hits.length; ++i) {
            int docId = hits[i].doc;
            SearchRecord item = new SearchRecord(docId, searcher.doc(docId));
            result.add(item);
        }
        return result;
    }

    private void addDoc(IndexWriter w, SearchRecord metadata) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("author", metadata.author, Field.Store.YES));
        doc.add(new TextField("title", metadata.title, Field.Store.YES));
        doc.add(new DoubleField("price", metadata.price, Field.Store.YES));
        doc.add(new StringField("link", metadata.link, Field.Store.YES));
        doc.add(new DoubleField("rating", metadata.rating, Field.Store.YES));
        w.addDocument(doc);
    }
}
