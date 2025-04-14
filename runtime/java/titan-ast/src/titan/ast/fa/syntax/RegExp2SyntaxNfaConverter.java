package titan.ast.fa.syntax;

import titan.ast.AstContext;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.RepeatTimes;
import titan.ast.grammar.regexp.UnitRegExp;

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

  public SyntaxNfa convert(AndCompositeRegExp andCompositeRegExp, ProductionRule productionRule) {
    this.currentProductionRule = productionRule;
    return buildByAndCompositeRegExp(andCompositeRegExp);
  }

  private SyntaxNfa buildByAndCompositeRegExp(AndCompositeRegExp andCompositeRegExp) {
    SyntaxNfa rSyntaxNfa = new SyntaxNfa(currentProductionRule);
    SyntaxNfaState prevState = rSyntaxNfa.start;
    for (UnitRegExp unitRegExp : andCompositeRegExp.children) {
      switch (unitRegExp.type) {
        case PARENTHESIS -> {
          SyntaxNfa parenthesisRegExpNfa = buildByParenthesisRegExp((ParenthesisRegExp) unitRegExp);
          prevState.addEdge(epsilon, parenthesisRegExpNfa.start);
          prevState = parenthesisRegExpNfa.end;
        }
        case GRAMMAR -> {
          SyntaxNfa grammarRegExpNfa = buildByGrammarRegExp((GrammarRegExp) unitRegExp);
          prevState.addEdge(epsilon, grammarRegExpNfa.start);
          prevState = grammarRegExpNfa.end;
        }
      }
    }
    prevState.addEdge(epsilon, rSyntaxNfa.end);
    return rSyntaxNfa;
  }

  private SyntaxNfa buildByOrCompositeRegExp(OrCompositeRegExp orCompositeRegExp) {
    SyntaxNfa rSyntaxNfa = new SyntaxNfa(currentProductionRule);
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      SyntaxNfa andCompositeRegExpNfa = buildByAndCompositeRegExp(andCompositeRegExp);
      rSyntaxNfa.start.addEdge(epsilon, andCompositeRegExpNfa.start);
      andCompositeRegExpNfa.end.addEdge(epsilon, rSyntaxNfa.end);
    }
    return rSyntaxNfa;
  }

  private SyntaxNfa buildByParenthesisRegExp(ParenthesisRegExp parenthesisRegExp) {
    SyntaxNfa orCompositeRegExpNfa = buildByOrCompositeRegExp(parenthesisRegExp.orCompositeRegExp);
    SyntaxNfa rSyntaxNfa = new SyntaxNfa(currentProductionRule);
    rSyntaxNfa.start.addEdge(epsilon, orCompositeRegExpNfa.start);
    orCompositeRegExpNfa.end.addEdge(epsilon, rSyntaxNfa.end);
    return buildByNfaAndTimes(rSyntaxNfa, parenthesisRegExp.repMinTimes, parenthesisRegExp.repMaxTimes);
  }

  private SyntaxNfa buildByGrammarRegExp(GrammarRegExp grammarRegExp) {
    SyntaxNfa rSyntaxNfa = new SyntaxNfa(currentProductionRule);
    rSyntaxNfa.start.addEdge(grammarRegExp.grammar, rSyntaxNfa.end);
    return buildByNfaAndTimes(rSyntaxNfa, grammarRegExp.repMinTimes, grammarRegExp.repMaxTimes);
  }

  private SyntaxNfa buildByNfaAndTimes(SyntaxNfa rightNfa, RepeatTimes repMinTimes, RepeatTimes repMaxTimes) {
    SyntaxNfa beBuildedNfa = new SyntaxNfa(currentProductionRule);
    SyntaxNfaState start = beBuildedNfa.start;
    SyntaxNfaState end = beBuildedNfa.end;
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
      return beBuildedNfa;
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
          return beBuildedNfa;
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
      return beBuildedNfa;
    }
    return null;
  }
}
