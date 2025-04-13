package titan.ast;

import java.util.List;
import titan.ast.fa.syntax.ProductionRuleBuilder;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataDataBuilder;
import titan.ast.fa.token.DfaTokenAutomataFactory;
import titan.ast.fa.token.TerminalFragmentGrammarNfaBuilder;
import titan.ast.fa.token.TerminalGrammarNfaBuilder;
import titan.ast.fa.token.TokenDfaBuilder;
import titan.ast.logger.Logger;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

public abstract class GrammarFileAutomataAstApplication {

  public GrammarFileAutomataAstApplication(List<String> grammarFilePaths) {
    setAstAutomataContext(grammarFilePaths);
  }

  public void setAstAutomataContext(List<String> grammarFilePaths) {
    AstContext.init();
    initGrammar(grammarFilePaths);
    //token
    buildTokenNfa();
    buildTokenDfa();
    buildDfaTokenAutomata();
    new DerivedTerminalGrammarAutomataDataBuilder().build();
    //syntax
    buildSyntaxNfa();
    buildSyntaxDfa();
  }

  private void buildDfaTokenAutomata() {
    DfaTokenAutomataFactory.create();
  }

  protected void buildTokenNfa() {
    new TerminalFragmentGrammarNfaBuilder().buildNfa();
    new TerminalGrammarNfaBuilder().buildNfa();
  }

  private void buildTokenDfa() {
    new TokenDfaBuilder().buildDfa();
  }

  protected void buildSyntaxNfa() {
    new ProductionRuleBuilder().build();
  }

  private void buildSyntaxDfa() {
  }


  protected abstract void initGrammar(List<String> grammarFilePaths);

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
