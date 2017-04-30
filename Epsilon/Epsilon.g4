/**
 * Define a grammar called Epsilon
 */
grammar Epsilon;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

// Start
start : structure ;

// Program contains one or many user defined definitions. Program must contain a main definition in the end
structure : (definitionDeclaration)* mainDefinitionDeclaration ;

definitionDeclaration : 'defn' IDENTIFIER '(' (definitionParameters) ')' '{' (definitionBody) '}';
mainDefinitionDeclaration : 'defn' 'main' '(' ')' '{' (mainDefinitionBody) '}';
definitionParameters : (() |(identifierDeclaration) (',' identifierDeclaration)*) ;
definitionBody :  (print | definitionReturn | construct | definitionInvocation)* ;

// Main Definition does not return a value
mainDefinitionBody : (print | definitionInvocation | mainConstruct)* ;
definitionInvocation : IDENTIFIER ('('(parameters)?(',' parameters)*')');
definitionReturn : 'return' (NUMERIC | BOOL | IDENTIFIER | STRING | CHARACTER | definitionInvocation);

print : 'print' '('(expression | dataType | definitionInvocation)')';

// Arguments for functions
parameters:
    NUMERIC
    |IDENTIFIER
    |STRING
    |CHARACTER
    |boolExpression
    |expression
    ;

//If-Else Control Structure Block
ifelseStatement: (ifStatement)(elseIfStatement)* (elseStatement)?;
ifStatement : prefIf prefConstruct;
prefIf : 'if' '(' (boolExpression) ')'  ;
elseIfStatement : prefElseIf prefConstruct;
prefElseIf : 'else if' '(' (boolExpression) ')' ;
elseStatement : prefElse prefConstruct;
prefElse : 'else' ;
prefConstruct : '{' (construct | print)* '}';


mainIfElseStatement : (mainIfStatement)(mainElseIfStatement)* (mainElseStatement)?;
mainIfStatement : prefIf mainPrefConstruct ;
mainElseIfStatement : prefElseIf mainPrefConstruct;
mainElseStatement : prefElse mainPrefConstruct;
mainPrefConstruct : '{' (mainConstruct | print)* '}' ;


mainConstruct : (identifierDeclaration | identifierDeclarationAssignment | identifierAssignment | mainIfElseStatement | mainWhileIterator) ;
construct : (definitionReturn | identifierDeclaration | identifierDeclarationAssignment | identifierAssignment | ifelseStatement | whileIterator);

//while Iterator
whilePrefix : 'loop' '(' (boolExpression) ')';
whileIterator : whilePrefix '{' (construct | print)* '}' ;
mainWhileIterator : whilePrefix '{' (mainConstruct | print)* '}' ;

//boolean expression syntax
boolExpression
    :
    LOGICNOT boolExpression
    | BOOL LOGICAND boolExpression              
    | BOOL LOGICOR boolExpression
    | BOOL
    | IDENTIFIER
    | expression COMPARATORS expression
    ;
    
//expression syntax    
expression
     :          
     '(' expression ')'                  
     | NUMERIC POWER expression                
     | NUMERIC MUL expression                
     | NUMERIC DIV expression                
     | NUMERIC MODULO expression               
     | NUMERIC ADD expression                
     | NUMERIC SUB expression  
     | IDENTIFIER POWER expression                
     | IDENTIFIER MUL expression                
     | IDENTIFIER DIV expression                
     | IDENTIFIER MODULO expression               
     | IDENTIFIER ADD expression                
     | IDENTIFIER SUB expression
     | FLOAT POWER expression                
     | FLOAT MUL expression                
     | FLOAT DIV expression                
     | FLOAT MODULO expression               
     | FLOAT ADD expression                
     | FLOAT SUB expression                       
     | NUMERIC                                                               
     | IDENTIFIER   
     | FLOAT                                  
     ;

//Identifier declarations
identifierDeclaration : 'var' IDENTIFIER ;
identifierDeclarationAssignment : 'var' IDENTIFIER ':=' (dataType |definitionInvocation | expression);
identifierAssignment : IDENTIFIER ':=' ( dataType | definitionInvocation | expression) ;
dataType : (IDENTIFIER | NUMERIC | STRING | CHARACTER | BOOL);

//Primitive data types
NUMERIC: [0-9]+;
BOOL: ('TRUE' | 'FALSE');
IDENTIFIER: ([a-zA-z] | [a-zA-z](LABEL)*);
FLOAT: NUMERIC ('.' NUMERIC*)?;
LABEL:[a-zA-Z0-9]+;
STRING: '"'LABEL'"';
CHARACTER: '\''[a-zA-z]'\'';
NULL: 'null';

//Arithmetic operations
DIV: '/';
MUL: '*';
SUB: '-';
ADD: '+';
POWER: '^';
MODULO: '%';

//logical operations
LOGICAND: 'AND';
LOGICOR: 'OR';
LOGICNOT: 'NOT';
LOGICEQUAL: 'IS';

//comparison operators 
COMPARATORS: ('<' | '>' | '=' | '<=' | '>=' | '!=');


