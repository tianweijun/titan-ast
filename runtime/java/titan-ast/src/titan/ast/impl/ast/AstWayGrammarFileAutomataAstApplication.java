package titan.ast.impl.ast;

import java.util.List;
import titan.ast.GrammarFileAutomataAstApplication;
import titan.ast.impl.ast.contextast.ContextAst;
import titan.ast.impl.ast.regexp.RegExpBuilder;

/**
 * .
 *
 * @author tian wei jun
 */
public class AstWayGrammarFileAutomataAstApplication extends GrammarFileAutomataAstApplication {

  public AstWayGrammarFileAutomataAstApplication() {
    super();
  }

  protected void doBeforeNfa(List<String> grammarFilePaths) {
    for (String grammarFilePath : grammarFilePaths) {
      ContextAst contextAst = AstBuilder.build(grammarFilePath);
      new RegExpBuilder(contextAst).build();
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
