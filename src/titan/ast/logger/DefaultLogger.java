package titan.ast.logger;

/**
 * 默认的日志实现类.
 *
 * @author tian wei jun
 */
public class DefaultLogger implements Loggerable {

  public LoggerLevelEnum loggerLevel = LoggerLevelEnum.INFO;

  public DefaultLogger(LoggerLevelEnum loggerLevel) {
    this.loggerLevel = loggerLevel;
  }

  @Override
  public LoggerLevelEnum loggerLevel() {
    return loggerLevel;
  }

  @Override
  public void setLoggerLevel(LoggerLevelEnum level) {
    this.loggerLevel = level;
  }

  private void print(String msg) {
    System.out.print(msg);
  }

  private void println(String msg) {
    System.out.println(msg);
  }

  @Override
  public void debug(String msg) {
    if (loggerLevel.getLevel() <= LoggerLevelEnum.DEBUG.getLevel()) {
      println(msg);
    }
  }

  @Override
  public void info(String msg) {
    if (loggerLevel.getLevel() <= LoggerLevelEnum.INFO.getLevel()) {
      println(msg);
    }
  }

  @Override
  public void warn(String msg) {
    if (loggerLevel.getLevel() <= LoggerLevelEnum.WARN.getLevel()) {
      println(msg);
    }
  }

  @Override
  public void error(String msg) {
    if (loggerLevel.getLevel() <= LoggerLevelEnum.ERROR.getLevel()) {
      println(msg);
    }
  }

  @Override
  public void fatal(String msg) {
    if (loggerLevel.getLevel() <= LoggerLevelEnum.FATAL.getLevel()) {
      println(msg);
    }
  }
}
