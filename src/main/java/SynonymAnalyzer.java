import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.util.Version;

import java.io.*;
import java.text.ParseException;

public class SynonymAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new ClassicTokenizer(Version.LUCENE_4_9, reader);
        TokenStream filter = new StandardFilter(Version.LUCENE_4_9, source);
        filter = new LowerCaseFilter(Version.LUCENE_4_9,filter);
        SynonymMap mySynonymMap = null;
        try {
            mySynonymMap = buildSynonym();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        filter = new SynonymFilter(filter, mySynonymMap, false);
        return new TokenStreamComponents(source, filter);
    }

    private SynonymMap buildSynonym() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/wordnet_wns.pl");
        Reader rulesReader = new InputStreamReader(stream);
        WordnetSynonymParser parser = new WordnetSynonymParser(true, true, new StandardAnalyzer(Version.LUCENE_4_9));
        try {
            parser.parse(rulesReader);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        SynonymMap synonymMap = parser.build();
        return synonymMap;
    }
}
