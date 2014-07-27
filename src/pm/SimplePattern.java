package pm;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jerry on 2014/7/27.
 */
public class SimplePattern {

    private ArrayList<JqAPI> apis = new ArrayList<JqAPI>();
    private ArrayList<Relation> relations = new ArrayList<Relation>();

    public SimplePattern() {
        apis = new ArrayList<JqAPI>();
        relations = new ArrayList<Relation>();
    }

    public SimplePattern(ArrayList<JqAPI> mapis, ArrayList<Relation> mrelations) {
        apis = mapis;
        relations = mrelations;
    }

    public void addAPI(JqAPI api) {
        apis.add(api);
    }

    public void addSeqAPI(JqAPI api, int lastIdx) {
        apis.add(api);
        relations.add(new Relation(lastIdx, apis.size() - 1, Relation.SEQUENCE));
    }

    public void addRelation(Relation r) {relations.add(r);}

    public int getAPINumber() {
        return apis.size();
    }

    public int getRelationNumber() { return relations.size(); }

    public ArrayList<JqAPI> getApis() {return apis;}

    public ArrayList<Relation> getRelations() { return relations; }

    public void sort() {
        Collections.sort(apis);
        Collections.sort(relations);
    }

    public boolean equals(SimplePattern sp) {
        boolean result = true;
        if (this.getAPINumber() != sp.getAPINumber() || this.getRelationNumber() != sp.getRelationNumber())
            result = false;
        else {
            for (int i = 0; i < this.getAPINumber(); i ++)
                if (!apis.get(i).equals(sp.apis.get(i))) { result = false; break; }
            if (result)
                for (int i = 0; i < this.getRelationNumber(); i ++)
                    if (!relations.get(i).equals(sp.relations.get(i))) { result = false; break; }
        }
        return result;
    }

    public String toString() {
        String pattern = new String();
        pattern += "API set:\n";
        for (JqAPI api : apis) pattern += (api + " ");
        pattern += "\nRelation set:\n";
        for (Relation relation : relations) pattern += (relation + " ");
        return pattern;
    }
}

class Relation implements Comparable<Relation> {
    public static final int SEQUENCE = 2, CALLBACK = 1;
    public int api1, api2;
    public int name;

    public Relation(int i1, int i2, int rn) {
        api1 = i1;api2 = i2;name = rn;
    }

    public boolean equals(Relation relation) {
        if (api1 == relation.api1 && api2 == relation.api2 && name == relation.name)
            return true;
        else
            return false;
    }

    public int compareTo(Relation relation) {
        if (api1 != relation.api1) {
            return ((Integer)api1).compareTo(relation.api1);
        }
        if (name != relation.name)
            return ((Integer)name).compareTo(relation.name);
        else
            return ((Integer)api2).compareTo(relation.api2);
    }

    public String toString() {
        String rn = new String();
        if (name == Relation.CALLBACK) rn = "Callback";
        else if (name == Relation.SEQUENCE) rn = "Sequential";
        else rn = "Unkown";
        return "<" + api1 + "," + api2 + "," + rn + ">";
    }
}
