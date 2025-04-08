package titan.ast.autocode;

/**
 * .
 *
 * @author tian wei jun
 */
public class CodeGenerator {

  public static String getFirstUpperCaseGrammarName(String grammarName) {
    return grammarName.substring(0, 1).toUpperCase() + grammarName.substring(1);
  }

  public static String getFirstLowerCaseGrammarName(String grammarName) {
    return grammarName.substring(0, 1).toLowerCase() + grammarName.substring(1);
  }
}
