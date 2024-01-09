```
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@[[[`     [[\@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@/[                  =@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@/`                       @@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@[                          =@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@/                           ,/@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@/                         ,/@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@/                ,].[              [[@@@@@@@@@@@@@
@@@@@@@@@@@@`           ]@[                          ,\@@@@@@@@
@@@@@@@@@@/        ,//`                                 \@@@@@@
@@@@@@@@@/      ]@/                                      @@@@@@
@@@@@@@@/    ]@/                                        =@@@@@@
@@@@@@@@  ,/@/           ,]]]]@@]@\]]]]]]             ]@@@@@@@@
@@@@@@@` /@/       ,/@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@ @@`    ,/@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@^=@   ,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@^@` /@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@^,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@/@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@.\@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@\ ,@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@    [[[[[[[@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@\           @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@]       /@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
```
# titan-ast

generate Abstract Syntax Tree by grammar file.


# quick start
a regular expression describes the format of grammars:

keywords of statement :  -> ; : # nfa(s,t) regexp

keywords of regular expression:  ()[]''?*+-{}\s

default terminal grammar:  Epsilon

default nonterminal grammar:  augmentedNonterminal

format of grammar's definition : nameOfGrammar  [nfa(start,end) regexp() greediness() laziness() acceptWhenFirstArriveAtTerminalState()] : regularExpression [-> skip] ;

The grammar file should be encoded in 8bits(iso-8859-1) format.

each token separated by white spaces.tokens include  //, /*, */, :, ; and regExpUnit.

content of grammar file like this；

```html
@StartGrammar compilationUnit ;

@NonterminalGrammar begin ;
argumentExpressionList
    :   assignmentExpression
    |   argumentExpressionList ',' assignmentExpression
    ;
...
@NonterminalGrammar end ;

@TerminalGrammar begin ;
Int : 'int' ;
...
@TerminalGrammar end ;

@TerminalFragmentGrammar begin ;
NewlineFragment
	: 	'\r'? '\n'
	;
...
@TerminalFragmentGrammar end ;

@RootKeyWord Identifier ;

@KeyWord begin ;
If : 'if' ;
...
@KeyWord end ;
```

java -jar  titan-ast.jar  -grammarFilePath D:/github-pro/titan/titan-ast/test/c/C.grammar -sourceFilePath D:/github-pro/titan/titan-ast/test/c/helloworld.c  --graphicalViewOfAst

java -jar  titan-ast.jar  -grammarFilePath  D:/github-pro/titan/titan-ast/test/c/C.grammar  -persistentAutomataFilePath  D:/github-pro/titan/titan-ast/test/c/automata.data

java -jar  titan-ast.jar  -automataFilePath D:/github-pro/titan/titan-ast/test/c/automata.data -sourceFilePath  D:/github-pro/titan/titan-ast/test/c/helloworld.c --graphicalViewOfAst

import titan-ast.jar and using it like this:

```java
    String automataFilePath = "D:/java-ws/titan-ast/test/c/automata.data";
    String sourceCodeFilePath = "D:/java-ws/titan-ast/test/c/helloworld.c";

    RuntimeAutomataAstApplication runtimeAstApplication = new RuntimeAutomataAstApplication();
    runtimeAstApplication.setContext(automataFilePath);

    Ast ast = runtimeAstApplication.buildAst(sourceCodeFilePath);
    runtimeAstApplication.displayGraphicalViewOfAst(ast);
```



# reporting vulnerabilities

titan takes security issues very seriously. If you have any concerns about titan-ast or believe you have uncovered a vulnerability, please get in touch via the QQ group chatting room 214515321 or the e-mail address 1932252321@qq.com. In the message, try to provide a description of the issue and ideally a way of reproducing it. i will get back to you as soon as possible.




# references

- Alfred V.Aho,Ravi Sethi,Jeffrey D.Ullman.*Compilers: Principles,Techniques,and Tools*.