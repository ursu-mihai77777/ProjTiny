package symbols;

enum IdentifierScope { Local, Global };
enum SymbolType { Function, Variable, DataType };

public class SymbolDetails {
	public String symbolName;
	public String dataType;
	public SymbolType symbolType;
	public IdentifierScope symbolScope;
	public String contextName;
}