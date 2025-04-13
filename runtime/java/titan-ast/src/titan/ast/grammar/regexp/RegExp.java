package titan.ast.grammar.regexp;

/**
 * 语法文件中所描述语法的文本形式.
 *
 * @author tian wei jun
 */
public abstract class RegExp {

  public final RegExpType type;
  //public RegExp parent = null;
  public RepeatTimes repMinTimes = RepeatTimes.getNumberTimes(1);
  public RepeatTimes repMaxTimes = RepeatTimes.getNumberTimes(1);

  public RegExp(RegExpType type) {
    this.type = type;
  }

  public void setRepeatTimes(RepeatTimes repMinTimes, RepeatTimes repMaxTimes) {
    this.repMinTimes.setTimes(repMinTimes);
    this.repMaxTimes.setTimes(repMaxTimes);
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
