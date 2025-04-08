package titan.ast;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import titan.ast.fa.syntax.ProductionRule;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;

/**
 * 当前应用程序的 上下文.
 *
 * @author tian wei jun
 */
public class AstContext {

  private static final ThreadLocal<AstContext> contextThreadLocal = new ThreadLocal<>();

  public IdGenerator idGenerator = new IdGenerator();
  public LanguageGrammar languageGrammar = null;

  public LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap = null;

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
