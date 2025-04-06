

set "automata_file_path=D:\github-pro\titan\titan-ast\runtime\java\titan-ast\src\resources\titanAstGrammar.automata"
if exist %automata_file_path% (
    del %automata_file_path%
)

java -jar D:\github-pro\titan\titan-ast\out\artifacts\titan_ast_jar\titan-ast.jar^
 -grammarFilePaths^
    titanAstGrammar.txt^
 -persistentAutomataFilePath^
    ./titanAstGrammar.automata

pause