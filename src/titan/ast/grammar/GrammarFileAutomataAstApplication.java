package titan.ast.grammar;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.grammar.ambiguity.GrammarAmbiguousJudge;
import titan.ast.grammar.ambiguity.GrammarAmbiguousJudgeResult;
import titan.ast.grammar.io.LanguageGrammarInitializer;
import titan.ast.grammar.regexp.LanguageGrammarRegExpBuilder;
import titan.ast.grammar.syntax.DfaAstAutomataBuilder;
import titan.ast.grammar.syntax.ProductionRuleBuilder;
import titan.ast.grammar.token.KeyWordAutomataBuilder;
import titan.ast.grammar.token.TokenAutomataBuilder;
import titan.ast.logger.Logger;
import titan.ast.persistence.AutomataDataBuilder;
import titan.ast.persistence.PersistentAutomataBuilder;
import titan.ast.runtime.Ast;
import titan.ast.runtime.AutomataData;
import titan.ast.runtime.RuntimeAutomataAstApplication;

/**
 * 语法文件的应用.
 *
 * @author tian wei jun
 */
public class GrammarFileAutomataAstApplication {
  RuntimeAutomataAstApplication runtimeAutomataAstApplication = null;

  public void setAstAutomataContext(String grammarFilePath) {
    AstContext.init();

    initGrammar(grammarFilePath);
    buildAstAutomataContext();
  }

  public void setAstAutomataContext(List<String> grammarFilePaths) {
    AstContext.init();

    initGrammar(grammarFilePaths);
    buildAstAutomataContext();
  }

  public void setAstAutomataContext(InputStream grammarFileInputStream) {
    AstContext.init();

    initGrammar(grammarFileInputStream);
    buildAstAutomataContext();
  }

  private void initGrammar(String grammarFilePath) {
    new LanguageGrammarInitializer().initGrammarByFile(grammarFilePath);
  }

  private void initGrammar(List<String> grammarFilePaths) {
    new LanguageGrammarInitializer().initGrammarByFiles(grammarFilePaths);
  }

  private void initGrammar(InputStream grammarFileInputStream) {
    new LanguageGrammarInitializer().initGrammarByInputStream(grammarFileInputStream);
  }

  /** 为了参与语法自动机的构建,keyWords不参与任何tokenDfa的构建，仅仅是LanguageGrammarInitializer.init(). */
  private void addKeyWord2Terminals() {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    // 将keyword 添加到 terminals
    for (Grammar keyWord : languageGrammar.keyWords) {
      languageGrammar.addGrammar(keyWord);
    }
  }

  /**
   * call after regExpBuilder.buildRegExpOfNonterminal().
   *
   * @param nonterminals nonterminals
   */
  private void buildProductionRule(LinkedHashMap<String, Grammar> nonterminals) {
    ProductionRuleBuilder productionRuleBuilder = new ProductionRuleBuilder(nonterminals);
    AstContext.get().nonterminalProductionRulesMap = productionRuleBuilder.build();
  }

  /** call after LanguageGrammarInitializer.init() */
  private void buildAstAutomataContext() {
    // LanguageGrammarInitializer.init()
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;

    // RegExp
    LanguageGrammarRegExpBuilder regExpBuilder = new LanguageGrammarRegExpBuilder(languageGrammar);
    // token自动机
    regExpBuilder.buildRegExpOfFragment();
    regExpBuilder.buildRegExpOfTerminal();
    new KeyWordAutomataBuilder(AstContext.get().languageGrammar).build();
    TokenAutomataBuilder tokenAutomataBuilder = new TokenAutomataBuilder();
    tokenAutomataBuilder.build();
    // 语法自动机
    addKeyWord2Terminals();
    regExpBuilder.buildRegExpOfNonterminal();
    buildProductionRule(languageGrammar.nonterminals);
    DfaAstAutomataBuilder dfaAstAutomataBuilder = new DfaAstAutomataBuilder();
    dfaAstAutomataBuilder.build();
  }

  public void setProductionRuleContext(List<String> grammarFilePaths) {
    AstContext astContext = AstContext.init();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // init grammar
    initGrammar(grammarFilePaths);
    // init RegExp
    LanguageGrammarRegExpBuilder regExpBuilder =
        new LanguageGrammarRegExpBuilder(astContext.languageGrammar);
    regExpBuilder.buildRegExpOfFragment();
    regExpBuilder.buildRegExpOfTerminal();

    addKeyWord2Terminals();
    regExpBuilder.buildRegExpOfNonterminal();
    // build ProductionRule
    buildProductionRule(languageGrammar.nonterminals);
  }

  public void clear() {
    AstContext.clear();
  }

  public void buildPersistentAutomata(String persistentAutomataFilePath) {
    PersistentAutomataBuilder persistentAutomataBuilder = new PersistentAutomataBuilder();
    persistentAutomataBuilder.build(persistentAutomataFilePath);
  }

  public Ast buildAst(String sourceFilePath) {
    return runtimeAutomataAstApplication.buildAst(sourceFilePath);
  }

  public Ast buildAst(InputStream sourceInputStream) {
    return runtimeAutomataAstApplication.buildAst(sourceInputStream);
  }

  public void displayGraphicalViewOfAst(Ast ast) {
    runtimeAutomataAstApplication.displayGraphicalViewOfAst(ast);
  }

  public void setRuntimeAutomataAstApplication() {
    this.runtimeAutomataAstApplication = buildRuntimeAutomataAstApplication();
  }

  public RuntimeAutomataAstApplication buildRuntimeAutomataAstApplication() {
    AutomataDataBuilder automataDataBuilder = new AutomataDataBuilder(AstContext.get());
    AutomataData automataData = automataDataBuilder.build();
    this.runtimeAutomataAstApplication = new RuntimeAutomataAstApplication(automataData);
    return runtimeAutomataAstApplication;
  }

  public void isAmbiguous() {
    GrammarAmbiguousJudge grammarAmbiguousJudge = new GrammarAmbiguousJudge();
    GrammarAmbiguousJudgeResult ambiguousJudgeResult = grammarAmbiguousJudge.isAmbiguous();
    Logger.info(null, ambiguousJudgeResult.toString());
  }
}
