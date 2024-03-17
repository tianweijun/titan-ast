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
import titan.ast.output.AstGuiOutputer;
import titan.ast.persistence.AutomataDataBuilder;
import titan.ast.persistence.PersistentAutomataBuilder;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.runtime.AutomataData;
import titan.ast.runtime.RuntimeAutomataAstApplication;
import titan.ast.target.Ast;
import titan.ast.target.Token;

/**
 * 语法文件的应用.
 *
 * @author tian wei jun
 */
public class GrammarFileAutomataAstApplication {

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

  public void initGrammar(String grammarFilePath) {
    new LanguageGrammarInitializer().initGrammarByFile(grammarFilePath);
  }

  public void initGrammar(List<String> grammarFilePaths) {
    new LanguageGrammarInitializer().initGrammarByFiles(grammarFilePaths);
  }

  public void initGrammar(InputStream grammarFileInputStream) {
    new LanguageGrammarInitializer().initGrammarByInputStream(grammarFileInputStream);
  }

  /** 为了参与语法自动机的构建,keyWords不参与任何tokenDfa的构建，仅仅是LanguageGrammarInitializer.init(). */
  public void addKeyWord2Terminals() {
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
  public void buildProductionRule(LinkedHashMap<String, Grammar> nonterminals) {
    ProductionRuleBuilder productionRuleBuilder = new ProductionRuleBuilder(nonterminals);
    AstContext.get().nonterminalProductionRulesMap = productionRuleBuilder.build();
  }

  /** call after LanguageGrammarInitializer.init() */
  public void buildAstAutomataContext() {
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
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // token
    List<Token> tokens = languageGrammar.tokenAutomata.buildToken(sourceFilePath);
    // ast
    return languageGrammar.astAutomata.buildAst(tokens);
  }

  public Ast buildAst(InputStream sourceInputStream) {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // token
    List<Token> tokens = languageGrammar.tokenAutomata.buildToken(sourceInputStream);
    // ast
    Ast ast = languageGrammar.astAutomata.buildAst(tokens);
    if (null != sourceInputStream) {
      try {
        sourceInputStream.close();
      } catch (Exception e) {
        throw new AstRuntimeException(e);
      }
    }
    return ast;
  }

  public void displayGraphicalViewOfAst(Ast ast) {
    new AstGuiOutputer().output(ast);
  }

  public RuntimeAutomataAstApplication getRuntimeAutomataAstApplication() {
    AutomataDataBuilder automataDataBuilder = new AutomataDataBuilder(AstContext.get());
    AutomataData automataData = automataDataBuilder.build();
    RuntimeAutomataAstApplication runtimeAutomataAstApplication =
        new RuntimeAutomataAstApplication();
    runtimeAutomataAstApplication.setContext(automataData);
    return runtimeAutomataAstApplication;
  }

  public void isAmbiguous() {
    GrammarAmbiguousJudge grammarAmbiguousJudge = new GrammarAmbiguousJudge();
    GrammarAmbiguousJudgeResult ambiguousJudgeResult = grammarAmbiguousJudge.isAmbiguous();
    Logger.info(null, ambiguousJudgeResult.toString());
  }
}
