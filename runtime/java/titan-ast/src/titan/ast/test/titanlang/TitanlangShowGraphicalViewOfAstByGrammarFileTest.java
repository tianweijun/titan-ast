package titan.ast.test.titanlang;

import titan.ast.CommandLineAstApplication;

/**
 * showGraphicalViewOfAstByAutomataFileTest.
 *
 * @author tian wei jun
 */
public class TitanlangShowGraphicalViewOfAstByGrammarFileTest {

  public static void main(String[] args) {
    String fileDirectory =
        "D:\\github-pro\\titan\\titan-language-compiler\\src\\resources\\grammar\\";
    String[] testArgs = {
        "-grammarFilePaths",
        fileDirectory + "titanLanguageAsciiLexer.txt",
        fileDirectory + "titanLanguageChineseLexer.txt",
        fileDirectory + "titanLanguageEncodingLexer.txt",
        fileDirectory + "titanLanguageNotTextTokenLexer.txt",
        fileDirectory + "titanLanguageNumberLiteralLexer.txt",
        fileDirectory + "titanLanguageCharsLiteralLexer.txt",
        fileDirectory + "titanLanguagePunctuationLexer.txt",
        fileDirectory + "titanLanguageIdentifierLexer.txt",
        fileDirectory + "titanLanguageParser.txt",
        "-sourceFilePath",
        "D://github-pro/titan/titan-ast/test/titanlang/helloworld.titan",
        "-graphicalViewOfAst"
    };


    new CommandLineAstApplication().run(testArgs);
  }
}
