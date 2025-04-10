package titan.ast.impl.ast;

import java.io.InputStream;
import titan.ast.AstRuntimeException;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * .
 *
 * @author tian wei jun
 */
public class RuntimeAutomataRichAstApplicationFactory {

  private static final String GRAMMAR_AUTOMATA_RESOUCES_PATH =
      "titanAstGrammar.automata";
  private static RuntimeAutomataRichAstApplication astApplication = null;

  public static RuntimeAutomataRichAstApplication getAstApplication() {
    if (astApplication != null) {
      return astApplication;
    }
    synchronized (RuntimeAutomataRichAstApplicationFactory.class) {
      if (null == astApplication) {
        InputStream automataInputStream =
            RuntimeAutomataRichAstApplicationFactory.class.getClassLoader()
                .getResourceAsStream(GRAMMAR_AUTOMATA_RESOUCES_PATH);
        astApplication = new RuntimeAutomataRichAstApplication();
        try {
          astApplication.setContext(automataInputStream);
        } catch (AutomataDataIoException e) {
          throw new AstRuntimeException(e);
        }
      }
    }
    return astApplication;
  }
}
