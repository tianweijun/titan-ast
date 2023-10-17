package titan.ast.logger;

/**
 * 日志接口.
 *
 * @author tian wei jun
 */
public interface Loggerable {

  LoggerLevelEnum loggerLevel();

  void setLoggerLevel(LoggerLevelEnum level);

  void debug(String msg);

  void info(String msg);

  void warn(String msg);

  void error(String msg);

  void fatal(String msg);
}
