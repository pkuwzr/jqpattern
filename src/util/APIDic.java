package util;

import pm.API;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Zerui
 * APIDic class contains the library apis and their indexs.
 * You can also get the indexOf of a given api(return -1 if the api is not in).
 */
public class APIDic {

    private static List<API> dic;

    public static void load() {
        File dicFile = new File(Configuration.getDicFile());
        BufferedReader br = null;
        dic = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(dicFile));
            String line = br.readLine();
            while (line != null) {
                String[] items = line.split("\\|");
                String name = items[0];
                ArrayList<String> params = new ArrayList<String>();
                for (String p : items[1].split(" "))
                    params.add(p);
                dic.add(new API(name, params));
                line = br.readLine();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println(dicFile.getAbsolutePath() + " not found!");
        } catch (IOException ioe) {
            System.err.println("Error when reading dic file.");
        }
    }

    public static int indexOf(API target) {
        for (int i = 0; i < dic.size(); i++) {
            if (target == null ? dic.get(i) == null : dic.get(i).equals(target))
                return i;
        }
        return -1;
    }

    public static API get(int index) {
        return dic.get(index);
    }

    public static int getSize() {
        return dic.size();
    }

}
