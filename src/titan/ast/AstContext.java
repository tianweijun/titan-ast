package titan.ast;

import java.util.LinkedHashMap;
import java.util.List;
import titan.ast.fa.syntax.AstAutomata;
import titan.ast.fa.syntax.ProductionRule;
import titan.ast.fa.syntax.SyntaxDfa;
import titan.ast.fa.token.TokenAutomata;
import titan.ast.fa.token.TokenDfa;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.NonterminalGrammar;

/**
 * 当前应用程序的 上下文.
 *
 * @author tian wei jun
 */
public class AstContext {

  private static final ThreadLocal<AstContext> contextThreadLocal = new ThreadLocal<>();

  public IdGenerator idGenerator = new IdGenerator();
  public LanguageGrammar languageGrammar = null;
  public TokenDfa tokenDfa;
  public TokenAutomata tokenAutomata;
  public LinkedHashMap<NonterminalGrammar, List<ProductionRule>> nonterminalProductionRulesMap;
  public SyntaxDfa astDfa;
  public AstAutomata astAutomata;

  /**
   * 初始化并生成一个当前应用程序的 上下文.
   *
   * @return 当前应用程序的 上下文
   */
  public static AstContext init() {
    AstContext astContext = new AstContext();
    astContext.languageGrammar = new LanguageGrammar();
    set(astContext);
    return astContext;
  }

  public static void set(AstContext ctx) {
    contextThreadLocal.set(ctx);
  }

  public static void clear() {
    contextThreadLocal.remove();
  }

  public static AstContext get() {
    return contextThreadLocal.get();
  }
}
