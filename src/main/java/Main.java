import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        testBestMatchNDCG();
        HashMap<String, Integer[]> trainQueries = new HashMap<String, Integer[]>();
        trainQueries.put("computer science", new Integer[]{2, 1, 1, 2, 1});
        trainQueries.put("Roberson Collection", new Integer[]{2, 1, 0, 1, 1});
        trainQueries.put("photo guide", new Integer[]{2, 2, 1, 0, 1});
        trainQueries.put("hunger games", new Integer[]{2, 2, 2, 0, 0});
        trainQueries.put("chef recipes", new Integer[]{2, 0, 2, 2, 1});
        int trainDatasetSize = trainQueries.keySet().size() * trainQueries.values().size();
        SearchRecordsDataset searchRecordsTrainDataset = new SearchRecordsDataset(trainDatasetSize);
        SearchEngine se = new SearchEngine(new File("/tmp/luceneIndex"), "product_catalog.json", Boolean.FALSE);
        List<List<Integer>> trainQueriesRanks = new ArrayList<List<Integer>>();
        for(String query: trainQueries.keySet()) {
            List<SearchRecord> records = se.processQuery(query);
            List<Integer> ranksList = Arrays.asList(trainQueries.get(query));
            trainQueriesRanks.add(ranksList);
            searchRecordsTrainDataset.appendSearchResults(query, records, ranksList);
        }
        System.out.println("Train Queries NDCG: " + NDCG.getNDCG(trainQueriesRanks));

        Classifier LRClassifier = new IBk();
        LRClassifier.buildClassifier(searchRecordsTrainDataset.toInstances());

        HashMap<String, Integer[]> testQueries = new HashMap<String, Integer[]>();
        testQueries.put("java programming", new Integer[]{ 2, 2, 1, 1, 1 });
        testQueries.put("html css", new Integer[]{ 2, 2, 2, 2, 1 });
        testQueries.put("cookbook", new Integer[]{ 1, 2, 2, 1, 1 });
        testQueries.put("hunger games", new Integer[]{ 2, 2, 2, 2, 1 });
        testQueries.put("harry potter", new Integer[]{ 2, 2, 1, 2, 1 });

        List<List<Integer>> testQueriesRanks = new ArrayList<List<Integer>>();

        for (String query: testQueries.keySet()) {
            List<SearchRecord> records = se.processQuery(query);
            SearchRecordsDataset searchQueryDataset = new SearchRecordsDataset(records.size());

            Map<SearchRecord, Double> rankedSearchRecords = new HashMap<SearchRecord, Double>();
            searchQueryDataset.appendSearchResults(query, records, null);

            Instances searchQueryInstances = searchQueryDataset.toInstances();
            for (int i = 0; i < records.size(); i++) {
                rankedSearchRecords.put(records.get(i), LRClassifier.classifyInstance(searchQueryInstances.instance(i)));
            }
            Map<SearchRecord, Double> sortedRankedSearchRecords = sortByValues(rankedSearchRecords);
            printRecordsWithRanks(query, sortedRankedSearchRecords);
            List<Integer> ranksList = Arrays.asList(testQueries.get(query));
            testQueriesRanks.add(ranksList);
        }
        System.out.println("Test Queries NDCG: " + NDCG.getNDCG(testQueriesRanks));
    }

    public static void printRecords(String query, List<SearchRecord> records) {
        System.out.println("Search query: " + query);
        for (SearchRecord record : records) {
            System.out.println(record);
        }
    }

    public static void printRecordsWithRanks(String query, Map<SearchRecord, Double> recordsWithRanks) {
        System.out.println("Search query: " + query);
        for (Map.Entry<SearchRecord, Double> entry: recordsWithRanks.entrySet()) {
            System.out.print(entry.getValue() + "\t");
            System.out.println(entry.getKey());
        }
    }
    public static void testBestMatchNDCG() {
        List<List<Integer>> test = new ArrayList<List<Integer>>();
        test.add(Arrays.asList(new Integer[] {2, 2, 2, 2, 2}));
        System.out.println("Best NDCG: " + NDCG.getNDCG(test));
    }

    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();

        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}