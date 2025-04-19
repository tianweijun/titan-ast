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
  final GrammarAutomataAstApplicationBuilder grammarAutomataAstApplicationBuilder;

  public CommandLineAstApplication(String[] args) {
    commandLineParameters = new CommandLineArgParser().parse(args);
    grammarAutomataAstApplicationBuilder = new DefaultGrammarAutomataAstApplicationBuilder();
  }

  public CommandLineAstApplication(
      String[] args, GrammarAutomataAstApplicationBuilder grammarAutomataAstApplicationBuilder) {
    commandLineParameters = new CommandLineArgParser().parse(args);
    this.grammarAutomataAstApplicationBuilder = grammarAutomataAstApplicationBuilder;
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
    grammarAutomataAstApplicationBuilder.build(commandLineParameters.grammarFilePaths);

    isAmbiguous();
    buildAstVisitor();
    persistAutomata();
    buildAstByGrammarFile();

    AstContext.clear();
  }

  private void buildAstVisitor() {
    if (commandLineParameters.isBuildingAstVisitor()) {
      grammarAutomataAstApplicationBuilder
          .get()
          .generateAstVisitor(
              commandLineParameters.astVisitorFileDirectory,
              commandLineParameters.astVisitorPackage);
    }
  }

  private void isAmbiguous() {
    if (commandLineParameters.isAmbiguous()) {
      grammarAutomataAstApplicationBuilder.get().isAmbiguous();
    }
  }

  private void persistAutomata() {
    if (commandLineParameters.isPersistentAutomata()) {
      grammarAutomataAstApplicationBuilder
          .get()
          .buildPersistentAutomata(commandLineParameters.persistentAutomataFilePath);
    }
  }

  private void buildAstByGrammarFile() {
    if (commandLineParameters.isBuildingAstByGrammarFile()) {
      RuntimeAutomataRichAstApplication runtimeAutomataRichAstApplication =
          grammarAutomataAstApplicationBuilder.get().getRuntimeAutomataRichAstApplication();
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
