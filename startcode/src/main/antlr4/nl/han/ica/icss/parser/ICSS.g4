grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---

stylesheet
    : (var | stylerule)* EOF
    ;

var
    : CAPITAL_IDENT ASSIGNMENT_OPERATOR expression SEMICOLON        #VariableAssignment
    ;

stylerule
    : selector OPEN_BRACE (var | declaration | ifclause)* CLOSE_BRACE
    ;

selector
    : ID_IDENT      #IdSelector
    | CLASS_IDENT   #ClassSelector
    | LOWER_IDENT   #TagSelector
    ;


declaration
    : propertyname COLON expression SEMICOLON
    ;

propertyname
    : LOWER_IDENT
    ;

expression
    : expression PLUS term       #AddOperation
    | expression MIN term        #SubtractOperation
    | term                       #SingleTerm
    ;

term
    : term MUL factor            #MultiplyOperation
    | factor                     #SingleFactor
    ;

factor
    : value
    ;

value
    : COLOR                      #ColorLiteral
    | SCALAR                     #ScalarLiteral
    | PIXELSIZE                  #PixelLiteral
    | PERCENTAGE                 #PercentageLiteral
    | CAPITAL_IDENT              #VariableReference
    | TRUE                       #BoolLiteral
    | FALSE                      #BoolLiteral
    ;

ifclause
    : IF BOX_BRACKET_OPEN value BOX_BRACKET_CLOSE
      OPEN_BRACE (var | declaration | ifclause)* CLOSE_BRACE
      elseclause?
    ;

elseclause
    : ELSE OPEN_BRACE (var | declaration | ifclause)* CLOSE_BRACE
    ;