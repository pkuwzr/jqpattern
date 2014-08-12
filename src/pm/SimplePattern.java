package pm;

import util.APIDic;

import java.util.ArrayList;

/**
 * @author Wang Zerui
 */
public class SimplePattern {

    // Api set by the index in api dic.
    private ArrayList<Integer> indexedApis = new ArrayList<>();
    // Api set by the API representation.
    private ArrayList<API> apis = new ArrayList<>();
    // Relation set.
    private ArrayList<Relation> relations = new ArrayList<>();
    // How many times this pattern has occured.
    private int count;
    // Snippets of source code.
    private ArrayList<String> src = new ArrayList<>();

        public SimplePattern() {
            indexedApis = new ArrayList<>();
            relations = new ArrayList<>();
        }

        public SimplePattern(ArrayList<Integer> mapis, ArrayList<Relation> mrelations) {
            indexedApis = mapis;
            relations = mrelations;
        }

    public boolean addAPI(API api) {
        int index = APIDic.indexOf(api);
        if (index >= 0) {
            indexedApis.add(index);
            apis.add(api);
            return true;
        }
        return false;
    }

    public void addSeqAPI(API api, int lastIdx) {
        boolean added = this.addAPI(api);
        if (added)
            relations.add(new Relation(lastIdx, indexedApis.size() - 1, Relation.SEQUENCE));
    }

    public void addRelation(Relation r) {relations.add(r);}

    public int getAPINumber() {
        return indexedApis.size();
    }

    public int getRelationNumber() { return relations.size(); }

    public ArrayList<API> getApis() {
        return apis;
    }

    public ArrayList<Integer> getIndexedApis() {
        return indexedApis;
    }

    public ArrayList<Relation> getRelations() { return relations; }

    public ArrayList<String> getSrc() {
        return src;
    }

    public void addSrcCode(String srcCode) {
        count ++;
        src.add(srcCode);
    }

    public void addAllSrcCode(ArrayList<String> srcCodes) {
        count += srcCodes.size();
        src.addAll(srcCodes);
    }

    public double getSimilarity(SimplePattern sp) {
        double iNum = getIntersectionNumber(sp);
        double uNum = getRelationNumber() + sp.getRelationNumber() - iNum;
        return (iNum / uNum);
    }

    /*public void sort() {
        Collections.sort(indexedApis);
        Collections.sort(relations);
    }*/

    public boolean equals(SimplePattern sp) {
        boolean result = true;
        if (this.getRelationNumber() != sp.getRelationNumber())
            result = false;
        else {
            int sameNum = getIntersectionNumber(sp);
            if (sameNum != getAPINumber()) result = false;
        }
        return result;
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < indexedApis.size(); i++) {
            result += indexedApis.get(i);
            if (i != indexedApis.size() - 1) result += " ";
        }
        result += "\n";
        for (int i = 0; i < relations.size(); i++) {
            result += relations.get(i).toString();
            if (i != relations.size() - 1) result += "|";
        }
        result += "\n";
        result += (count + "\n");
        for (int i = 0; i < count; i++) {
            result += (src.get(i) + "\n");
            result += "$END$\n";
        }
        return result;
    }

    private int getIntersectionNumber(SimplePattern target) {
        boolean[] matched = new boolean[getRelationNumber()];
        for (int i = 0; i < matched.length; i ++) matched[i] = false;
        ArrayList<Integer> target_apis = target.indexedApis;
        ArrayList<Relation> target_relations = target.relations;
        int num = 0;
        for (Relation r : target_relations) {
            for (int i = 0; i < matched.length; i ++) {
                if (!matched[i]) {
                    Relation tmp = relations.get(i);
                    if (tmp.name == r.name
                            && indexedApis.get(tmp.api1) == target_apis.get(r.api1)
                            && indexedApis.get(tmp.api2) == target_apis.get(r.api2))
                        num ++;
                        matched[i] = true;
                }
            }
        }
        return num;
    }

}
