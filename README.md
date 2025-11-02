This is a Faculty project that involves four main phases: scanning, parsing, semantic analysis and code generation. One lexical analyser for the proposed language has to be constructed. The parser must detect the tokens of 
the language and report some lexical errors. A parser for the proposed language has to be constructed. The parser has two main goals: 
a) to check whether the input is a syntactically correct program, and  
b) to generate an abstract syntax tree (AST) that records all important information about the 
program (the intermediate representation); to construct AST, some actions must to be added to the parser.

A two-pass static-semantic analyzer for the programs represented as abstract-syntax trees has to be 
constructed: 
a) the name analysis method, and 
b) type checking method. 

Finally, the abstract syntax tree will be traversed again to perform final code generation. The code 
will be generated for a specified assembly language. 
