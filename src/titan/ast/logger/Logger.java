package titan.ast.logger;

/**
 * 日志.
 *
 * @author tian wei jun
 */
public class Logger {

  private static Loggerable log = new DefaultLogger(LoggerLevelEnum.DEBUG);

  public static void setLoggerLevel(LoggerLevelEnum level) {
    log.setLoggerLevel(level);
  }

  public static LoggerLevelEnum loggerLevel() {
    return log.loggerLevel();
  }

  public static void setLogger(Loggerable logger) {
    Logger.log = logger;
  }

  public static void debug(String msg) {
    log.debug(msg);
  }

  public static void info(String msg) {
    log.info(msg);
  }

  public static void warn(String msg) {
    log.warn(msg);
  }

  public static void error(String msg) {
    log.error(msg);
  }

  public static void fatal(String msg) {
    log.error(msg);
  }
}
