set "code_file_path=D:\github-pro\titan\titan-language-compiler\src\resources\grammar\titanLanguageEncodingLexer.txt"

java -jar  D:\github-pro\titan\titan-language-compiler\libs\titan-ast-runtime.jar^
    titanAstGrammar.automata %code_file_path% utf-8
pause