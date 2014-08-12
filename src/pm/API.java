package pm;

import java.util.ArrayList;

/**
 * @author Wang Zerui
 */
public class API implements Comparable<API> {

    private String name;
    private ArrayList<String> params = new ArrayList<String>();
    private int depth;

    public API(String mname, ArrayList<String> mparams, int mdepth) {
        name = mname;
        params = mparams;
        depth = mdepth;
    }

    public API(String mname, ArrayList<String> mparams) {
        name = mname;
        params = mparams;
    }

    public int getDepth() {
        return depth;
    }

    public String toString() {
        String api = name + "(";
        for (int i = 0; i < params.size(); i++) {
            api += params.get(i);
            if (i != params.size() - 1) api += ",";
        }
        api += ")";
        return api;
    }

    public boolean equals(API api) {
        if (!name.equals(api.name))
            return false;
        if (params.size() != api.params.size())
            return false;
        for (int i = 0; i < params.size(); i++)
            if (!(params.get(i).equals("VAR") || api.params.get(i).equals("VAR"))
                    && !(params.get(i).equals(api.params.get(i))))
                return false;
        return true;
    }

    public int compareTo(API api) {
        return this.toString().compareTo(api.toString());
    }

}
