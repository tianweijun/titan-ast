package titan.ast;

import java.util.List;

/**
 * .
 *
 * @author tian wei jun
 */
public interface GrammarAutomataAstApplicationBuilder {

  void build(List<String> grammarFilePaths);

  GrammarAutomataAstApplication get();


}
