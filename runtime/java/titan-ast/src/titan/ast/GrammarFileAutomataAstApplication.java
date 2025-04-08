package titan.ast;

import java.util.List;
import titan.ast.logger.Logger;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

public abstract class GrammarFileAutomataAstApplication {

  public void setAstAutomataContext(List<String> grammarFilePaths) {
    doBeforeNfa(grammarFilePaths);
    buildTokenNfa();
    buildTokenDfa();
    buildSyntaxNfa();
    buildSyntaxDfa();
  }

  protected abstract void buildSyntaxDfa();

  protected abstract void buildSyntaxNfa();

  protected abstract void buildTokenDfa();

  protected abstract void buildTokenNfa();
  

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
