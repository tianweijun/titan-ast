package titan.ast;

/**
 * 当前应用程序入口.
 *
 * @author tian wei jun
 */
public class AstApplication {

  /**
   * 入口函数.
   *
   * @param args 形如 -grammarFilePath C.grammar -sourceFilePath helloworld.c --graphicalViewOfAst
   */
  public static void main(String[] args) {
    new CommandLineAstApplication().run(args);
  }
}
