package titan.ast.impl.ast;

import java.util.List;
import titan.ast.GrammarFileAutomataAstApplication;
import titan.ast.impl.ast.contextast.ContextAst;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstWayGrammarFileAutomataAstApplication extends GrammarFileAutomataAstApplication {

  protected void doBeforeNfa(List<String> grammarFilePaths) {
    for (String grammarFilePath : grammarFilePaths) {
      ContextAst contextAst = AstBuilder.build(grammarFilePath);
    }
  }

  @Override
  protected void buildSyntaxDfa() {

  }

  @Override
  protected void buildSyntaxNfa() {

  }

  @Override
  protected void buildTokenDfa() {

  }

  @Override
  protected void buildTokenNfa() {

  }
}
