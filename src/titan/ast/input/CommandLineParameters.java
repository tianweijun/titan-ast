package titan.ast.input;

import java.util.LinkedList;
import java.util.List;
import titan.ast.util.StringUtils;

/**
 * 命令行参数对应的实体.
 *
 * @author tian wei jun
 */
public class CommandLineParameters {

  public List<String> grammarFilePaths = new LinkedList<>();
  public String sourceFilePath = "";
  public String persistentAutomataFilePath = "";
  public String automataFilePath = "";

  public boolean graphicalViewOfAst = false;
  public String graphicalViewOfAstCharSet = "";
  public boolean isAmbiguous = false;

  public boolean isRight() {
    return isBuildingAstByAutomataFile()
        || isBuildingAstByGrammarFile()
        || isPersistentAutomata()
        || isAmbiguous();
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

  public boolean isAmbiguous() {
    return !grammarFilePaths.isEmpty() && isAmbiguous;
  }

  public String infoOfHelper() {

    String stringBuilder =
        "usage: java -jar  titan-ast.jar"
            + "\n"
            + "         "
            + "[-grammarFilePath <filepath>] [-grammarFilePaths <filepath1 filepath2 ...>]"
            + "\n"
            + "         "
            + "[-sourceFilePath <filepath>]"
            + "\n"
            + "         "
            + "[-persistentAutomataFilePath <filepath>]"
            + "\n"
            + "         "
            + "[-automataFilePath <filepath>]"
            + "\n"
            + "         "
            + "[-graphicalViewOfAst [charsetName]"
            + "\n"
            + "         "
            + "[--isAmbiguous --help]"
            + "\n";
    return stringBuilder;
  }
}
