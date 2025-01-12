package titan.ast.runtime;

import java.io.InputStream;

/**
 * RuntimeAutomataAstApplication.
 *
 * @author tian wei jun
 */
public class RuntimeAutomataRichAstApplication extends RuntimeAutomataAstApplication
    implements Cloneable {
  AstGeneratorResult2RichResultConverter richResultConverter =
      new AstGeneratorResult2RichResultConverter();

  public void setNewline(byte newline) {
    richResultConverter.setNewline(newline);
  }

  public RichAstGeneratorResult buildRichAst(String sourceFilePath) {
    return richResultConverter.convert(buildAst(sourceFilePath));
  }

  public RichAstGeneratorResult buildRichAst(InputStream sourceByteInputStream) {
    return richResultConverter.convert(buildAst(sourceByteInputStream));
  }

  public RuntimeAutomataRichAstApplication clone() {
    RuntimeAutomataRichAstApplication app = null;
    app = (RuntimeAutomataRichAstApplication) super.clone();
    app.setContext(this.automataData);
    // set newline
    app.setNewline(this.richResultConverter.getNewline());
    return app;
  }
}
