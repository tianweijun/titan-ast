package titan.ast.runtime;

import java.io.InputStream;
import java.util.List;

/**
 * 生成语法树的应用.
 *
 * @author tian wei jun
 */
public class PersistentAutomataAstApplication implements Cloneable {
  PersistentObject persistentObject;
  DfaTokenAutomata dfaTokenAutomata;
  BacktrackingBottomUpAstAutomata astAutomata;

  public PersistentAutomataAstApplication(String persistentDataFilePath) {
    PersistentData persistentData = new PersistentData(persistentDataFilePath);
    this.buildContext(persistentData);
  }

  public PersistentAutomataAstApplication(InputStream byteInputStream) {
    PersistentData persistentData = new PersistentData(byteInputStream);
    this.buildContext(persistentData);
  }

  public void buildContext(PersistentData persistentData) {
    persistentObject = new PersistentObject(persistentData);
    dfaTokenAutomata = new DfaTokenAutomata(persistentObject.keyWordAutomata,
        persistentObject.tokenDfa);
    astAutomata =
        new BacktrackingBottomUpAstAutomata(persistentObject.astDfa, persistentObject.startGrammar);
  }

  public List<Ast> buildAsts(String sourceCodeFilePath) {
    List<Token> tokens = dfaTokenAutomata.buildToken(sourceCodeFilePath);
    return astAutomata.buildAsts(tokens);
  }

  public List<Ast> buildAsts(InputStream sourceByteInputStream) {
    List<Token> tokens = dfaTokenAutomata.buildToken(sourceByteInputStream);
    return astAutomata.buildAsts(tokens);
  }

  public Ast buildAst(String sourceCodeFilePath) {
    List<Token> tokens = dfaTokenAutomata.buildToken(sourceCodeFilePath);
    return astAutomata.buildAst(tokens);
  }

  public Ast buildAst(InputStream byteInputStream) {
    List<Token> tokens = dfaTokenAutomata.buildToken(byteInputStream);
    return astAutomata.buildAst(tokens);
  }

  @Override
  protected PersistentAutomataAstApplication clone() {
    PersistentAutomataAstApplication app = null;
    try {
      app = (PersistentAutomataAstApplication) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    app.persistentObject = this.persistentObject;
    dfaTokenAutomata = new DfaTokenAutomata(persistentObject.keyWordAutomata,
        persistentObject.tokenDfa);
    astAutomata =
        new BacktrackingBottomUpAstAutomata(persistentObject.astDfa, persistentObject.startGrammar);
    return app;
  }
}
