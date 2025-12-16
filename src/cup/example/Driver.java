package cup.example;
import cup.example.*;
import java.io.FileInputStream;
import java.io.IOException;
import java_cup.runtime.*;
import symbols.SymbolsTable;


class Driver {

	public static void main(String[] args) throws Exception {
		 Parser parser = new Parser();
	        parser.parse();
	        //parser.debug_parse();
	        TreeNode root = parser.getSyntaxTree();
	        TreeNodePrinter treePrinter = new TreeNodePrinter(root);
	        treePrinter.print();
	        SymbolsTable table = new SymbolsTable(root);
	        table.createTable();
	        table.printTable();
		/*TreeNode root = new TreeNode("Root");
		root.addChild(new TreeNode("Root->Child1"));
		root.addChild(new TreeNode("Root->Child2"));
		root.addChild(new TreeNode("Root->Child3"));
		
		TreeNode child4 = new TreeNode("Root->Child4");
		child4.addChild(new TreeNode("Root->Child4->Child1"));
		child4.addChild(new TreeNode("Root->Child4->Child2"));
		child4.addChild(new TreeNode("Root->Child4->Child3"));
		root.addChild(child4);
		
		root.printNode();*/
	}
	
}