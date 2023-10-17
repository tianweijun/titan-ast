package titan.ast.input;

import java.util.LinkedList;
import java.util.List;
import titan.util.StringUtils;

/**
 * 命令行参数对应的实体.
 *
 * @author tian wei jun
 */
public class CommandLineParameters {

  public static final String keyOfGrammarFilePath = "-grammarFilePath";
  public static final String keyOfGrammarFilePaths = "-grammarFilePaths";
  public static final String keyOfSourceFilePath = "-sourceFilePath";
  public static final String keyOfGraphicalViewOfAst = "--graphicalViewOfAst";
  public static final String keyOfPersistentAutomataFilePath = "-persistentAutomataFilePath";
  public static final String keyOfAutomataFilePath = "-automataFilePath";

  public List<String> grammarFilePaths = new LinkedList<>();
  public String sourceFilePath = "";
  public String persistentAutomataFilePath = "";
  public boolean graphicalViewOfAst = false;
  public String automataFilePath = "";

  public CommandLineParameters(String[] args) {
    initByCommandLineArgs(args);
  }

  /**
   * 形如java -jar titan-ast.jar [-grammarFilePath <path>] [-sourceFilePath <path>]
   * [--graphicalViewOfAst] [-persistentAutomataFile <filepath>] [-automataFilePath <path>]
   *
   * @param args 命令行参数
   */
  private void initByCommandLineArgs(String[] args) {
    if (null == args || args.length < 1) {
      return;
    }
    for (int indexOfArg = 0; indexOfArg < args.length; ) {
      String key = args[indexOfArg];
      if (keyOfGraphicalViewOfAst.equals(key)) {
        graphicalViewOfAst = true;
        ++indexOfArg;
      } else if (keyOfPersistentAutomataFilePath.equals(key)) {
        ++indexOfArg;
        if (indexOfArg < args.length) {
          String param = args[indexOfArg];
          persistentAutomataFilePath = param;
          ++indexOfArg;
        }
      } else if (keyOfGrammarFilePath.equals(key)) {
        ++indexOfArg;
        if (indexOfArg < args.length) {
          String param = args[indexOfArg];
          grammarFilePaths.add(param);
          ++indexOfArg;
        }
      } else if (keyOfGrammarFilePaths.equals(key)) {
        ++indexOfArg; // next is filePath ?
        while (indexOfArg < args.length) {
          String param = args[indexOfArg];
          if (param.startsWith("-")) { // next is not filePath,end.
            --indexOfArg; // try other param case.
            break;
          }
          // next is filePath,add it.
          grammarFilePaths.add(param);
          // try it until args end or next is not  filePath
          ++indexOfArg;
        }
      } else if (keyOfSourceFilePath.equals(key)) {
        ++indexOfArg;
        if (indexOfArg < args.length) {
          String param = args[indexOfArg];
          sourceFilePath = param;
          ++indexOfArg;
        }
      } else if (keyOfAutomataFilePath.equals(key)) {
        ++indexOfArg;
        if (indexOfArg < args.length) {
          String param = args[indexOfArg];
          automataFilePath = param;
          ++indexOfArg;
        }
      } else {
        ++indexOfArg;
      }
    }
  }

  public String infoOfHelper() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("usage: java -jar  titan-ast.jar").append("\n");
    stringBuilder
        .append("         ")
        .append("[-grammarFilePath <filepath>] [-sourceFilePath <filepath>]")
        .append("\n");
    stringBuilder
        .append("         ")
        .append("[-persistentAutomataFilePath <filepath>]")
        .append("\n");
    stringBuilder.append("         ").append("[-automataFilePath <filepath>]").append("\n");
    return stringBuilder.toString();
  }

  public boolean isRight() {
    return isBuildingAstByAutomataFile() || isBuildingAstByGrammarFile() || isPersistentAutomata();
  }

  public boolean isBuildingAstByAutomataFile() {
    return StringUtils.isNotBlank(automataFilePath) && StringUtils.isNotBlank(sourceFilePath);
  }

  public boolean isBuildingAstByGrammarFile() {
    return !grammarFilePaths.isEmpty() && StringUtils.isNotBlank(sourceFilePath);
  }

  public boolean isPersistentAutomata() {
    return !grammarFilePaths.isEmpty() && StringUtils.isNotBlank(persistentAutomataFilePath);
  }
}
