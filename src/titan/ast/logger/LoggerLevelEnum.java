package titan.ast.logger;

/**
 * 日志级别.
 *
 * @author tian wei jun
 */
public enum LoggerLevelEnum {
  // debug < info < warn < error < fatal
  DEBUG(0),
  INFO(1),
  WARN(2),
  ERROR(3),
  FATAL(4);

  private final int level;

  LoggerLevelEnum(int level) {
    this.level = level;
  }

  /**
   * 根据 日志级别名字 返回 日志级别.
   *
   * @param name 日志级别名字
   * @return 日志级别
   */
  public static LoggerLevelEnum getByName(String name) {
    LoggerLevelEnum levelEnum = INFO;
    if ("DEBUG".equalsIgnoreCase(name)) {
      levelEnum = DEBUG;
    }
    if ("INFO".equalsIgnoreCase(name)) {
      levelEnum = INFO;
    }
    if ("WARN".equalsIgnoreCase(name)) {
      levelEnum = WARN;
    }
    if ("ERROR".equalsIgnoreCase(name)) {
      levelEnum = ERROR;
    }
    if ("FATAL".equalsIgnoreCase(name)) {
      levelEnum = FATAL;
    }
    return levelEnum;
  }

  public int getLevel() {
    return level;
  }
}
