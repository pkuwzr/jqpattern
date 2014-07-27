package pm;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.*;

import java.util.ArrayList;

/**
 *
 * @author Wang Zerui
 *
 */
public class MNodeVisitor implements NodeVisitor
{

    private ArrayList<MContext> contexts = null;
    private int contextIdx;

    @Override
    public boolean visit(AstNode node) {
        if (node == null)
            return false;

        // If there is no context, build one.
        if (contexts == null) {
            contexts = new ArrayList<MContext>();
            contexts.add(new MContext());
            contextIdx = 0;
        }

        int nodeType = node.getType();
        if (nodeType == Token.CALL) {
            // Get the basic info of current function call node.
            FunctionCall call_node = (FunctionCall) node;
            AstNode target = call_node.getTarget();
            int target_type = target.getType();
            MContext mcontext = contexts.get(contexts.size() - 1);

            /*
            If it's the first time visiting function call node, start mining.
             */
            if (!mcontext.inMining) {
                mcontext.inMining = true;
                mcontext.livePattern = new SimplePattern();
                mcontext.topNode = call_node;
            }

            JqAPI thisAPI = getAPI(call_node);
            /*
            to do : add this API and relations of this api to the live pattern of every context.
             */
            if (mcontext.lastIdx == -1) {
                mcontext.livePattern.addAPI(thisAPI);
                mcontext.lastIdx = 0;
            }
            else {
                mcontext.livePattern.addSeqAPI(thisAPI, mcontext.lastIdx);
                mcontext.lastIdx = mcontext.livePattern.getAPINumber() - 1;
            }

            /*
            Processing all param nodes, and if the param node is a function node, add new context and visit
            the function node.
             */
            for (AstNode param : call_node.getArguments()) {
                if (param.getType() == Token.FUNCTION) {
                    MContext new_context = new MContext();
                    contexts.add(new_context);
                    contextIdx ++;
                    param.visit(this);
                    mergeContext();
                    contexts.remove(contextIdx);
                    contextIdx --;
                }
            }

            /*
            If current node is the node of top level, then add the live pattern of current context to
            collector of pattern.
            */
            if (mcontext.inMining && call_node.equals(mcontext.topNode)) {
                mcontext.allPatterns.add(mcontext.livePattern);
                mcontext.inMining = false;
                mcontext.lastIdx = -1;
                mcontext.livePattern = null;
                mcontext.topNode = null;
            }

            return false;
        }

        return true;
    }

    private JqAPI getAPI(FunctionCall callNode) {

        AstNode target = callNode.getTarget();
        int target_type = target.getType();
        String function_name = new String();
        ArrayList<String> params = new ArrayList<String>();
        // Get the function name of this api call;
        switch (target_type) {
            case Token.NAME:
                function_name = ((Name)target).getIdentifier();break;
            case Token.GETPROP:
                AstNode left = ((PropertyGet)target).getLeft();
                Name right = ((Name)((PropertyGet)target).getRight());
                if (left.getType() == Token.NAME)
                    function_name = ((Name)left).getIdentifier() + "." + right.getIdentifier();
                else {
                    function_name = right.getIdentifier();
                    target.visit(this);
                }
                break;
            default:
                function_name = "Unkown";break;
        }
        // Get the type of the param list.
        // Only deal with the literals.
        for (AstNode param : callNode.getArguments()) {
            int paramType = param.getType();
            switch (paramType) {
                case Token.ARRAYLIT:
                    params.add("ARRAY");break;
                case Token.TRUE:
                case Token.FALSE:
                    params.add("BOOLEAN");break;
                case Token.FUNCTION:
                    params.add("FUNCTION");break;
                case Token.NUMBER:
                    params.add("NUMBER");break;
                case Token.OBJECTLIT:
                    params.add("OBJECT");break;
                case Token.STRING:
                    params.add("STRING");break;
                default:
                    params.add("VAR");break;
            }
        }
        // Return the result.
        return new JqAPI(function_name, params, contextIdx);
    }

    private void mergeContext() {
        SimplePattern parent = contexts.get(contextIdx - 1).livePattern;
        ArrayList<SimplePattern> children = contexts.get(contextIdx).allPatterns;
        int caller = parent.getAPINumber();
        int base = caller;
        for (int i = 0; i < children.size(); i ++) {
            for (JqAPI api : children.get(i).getApis()) {
                parent.addAPI(api);
                if (api.getDepth() == contextIdx)
                    parent.addRelation(new Relation(caller - 1, parent.getAPINumber() - 1, Relation.CALLBACK));
            }
            for (Relation relation : children.get(i).getRelations())
                parent.addRelation(new Relation(relation.api1 + base, relation.api2 + base, relation.name));
            base = parent.getAPINumber();
        }
        contexts.get(contextIdx - 1).allPatterns.addAll(children);
    }

    public ArrayList<SimplePattern> getAllPatterns() {
        return contexts.get(0).allPatterns;
    }
}

/*
MContext represents the local context state of visitor.
 */
class MContext {
    // Whether the visitor is mining a pattern.
    public boolean inMining;
    // last function call index in the call set of live pattern in current context.
    // for remembering the sequential relation.
    public int lastIdx;
    // call function index in the call set of live pattern in the parent context.
    public int callerIdx;
    public AstNode topNode;
    public SimplePattern livePattern;
    public ArrayList<SimplePattern> allPatterns;

    public MContext() {
        inMining = false;
        lastIdx = -1;
        topNode = null;
        livePattern = null;
        allPatterns = new ArrayList<SimplePattern>();
    }

}