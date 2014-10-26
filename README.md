Lucene 4 Search + IBk Classifier
================================

### Overview
Java application, which using [Apache Lucene](http://lucene.apache.org), [WordNet](http://wordnet.princeton.edu) and [WEKA](http://www.cs.waikato.ac.nz/ml/weka) to proof increasing search results relevance using post-ranking of requests with classifier (IBk Classifier). Search queries runs with additional synonim keywords added from WordNet dictionary to improve results.

![Lucene](http://lucene.apache.org/images/mantle-innovation.png)
![WordNet](http://wordnet.princeton.edu/wordnet/banner_logo.png)
![WEKA](http://upload.wikimedia.org/wikipedia/commons/0/07/Weka_(software)_logo.png)

### Execution steps:
- If `override` set to `true` generate index from product_catalog.json with Lunece, otherwise trying use existent index from `\tmp\lunceneIndex`
- Run 5 train queries, which returns 5 top results. Their ranks from 0 to 2, setted manually on Integer[].
- Calculate NDCG for queries from previous step using manually added ranks.
- Use manually added ranks for training WEKA IBk(k-nearest neighbour learner) classifier.
- Run 5 queries with additional ranking with classifier and print them in order associated with rank from classifier.
- Calculate NDCG for queries from previous step using manually added ranks.

Because of additional ranking on test queries, NDCG have to increase (on real example it may not)
