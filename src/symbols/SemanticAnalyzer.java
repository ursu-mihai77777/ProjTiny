package symbols;

import cup.example.TreeNode;

public class SemanticAnalyzer {

    private SymbolsTable currentScope;
    private int indent = 0;

    public void analyze(TreeNode root) {
        currentScope = new SymbolsTable(null);
        analyzeNode(root);
    }

    private void analyzeNode(TreeNode node) {
        if (node == null) return;

        switch (node.getData()) {

            case "Program":
                System.out.println("Program: " + node.getChildren().length + " declarations");
                for (TreeNode c : node.getChildren())
                    analyzeNode(c);
                break;

            case "VarDeclaration":
                handleVarDeclaration(node);
                break;

            case "FunDeclaration":
                handleFunction(node);
                break;

            case "VarDeclarations":
                // Process all variable declarations inside
                if (node.getChildren() != null) {
                    for (TreeNode c : node.getChildren())
                        analyzeNode(c);
                }
                break;

            case "Block":
                handleBlock(node);
                break;

            case "Statement Declaration":
                // Process all statements in this node
                if (node.getChildren() != null) {
                    for (TreeNode c : node.getChildren())
                        analyzeNode(c);
                }
                break;

            case "ASSIGN":
                printIndent();
                System.out.println("Assignment");
                indent++;
                if (node.getChildren() != null && node.getChildren().length >= 2) {
                    analyzeNode(node.getChildren()[0]);
                    analyzeNode(node.getChildren()[1]);
                }
                indent--;
                break;

            case "IF ELSE":
                handleIfElse(node);
                break;

            case "RETURN":
                printIndent();
                System.out.println("Return declaration");
                indent++;
                if (node.getChildren() != null && node.getChildren().length > 0) {
                    analyzeNode(node.getChildren()[0]);
                }
                indent--;
                break;

            case "Expression":
                handleExpression(node);
                break;

            case "Left expression list":
                if (node.getChildren() != null) {
                    for (TreeNode c : node.getChildren())
                        analyzeNode(c);
                }
                break;

            case "Variable":
                printIndent();
                System.out.println(node);
                break;

            case "Number: ":
                printIndent();
                System.out.println("IntLiteral: 0");
                break;

            case "BLOCK":
                // A statement that wraps a block
                if (node.getChildren() != null && node.getChildren().length > 0) {
                    analyzeNode(node.getChildren()[0]);
                }
                break;

            default:
                if (node.getChildren() != null) {
                    for (TreeNode c : node.getChildren())
                        analyzeNode(c);
                }
        }
    }

    private void handleVarDeclaration(TreeNode node) {
        TreeNode[] children = node.getChildren();
        
        // Navigate nested VarDeclaration structure
        TreeNode current = node;
        while (current.getData().equals("VarDeclaration") && 
               current.getChildren() != null && 
               current.getChildren().length == 1 &&
               current.getChildren()[0].getData().equals("VarDeclaration")) {
            current = current.getChildren()[0];
        }

        // Now current should have: name and TYPE (2 children)
        if (current.getChildren() == null || current.getChildren().length < 2) return;
        
        String name = current.getChildren()[0].getData();
        TreeNode typeNode = current.getChildren()[1];
        
        if (typeNode.getChildren() == null || typeNode.getChildren().length == 0) return;
        String type = typeNode.getChildren()[0].getData();

        try {
            currentScope.insert(name, type);
            printIndent();
            System.out.println("VarDecl " + name + "; type:  " + type);
        } catch (RuntimeException e) {
            printIndent();
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleFunction(TreeNode node) {
        TreeNode[] children = node.getChildren();
        if (children == null || children.length < 1) return;

        TreeNode functionNode = children[0];
        
        if (functionNode.getChildren() == null || functionNode.getChildren().length < 3) return;

        // Get function name from extraData
        String functionName = functionNode.getExtraData();
        if (functionName == null || functionName.isEmpty()) {
            functionName = "unknown";
        }

        TreeNode typeNode = functionNode.getChildren()[0];
        TreeNode paramsNode = functionNode.getChildren()[1];
        TreeNode blockNode = functionNode.getChildren()[2];

        String returnType = "void";
        if (typeNode.getChildren() != null && typeNode.getChildren().length > 0) {
            returnType = typeNode.getChildren()[0].getData();
        }

        int paramCount = countParams(paramsNode);

        printIndent();
        System.out.println("FunDecl: " + functionName + "Args count: " + paramCount + "Return: " + returnType);

        enterScope();
        collectParams(paramsNode);
        analyzeNode(blockNode);
        exitScope();
    }

    private void handleBlock(TreeNode node) {
        if (node.getChildren() == null) return;

        // Count local variables and statements
        int localVarCount = 0;
        int statementCount = 0;
        TreeNode varDeclarations = null;
        TreeNode localVariables = null;
        TreeNode statementDeclaration = null;

        for (TreeNode child : node.getChildren()) {
            if (child.getData().equals("VarDeclarations")) {
                varDeclarations = child;
                // Count variable declarations
                if (child.getChildren() != null) {
                    localVarCount = child.getChildren().length;
                }
            } else if (child.getData().equals("LocalVariables")) {
                localVariables = child;
            } else if (child.getData().equals("Statement Declaration")) {
                statementDeclaration = child;
                // Count actual statements in Statement Declaration
                if (child.getChildren() != null) {
                    statementCount = child.getChildren().length;
                }
            }
        }

        printIndent();
        System.out.println("Block - " + localVarCount + " locals; " + statementCount + " statements;");

        enterScope();

        // Process variable declarations
        if (varDeclarations != null && varDeclarations.getChildren() != null) {
            for (TreeNode varDecl : varDeclarations.getChildren()) {
                analyzeNode(varDecl);
            }
        }

        // Process statements
        if (statementDeclaration != null) {
            analyzeNode(statementDeclaration);
        }

        exitScope();
    }

    private void handleIfElse(TreeNode node) {
        printIndent();
        System.out.println("IF Statement");
        indent++;

        if (node.getChildren() != null && node.getChildren().length >= 3) {
            // First child is the condition expression
            analyzeNode(node.getChildren()[0]);
            
            // Second child is the then block
            analyzeNode(node.getChildren()[1]);
            
            // Third child is the else block
            analyzeNode(node.getChildren()[2]);
        }

        indent--;
    }

    private void handleExpression(TreeNode node) {
        if (node.getChildren() == null || node.getChildren().length == 0) return;

        // Check if this is a binary operation
        // Expression structure: [Left expression list, operator, Expression]
        String operator = null;
        TreeNode leftExpr = null;
        TreeNode rightExpr = null;

        if (node.getChildren().length == 1) {
            // Simple expression, just process the child
            analyzeNode(node.getChildren()[0]);
            return;
        }

        // Look for binary operation pattern
        for (int i = 0; i < node.getChildren().length; i++) {
            TreeNode child = node.getChildren()[i];
            String data = child.getData();

            if (data.equals("Left expression list")) {
                leftExpr = child;
            } else if (data.equals("Greater")) {
                operator = "GREATER";
            } else if (data.equals("Plus")) {
                operator = "PLUS";
            } else if (data.equals("Minus")) {
                operator = "SUBSTRACTION";
            } else if (data.equals("Equal")) {
                operator = "EQUALS";
            } else if (data.equals("Times")) {
                operator = "MULTIPLICATION";
            } else if (data.equals("Divide")) {
                operator = "DIVISION";
            } else if (data.equals("Expression")) {
                rightExpr = child;
            }
        }

        if (operator != null && leftExpr != null && rightExpr != null) {
            printIndent();
            System.out.println("BinOp - " + operator);
            indent++;
            analyzeNode(leftExpr);
            analyzeNode(rightExpr);
            indent--;
        } else if (leftExpr != null) {
            analyzeNode(leftExpr);
        } else {
            // Fallback: process all children
            for (TreeNode child : node.getChildren()) {
                analyzeNode(child);
            }
        }
    }

    private int countParams(TreeNode node) {
        if (node == null || node.getChildren() == null) return 0;
        
        // Check if this is empty parameters
        if (node.getData().equals("Formal Parameters")) {
            return 0;
        }
        
        // ParamsList structure: the outer ParamsList contains another ParamsList
        // Navigate to the inner ParamsList which contains actual parameters
        TreeNode paramsList = node;
        if (node.getData().equals("ParamsList") && 
            node.getChildren() != null && 
            node.getChildren().length == 1 &&
            node.getChildren()[0].getData().startsWith("ParamsList")) {
            paramsList = node.getChildren()[0];
        }
        
        int count = 0;
        TreeNode current = paramsList;
        
        // Now traverse the nested ParamsList structure
        while (current != null && current.getData().startsWith("ParamsList")) {
            TreeNode[] children = current.getChildren();
            if (children == null || children.length == 0) break;
            
            // Count this parameter if it has TYPE and extraData (parameter name)
            boolean hasType = false;
            TreeNode nextParamsList = null;
            
            for (TreeNode child : children) {
                if (child.getData().equals("TYPE")) {
                    hasType = true;
                } else if (child.getData().startsWith("ParamsList")) {
                    nextParamsList = child;
                }
            }
            
            // A parameter node has both TYPE child and a name in extraData
            if (hasType && current.getExtraData() != null && !current.getExtraData().isEmpty()) {
                count++;
            }
            
            current = nextParamsList;
        }
        
        return count;
    }

    private void collectParams(TreeNode node) {
        if (node == null) return;
        
        // Check if this is empty parameters
        if (node.getData().equals("Formal Parameters")) {
            return;
        }
        
        // Navigate to inner ParamsList if needed
        TreeNode paramsList = node;
        if (node.getData().equals("ParamsList") && 
            node.getChildren() != null && 
            node.getChildren().length == 1 &&
            node.getChildren()[0].getData().startsWith("ParamsList")) {
            paramsList = node.getChildren()[0];
        }
        
        // Collect all parameters recursively
        collectParamsRecursive(paramsList);
    }
    
    private void collectParamsRecursive(TreeNode node) {
        if (node == null || !node.getData().startsWith("ParamsList")) return;
        
        TreeNode[] children = node.getChildren();
        if (children == null || children.length == 0) return;
        
        TreeNode typeNode = null;
        String paramName = null;
        
        // Get parameter name from extraData if available
        if (node.getExtraData() != null && !node.getExtraData().isEmpty()) {
            paramName = node.getExtraData();
        }
        
        // Process all children
        for (TreeNode child : children) {
            if (child.getData().equals("TYPE")) {
                typeNode = child;
            } else if (child.getData().startsWith("ParamsList")) {
                // Recursively process all ParamsList children
                collectParamsRecursive(child);
            }
        }
        
        // Add this parameter if it has a name and type
        if (typeNode != null && paramName != null && !paramName.isEmpty() && 
            typeNode.getChildren() != null && typeNode.getChildren().length > 0) {
            String type = typeNode.getChildren()[0].getData();
            
            try {
                currentScope.insert(paramName, type);
                printIndent();
                System.out.println("VarDecl " + paramName + "; type:  " + type);
            } catch (RuntimeException e) {
                printIndent();
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void enterScope() {
        currentScope = new SymbolsTable(currentScope);
        indent++;
    }

    private void exitScope() {
        if (currentScope != null && currentScope.parent != null) {
            currentScope = currentScope.parent;
        }
        indent--;
    }

    private void printIndent() {
        for (int i = 0; i < indent; i++)
            System.out.print("\t");
    }
}