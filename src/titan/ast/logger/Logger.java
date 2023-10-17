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

  public static void debug(String source, String msg) {
    log.debug(formatString(source, msg));
  }

  public static void info(String source, String msg) {
    log.info(formatString(source, msg));
  }

  public static void warn(String source, String msg) {
    log.warn(formatString(source, msg));
  }

  public static void error(String source, String msg) {
    log.error(formatString(source, msg));
  }

  public static void fatal(String source, String msg) {
    log.error(formatString(source, msg));
  }

  private static String formatString(String source, String msg) {
    StringBuilder stringBuilder = new StringBuilder();
    if (source != null && source.length() > 0) {
      stringBuilder.append("[");
      stringBuilder.append(source);
      stringBuilder.append("]");
      stringBuilder.append("  ");
    }
    stringBuilder.append(msg);
    return stringBuilder.toString();
  }
}
