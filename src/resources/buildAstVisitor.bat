

set "ast_file_dir=D:\github-pro\titan\titan-ast\src\titan\ast\impl\ast\contextast\"

java -jar D:\github-pro\titan\titan-ast\out\artifacts\titan_ast_jar\titan-ast.jar^
 -grammarFilePaths^
    titanAstGrammar.txt^
 -astVisitorFileDirectory^
    %ast_file_dir%^
    titan.ast.impl.ast.contextast

pause