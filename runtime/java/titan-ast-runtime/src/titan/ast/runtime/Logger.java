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

  public static void debug(String source, String msg) {
    println(formatString(source, msg));
  }

  public static void info(String source, String msg) {
    println(formatString(source, msg));
  }

  public static void warn(String source, String msg) {
    println(formatString(source, msg));
  }

  public static void error(String source, String msg) {
    println(formatString(source, msg));
  }

  public static void fatal(String source, String msg) {
    println(formatString(source, msg));
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
