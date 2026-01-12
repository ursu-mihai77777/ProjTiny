package symbols;

import java.util.HashMap;
import java.util.Map;

public class SymbolsTable {

    private Map<String, SymbolDetails> table = new HashMap<>();
    SymbolsTable parent;

    public SymbolsTable(SymbolsTable parent) {
        this.parent = parent;
    }

    public void insert(String name, String type) {
        if (table.containsKey(name)) {
            throw new RuntimeException("Duplicate declaration of " + name);
        }
        table.put(name, new SymbolDetails(name, type));
    }

    public SymbolDetails lookup(String name) {
        SymbolDetails s = table.get(name);
        if (s != null) return s;
        if (parent != null) return parent.lookup(name);
        throw new RuntimeException("Undeclared identifier: " + name);
    }
}
