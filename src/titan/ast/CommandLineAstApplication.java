package titan.ast;

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

  final CommandLineParameters commandLineParameters;
  final GrammarFileAutomataAstApplicationBuilder grammarFileAutomataAstApplicationBuilder;

  public CommandLineAstApplication(String[] args) {
    commandLineParameters = new CommandLineArgParser().parse(args);
    grammarFileAutomataAstApplicationBuilder =
        new DefaultGrammarFileAutomataAstApplicationBuilder();
  }

  public CommandLineAstApplication(
      String[] args,
      GrammarFileAutomataAstApplicationBuilder grammarFileAutomataAstApplicationBuilder) {
    commandLineParameters = new CommandLineArgParser().parse(args);
    this.grammarFileAutomataAstApplicationBuilder = grammarFileAutomataAstApplicationBuilder;
  }

  /**
   * 入口函数.
   *
   * @param args 形如 -grammarFilePath C.grammar -sourceFilePath helloworld.c -graphicalViewOfAst
   */
  public static void main(String[] args) {
    new CommandLineAstApplication(args).run();
  }

  /** 接受命令行参数来运行. */
  public void run() {
    if (!commandLineParameters.isRight()) {
      Logger.warn(commandLineParameters.infoOfHelper());
      return;
    }
    try {
      buildAstByAutomataFile();
      buildByGrammarFile();
    } catch (AstRuntimeException e) {
      Logger.info(
          String.format("CommandLineAstApplication run failed,cause by %s", e.getMessage()));
      return;
      // throw e;
    }

    Logger.info("CommandLineAstApplication  run successfully");
  }

  private void buildByGrammarFile() {
    if (!(commandLineParameters.isPersistentAutomata()
        || commandLineParameters.isBuildingAstByGrammarFile()
        || commandLineParameters.isAmbiguous()
        || commandLineParameters.isBuildingAstVisitor())) {
      return;
    }
    grammarFileAutomataAstApplicationBuilder.build(commandLineParameters.grammarFilePaths);

    isAmbiguous();
    buildAstVisitor();
    persistAutomata();
    buildAstByGrammarFile();

    AstContext.clear();
  }

  private void buildAstVisitor() {
    if (commandLineParameters.isBuildingAstVisitor()) {
      grammarFileAutomataAstApplicationBuilder
          .get()
          .generateAstVisitor(
              commandLineParameters.astVisitorFileDirectory,
              commandLineParameters.astVisitorPackage);
    }
  }

  private void isAmbiguous() {
    if (commandLineParameters.isAmbiguous()) {
      grammarFileAutomataAstApplicationBuilder.get().isAmbiguous();
    }
  }

  private void persistAutomata() {
    if (commandLineParameters.isPersistentAutomata()) {
      grammarFileAutomataAstApplicationBuilder
          .get()
          .buildPersistentAutomata(commandLineParameters.persistentAutomataFilePath);
    }
  }

  private void buildAstByGrammarFile() {
    if (commandLineParameters.isBuildingAstByGrammarFile()) {
      RuntimeAutomataRichAstApplication runtimeAutomataRichAstApplication =
          grammarFileAutomataAstApplicationBuilder.get().getRuntimeAutomataRichAstApplication();
      runtimeAutomataRichAstApplication.setCharset(commandLineParameters.graphicalViewOfAstCharSet);
      RichAstGeneratorResult astGeneratorResult =
          runtimeAutomataRichAstApplication.buildRichAst(commandLineParameters.sourceFilePath);
      if (!astGeneratorResult.isOk()) {
        Logger.info(astGeneratorResult.getErrorMsg());
      }
      if (commandLineParameters.graphicalViewOfAst && astGeneratorResult.isOk()) {
        runtimeAutomataRichAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
      }
    }
  }

  private void buildAstByAutomataFile() {
    if (commandLineParameters.isBuildingAstByAutomataFile()) {
      RuntimeAutomataRichAstApplication runtimeAutomataAstApplication =
          new RuntimeAutomataRichAstApplication();
      try {
        runtimeAutomataAstApplication.setContext(commandLineParameters.automataFilePath);
        runtimeAutomataAstApplication.setCharset(commandLineParameters.graphicalViewOfAstCharSet);
      } catch (AutomataDataIoException e) {
        throw new AstRuntimeException(e);
      }
      RichAstGeneratorResult astGeneratorResult =
          runtimeAutomataAstApplication.buildRichAst(commandLineParameters.sourceFilePath);
      if (!astGeneratorResult.isOk()) {
        Logger.info(astGeneratorResult.getErrorMsg());
      }
      if (commandLineParameters.graphicalViewOfAst && astGeneratorResult.isOk()) {
        runtimeAutomataAstApplication.displayGraphicalViewOfAst(astGeneratorResult.getOkAst());
      }
    }
  }
}
