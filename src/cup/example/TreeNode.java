package cup.example;

import java.util.ArrayList;

public class TreeNode {
	
	private String text;
	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	
	public TreeNode(String text) {
		this.text = text;
	}	
	
	public void addChild(TreeNode childToAdd) {
		children.add(childToAdd);		
	}
	
	public void printNode()
	{
		printTreeNode(0);
	}
	
	private void printTreeNode(int nodeLevel)
	{
		String identString="";
		for (int ident = 0; ident < nodeLevel; ident++)
			identString+="	";
			
		System.out.println(identString + "" + text);
		for (int i = 0; i < children.size(); i++)
		{
			if(children.get(i) != null) 
				children.get(i).printTreeNode(nodeLevel + 1);
		}		
	}
	

}