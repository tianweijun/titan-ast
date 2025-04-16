package titan.ast;

import java.util.List;
import titan.ast.impl.ast.AstWayGrammarAutomataAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class DefaultGrammarAutomataAstApplicationBuilder implements GrammarAutomataAstApplicationBuilder {

  private final GrammarFileAutomataAstApplicationEnum type;
  private GrammarAutomataAstApplication automataAstApplication = null;

  public DefaultGrammarAutomataAstApplicationBuilder() {
    type =
        GrammarFileAutomataAstApplicationEnum.AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION;
  }

  public DefaultGrammarAutomataAstApplicationBuilder(GrammarFileAutomataAstApplicationEnum type) {
    this.type = type;
  }

  @Override
  public void build(List<String> grammarFilePaths) {
    if (grammarFilePaths == null || grammarFilePaths.isEmpty()) {
      return;
    }
    switch (type) {
      case AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION -> {
        automataAstApplication = new AstWayGrammarAutomataAstApplication(grammarFilePaths);
      }
    }
  }

  @Override
  public GrammarAutomataAstApplication get() {
    return automataAstApplication;
  }

  public enum GrammarFileAutomataAstApplicationEnum {
    AST_WAY_GRAMMAR_FILE_AUTOMATA_AST_APPLICATION
  }
}
