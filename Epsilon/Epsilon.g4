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
mainDefinitionBody : (print | definitionInvocation | construct)* ;
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

