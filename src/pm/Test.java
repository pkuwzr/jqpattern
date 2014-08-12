package pm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jerry on 2014/7/27.
 */
public class Test {
    public static void main(String[] args) throws Exception{
        File rootDir = new File(args[0]);
        if (!rootDir.isDirectory()) System.out.println("Input a directory, please!");
        else {
            Test parser = new Test();
            parser.parse(rootDir);
        }
    }

    public void parse(File rootDir) {
        if (rootDir.getParent().equals("D:\\Development\\workspaces\\tmp\\src_code"))
            System.out.println(rootDir.getName());
        File[] children = rootDir.listFiles();
        for (File child : children) {
            try {
                if (child.isDirectory()) parse(child);
                /*
                else if (child.getName().endsWith(".html") || child.getName().endsWith(".htm")
                        || child.getName().endsWith(".xhtml")) parseHtml(child.getAbsolutePath());
                        */
            } catch (Exception e) {
                try {
                    File errFile = new File("error.log");
                    FileWriter fw = new FileWriter(errFile, true);
                    fw.write(child.getAbsolutePath() + "\n");
                    fw.write(e.toString() + "\n");
                    fw.close();
                } catch (IOException ioe) {
                    System.out.println(ioe);
                }
            }
        }
    }

    public void parseHtml(String filePath) throws Exception {

        File htmlFile = new File(filePath);
        Document document = Jsoup.parse(htmlFile, null);
        Elements els = document.getElementsByTag("script");
        String jsCode = new String();
        for (int i = 0; i < els.size(); i ++) jsCode += els.get(i).data();
        File jsFile = new File(filePath + ".js");
        FileWriter fw = new FileWriter(jsFile);
        fw.write(jsCode);
        fw.close();
        if (htmlFile.exists()) htmlFile.delete();

    }

}
