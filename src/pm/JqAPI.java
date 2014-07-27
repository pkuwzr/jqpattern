package pm;

import java.util.ArrayList;

/**
 * Created by Jerry on 2014/7/27.
 */
public class JqAPI implements Comparable<JqAPI> {

    private String name;
    private ArrayList<String> params = new ArrayList<String>();
    private int depth;

    public JqAPI(String mname, ArrayList<String> mparams, int mdepth) {
        name = mname;
        params = mparams;
        depth = mdepth;
    }

    public int getDepth() { return depth; }

    public String toString() {
        String api = name + "(";
        for (int i = 0; i < params.size(); i ++) {
            api += params.get(i);
            if (i != params.size() - 1) api += ",";
        }
        api += ")";
        return api;
    }

    public boolean equals(JqAPI api) {
        return this.toString() == api.toString();
    }

    public int compareTo(JqAPI api) {
        return this.toString().compareTo(api.toString());
    }

}
