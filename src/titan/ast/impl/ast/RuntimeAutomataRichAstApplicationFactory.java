package titan.ast.impl.ast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

  private static final String GRAMMAR_AUTOMATA_RESOUCES_PATH = "titanAstGrammar.automata";
  private static RuntimeAutomataRichAstApplication astApplication = null;

  public static RuntimeAutomataRichAstApplication getAstApplication() {
    if (astApplication != null) {
      return astApplication;
    }
    synchronized (RuntimeAutomataRichAstApplicationFactory.class) {
      if (null == astApplication) {
        astApplication = new RuntimeAutomataRichAstApplication();

        try (InputStream automataInputStream =
            RuntimeAutomataRichAstApplicationFactory.class
                .getClassLoader()
                .getResourceAsStream(GRAMMAR_AUTOMATA_RESOUCES_PATH)) {
          astApplication.setContext(automataInputStream);
        } catch (AutomataDataIoException | IOException e) {
          throw new AstRuntimeException(e);
        }
      }
    }
    return astApplication;
  }
}
