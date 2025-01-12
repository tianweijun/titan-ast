package titan.ast.runtime;

/**
 * 默认的日志实现类.
 *
 * @author tian wei jun
 */
public class Logger {

  private static void print(String msg) {
    System.out.print(msg);
  }

  private static void println(String msg) {
    System.out.println(msg);
  }

  public static void debug(String msg) {
    println(msg);
  }

  public static void info(String msg) {
    println(msg);
  }

  public static void warn(String msg) {
    println(msg);
  }

  public static void error(String msg) {
    println(msg);
  }

  public static void fatal(String msg) {
    println(msg);
  }
}
