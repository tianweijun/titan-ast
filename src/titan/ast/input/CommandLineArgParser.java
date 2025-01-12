package titan.ast.input;

import java.util.Iterator;
import titan.ast.logger.Logger;

/**
 * .
 *
 * @author tian wei jun
 */
public class CommandLineArgParser {

  /**
   * 形如java -jar titan-ast.jar [-grammarFilePath <path>] [-sourceFilePath <path>]
   * [-graphicalViewOfAst [charsetName] [-persistentAutomataFile <filepath>] [-automataFilePath
   * <path>]
   *
   * @param args 命令行参数
   */
  public CommandLineParameters parse(String[] args) {
    if (null == args || args.length < 1) {
      return new CommandLineParameters();
    }
    OptionParserDto optionParserDto = new OptionParserDto(new CommandLineParameters(), args, 0);
    while (optionParserDto.hasNext()) {
      String strOptionType = optionParserDto.next();
      OptionType optionType = OptionType.getOptionTypeByString(strOptionType);
      optionType.getParser().parse(optionParserDto);
    }

    return optionParserDto.commandLineParameters;
  }

  public static enum OptionType {
    GRAMMAR_FILE_PATH("-grammarFilePath", new GrammarFilePathOptionParser()),
    GRAMMAR_FILE_PATHS("-grammarFilePaths", new GrammarFilePathsOptionParser()),
    SOURCE_FILE_PATH("-sourceFilePath", new SourceFilePathOptionParser()),
    PERSISTENT_AUTOMATA_FILE_PATH(
        "-persistentAutomataFilePath", new PersistentAutomataFilePathOptionParser()),
    AUTOMATA_FILE_PATH("-automataFilePath", new AutomataFilePathOptionParser()),
    GRAPHICAL_VIEW_OF_AST("-graphicalViewOfAst", new GraphicalViewOfAstOptionParser()),
    IS_AMBIGUOUS("--isAmbiguous", new IsAmbiguousOptionParser()),
    HELP("--help", new HelpOptionParser()),
    ILLEGAL("", new IllegalOptionParser());

    private final String strOptionType;
    private final OptionParser parser;

    OptionType(String strOptionType, OptionParser parser) {
      this.strOptionType = strOptionType;
      this.parser = parser;
    }

    static boolean isRightStrOption(String strOption) {
      if (null == strOption) {
        return false;
      }
      OptionType strOptionType = ILLEGAL;
      for (OptionType option : OptionType.values()) {
        if (option.getStrOptionType().equals(strOption)) {
          strOptionType = option;
          break;
        }
      }
      return strOptionType != ILLEGAL;
    }

    static OptionType getOptionTypeByString(String strOption) {
      if (null == strOption) {
        return ILLEGAL;
      }
      for (OptionType option : OptionType.values()) {
        if (option.getStrOptionType().equals(strOption)) {
          return option;
        }
      }
      return ILLEGAL;
    }

    public String getStrOptionType() {
      return strOptionType;
    }

    public OptionParser getParser() {
      return parser;
    }
  }

  interface OptionParser {
    void parse(OptionParserDto dto);
  }

  static class OptionParserDto implements Iterator<String> {
    CommandLineParameters commandLineParameters;
    String[] args;
    int indexOfArg;

    public OptionParserDto(
        CommandLineParameters commandLineParameters, String[] args, int indexOfArg) {
      this.commandLineParameters = commandLineParameters;
      this.args = args;
      this.indexOfArg = indexOfArg;
    }

    @Override
    public boolean hasNext() {
      return indexOfArg < args.length;
    }

    @Override
    public String next() {
      return args[indexOfArg++];
    }

    public String peek() {
      return args[indexOfArg];
    }
  }

  static class GrammarFilePathOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      if (dto.hasNext()) {
        dto.commandLineParameters.grammarFilePaths.add(dto.next());
      }
    }
  }

  static class GrammarFilePathsOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      while (dto.hasNext()) {
        String arg = dto.peek();
        if (OptionType.isRightStrOption(arg)) { // next is not filePath,end.
          break;
        }
        // next is filePath,add it.
        dto.commandLineParameters.grammarFilePaths.add(dto.next());
      }
    }
  }

  static class SourceFilePathOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      if (dto.hasNext()) {
        dto.commandLineParameters.sourceFilePath = dto.next();
      }
    }
  }

  static class PersistentAutomataFilePathOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      if (dto.hasNext()) {
        dto.commandLineParameters.persistentAutomataFilePath = dto.next();
      }
    }
  }

  static class AutomataFilePathOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      if (dto.hasNext()) {
        dto.commandLineParameters.automataFilePath = dto.next();
      }
    }
  }

  static class GraphicalViewOfAstOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      dto.commandLineParameters.graphicalViewOfAst = true;
      if (dto.hasNext()) {
        String arg = dto.peek();
        if (OptionType.isRightStrOption(arg)) { // next is not charset,end.
          return;
        }
        // next is charset,add it.
        dto.commandLineParameters.graphicalViewOfAstCharSet = dto.next();
      }
    }
  }

  static class IsAmbiguousOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      dto.commandLineParameters.isAmbiguous = true;
    }
  }

  static class HelpOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {
      Logger.info(dto.commandLineParameters.infoOfHelper());
    }
  }

  static class IllegalOptionParser implements OptionParser {

    @Override
    public void parse(OptionParserDto dto) {}
  }
}
