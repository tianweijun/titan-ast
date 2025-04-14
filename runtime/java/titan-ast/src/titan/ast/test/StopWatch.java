package titan.ast.test;

/**
 * StopWatch.
 *
 * @author tian wei jun
 */
public class StopWatch {
  long stime = 0L;

  long etime = 0L;

  public void start() {
    stime = System.nanoTime();
  }

  public void stop() {
    etime = System.nanoTime();
  }

  public long getNanoTime() {
    return etime - stime;
  }

  public long getSecondsTime() {
    return (etime - stime) / 1000_000_000;
  }

  public long getTime() {
    return (etime - stime) / 1000_000;
  }

  public long getMillTime() {
    return (etime - stime) / 1000_000;
  }
}
