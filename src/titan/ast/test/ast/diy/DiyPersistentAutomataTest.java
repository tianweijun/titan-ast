package titan.ast.test.ast.diy;

import titan.ast.CommandLineAstApplication;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder;
import titan.ast.DefaultGrammarAutomataAstApplicationBuilder.GrammarFileAutomataAstApplicationEnum;

/**
 * 生成持久化自动机代码测试.
 *
 * @author tian wei jun
 */
public class DiyPersistentAutomataTest {

  public static void main(String[] args) {
    String fileDirectory = "D:\\github-pro\\titan\\titan-ast\\test\\titanlang\\";
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
        "D://github-pro/titan/titan-ast/test/diy/diy.grammar",
        "-persistentAutomataFilePath",
        "D://github-pro/titan/titan-ast/test/diy/automata.data"
    };
    new CommandLineAstApplication(testArgs,
        new DefaultGrammarAutomataAstApplicationBuilder(
            GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION)).run();
  }
}
