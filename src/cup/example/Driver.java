package cup.example;


class Driver {

	public static void main(String[] args) throws Exception {
		Parser parser = new Parser();
		parser.parse();
		TreeNode root = parser.getParseTreeRoot();
		if (root != null)
			root.printNode();
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