package titan.ast.runtime;

import java.io.InputStream;
import java.nio.charset.Charset;

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

  public void setCharset(Charset charset) {
    richResultConverter.setCharset(charset);
  }

  public void setCharset(String charsetName) {
    richResultConverter.setCharset(charsetName);
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
    // set newline ,setCharset
    app.setNewline(this.richResultConverter.getNewline());
    app.setCharset(this.richResultConverter.getCharset());
    return app;
  }
}
