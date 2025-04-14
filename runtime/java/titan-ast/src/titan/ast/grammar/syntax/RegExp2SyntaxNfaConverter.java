package titan.ast.grammar.syntax;

import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.regexp.RegExp;
import titan.ast.grammar.regexp.RegExp.RepTimes;

/**
 * .
 *
 * @author tian wei jun
 */
public class RegExp2SyntaxNfaConverter {
  private final Grammar epsilon;
  private ProductionRule currentProductionRule;

  public RegExp2SyntaxNfaConverter() {
    this.epsilon = AstContext.get().languageGrammar.epsilon;
  }

  public SyntaxNfa convert(RegExp regExp, ProductionRule productionRule) {
    this.currentProductionRule = productionRule;
    buildSyntaxNfa(regExp);
    return regExp.syntaxNfa;
  }

  private void buildSyntaxNfa(RegExp regExp) {
    switch (regExp.type) {
      case COMPOSITE:
        for (RegExp child : regExp.children) {
          buildSyntaxNfa(child);
        }
        doBuildSyntaxNfaByCompositeRegExp(regExp);
        break;
      case UNIT:
        doBuildSyntaxNfaByGrammarUnitRegExp(regExp);
        break;
      case NFA:
        break;
      default:
    }
  }

  /**
   * .
   *
   * @param compositeRegExp not null,see AbstractRegExpBuilder.createAndInitRegExps(RegExp
   *     rootRegExp)
   */
  private void doBuildSyntaxNfaByCompositeRegExp(RegExp compositeRegExp) {
    RepTimes repMinTimes = compositeRegExp.repMinTimes;
    RepTimes repMaxTimes = compositeRegExp.repMaxTimes;
    SyntaxNfa syntaxNfa = compositeRegExp.syntaxNfa;
    if (null == syntaxNfa) {
      syntaxNfa = new SyntaxNfa(currentProductionRule);
      compositeRegExp.syntaxNfa = syntaxNfa;
    }
    SyntaxNfaState start = syntaxNfa.start;
    SyntaxNfaState end = syntaxNfa.end;
    // ------baseNfa start------
    SyntaxNfa rightNfa = new SyntaxNfa(currentProductionRule);
    switch (compositeRegExp.relationshipOfChildren) {
      case NOT:
        break;
      case OR:
        for (RegExp child : compositeRegExp.children) {
          SyntaxNfa childSyntaxNfa = child.syntaxNfa.cloneForRegExp2SyntaxNfaConverter();
          rightNfa.start.addEdge(epsilon, childSyntaxNfa.start);
          childSyntaxNfa.end.addEdge(epsilon, rightNfa.end);
        }
        break;
      case AND:
        SyntaxNfaState prevState = rightNfa.start;
        for (RegExp child : compositeRegExp.children) {
          SyntaxNfa childSyntaxNfa = child.syntaxNfa.cloneForRegExp2SyntaxNfaConverter();

          prevState.addEdge(epsilon, childSyntaxNfa.start);
          prevState = childSyntaxNfa.end;
        }
        prevState.addEdge(epsilon, rightNfa.end);
        break;
      default:
    }
    // ------baseNfa end------

    // 0  n(n>=1) infinity
    // 0
    if (repMinTimes.isZeroTimes()) {
      // [0,0]
      if (repMaxTimes.isZeroTimes()) {
        start.addEdge(epsilon, end);
      } else if (repMaxTimes.isNumberTimesAndGreatThanOrEqual(1)) { // [0,n]
        // 0
        start.addEdge(epsilon, end);

        // 1 start---epsilon--->preNfa--epsilon-->end
        SyntaxNfa preNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
        start.addEdge(epsilon, preNfa.start);
        preNfa.end.addEdge(epsilon, end);
        // [2,n]
        for (int repTimes = 2; repTimes <= repMaxTimes.times; repTimes++) {
          SyntaxNfa nextNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
          preNfa.end.addEdge(epsilon, nextNfa.start);
          nextNfa.end.addEdge(epsilon, end);

          preNfa = nextNfa;
        }
      } else { // [0,infinity]
        // 0
        start.addEdge(epsilon, end);
        SyntaxNfa cloneNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
        start.addEdge(epsilon, cloneNfa.start);
        cloneNfa.end.addEdge(epsilon, end);
        // infinity
        cloneNfa.end.addEdge(epsilon, cloneNfa.start);
      }
      return;
    }

    // n
    if (repMinTimes.isNumberTimesAndGreatThanOrEqual(1)) {
      // [n,0] error
      // [m,n]
      if (repMaxTimes.isNumberTimesAndGreatThanOrEqual(1)) {
        // -------ma start------
        // 1
        SyntaxNfa prevNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
        start.addEdge(epsilon, prevNfa.start);
        // [2,m]
        for (int repTimes = 2; repTimes <= repMinTimes.times; repTimes++) {
          SyntaxNfa nextNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
          prevNfa.end.addEdge(epsilon, nextNfa.start);
          prevNfa = nextNfa;
        }
        final int maxOfDifferenceTimes = repMaxTimes.times - repMinTimes.times;
        if (maxOfDifferenceTimes == 0) { // [m,m]
          prevNfa.end.addEdge(epsilon, end);
          return;
        }
        // -------ma end------
        // ------max times start------
        SyntaxNfaState middle = prevNfa.end;
        // [0,maxOfDifferenceTimes] maxOfDifferenceTimes>=1
        // 0
        middle.addEdge(epsilon, end);
        // [1-maxOfDifferenceTimes]
        // 1 middle---epsilon--->prevNfa
        prevNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
        middle.addEdge(epsilon, prevNfa.start);
        prevNfa.end.addEdge(epsilon, end);
        // [2-maxOfDifferenceTimes]
        for (int repTimes = 2; repTimes <= maxOfDifferenceTimes; repTimes++) {
          SyntaxNfa nextNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
          prevNfa.end.addEdge(epsilon, nextNfa.start);
          nextNfa.end.addEdge(epsilon, end);
          prevNfa = nextNfa;
        }
        // ------max times end------
      } else { // [m,infinity] m>=1
        // -------ma start------
        SyntaxNfa prevNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
        // 1
        start.addEdge(epsilon, prevNfa.start);
        // [2-m]
        for (int repTimes = 2; repTimes <= repMinTimes.times; repTimes++) {
          SyntaxNfa nextNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
          prevNfa.end.addEdge(epsilon, nextNfa.start);
          prevNfa = nextNfa;
        }
        prevNfa.end.addEdge(epsilon, end);
        // -------ma end------
        SyntaxNfa infinityNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
        prevNfa.end.addEdge(epsilon, infinityNfa.start);
        infinityNfa.end.addEdge(epsilon, end);
        // infinity
        infinityNfa.end.addEdge(epsilon, infinityNfa.start);
      }
      return;
    }

    // infinity
    // [infinity,0] error
    // [infinity,n] error
    // [infinity,infinity]==[1,infinity]==A+
    if (repMinTimes.isInfinityTimes() && repMaxTimes.isInfinityTimes()) {
      // 1
      SyntaxNfa oneNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
      start.addEdge(epsilon, oneNfa.start);
      oneNfa.end.addEdge(epsilon, end);
      // 2
      SyntaxNfa infinityNfa = rightNfa.cloneForRegExp2SyntaxNfaConverter();
      oneNfa.end.addEdge(epsilon, infinityNfa.start);
      infinityNfa.end.addEdge(epsilon, end);
      // infinity[2,infinity]
      infinityNfa.end.addEdge(epsilon, infinityNfa.start);
    }
  }

  private void doBuildSyntaxNfaByGrammarUnitRegExp(RegExp grammarUnitRegExp) {
    if (grammarUnitRegExp.isEmpty()) {
      SyntaxNfa emptySyntaxNfa = new SyntaxNfa(currentProductionRule);
      grammarUnitRegExp.syntaxNfa = emptySyntaxNfa;
      // start--epsilon-->end
      emptySyntaxNfa.start.addEdge(epsilon, emptySyntaxNfa.end);
      return;
    }

    RepTimes repMinTimes = grammarUnitRegExp.repMinTimes;
    RepTimes repMaxTimes = grammarUnitRegExp.repMaxTimes;
    Grammar grammar = grammarUnitRegExp.sets.getFirst().grammar;
    SyntaxNfa syntaxNfa = grammarUnitRegExp.syntaxNfa;
    if (null == syntaxNfa) {
      syntaxNfa = new SyntaxNfa(currentProductionRule);
      grammarUnitRegExp.syntaxNfa = syntaxNfa;
    }
    SyntaxNfaState start = syntaxNfa.start;
    SyntaxNfaState end = syntaxNfa.end;
    // 0  n(n>=1) infinity
    // 0
    if (repMinTimes.isZeroTimes()) {
      // [0,0]
      if (repMaxTimes.isZeroTimes()) {
        // start--epsilon-->end
        start.addEdge(epsilon, end);
      } else if (repMaxTimes.isNumberTimesAndGreatThanOrEqual(1)) { // [0,n]
        // 0
        start.addEdge(epsilon, end);
        // [1,n]
        SyntaxNfaState preState = start;
        for (int times = 1; times <= repMaxTimes.times; times++) {
          SyntaxNfaState grammarState = new SyntaxNfaState(currentProductionRule);
          preState.addEdge(grammar, grammarState);
          grammarState.addEdge(epsilon, end);

          preState = grammarState;
        }
      } else { // [0,infinity]
        // 0
        start.addEdge(epsilon, end);
        // 1
        SyntaxNfaState grammarState = new SyntaxNfaState(currentProductionRule);
        start.addEdge(grammar, grammarState);
        grammarState.addEdge(epsilon, end);
        // infinity[1,infinity]
        grammarState.addEdge(epsilon, start);
      }
      return;
    }

    // n
    if (repMinTimes.isNumberTimesAndGreatThanOrEqual(1)) {
      // [n,0] error
      if (repMaxTimes.isNumberTimesAndGreatThanOrEqual(1)) { // [m,n]
        // m
        SyntaxNfaState preState = start;
        for (int times = 1; times <= repMaxTimes.times; times++) {
          SyntaxNfaState grammarState = new SyntaxNfaState(currentProductionRule);
          preState.addEdge(grammar, grammarState);

          preState = grammarState;
        }
        int maxOfDifferenceTimes = repMaxTimes.times - repMinTimes.times;
        if (maxOfDifferenceTimes == 0) { // {m,m}
          preState.addEdge(epsilon, end);
          return;
        }
        // [0,maxOfDifferenceTimes] maxOfDifferenceTimes>=1
        // 0
        preState.addEdge(epsilon, end);
        // maxOfDifferenceTimes
        for (int times = 1; times <= repMaxTimes.times; times++) {
          SyntaxNfaState grammarState = new SyntaxNfaState(currentProductionRule);
          preState.addEdge(grammar, grammarState);
          grammarState.addEdge(epsilon, end);

          preState = grammarState;
        }
      } else { // [n,infinity]
        // n
        SyntaxNfaState preState = start;
        for (int times = 1; times <= repMaxTimes.times; times++) {
          SyntaxNfaState grammarState = new SyntaxNfaState(currentProductionRule);
          preState.addEdge(grammar, grammarState);

          preState = grammarState;
        }
        SyntaxNfaState lastGrammarState = preState;
        lastGrammarState.addEdge(epsilon, end);
        // infinity
        SyntaxNfaState infinityGrammarState = new SyntaxNfaState(currentProductionRule);
        lastGrammarState.addEdge(grammar, infinityGrammarState);
        infinityGrammarState.addEdge(epsilon, end);
        infinityGrammarState.addEdge(epsilon, lastGrammarState);
      }
      return;
    }

    // infinity
    // [infinity,0] error
    // [infinity,n] error
    // [infinity,infinity]==[1,infinity]==A+
    if (repMinTimes.isInfinityTimes() && repMaxTimes.isInfinityTimes()) {
      // 1
      SyntaxNfaState oneGrammarState = new SyntaxNfaState(currentProductionRule);
      start.addEdge(grammar, oneGrammarState);
      oneGrammarState.addEdge(epsilon, end);
      //
      SyntaxNfaState infinityGrammarState = new SyntaxNfaState(currentProductionRule);
      // 2
      oneGrammarState.addEdge(grammar, infinityGrammarState);
      infinityGrammarState.addEdge(epsilon, end);
      // infinity[2,infinity]
      infinityGrammarState.addEdge(epsilon, oneGrammarState);
    }
  }
}
