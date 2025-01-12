package titan.ast.runtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import titan.ast.runtime.AstGeneratorResult.AstResult;
import titan.ast.runtime.AstGeneratorResult.TokensResult;

/**
 * .
 *
 * @author tian wei jun
 */
public class RuntimeAutomataAstApplication implements Cloneable {
  AutomataData automataData;
  TokenAutomata tokenAutomata;
  AstAutomata astAutomata;

  public void setContext(InputStream automataByteInputStream) throws AutomataDataIoException {
    PersistentAutomataData persistentAutomataData =
        new PersistentAutomataData(automataByteInputStream);
    AutomataData automataData = buildAutomataData(persistentAutomataData);
    setContext(automataData);
  }

  public void setContext(String automataFilePath) throws AutomataDataIoException {
    try (InputStream automataByteInputStream = new FileInputStream(automataFilePath)) {
      PersistentAutomataData persistentAutomataData =
          new PersistentAutomataData(automataByteInputStream);
      AutomataData automataData = buildAutomataData(persistentAutomataData);
      setContext(automataData);
    } catch (IOException e) {
      throw new AutomataDataIoException(e);
    }
  }

  public void setContext(AutomataData automataData) {
    this.automataData = automataData;
    tokenAutomata = new TokenAutomataBuilder().build(automataData);
    astAutomata = new AstAutomataBuilder().build(automataData);
  }

  private AutomataData buildAutomataData(PersistentAutomataData persistentAutomataData)
      throws AutomataDataIoException {
    PersistentAutomataObject persistentAutomataObject =
        new PersistentAutomataObject(persistentAutomataData);
    persistentAutomataObject.init();
    return persistentAutomataObject.toAutomataData();
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

  public AstGeneratorResult buildAst(String sourceFilePath) {
    TokensResult tokensResult = tokenAutomata.buildToken(sourceFilePath);
    AstResult astResult;
    if (tokensResult.isOk()) {
      astResult = astAutomata.buildAst(tokensResult.getOkData());
    } else {
      astResult = AstResult.generateTokensErrorResult();
    }
    return new AstGeneratorResult(tokensResult, astResult);
  }

  public AstGeneratorResult buildAst(InputStream sourceByteInputStream) {
    TokensResult tokensResult = tokenAutomata.buildToken(sourceByteInputStream);
    AstResult astResult;
    if (tokensResult.isOk()) {
      astResult = astAutomata.buildAst(tokensResult.getOkData());
    } else {
      astResult = AstResult.generateTokensErrorResult();
    }
    return new AstGeneratorResult(tokensResult, astResult);
  }

  public void displayGraphicalViewOfAst(Ast ast) {
    new titan.ast.runtime.AstGuiOutputer(ast).output();
  }

  public void displayGraphicalViewOfAst(Ast ast, String charsetName) {
    new titan.ast.runtime.AstGuiOutputer(ast, charsetName).output();
  }

  @Override
  public RuntimeAutomataAstApplication clone() {
    RuntimeAutomataAstApplication cloner = null;
    try {
      cloner = (RuntimeAutomataAstApplication) super.clone();
    } catch (CloneNotSupportedException e) {
      cloner = new RuntimeAutomataAstApplication();
    }
    cloner.setContext(this.automataData);
    return cloner;
  }
}
