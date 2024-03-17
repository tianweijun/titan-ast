package titan.ast.grammar;

public enum LookaheadMatchingMode {
  GREEDINESS,
  LAZINESS;

  public static titan.ast.runtime.LookaheadMatchingMode toRuntimeLookaheadMatchingMode(
      LookaheadMatchingMode lookaheadMatchingMode) {
    titan.ast.runtime.LookaheadMatchingMode runtimeLookaheadMatchingMode =
        titan.ast.runtime.LookaheadMatchingMode.GREEDINESS;
    switch (lookaheadMatchingMode) {
      case GREEDINESS:
        runtimeLookaheadMatchingMode = titan.ast.runtime.LookaheadMatchingMode.GREEDINESS;
        break;
      case LAZINESS:
        runtimeLookaheadMatchingMode = titan.ast.runtime.LookaheadMatchingMode.LAZINESS;
        break;
    }
    return runtimeLookaheadMatchingMode;
  }
}
