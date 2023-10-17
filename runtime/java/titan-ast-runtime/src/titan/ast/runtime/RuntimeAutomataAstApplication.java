package titan.ast.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * RuntimeAutomataAstApplication.
 *
 * @author tian wei jun
 */
public class RuntimeAutomataAstApplication implements Cloneable {
  PersistentAutomataAstApplication persistentAutomataAstApplication;

  public Grammar[] getGrammars() {
    Grammar[] oriGrammars =
        persistentAutomataAstApplication.persistentObject.persistentData.grammars;
    Grammar[] grammars = new Grammar[oriGrammars.length];
    System.arraycopy(oriGrammars, 0, grammars, 0, oriGrammars.length);
    return grammars;
  }

  public List<Ast> buildAsts(String sourceCodeFilePath) {
    return persistentAutomataAstApplication.buildAsts(sourceCodeFilePath);
  }

  public List<Ast> buildAsts(InputStream sourceByteInputStream) {
    List<Ast> asts = persistentAutomataAstApplication.buildAsts(sourceByteInputStream);
    if (null != sourceByteInputStream) {
      try {
        sourceByteInputStream.close();
      } catch (Exception e) {
        throw new AstRuntimeException(e);
      }
    }
    return asts;
  }

  public Ast buildAst(String sourceFilePath) {
    return persistentAutomataAstApplication.buildAst(sourceFilePath);
  }

  public Ast buildAst(InputStream sourceByteInputStream) {
    Ast ast = persistentAutomataAstApplication.buildAst(sourceByteInputStream);
    if (null != sourceByteInputStream) {
      try {
        sourceByteInputStream.close();
      } catch (Exception e) {
        throw new AstRuntimeException(e);
      }
    }
    return ast;
  }

  public void setContext(InputStream automataByteInputStream) {
    persistentAutomataAstApplication =
        new PersistentAutomataAstApplication(automataByteInputStream);
    if (null != automataByteInputStream) {
      try {
        automataByteInputStream.close();
      } catch (IOException e) {
        throw new AstRuntimeException(e);
      }
    }
  }

  public void setContext(String automataFilePath) {
    persistentAutomataAstApplication = new PersistentAutomataAstApplication(automataFilePath);
  }

  public void displayGraphicalViewOfAst(Ast ast) {
    new titan.ast.runtime.AstGuiOutputer().output(ast);
  }

  public RuntimeAutomataAstApplication clone() {
    RuntimeAutomataAstApplication app = null;
    try {
      app = (RuntimeAutomataAstApplication) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    app.persistentAutomataAstApplication = this.persistentAutomataAstApplication.clone();
    return app;
  }
}
