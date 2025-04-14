package titan.ast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import titan.ast.autocode.visitor.ContextAstCodeGenerator;
import titan.ast.autocode.visitor.VisitorCodeGenerator;
import titan.ast.fa.syntax.DfaAstAutomataFactory;
import titan.ast.fa.syntax.ProductionRuleInitializer;
import titan.ast.fa.syntax.ProductionRuleNfaBuilder;
import titan.ast.fa.syntax.ProductionRuleReducingDfaBuilder;
import titan.ast.fa.syntax.SyntaxDfaBuilder;
import titan.ast.fa.token.DerivedTerminalGrammarAutomataDataBuilder;
import titan.ast.fa.token.DfaTokenAutomataFactory;
import titan.ast.fa.token.TerminalFragmentGrammarNfaBuilder;
import titan.ast.fa.token.TerminalGrammarNfaBuilder;
import titan.ast.fa.token.TokenDfaBuilder;
import titan.ast.logger.Logger;
import titan.ast.persistence.PersistentAutomataBuilder;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

public abstract class GrammarFileAutomataAstApplication {

  public GrammarFileAutomataAstApplication(String grammarFilePath) {
    ArrayList<String> grammarFilePaths = new ArrayList<>(1);
    grammarFilePaths.add(grammarFilePath);
    setAstAutomataContext(grammarFilePaths);
  }

  public GrammarFileAutomataAstApplication(List<String> grammarFilePaths) {
    setAstAutomataContext(grammarFilePaths);
  }

  public void setAstAutomataContext(List<String> grammarFilePaths) {
    AstContext.init();
    initGrammar(grammarFilePaths);
    //token
    buildTokenNfa();
    buildTokenDfa();
    new DerivedTerminalGrammarAutomataDataBuilder().build();
    buildTokenAutomata();
    //syntax
    buildProductionRule();
    buildSyntaxDfa();
    buildAstAutomata();
  }

  private void buildAstAutomata() {
    DfaAstAutomataFactory.create();
  }

  private void buildTokenAutomata() {
    DfaTokenAutomataFactory.create();
  }

  protected void buildTokenNfa() {
    new TerminalFragmentGrammarNfaBuilder().buildNfa();
    new TerminalGrammarNfaBuilder().buildNfa();
  }

  private void buildTokenDfa() {
    new TokenDfaBuilder().buildDfa();
  }

  protected void buildProductionRule() {
    new ProductionRuleInitializer().init();
    ProductionRuleReducingDfaBuilder.build();
    new ProductionRuleNfaBuilder().build();
  }

  private void buildSyntaxDfa() {
    new SyntaxDfaBuilder().build();
  }


  protected abstract void initGrammar(List<String> grammarFilePaths);

  public void buildPersistentAutomata(String persistentAutomataFilePath) {
    new PersistentAutomataBuilder().build(persistentAutomataFilePath);
  }

  public RuntimeAutomataRichAstApplication getRuntimeAutomataRichAstApplication() {
    return buildRuntimeAutomataRichAstApplication();
  }

  private RuntimeAutomataRichAstApplication buildRuntimeAutomataRichAstApplication() {
    PersistentAutomataBuilder persistentAutomataBuilder = new PersistentAutomataBuilder();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    persistentAutomataBuilder.savePersistentDataToOutputStream(outputStream);

    RuntimeAutomataRichAstApplication runtimeAutomataRichAstApplication =
        new RuntimeAutomataRichAstApplication();
    try {
      runtimeAutomataRichAstApplication.setContext(
          new ByteArrayInputStream(outputStream.toByteArray()));
      outputStream.close();
    } catch (AutomataDataIoException | IOException e) {
      throw new AstRuntimeException(e);
    }
    return runtimeAutomataRichAstApplication;
  }

  public void generateAstVisitor(String astVisitorFileDirectory, String astVisitorPackage) {
    new ContextAstCodeGenerator(astVisitorFileDirectory, astVisitorPackage).generate();
    new VisitorCodeGenerator(astVisitorFileDirectory, astVisitorPackage).generate();
  }

  public void isAmbiguous() {
    Logger.info(
        "the feature(isAmbiguous) is not available. be careful of precedence, associativity and uniqueness properties"
            + ".");
  }
}
