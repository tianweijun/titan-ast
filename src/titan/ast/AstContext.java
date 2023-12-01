package titan.ast;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.syntax.ProductionRule;

/**
 * 当前应用程序的 上下文.
 *
 * @author tian wei jun
 */
public class AstContext {

  private static ThreadLocal<AstContext> contextThreadLocal = new ThreadLocal<>();

  public ResourceGenerator resourceGenerator = new ResourceGenerator();
  public LanguageGrammar languageGrammar = null;
  public GrammarCharset grammarCharset = null;

  public LinkedHashMap<Grammar, LinkedList<ProductionRule>> nonterminalProductionRulesMap = null;

  /**
   * 初始化并生成一个当前应用程序的 上下文.
   *
   * @return 当前应用程序的 上下文
   */
  public static AstContext init() {
    AstContext astContext = new AstContext();
    astContext.grammarCharset = new GrammarCharset();
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
