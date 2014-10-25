import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchRecordsDataset {
    private Instances dataset;
    private FastVector searchRecordAttrs;

    public SearchRecordsDataset(int size) {
        searchRecordAttrs = new FastVector();
        searchRecordAttrs.addElement(new Attribute("rank"));
        searchRecordAttrs.addElement(new Attribute("price"));
        searchRecordAttrs.addElement(new Attribute("rating"));
        searchRecordAttrs.addElement(new Attribute("titleOccurrence"));
        searchRecordAttrs.addElement(new Attribute("authorOccurrence"));
        dataset = new Instances("searchRecordAttrs", searchRecordAttrs, size);
        dataset.setClassIndex(0);
    }

    public void appendSearchResults(List<String> query, List<SearchRecord> records, List<Integer> ranks) {
        for (int i=0; i < records.size(); i++) {
            Instance processedRecord = processSearchRecord(query, records.get(i), ranks.get(i));
            dataset.add(processedRecord);
        }
    }

    public void save(File file) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        saver.setFile(file);
        saver.writeBatch();
    }

    public Instance processSearchRecord(List<String> query, SearchRecord record, Integer rank) {
        Instance result = new Instance(5);
        Integer searchWordsTitleCount = 0;
        Integer searchWordsAuthorCount = 0;
        for (String word: query) {
            searchWordsTitleCount += countOccurrences(record.title.toLowerCase(), word.toLowerCase());
            searchWordsAuthorCount += countOccurrences(record.author.toLowerCase(), word.toLowerCase());
        }
        result.setValue((Attribute)searchRecordAttrs.elementAt(0), rank);
        result.setValue((Attribute)searchRecordAttrs.elementAt(1), record.price);
        result.setValue((Attribute)searchRecordAttrs.elementAt(2), record.rating);
        result.setValue((Attribute)searchRecordAttrs.elementAt(3), (double)searchWordsTitleCount);
        result.setValue((Attribute)searchRecordAttrs.elementAt(4), (double)searchWordsAuthorCount);
        return result;
    }

    public static int countOccurrences(String main, String sub) {
        return (main.length() - main.replace(sub, "").length()) / sub.length();
    }

    public Instances toInstances() {
        return dataset;
    }
}
