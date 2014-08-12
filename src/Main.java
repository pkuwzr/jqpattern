import pm.Relation;
import pm.SearchResult;
import pm.SimplePattern;
import util.PatterHelper;

import java.util.ArrayList;

/**
 * Created by Jerry on 2014/8/11.
 */
public class Main {
    public static void main(String[] args) {
        PatterHelper ph = new PatterHelper();
        // Run mining.
        // ph.runMining();
        // Search.
        ArrayList<Integer> apis = new ArrayList<>();
        apis.add(1);
        apis.add(0);
        ArrayList<Relation> relations = new ArrayList<>();
        relations.add(new Relation(0, 1, Relation.SEQUENCE));
        SimplePattern target = new SimplePattern(apis, relations);
        ArrayList<SearchResult> srs = (ArrayList<SearchResult>)ph.search(target, true);
        for (SearchResult sr : srs) {
            System.out.println(sr.getPattern());
            System.out.println(sr.getSimilarity());
        }
    }
}
