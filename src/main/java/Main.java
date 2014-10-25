import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        HashMap<String, Integer[]> trainQueries = new HashMap<String, Integer[]>();
        trainQueries.put("computer science", new Integer[]{2, 1, 1, 2, 1});
        trainQueries.put("Roberson Collection", new Integer[]{2, 1, 0, 1, 1});
        trainQueries.put("photo guide", new Integer[]{2, 2, 1, 0, 1});
        trainQueries.put("hunger games", new Integer[]{2, 2, 2, 0, 0});
        trainQueries.put("chef recipes", new Integer[]{2, 0, 2, 2, 1});

        SearchRecordsDataset searchRecordsTrainDataset = new SearchRecordsDataset(25);
        SearchEngine se = new SearchEngine(new File("/tmp/luceneIndex"), "product_catalog.json", Boolean.FALSE);
        List<List<Integer>> queriesRanks = new ArrayList<List<Integer>>();
        for(String query: trainQueries.keySet()) {
            List<SearchRecord> records = se.processQuery(query);
            printRecords(records);
            List<Integer> ranksList = Arrays.asList(trainQueries.get(query));
            List<String> queryList = Arrays.asList(query.split(" "));
            queriesRanks.add(ranksList);
            searchRecordsTrainDataset.appendSearchResults(queryList, records, ranksList);
        }
        System.out.println(NDCG.getNDCG(queriesRanks));

        Classifier LRClassifier = new LinearRegression();
        LRClassifier.buildClassifier(searchRecordsTrainDataset.toInstances());

//        Instance itmp = new Instance(5);
//        itmp = searchRecordsTrainDataset.processSearchRecord(
//                Arrays.asList(queries.keySet().toArray()[0].toString().split(" ")),
//                tmp.get(0).get(4), 2);
//        System.out.println(queries.keySet().toArray()[0].toString());
//        System.out.println(LRClassifier.classifyInstance(itmp));
    }

    public static void printRecords(List<SearchRecord> records) {
        for (SearchRecord record : records) {
            System.out.println(record.toString());
        }
    }
    public static void testBestMatchNDCG() {
        List<List<Integer>> test = new ArrayList<List<Integer>>();
        test.add(Arrays.asList(new Integer[] {2, 2, 2, 2, 2}));
        System.out.println(NDCG.getNDCG(test));
    }
}