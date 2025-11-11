package cup.example;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import java.lang.*;
import java.io.InputStreamReader;

%%

%class Lexer
%implements sym
%public
%unicode
%line
%column
%cup
%char
%{
	

    public Lexer(ComplexSymbolFactory sf, java.io.InputStream is){
		this(is);
        symbolFactory = sf;
    }
	public Lexer(ComplexSymbolFactory sf, java.io.Reader reader){
		this(reader);
        symbolFactory = sf;
    }
    
    private StringBuffer sb;
    private ComplexSymbolFactory symbolFactory;
    private int csline,cscolumn;

    public Symbol symbol(String name, int code){
		return symbolFactory.newSymbol(name, code,
						new Location(yyline+1,yycolumn+1, yychar), // -yylength()
						new Location(yyline+1,yycolumn+yylength(), yychar+yylength())
				);
    }
    public Symbol symbol(String name, int code, String lexem){
	return symbolFactory.newSymbol(name, code, 
						new Location(yyline+1, yycolumn +1, yychar), 
						new Location(yyline+1,yycolumn+yylength(), yychar+yylength()), lexem);
    }
    
    protected void emit_warning(String message){
    	System.out.println("scanner warning: " + message + " at : 2 "+ 
    			(yyline+1) + " " + (yycolumn+1) + " " + yychar);
    }
    
    protected void emit_error(String message){
    	System.out.println("scanner error: " + message + " at : 2" + 
    			(yyline+1) + " " + (yycolumn+1) + " " + yychar);
    }
%}

Newline    = \r | \n | \r\n
Whitespace = [ \t\f] | {Newline}
Number     = [0-9]+


/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment = "/*" {CommentContent} \*+ "/"
EndOfLineComment = "//" [^\r\n]* {Newline}
CommentContent = ( [^*] | \*+[^*/] )*
Quotes = \'
QChar = \'[^\']\'

ident = ([:jletter:]) ([:jletterdigit:] | [:jletter:] | "_" )*


%eofval{
    return symbolFactory.newSymbol("EOF",sym.EOF);
%eofval}

%state CODESEG

%%  

<YYINITIAL> {

  {Whitespace} {                              }
  {EndOfLineComment} {}
  ";"          { return symbolFactory.newSymbol("SEMI", SEMI); }
  ","          { return symbolFactory.newSymbol("COMMA", COMMA); }
  
  "+"          { return symbolFactory.newSymbol("PLUS", PLUS); }
  "-"          { return symbolFactory.newSymbol("MINUS", MINUS); }
  "*"          { return symbolFactory.newSymbol("TIMES", TIMES); }
  "/"          { return symbolFactory.newSymbol("DIVIDE", DIVIDE); }
  
  
  "("          { return symbolFactory.newSymbol("LPAR", LPAR); }
  ")"          { return symbolFactory.newSymbol("RPAR", RPAR); }
  
  "{"          { return symbolFactory.newSymbol("LBRACE", LBRACE); }
  "}"          { return symbolFactory.newSymbol("RBRACE", RBRACE); }
  
  "["          { return symbolFactory.newSymbol("LBRACK", LBRACK); }
  "]"          { return symbolFactory.newSymbol("RBRACK", RBRACK); }
  
  "="          { return symbolFactory.newSymbol("ASSIGN", ASSIGN); }
  "!"          { return symbolFactory.newSymbol("NOT", NOT); }
  
  "=="         { return symbolFactory.newSymbol("EQUAL", EQUAL); } 
  "!="         { return symbolFactory.newSymbol("NEQUAL", NEQUAL); } 
  ">"          { return symbolFactory.newSymbol("GREATER", GREATER); }
  "<"          { return symbolFactory.newSymbol("LESS", LESS); }
  
  "int"        { return symbolFactory.newSymbol("INT", sym.INT); }
  "char"       { return symbolFactory.newSymbol("CHAR", sym.CHAR); }
  
  "if"         { return symbolFactory.newSymbol("IF", sym.IF); }
  "else"       { return symbolFactory.newSymbol("ELSE", sym.ELSE); }
  "while"      { return symbolFactory.newSymbol("WHILE", sym.WHILE); }
  "return"     { return symbolFactory.newSymbol("RETURN", sym.RETURN); }
  "length"     { return symbolFactory.newSymbol("LENGTH", sym.LENGTH); }
  
  "write"      { return symbolFactory.newSymbol("WRITE", sym.WRITE); }
  "read"       { return symbolFactory.newSymbol("READ", sym.READ); }
  
  
  {QChar}      { return symbolFactory.newSymbol("QCHAR", QCHAR, yytext().charAt(1)); }
  {Number}     { return symbolFactory.newSymbol("NUMBER", NUMBER, Integer.parseInt(yytext())); }
  {ident}      { return symbolFactory.newSymbol("NAME", NAME, yytext()); }
}



// error fallback
.|\n          { emit_warning("Unrecognized character '" +yytext()+"' -- ignored"); }