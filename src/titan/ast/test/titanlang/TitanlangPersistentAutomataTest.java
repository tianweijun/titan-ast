package titan.ast.test.titanlang;

import titan.ast.CommandLineAstApplication;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class TitanlangPersistentAutomataTest {

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
      "-persistentAutomataFilePath",
      fileDirectory + "titanLanguageGrammar.automata"
    };

    new CommandLineAstApplication().run(testArgs);
  }
}
