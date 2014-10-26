import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import java.util.*;

public class SearchRecordsDataset {
    private Instances dataset;
    private FastVector searchRecordAttrs;

    public SearchRecordsDataset(int size) {
        searchRecordAttrs = new FastVector();
        searchRecordAttrs.addElement(new Attribute("rank"));
        searchRecordAttrs.addElement(new Attribute("price"));
        searchRecordAttrs.addElement(new Attribute("rating"));
        searchRecordAttrs.addElement(new Attribute("occurrence"));
        dataset = new Instances("searchRecordAttrs", searchRecordAttrs, size);
        dataset.setClassIndex(0);
    }

    public void appendSearchResults(String query, List<SearchRecord> records, List<Integer> ranks) {
        for (int i=0; i < records.size(); i++) {
            Instance processedRecord;
            if (ranks == null) {
                 processedRecord = processSearchRecord(query, records.get(i), null);
            } else {
                processedRecord = processSearchRecord(query, records.get(i), ranks.get(i));
            }
            dataset.add(processedRecord);
        }
    }

    public void appendInstance(Instance instance) {
        dataset.add(instance);
    }

    public Instance processSearchRecord(String query, SearchRecord record, Integer rank) {
        Instance result = new Instance(dataset.numAttributes());
        Integer searchWordsCount = 0;
        for (String word: query.split(" ")) {
            searchWordsCount += countOccurrences(record.title.toLowerCase(), word.toLowerCase());
            searchWordsCount += countOccurrences(record.author.toLowerCase(), word.toLowerCase());
        }
        if (rank != null)
            result.setValue((Attribute)searchRecordAttrs.elementAt(0), rank);
        result.setValue((Attribute)searchRecordAttrs.elementAt(1), record.price);
        result.setValue((Attribute)searchRecordAttrs.elementAt(2), record.rating);
        result.setValue((Attribute)searchRecordAttrs.elementAt(3), (double)searchWordsCount);
        return result;
    }

    public static int countOccurrences(String main, String sub) {
        return (main.length() - main.replace(sub, "").length()) / sub.length();
    }

    public Instances toInstances() {
        return dataset;
    }
}
