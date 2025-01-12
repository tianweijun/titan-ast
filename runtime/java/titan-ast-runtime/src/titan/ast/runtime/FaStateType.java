package titan.ast.runtime;

/**
 * 有限状态自动机 状态类型.
 *
 * @author tian wei jun
 */
public enum FaStateType {
  NONE(0),
  NORMAL(1),
  OPENING_TAG(2),
  CLOSING_TAG(4);

  private final int v;

  FaStateType(int v) {
    this.v = v;
  }

  public static int appendOpeningTag(int state) {
    return appendState(state, OPENING_TAG);
  }

  public static int appendClosingTag(int state) {
    return appendState(state, CLOSING_TAG);
  }

  public static int appendState(int state, FaStateType appendState) {
    return state | appendState.v;
  }

  public static int removeOpeningTag(int state) {
    return removeState(state, OPENING_TAG);
  }

  public static int removeClosingTag(int state) {
    return removeState(state, CLOSING_TAG);
  }

  public static int removeState(int state, FaStateType removeState) {
    return state & (~removeState.v);
  }

  public static boolean isNone(int state) {
    return state == NONE.v;
  }

  public static boolean isNormal(int state) {
    return (state & NORMAL.v) != 0;
  }

  public static boolean isOpeningTag(int state) {
    return (state & OPENING_TAG.v) != 0;
  }

  public static boolean isClosingTag(int state) {
    return (state & CLOSING_TAG.v) != 0;
  }

  public int getValue() {
    return v;
  }
}
