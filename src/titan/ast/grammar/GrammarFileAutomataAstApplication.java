package titan.ast.grammar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.ambiguity.GrammarAmbiguousJudge;
import titan.ast.grammar.ambiguity.GrammarAmbiguousJudgeResult;
import titan.ast.grammar.io.LanguageGrammarInitializer;
import titan.ast.grammar.regexp.LanguageGrammarRegExpBuilder;
import titan.ast.grammar.syntax.DfaAstAutomataBuilder;
import titan.ast.grammar.syntax.ProductionRuleBuilder;
import titan.ast.grammar.token.KeyWordAutomataBuilder;
import titan.ast.grammar.token.TokenAutomataBuilder;
import titan.ast.logger.Logger;
import titan.ast.persistence.PersistentAutomataBuilder;
import titan.ast.runtime.Ast;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RuntimeAutomataAstApplication;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * 语法文件的应用.
 *
 * @author tian wei jun
 */
public class GrammarFileAutomataAstApplication {
  RuntimeAutomataRichAstApplication runtimeAutomataRichAstApplication = null;

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
    for (Grammar keyWord : languageGrammar.keyWordAutomataDetail.keyWords.keySet()) {
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
    new KeyWordAutomataBuilder(languageGrammar).build();
    TokenAutomataBuilder tokenAutomataBuilder = new TokenAutomataBuilder();
    tokenAutomataBuilder.build();
    // 语法自动机
    addKeyWord2Terminals();
    regExpBuilder.buildRegExpOfNonterminal();
    buildProductionRule(languageGrammar.nonterminals);
    languageGrammar.clearTokens();
    DfaAstAutomataBuilder dfaAstAutomataBuilder = new DfaAstAutomataBuilder();
    dfaAstAutomataBuilder.build();
  }

  public void setProductionRuleContext(List<String> grammarFilePaths) {
    AstContext astContext = AstContext.init();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // init grammar
    initGrammar(grammarFilePaths);
    // init RegExp
    LanguageGrammarRegExpBuilder regExpBuilder = new LanguageGrammarRegExpBuilder(languageGrammar);
    regExpBuilder.buildRegExpOfFragment();
    regExpBuilder.buildRegExpOfTerminal();

    addKeyWord2Terminals();
    regExpBuilder.buildRegExpOfNonterminal();
    // build ProductionRule
    buildProductionRule(languageGrammar.nonterminals);
    languageGrammar.clearTokens();
  }

  public void clear() {
    AstContext.clear();
  }

  public void buildPersistentAutomata(String persistentAutomataFilePath) {
    PersistentAutomataBuilder persistentAutomataBuilder = new PersistentAutomataBuilder();
    persistentAutomataBuilder.build(persistentAutomataFilePath);
  }

  public RichAstGeneratorResult buildAst(String sourceFilePath) {
    if (null == runtimeAutomataRichAstApplication) {
      setRuntimeAutomataRichAstApplication();
    }
    return runtimeAutomataRichAstApplication.buildRichAst(sourceFilePath);
  }

  public RichAstGeneratorResult buildAst(InputStream sourceInputStream) {
    if (null == runtimeAutomataRichAstApplication) {
      setRuntimeAutomataRichAstApplication();
    }
    return runtimeAutomataRichAstApplication.buildRichAst(sourceInputStream);
  }

  public void displayGraphicalViewOfAst(Ast ast) {
    runtimeAutomataRichAstApplication.displayGraphicalViewOfAst(ast);
  }

  private void setRuntimeAutomataRichAstApplication() {
    this.runtimeAutomataRichAstApplication = buildRuntimeAutomataRichAstApplication();
  }

  public void setRuntimeAstApplicationCharset(String charsetName) {
    if (null == runtimeAutomataRichAstApplication) {
      setRuntimeAutomataRichAstApplication();
    }
    runtimeAutomataRichAstApplication.setCharset(charsetName);
  }

  public RuntimeAutomataAstApplication buildRuntimeAutomataAstApplication() {
    PersistentAutomataBuilder persistentAutomataBuilder = new PersistentAutomataBuilder();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    persistentAutomataBuilder.savePersistentDataToOutputStream(outputStream);

    RuntimeAutomataAstApplication runtimeAutomataAstApplication =
        new RuntimeAutomataAstApplication();
    try {
      runtimeAutomataAstApplication.setContext(
          new ByteArrayInputStream(outputStream.toByteArray()));
      outputStream.close();
    } catch (AutomataDataIoException | IOException e) {
      throw new AstRuntimeException(e);
    }
    return runtimeAutomataAstApplication;
  }

  public RuntimeAutomataRichAstApplication buildRuntimeAutomataRichAstApplication() {
    PersistentAutomataBuilder persistentAutomataBuilder = new PersistentAutomataBuilder();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    persistentAutomataBuilder.savePersistentDataToOutputStream(outputStream);

    RuntimeAutomataRichAstApplication runtimeAutomataRichAstApplication =
        new RuntimeAutomataRichAstApplication();
    try {
      runtimeAutomataRichAstApplication.setContext(
          new ByteArrayInputStream(outputStream.toByteArray()));
      outputStream.close();
    } catch (AutomataDataIoException | IOException e) {
      throw new AstRuntimeException(e);
    }
    return runtimeAutomataRichAstApplication;
  }

  public void isAmbiguous() {
    GrammarAmbiguousJudge grammarAmbiguousJudge = new GrammarAmbiguousJudge();
    GrammarAmbiguousJudgeResult ambiguousJudgeResult = grammarAmbiguousJudge.isAmbiguous();
    Logger.info(ambiguousJudgeResult.toString());
  }
}
