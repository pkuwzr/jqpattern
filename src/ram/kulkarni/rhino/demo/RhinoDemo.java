package ram.kulkarni.rhino.demo;

import java.io.FileReader;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstRoot;

/**
 * 
 * @author Ram Kulkarni
 *
 */
public class RhinoDemo {

	public static void main(String[] args) throws Exception
	{
		String filePath = "Change This : specify full path of some JS file";
		
		RhinoDemo demo = new RhinoDemo();
		demo.parseJS(filePath);
	}
	
	public void parseJS (String filePath) throws Exception
	{
		CompilerEnvirons env = new CompilerEnvirons();
		env.setRecoverFromErrors(true);
		
		FileReader strReader = new FileReader(filePath);

		IRFactory factory = new IRFactory(env, new JSErrorReporter());
		AstRoot rootNode = factory.parse(strReader, null, 0);
		
		JSNodeVisitor nodeVisitor = new JSNodeVisitor();
		
		rootNode.visit(nodeVisitor);
		
		nodeVisitor.getRoot().visit(new JSSymbolVisitor());
	}	
}
