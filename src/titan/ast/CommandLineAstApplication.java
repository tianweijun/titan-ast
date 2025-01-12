package titan.ast;

import titan.ast.grammar.GrammarFileAutomataAstApplication;
import titan.ast.input.CommandLineArgParser;
import titan.ast.input.CommandLineParameters;
import titan.ast.logger.Logger;
import titan.ast.runtime.AutomataDataIoException;
import titan.ast.runtime.RichAstGeneratorResult;
import titan.ast.runtime.RuntimeAutomataRichAstApplication;

/**
 * 命令行应用.
 *
 * @author tian wei jun
 */
public class CommandLineAstApplication {

  /**
   * 入口函数.
   *
   * @param args 形如 -grammarFilePath C.grammar -sourceFilePath helloworld.c -graphicalViewOfAst
   */
  public static void main(String[] args) {
    new CommandLineAstApplication().run(args);
  }

  /**
   * 接受命令行参数来运行.
   *
   * @param args 形如 -grammarFilePath C.grammar -sourceFilePath helloworld.c -graphicalViewOfAst
   */
  public void run(String[] args) {
    CommandLineParameters commandLineParameters = new CommandLineArgParser().parse(args);
    if (!commandLineParameters.isRight()) {
      Logger.warn(commandLineParameters.infoOfHelper());
      return;
    }

    try {
      buildAstByAutomataFile(commandLineParameters);
      buildByGrammarFile(commandLineParameters);
    } catch (AstRuntimeException e) {
      Logger.info(
          String.format(
              "CommandLineAstApplication run failed,cause by '%s'", e.getLocalizedMessage()));
      return;
    }

    Logger.info("CommandLineAstApplication  run successfully");
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
      RichAstGeneratorResult astGeneratorResult =
          grammarFileAutomataAstApplication.buildAst(commandLineParameters.sourceFilePath);
      if (!astGeneratorResult.isOk()) {
        Logger.info(astGeneratorResult.getErrorMsg());
      }
      if (commandLineParameters.graphicalViewOfAst && astGeneratorResult.isOk()) {
        grammarFileAutomataAstApplication.displayGraphicalViewOfAst(
            astGeneratorResult.getOkAst(), commandLineParameters.graphicalViewOfAstCharSet);
      }
    }
  }

  private void buildAstByAutomataFile(CommandLineParameters commandLineParameters) {
    if (commandLineParameters.isBuildingAstByAutomataFile()) {
      RuntimeAutomataRichAstApplication runtimeAutomataAstApplication =
          new RuntimeAutomataRichAstApplication();
      try {
        runtimeAutomataAstApplication.setContext(commandLineParameters.automataFilePath);
      } catch (AutomataDataIoException e) {
        throw new AstRuntimeException(e);
      }
      RichAstGeneratorResult astGeneratorResult =
          runtimeAutomataAstApplication.buildRichAst(commandLineParameters.sourceFilePath);
      if (!astGeneratorResult.isOk()) {
        Logger.info(astGeneratorResult.getErrorMsg());
      }
      if (commandLineParameters.graphicalViewOfAst && astGeneratorResult.isOk()) {
        runtimeAutomataAstApplication.displayGraphicalViewOfAst(
            astGeneratorResult.getOkAst(), commandLineParameters.graphicalViewOfAstCharSet);
      }
    }
  }
}
