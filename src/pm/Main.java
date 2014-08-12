package pm;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstRoot;

import java.io.*;

/**
 * @author Wang Zerui
 */
public class Main {

    private int fileNumber;
    private int snippetNumber;

    public Main() {
        fileNumber = 0;
        snippetNumber = 0;
    }

    public static void main(String[] args) throws Exception
    {
        File rootDir = new File(args[0]);
        if (!rootDir.isDirectory()) System.out.println("Input a directory, please!");
        else {
            Main parser = new Main();
            System.setOut(new PrintStream("D:\\Development\\workspaces\\tmp\\result.txt"));
            System.setErr(new PrintStream("D:\\Development\\workspaces\\tmp\\run.log"));
            parser.parse(rootDir);
            System.out.println("File number: " + parser.fileNumber);
            System.out.println("Snippet number: " + parser.snippetNumber);
            System.setOut(System.out);
            System.setErr(System.err);
        }
    }

    public void parse(File rootDir) {
        if (rootDir.getParent().equals("D:\\Development\\workspaces\\tmp\\src_code"))
            System.err.println(rootDir.getName());
        File[] children = rootDir.listFiles();
        for (File child : children) {
            try {
                if (child.isDirectory()) parse(child);
                else if (child.getName().endsWith(".js")) {
                    fileNumber ++;
                    parseJS(child.getAbsolutePath());
                }
            } catch (Exception e) {
                try {
                    File errFile = new File("error.log");
                    FileWriter fw = new FileWriter(errFile, true);
                    fw.write(child.getAbsolutePath() + "\n");
                    fw.write(e.toString() + "\n");
                    fw.close();
                } catch (IOException ioe) {
                    System.err.println(ioe);
                }
            }
        }
    }

    public void parseJS (String filePath) throws Exception
    {
        CompilerEnvirons env = new CompilerEnvirons();
        env.setRecoverFromErrors(true);

        FileReader strReader = new FileReader(filePath);

        IRFactory factory = new IRFactory(env, new JSErrorReporter());
        AstRoot rootNode = factory.parse(strReader, null, 0);

        MNodeVisitor mNodeVisitor = new MNodeVisitor();

        rootNode.visit(mNodeVisitor);

        for (SimplePattern sp : mNodeVisitor.getAllPatterns()) {
            for (API api : sp.getApis()) System.out.print(api.toString() + " ");
            System.out.println();
        }

        this.snippetNumber += mNodeVisitor.getSnippetNumber();
    }

}
