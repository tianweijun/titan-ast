package titan.ast;

import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.input.CommandLineParameters;
import titan.ast.logger.Logger;
import titan.ast.runtime.RuntimeAutomataAstApplication;

/**
 * 命令行应用.
 *
 * @author tian wei jun
 */
public class CommandLineAstApplication {

  /**
   * 接受命令行参数来运行.
   *
   * @param args 形如 -grammarFilePath C.grammar -sourceFilePath helloworld.c --graphicalViewOfAst
   */
  public void run(String[] args) {
    CommandLineParameters commandLineParameters = new CommandLineParameters(args);
    if (!commandLineParameters.isRight()) {
      Logger.warn(null, commandLineParameters.infoOfHelper());
      return;
    }

    buildAstByAutomataFile(commandLineParameters);
    buildByGrammarFile(commandLineParameters);

    Logger.info("CommandLineAstApplication run", "run successfully");
  }

  private void buildByGrammarFile(CommandLineParameters commandLineParameters) {
    if (!(commandLineParameters.isPersistentAutomata()
        || commandLineParameters.isBuildingAstByGrammarFile()
        || commandLineParameters.isAmbiguous())) {
      return;
    }
    GrammarFileAutomataAstApplication grammarFileAutomataAstApplication =
        new GrammarFileAutomataAstApplication();
    // build context
    if (commandLineParameters.isPersistentAutomata()
        || commandLineParameters.isBuildingAstByGrammarFile()) {
      grammarFileAutomataAstApplication.setAstAutomataContext(
          commandLineParameters.grammarFilePaths);
    } else if (commandLineParameters.isAmbiguous()) {
      grammarFileAutomataAstApplication.setProductionRuleContext(
          commandLineParameters.grammarFilePaths);
    }

    isAmbiguous(grammarFileAutomataAstApplication, commandLineParameters);
    persistAutomata(grammarFileAutomataAstApplication, commandLineParameters);
    buildAstByGrammarFile(grammarFileAutomataAstApplication, commandLineParameters);

    grammarFileAutomataAstApplication.clear();
  }

  private void isAmbiguous(
      GrammarFileAutomataAstApplication grammarFileAutomataAstApplication,
      CommandLineParameters commandLineParameters) {
    if (commandLineParameters.isAmbiguous()) {
      grammarFileAutomataAstApplication.isAmbiguous();
    }
  }

  private void persistAutomata(
      GrammarFileAutomataAstApplication grammarFileAutomataAstApplication,
      CommandLineParameters commandLineParameters) {
    if (commandLineParameters.isPersistentAutomata()) {
      grammarFileAutomataAstApplication.buildPersistentAutomata(
          commandLineParameters.persistentAutomataFilePath);
    }
  }

  private void buildAstByGrammarFile(
      GrammarFileAutomataAstApplication grammarFileAutomataAstApplication,
      CommandLineParameters commandLineParameters) {
    if (commandLineParameters.isBuildingAstByGrammarFile()) {
      grammarFileAutomataAstApplication.setRuntimeAutomataAstApplication();
      titan.ast.runtime.Ast ast =
          grammarFileAutomataAstApplication.buildAst(commandLineParameters.sourceFilePath);
      if (commandLineParameters.graphicalViewOfAst) {
        grammarFileAutomataAstApplication.displayGraphicalViewOfAst(ast);
      }
    }
  }

  private void buildAstByAutomataFile(CommandLineParameters commandLineParameters) {
    if (commandLineParameters.isBuildingAstByAutomataFile()) {
      RuntimeAutomataAstApplication runtimeAutomataAstApplication =
          new RuntimeAutomataAstApplication(commandLineParameters.automataFilePath);
      titan.ast.runtime.Ast ast =
          runtimeAutomataAstApplication.buildAst(commandLineParameters.sourceFilePath);
      if (commandLineParameters.graphicalViewOfAst) {
        runtimeAutomataAstApplication.displayGraphicalViewOfAst(ast);
      }
    }
  }
}
