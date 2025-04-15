package titan.ast.test.ast.titanlang;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarFileAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

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

    new CommandLineAstApplication(testArgs,
        new DefaultGrammarFileAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
