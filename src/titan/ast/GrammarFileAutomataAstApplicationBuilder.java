package titan.ast;

import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public interface GrammarFileAutomataAstApplicationBuilder {

  void build(List<String> grammarFilePaths);

  GrammarFileAutomataAstApplication get();


}
