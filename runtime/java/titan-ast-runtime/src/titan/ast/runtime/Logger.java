package titan.ast.runtime;

/**
 * 默认的日志实现类.
 *
 * @author tian wei jun
 */
class Logger {

  private static void print(String msg) {
    System.out.print(msg);
  }

  private static void println(String msg) {
    System.out.println(msg);
  }

  static void debug(String msg) {
    println(msg);
  }

  static void info(String msg) {
    println(msg);
  }

  static void warn(String msg) {
    println(msg);
  }

  static void error(String msg) {
    println(msg);
  }

  static void fatal(String msg) {
    println(msg);
  }
}
