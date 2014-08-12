package pm;

/**
 * @author Wang Zerui
 */
public class Relation{
    public static final int SEQUENCE = 2, CALLBACK = 1;
    public int api1, api2;
    public int name;

    public Relation(int i1, int i2, int rn) {
        api1 = i1;api2 = i2;name = rn;
    }

    public String toString() {
        /*String rn = new String();
        if (name == Relation.CALLBACK) rn = "Callback";
        else if (name == Relation.SEQUENCE) rn = "Sequential";
        else rn = "Unkown";*/
        return "" + api1 + "," + api2 + "," + name;
    }
}
