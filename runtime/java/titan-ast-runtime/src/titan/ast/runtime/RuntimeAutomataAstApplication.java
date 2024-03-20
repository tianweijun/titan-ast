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
  AutomataData automataData;
  TokenAutomata tokenAutomata;
  AstAutomata astAutomata;

  public RuntimeAutomataAstApplication(InputStream automataByteInputStream) {
    setContext(automataByteInputStream);
  }

  public RuntimeAutomataAstApplication(String automataFilePath) {
    setContext(automataFilePath);
  }

  public RuntimeAutomataAstApplication(AutomataData automataData) {
    setContext(automataData);
  }

  public Ast buildAst(String sourceFilePath) {
    List<Token> tokens = tokenAutomata.buildToken(sourceFilePath);
    return astAutomata.buildAst(tokens);
  }

  public Ast buildAst(InputStream sourceByteInputStream) {
    List<Token> tokens = tokenAutomata.buildToken(sourceByteInputStream);
    return astAutomata.buildAst(tokens);
  }

  public AstGrammar[] getGrammars() {
    Grammar[] oriGrammars = automataData.grammars;
    AstGrammar[] grammars = new AstGrammar[oriGrammars.length];
    for (int indexOfGrammar = 0; indexOfGrammar < oriGrammars.length; indexOfGrammar++) {
      Grammar oriGrammar = oriGrammars[indexOfGrammar];
      grammars[indexOfGrammar] = new AstGrammar(oriGrammar.name, oriGrammar.type);
    }
    return grammars;
  }

  public void setContext(InputStream automataByteInputStream) {
    PersistentData persistentData = new PersistentData(automataByteInputStream);
    AutomataData automataData = buildAutomataData(persistentData);
    setContext(automataData);
    if (null != automataByteInputStream) {
      try {
        automataByteInputStream.close();
      } catch (IOException e) {
        throw new AstRuntimeException(e);
      }
    }
  }

  public void setContext(String automataFilePath) {
    PersistentData persistentData = new PersistentData(automataFilePath);
    AutomataData automataData = buildAutomataData(persistentData);
    setContext(automataData);
  }

  public void setContext(AutomataData automataData) {
    this.automataData = automataData;
    tokenAutomata = new TokenAutomataBuilder().build(automataData);
    astAutomata = new AstAutomataBuilder().build(automataData);
  }

  private AutomataData buildAutomataData(PersistentData persistentData) {
    PersistentObject persistentObject = new PersistentObject(persistentData);
    return persistentObject.toAutomataData();
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
    app.setContext(app.automataData);
    return app;
  }
}
