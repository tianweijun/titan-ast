package titan.ast;

/**
 * 资源生成器.
 *
 * @author tian wei jun
 */
public class IdGenerator {
  private int tokenNfaStateId = 0;
  private int tokenDfaStateId = 0;
  private int productionRuleId = 0;
  private int syntaxNfaStateId = 0;
  private int syntaxDfaStateId = 0;

  public int generateTokenNfaStateId() {
    return ++tokenNfaStateId;
  }

  public int generateTokenDfaStateId() {
    return ++tokenDfaStateId;
  }

  public int generateProductionRuleId() {
    return ++productionRuleId;
  }

  public int generateSyntaxNfaStateId() {
    return ++syntaxNfaStateId;
  }

  public int generateSyntaxDfaStateId() {
    return ++syntaxDfaStateId;
  }
}
