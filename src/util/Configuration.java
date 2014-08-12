package util;

import java.io.*;
import java.util.HashMap;

/**
 * @author Wang Zerui
 */
public class Configuration {

    // The api dic file position
    private static String dicFile;
    // The numbers of patterns returned
    private static int K;
    // Source dir for mining
    private static String srcDir;
    // Target file for storing mining results.
    private static String targetFile;
    // Whether has been inited
    private static boolean isInited = false;
    // Log file for mining
    private static String logFile;

    public static void init() {
        if (!isInited) {
            HashMap<String, String> conf = load();
            dicFile = conf.get("APIDicFile");
            K = Integer.parseInt(conf.get("ReturnedNumber"));
            srcDir = conf.get("SrcDir");
            targetFile = conf.get("TargetFile");
            logFile = conf.get("LogFile");
        }
    }

    private static HashMap<String, String> load() {
        File cFile = new File("conf/configuration.txt");
        HashMap<String, String> conf = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(cFile));
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("#")) {
                    line = br.readLine();
                    continue;
                }
                String[] pairs = line.split("=");
                conf.put(pairs[0], pairs[1]);
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println(cFile.getAbsolutePath() + " not found!");
        } catch (IOException ioe) {
            System.err.println("Error when reading configuration file.");
        } finally {
            return conf;
        }
    }

    public static String getDicFile() {
        return dicFile;
    }

    public static int getK() {
        return K;
    }

    public static String getSrcDir() {
        return srcDir;
    }

    public static String getTargetFile() {
        return targetFile;
    }

    public static String getLogFile() {
        return logFile;
    }
}
