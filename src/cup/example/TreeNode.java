package cup.example;

import java.util.ArrayList;



public class TreeNode {
	
	private String data;
	private String extraData;
	private ArrayList<TreeNode> children;	
	private int descendentsCount = 0; 
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;			
	}
	
	public String getExtraData()
	{
		return extraData;
	}
	
	public int getDescendentsCount()
	{		
		return descendentsCount;
	}

	public TreeNode[] getChildren() {
		TreeNode[] childrenArray = new TreeNode[children.size()];		
		return children.toArray(childrenArray);
	}	
	
	public TreeNode(String data, String extraData)
	{
		this.data = data;
		this.extraData = extraData;
		children = new ArrayList<TreeNode>();
	}
	
	public TreeNode(String data) 
	{
		this(data, "");
	}
	
	public TreeNode addChild(String childData)
	{
		TreeNode addedNode = new TreeNode(childData);
		children.add(addedNode);
		return addedNode;
	}
	
	public void addChild(TreeNode node)
	{
		children.add(node);
		descendentsCount += node.descendentsCount + 1;
	}
	
	public void printNode(int level)
	{
		for (int i = 0; i < level; i++)
		{
			System.out.print(" ");
		}
		System.out.print(data);
		if (extraData != null && extraData.length() > 0)
		{
			System.out.print(" - " + extraData + " - ");
		}
		System.out.println("");
		
		for (TreeNode multiTreeNode : children) {
			multiTreeNode.printNode(level + 1);
		}
	}

}
