package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstRoot;
import pm.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Zerui
 * For now, PatternHelper can do two things:
 * 1.Mining patterns from Configuration.SrcDir.
 * And store these patterns in Configuration.TargetFile.
 * 2.Searching the top Kth patterns from current patterns library
 * as a list.
 */
public class PatterHelper {

    private int fileNumber;
    private int snippetNumber;
    private ArrayList<SimplePattern> allPatterns;

    public PatterHelper() {
        Configuration.init();
        APIDic.load();
        fileNumber = 0;
        snippetNumber = 0;
        allPatterns = null;
    }

    /*
    * Run the mining process for js files.
    * */
    public void runMining() {
        try {
            File srcDir = new File(Configuration.getSrcDir());
            if (!srcDir.isDirectory())
                System.err.println(srcDir.getAbsolutePath() + " is not a directory!");
            else {
                System.setOut(new PrintStream(Configuration.getTargetFile()));
                System.setErr(new PrintStream(Configuration.getLogFile()));
                preprocess(srcDir);
                fileNumber = 0;
                snippetNumber = 0;
                allPatterns = new ArrayList<>();
                parse(srcDir);
                for (SimplePattern sp : allPatterns) {
                    System.out.println(sp);
                }
                System.err.println("Statistics");
                System.err.println("---------------------------------");
                System.err.println("Total files: " + fileNumber);
                System.err.println("Total snippets: " + snippetNumber);
                System.setOut(System.out);
                System.setErr(System.err);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(Configuration.getTargetFile() + " or "
                    + Configuration.getLogFile() + " not found!");
        }
    }

    /*
    * target: the target pattern to search.
    * reload: If you have run mining lately, then all patterns may probably
    * be still in memory. You can set reload true and force a reload of patterns
    * from Configuration.targetFile.
    * */
    public List<SearchResult> search(SimplePattern target, boolean reload) {
        if (allPatterns == null || reload) load();
        List<SearchResult> searchResults = new ArrayList<>(Configuration.getK() + 1);
        for (int i = 0; i < Configuration.getK() + 1; i++)
            searchResults.add(new SearchResult(new SimplePattern(), -1));
        for (SimplePattern sp : allPatterns) {
            double sim = sp.getSimilarity(target);
            int i;
            for (i = searchResults.size()-2; i >= 0; i--) {
                if (searchResults.get(i).getSimilarity() < sim)
                    searchResults.set(i + 1, searchResults.get(i));
                else break;
            }
            searchResults.set(i + 1, new SearchResult(sp, sim));
        }
        return searchResults;
    }

    /*
    * Deal with all html files in srcDir recursively.
    * */
    private void preprocess(File srcDir) {
        File[] children = srcDir.listFiles();
        for (File child : children) {
            try {
                if (child.isDirectory()) parse(child);
                else if (child.getName().endsWith(".html") || child.getName().endsWith(".htm")
                        || child.getName().endsWith(".xhtml")) transferHtml(child.getAbsolutePath());
            } catch (Exception e) {
                System.err.println(child.getAbsolutePath());
                System.err.println(e.toString());
            }
        }
    }

    /*
    * Abstract js snippets from one .html file, store these snippets
    * in a .js file and remove the .html file.
    * */
    private void transferHtml(String filePath) throws Exception {

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

    /*
    * Parse src dir and get all patterns.
    * */
    private void parse(File srcDir) {

        File[] children = srcDir.listFiles();
        for (File child : children) {
            try {
                if (child.isDirectory()) parse(child);
                else if (child.getName().endsWith(".js")) {
                    fileNumber ++;
                    parseJS(child.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println(child.getAbsolutePath());
                System.err.println(e);
            }
        }

    }

    /*
    * Parse a js file and collect the patters found.
    * */
    private void parseJS(String filePath) {
        CompilerEnvirons env = new CompilerEnvirons();
        env.setRecoverFromErrors(true);

        try {
            FileReader strReader = new FileReader(filePath);

            IRFactory factory = new IRFactory(env, new JSErrorReporter());
            AstRoot rootNode = factory.parse(strReader, null, 0);

            MNodeVisitor mNodeVisitor = new MNodeVisitor();

            rootNode.visit(mNodeVisitor);

            ArrayList<SimplePattern> patterns = mNodeVisitor.getAllPatterns();

            for (SimplePattern sp : patterns) {
                boolean existed = false;
                for (int i = 0; i < allPatterns.size(); i++) {
                    if (sp.equals(allPatterns.get(i))) {
                        existed = true;
                        allPatterns.get(i).addAllSrcCode(sp.getSrc());
                    }
                }
                if (!existed) allPatterns.add(sp);
            }

            this.snippetNumber += mNodeVisitor.getSnippetNumber();
        } catch (FileNotFoundException fnfe) {
            System.err.println(filePath + " not found!");
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        }
    }

    /*
    * Load patterns from Configuration.targetFile.
    * */
    private void load() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(Configuration.getTargetFile()));
            allPatterns = new ArrayList<>();
            ArrayList<Integer> apis = new ArrayList<>();
            ArrayList<Relation> relations = new ArrayList<>();
            String aline = br.readLine();
            String rline = br.readLine();
            while (aline != null && rline != null) {
                String[] apiStrings = aline.split(" ");
                String[] relationStrings = rline.split("\\|");
                for (String as : apiStrings) apis.add(Integer.parseInt(as));
                for (String rs : relationStrings) {
                    String[] items = rs.split(",");
                    relations.add(new Relation(Integer.parseInt(items[0]),
                            Integer.parseInt(items[1]),
                            Integer.parseInt(items[2])));
                }
                SimplePattern sp = new SimplePattern(apis, relations);
                int count = Integer.parseInt(br.readLine());
                for (int i = 0; i < count; i++) {
                    String code = "";
                    String ite = br.readLine();
                    while (ite != null && !ite.equals("$END$")) {
                        code += (ite + "\n");
                        ite = br.readLine();
                    }
                    sp.addSrcCode(code);
                }
                allPatterns.add(sp);
                aline = br.readLine();
                rline = br.readLine();
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println(Configuration.getTargetFile() + " not found!");
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

}
