package titan.ast;

import java.util.List;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataDataBuilder;
import titan.ast.fa.token.FragmentNfaBuilder;
import titan.ast.grammar.DerivedTerminalGrammarAutomataDetail.RootTerminalGrammarMapDetail;
import titan.ast.grammar.LanguageGrammar;
import titan.ast.grammar.TerminalGrammar;
import titan.ast.logger.Logger;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

public abstract class GrammarFileAutomataAstApplication {

  public GrammarFileAutomataAstApplication(List<String> grammarFilePaths) {
    setAstAutomataContext(grammarFilePaths);
  }

  public void setAstAutomataContext(List<String> grammarFilePaths) {
    AstContext.init();
    buildNfa(grammarFilePaths);
    buildTokenDfa();
    buildSyntaxDfa();
  }

  protected void buildNfa(List<String> grammarFilePaths) {
    doBeforeNfa(grammarFilePaths);
    buildTokenNfa();
    buildSyntaxNfa();
    new DerivedTerminalGrammarAutomataDataBuilder().build();
    addDerivedTerminalGrammar2Terminals();
  }

  private void buildTokenNfa() {
    // fragment
    FragmentNfaBuilder.buildNfa();
  }

  private void buildTokenDfa() {
  }

  private void buildSyntaxNfa() {

  }

  private void buildSyntaxDfa() {
  }

  /**
   * 为了参与语法自动机的构建,derivedTerminalGrammars不参与任何tokenDfa的构建，参与syntaxDfa的构建.
   */
  private void addDerivedTerminalGrammar2Terminals() {
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    // 将keyword 添加到 terminals
    for (RootTerminalGrammarMapDetail rootTerminalGrammarMapDetail :
        languageGrammar.derivedTerminalGrammarAutomataDetail.rootTerminalGrammarMaps.values()) {
      for (TerminalGrammar derivedTerminalGrammar :
          rootTerminalGrammarMapDetail.derivedTerminalGrammars.keySet()) {
        languageGrammar.addTerminalGrammar(derivedTerminalGrammar);
      }
    }
  }

  protected abstract void doBeforeNfa(List<String> grammarFilePaths);

  public void buildPersistentAutomata(String persistentAutomataFilePath) {

  }

  public RuntimeAutomataRichAstApplication getRuntimeAutomataRichAstApplication() {
    return null;
  }

  public void isAmbiguous() {
    Logger.info(
        "the feature(isAmbiguous) is not available. be careful of precedence, associativity and uniqueness properties"
            + ".");
  }
}
