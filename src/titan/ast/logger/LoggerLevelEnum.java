package titan.ast.logger;

/**
 * 日志级别.
 *
 * @author tian wei jun
 */
public enum LoggerLevelEnum {
  // debug < info < warn < error < fatal
  DEBUG(0, "debug"),
  INFO(1, "info"),
  WARN(2, "warn"),
  ERROR(3, "error"),
  FATAL(4, "fatal");

  private final int level;
  private final String name;

  LoggerLevelEnum(int level, String name) {
    this.level = level;
    this.name = name;
  }

  /**
   * 根据 日志级别名字 返回 日志级别.
   *
   * @param name 日志级别名字
   * @return 日志级别
   */
  public static LoggerLevelEnum getByName(String name) {
    LoggerLevelEnum levelEnum = INFO;
    for (LoggerLevelEnum loggerLevelEnum : LoggerLevelEnum.values()) {
      if (loggerLevelEnum.name.equals(name)) {
        levelEnum = loggerLevelEnum;
        break;
      }
    }
    return levelEnum;
  }

  public int getLevel() {
    return level;
  }
}
