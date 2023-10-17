package titan.ast.grammar;

import java.io.InputStream;
import java.util.List;
import titan.ast.AstContext;
import titan.ast.output.AstGuiOutputer;
import titan.ast.persistence.PersistentAutomataBuilder;
import titan.ast.runtime.AstRuntimeException;
import titan.ast.target.Ast;
import titan.ast.target.Token;

/**
 * 语法文件的应用.
 *
 * @author tian wei jun
 */
public class GrammarFileAutomataAstApplication {

  public void setContext(String grammarFilePath) {
    AstContext.build();
    AstAutomataBuilder astAutomataBuilder = new AstAutomataBuilder();
    astAutomataBuilder.build(grammarFilePath);
  }

  public void setContext(List<String> grammarFilePaths) {
    AstContext.build();
    AstAutomataBuilder astAutomataBuilder = new AstAutomataBuilder();
    astAutomataBuilder.buildByFiles(grammarFilePaths);
  }

  public void setContext(InputStream grammarFileInputStream) {
    AstContext.build();
    AstAutomataBuilder astAutomataBuilder = new AstAutomataBuilder();
    astAutomataBuilder.build(grammarFileInputStream);
  }

  public void clear() {
    AstContext.clear();
  }

  public void buildPersistentAutomata(String persistentAutomataFilePath) {
    PersistentAutomataBuilder persistentAutomataBuilder = new PersistentAutomataBuilder();
    persistentAutomataBuilder.build(persistentAutomataFilePath);
  }

  public List<Ast> buildAsts(String sourceFilePath) {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // token
    List<Token> tokens = languageGrammar.tokenAutomata.buildToken(sourceFilePath);
    // ast
    return languageGrammar.astAutomata.buildAsts(tokens);
  }

  public List<Ast> buildAsts(InputStream sourceInputStream) {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // token
    List<Token> tokens = languageGrammar.tokenAutomata.buildToken(sourceInputStream);
    // ast
    List<Ast> asts = languageGrammar.astAutomata.buildAsts(tokens);
    if (null != sourceInputStream) {
      try {
        sourceInputStream.close();
      } catch (Exception e) {
        throw new AstRuntimeException(e);
      }
    }
    return asts;
  }

  public Ast buildAst(String sourceFilePath) {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // token
    List<Token> tokens = languageGrammar.tokenAutomata.buildToken(sourceFilePath);
    // ast
    return languageGrammar.astAutomata.buildAst(tokens);
  }

  public Ast buildAst(InputStream sourceInputStream) {
    AstContext astContext = AstContext.get();
    LanguageGrammar languageGrammar = astContext.languageGrammar;
    // token
    List<Token> tokens = languageGrammar.tokenAutomata.buildToken(sourceInputStream);
    // ast
    Ast ast = languageGrammar.astAutomata.buildAst(tokens);
    if (null != sourceInputStream) {
      try {
        sourceInputStream.close();
      } catch (Exception e) {
        throw new AstRuntimeException(e);
      }
    }
    return ast;
  }

  public void displayGraphicalViewOfAst(Ast ast) {
    new AstGuiOutputer().output(ast);
  }
}
