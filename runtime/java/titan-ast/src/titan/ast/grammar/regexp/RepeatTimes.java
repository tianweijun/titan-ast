package titan.ast.grammar.regexp;

import titan.ast.AstRuntimeException;

/**
 * .
 *
 * @author tian wei jun
 */
public class RepeatTimes implements Cloneable {

  private static RepeatTimes INFINITY_TIMES = new RepeatTimes(RepeatTimesType.INFINITY, 0);
  public RepeatTimesType type; // num
  public int times;

  private RepeatTimes(RepeatTimesType type, int times) {
    this.type = type;
    this.times = times;
  }

  public static RepeatTimes getInfinityTimes() {
    return INFINITY_TIMES;
  }

  public static RepeatTimes getNumberTimes(int times) {
    return new RepeatTimes(RepeatTimesType.NUMBER, times);
  }

  public void setTimes(RepeatTimes repeatTimes) {
    this.type = repeatTimes.type;
    this.times = repeatTimes.times;
  }

  public void setTimes(int times) {
    this.type = RepeatTimesType.NUMBER;
    this.times = times;
  }

  public void setInfinity() {
    this.type = RepeatTimesType.INFINITY;
    this.times = 0;
  }

  public boolean isZeroTimes() {
    return type == RepeatTimesType.NUMBER && 0 == times;
  }

  public boolean isInfinityTimes() {
    return type == RepeatTimesType.INFINITY;
  }
  
  public boolean isNumberTimesAndGreatThanOrEqual(int otherTimes) {
    return type == RepeatTimesType.NUMBER && this.times >= otherTimes;
  }

  public boolean isNumberTimesAndEqual(int otherTimes) {
    return type == RepeatTimesType.NUMBER && this.times == otherTimes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RepeatTimes repeatTimes = (RepeatTimes) o;

    if (times != repeatTimes.times) {
      return false;
    }
    return type == repeatTimes.type;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + times;
    return result;
  }

  public RepeatTimes clone() {
    RepeatTimes copy = null;
    try {
      copy = (RepeatTimes) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AstRuntimeException(e);
    }
    return copy;
  }

  public enum RepeatTimesType {
    NUMBER,
    INFINITY
  }
}