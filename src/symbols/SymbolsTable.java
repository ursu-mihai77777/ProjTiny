package symbols;

import java.util.HashMap;
import java.util.Map;

import cup.example.*;

public class SymbolsTable {

    // private Tree syntaxTree;
    private HashMap<String, SymbolDetails> table = new HashMap<String, SymbolDetails>();
    private TreeNode root = null;

    private void extractSymbolsFromNode(TreeNode node,String currentContext,IdentifierScope scope) {

        String context = currentContext;
        IdentifierScope currentScope = scope;

        /* ================= FUNCTION ================= */
        if (node.getData().equals("FunDeclaration")) {

            TreeNode fn = node.getChildren()[0];

            SymbolDetails details = new SymbolDetails();
            details.symbolName = fn.getExtraData();
            details.contextName = "Global";
            details.symbolScope = IdentifierScope.Global;
            details.symbolType = SymbolType.Function;

            if (fn.getChildren().length > 0) {
                details.dataType =fn.getChildren()[0].getChildren()[0].getData();
            }

            table.put("Global:" + details.symbolName, details);

           //local context
            context = details.symbolName;
            currentScope = IdentifierScope.Local;
        }

        /* ================= VARIABLE ================= */
        if (node.getData().equals("VarDeclaration")&& node.getChildren().length == 2) {

            TreeNode nameNode = node.getChildren()[0];
            TreeNode typeNode = node.getChildren()[1];

            SymbolDetails details = new SymbolDetails();
            details.symbolName = nameNode.getData();
            details.dataType = typeNode.getChildren()[0].getData();
            details.contextName = context;
            details.symbolScope = currentScope;
            details.symbolType = SymbolType.Variable;

            table.put(context + ":" + details.symbolName, details);
        }

        /* ================= RECURSION ================= */
        for (TreeNode child : node.getChildren()) {
            extractSymbolsFromNode(child, context, currentScope);
        }
    }

    public SymbolsTable(TreeNode root) {
        this.root = root;
    }

    public void createTable() {
        extractSymbolsFromNode(root, "Global", IdentifierScope.Global);
    }

    public SymbolDetails getSymbol(String symbol) {
        if (table.containsKey(symbol)) {
            return table.get(symbol);
        }
        return null;
    }

    public void printTable() {
        for (Map.Entry<String, SymbolDetails> mapEntry : table.entrySet()) {
            String symbol = mapEntry.getKey();
            SymbolDetails details = mapEntry.getValue();
            System.out.println("------------ SYMBOL: " + symbol + " -----------------");
            System.out.println("Data Type: " + details.dataType);
            System.out.println("Context: " + details.contextName);
            System.out.println("Symbol Type: " + details.symbolType);
            System.out.println("Symbol Scope: " + details.symbolScope);
        }
    }
}
