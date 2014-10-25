import java.util.*;

public class NDCG {
    private static double Zk = 0.23;
    public static double getNDCG(List<List<Integer>> queries) {
        double value = 0;
        for (List<Integer> query_results: queries) {
            value += Zk * getNDCGForRequest(query_results);
        }
        value /= queries.size();
        return value;
    }
    private static double getNDCGForRequest(List<Integer> docs) {
        double value = 0;
        for (int i = 0; i < docs.size(); i++) {
            value += (Math.pow(2, docs.get(i)) - 1) / (2 + i);
        }
        return value;
    }
}
