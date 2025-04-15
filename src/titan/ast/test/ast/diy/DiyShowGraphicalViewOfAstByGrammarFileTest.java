package titan.ast.test.ast.diy;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

public class DiyShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String fileDirectory = "D:\\github-pro\\titan\\titan-ast\\test\\titanlang\\";
    String[] testArgs = {
        "-grammarFilePaths",
        /*fileDirectory + "titanLanguageAsciiLexer.txt",
        fileDirectory + "titanLanguageChineseLexer.txt",
        fileDirectory + "titanLanguageEncodingLexer.txt",
        fileDirectory + "titanLanguageNotTextTokenLexer.txt",
        fileDirectory + "titanLanguageNumberLiteralLexer.txt",
        fileDirectory + "titanLanguageCharsLiteralLexer.txt",
        fileDirectory + "titanLanguagePunctuationLexer.txt",
        fileDirectory + "titanLanguageIdentifierLexer.txt",*/
        "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
        "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/diy/diy.txt",
        "-graphicalViewOfAst",
        "utf-8"
    };

    CommandLineAstApplication commandLineAstApplication = new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION));
    commandLineAstApplication.run();
  }
}
