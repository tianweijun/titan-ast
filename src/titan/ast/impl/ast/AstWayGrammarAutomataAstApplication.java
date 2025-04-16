package titan.ast.impl.ast;

import java.util.List;
import titan.ast.GrammarAutomataAstApplication;
import titan.ast.impl.ast.contextast.ContextAst;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstWayGrammarAutomataAstApplication extends GrammarAutomataAstApplication {

  public AstWayGrammarAutomataAstApplication(String grammarFilePath) {
    super(grammarFilePath);
  }

  public AstWayGrammarAutomataAstApplication(List<String> grammarFilePaths) {
    super(grammarFilePaths);
  }

  protected void initGrammar(List<String> grammarFilePaths) {
    for (String grammarFilePath : grammarFilePaths) {
      ContextAst contextAst = AstBuilder.build(grammarFilePath);
      new GrammarInitializer(contextAst).init();
    }
  }
}
