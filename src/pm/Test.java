package pm;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstRoot;
import ram.kulkarni.rhino.demo.JSErrorReporter;

import java.io.FileReader;

/**
 * Created by Jerry on 2014/7/27.
 */
public class Test {
    public static void main(String[] args) throws Exception
    {
        String filePath = "test.js";

        Test test = new Test();
        test.parseJS(filePath);
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

        for (SimplePattern sp : mNodeVisitor.getAllPatterns()) System.out.println(sp);
    }
}
