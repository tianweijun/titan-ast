package titan.ast.impl.ast.automata;

import java.util.List;
import titan.ast.AstContext;
import titan.ast.GrammarAutomataAstApplication;
import titan.ast.grammar.LanguageGrammar;

public class StaticAstGrammarAutomataAstApplication extends GrammarAutomataAstApplication {

  public StaticAstGrammarAutomataAstApplication(String grammarFilePath) {
    super(grammarFilePath);
  }

  @Override
  protected void initGrammar(List<String> grammarFilePaths) {
    // grammarFilePaths里面的内容为空或null
    // 忽略grammarFilePaths
    LanguageGrammar languageGrammar = AstContext.get().languageGrammar;
    new AstGrammarInitializer(languageGrammar).init();
  }
}
