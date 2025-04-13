package titan.ast;

import java.util.List;
import titan.ast.impl.ast.AstWayGrammarFileAutomataAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class DefaultGrammarFileAutomataAstApplicationBuilder implements GrammarFileAutomataAstApplicationBuilder {

  private final GrammarFileAutomataAstApplicationEnum type;
  private GrammarFileAutomataAstApplication automataAstApplication = null;

  public DefaultGrammarFileAutomataAstApplicationBuilder() {
    type =
        GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION;
  }

  public DefaultGrammarFileAutomataAstApplicationBuilder(GrammarFileAutomataAstApplicationEnum type) {
    this.type = type;
  }

  @Override
  public void build(List<String> grammarFilePaths) {
    if (grammarFilePaths == null || grammarFilePaths.isEmpty()) {
      return;
    }
    switch (type) {
      case AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION -> {
        automataAstApplication = new AstWayGrammarFileAutomataAstApplication(grammarFilePaths);
      }
    }
  }

  @Override
  public GrammarFileAutomataAstApplication get() {
    return automataAstApplication;
  }

  public enum GrammarFileAutomataAstApplicationEnum {
    AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION
  }
}
