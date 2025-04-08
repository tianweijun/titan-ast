package titan.ast.grammar.regexp;

import titan.ast.grammar.regexp.RepeatTimes.RepeatTimesType;

/**
 * 语法文件中所描述语法的文本形式.
 *
 * @author tian wei jun
 */
public abstract class RegExp {

  public RegExp parent = null;

  public final RegExpType type;

  public RepeatTimes repMinTimes = new RepeatTimes(RepeatTimesType.NUMBER, 1);
  public RepeatTimes repMaxTimes = new RepeatTimes(RepeatTimesType.NUMBER, 1);

  public RegExpSource regExpSource;

  public RegExp(RegExpType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RegExp regExp = (RegExp) o;
    return type == regExp.type && repMinTimes.equals(regExp.repMinTimes) && repMaxTimes.equals(regExp.repMaxTimes);
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + repMinTimes.hashCode();
    result = 31 * result + repMaxTimes.hashCode();
    return result;
  }
}
