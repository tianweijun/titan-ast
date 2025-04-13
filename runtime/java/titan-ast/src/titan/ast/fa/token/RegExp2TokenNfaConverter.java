package titan.ast.fa.token;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.PrimaryGrammarContent.RegExpPrimaryGrammarContent;
import titan.ast.grammar.regexp.AndCompositeRegExp;
import titan.ast.grammar.regexp.GrammarRegExp;
import titan.ast.grammar.regexp.OneCharOptionCharsetRegExp;
import titan.ast.grammar.regexp.OrCompositeRegExp;
import titan.ast.grammar.regexp.ParenthesisRegExp;
import titan.ast.grammar.regexp.RepeatTimes;
import titan.ast.grammar.regexp.SequenceCharsRegExp;
import titan.ast.grammar.regexp.UnitRegExp;

/**
 * 构造正则的nfa，并将其设置.
 *
 * @author tian wei jun
 */
public class RegExp2TokenNfaConverter {

  LinkedList<Grammar> tasks;
  Integer epsilon = TokenNfa.EPSILON;
  Grammar taskGrammar;

  /**
   * 初始化字段:grammarCharset、epsilon、tasks、completedTasks、dependentSources、nfaReg2TokenNfaConverter.
   */
  public RegExp2TokenNfaConverter(Collection<Grammar> taskGrammars) {
    tasks = new LinkedList<>(taskGrammars);
  }

  /**
   * 把所有的tasks对应正则的nfa构造并设置.
   */
  public void convert() {
    int convertMaxTimes = tasks.size();
    int convertTimes = 0;
    while (convertTimes <= convertMaxTimes && !tasks.isEmpty()) {
      doConvert();
      ++convertTimes;
    }
    if (!tasks.isEmpty()) {
      StringBuilder cycleDepentGrammars = new StringBuilder();
      for (Grammar grammar : tasks) {
        cycleDepentGrammars.append(grammar.name);
        cycleDepentGrammars.append(" ");
      }
      cycleDepentGrammars.delete(cycleDepentGrammars.length() - 1, cycleDepentGrammars.length());
      throw new AstRuntimeException(String.format("cycleDepentTokenGrammars:'%s'", cycleDepentGrammars));
    }
  }

  private void doConvert() {
    Iterator<Grammar> tasksIt = tasks.iterator();
    while (tasksIt.hasNext()) {
      taskGrammar = tasksIt.next();
      if (isReadyToBuild(taskGrammar)) {
        buildNfa(taskGrammar);
        tasksIt.remove();
      }
    }
  }

  private boolean isReadyToBuild(Grammar grammar) {
    return isReadyToBuild(((RegExpPrimaryGrammarContent) grammar.primaryGrammarContent).orCompositeRegExp);
  }

  private boolean isReadyToBuild(OrCompositeRegExp orCompositeRegExp) {
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      for (UnitRegExp unitRegExp : andCompositeRegExp.children) {
        if (unitRegExp instanceof GrammarRegExp grammarRegExp) {
          if (null == ((TokenNfable) grammarRegExp.grammar).getTokenNfa()) {
            return false;
          }
        }
        if (unitRegExp instanceof ParenthesisRegExp parenthesisRegExp) {
          if (!isReadyToBuild(parenthesisRegExp.orCompositeRegExp)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private void buildNfa(Grammar grammar) {
    RegExpPrimaryGrammarContent grammarContent = (RegExpPrimaryGrammarContent) grammar.primaryGrammarContent;
    TokenNfa tokenNfa = buildNfaByOrCompositeRegExp(grammarContent.orCompositeRegExp);
    ((TokenNfable) grammar).setTokenNfa(tokenNfa);
  }

  /**
   * 按照后序遍历顺序构造正则节点对应的nfa.
   */
  private TokenNfa buildNfaByOrCompositeRegExp(OrCompositeRegExp orCompositeRegExp) {
    TokenNfa orCompositeRegExpNfa = new TokenNfa();
    TokenNfaState start = orCompositeRegExpNfa.start;
    TokenNfaState end = orCompositeRegExpNfa.end;
    for (AndCompositeRegExp andCompositeRegExp : orCompositeRegExp.children) {
      TokenNfa andCompositeRegExpNfa = buildByAndCompositeRegExp(andCompositeRegExp);
      start.addEdge(epsilon, andCompositeRegExpNfa.start);
      andCompositeRegExpNfa.end.addEdge(epsilon, end);
    }
    return orCompositeRegExpNfa;
  }

  private TokenNfa buildByAndCompositeRegExp(AndCompositeRegExp andCompositeRegExp) {
    TokenNfa andCompositeRegExpNfa = new TokenNfa();
    TokenNfaState prev = andCompositeRegExpNfa.start;
    for (UnitRegExp unitRegExp : andCompositeRegExp.children) {
      TokenNfa unitRegExpNfa = buildByUnitRegExp(unitRegExp);
      prev.addEdge(epsilon, unitRegExpNfa.start);
      prev = unitRegExpNfa.end;
    }
    prev.addEdge(epsilon, andCompositeRegExpNfa.end);
    return andCompositeRegExpNfa;
  }

  private TokenNfa buildByUnitRegExp(UnitRegExp unitRegExp) {
    TokenNfa unitRegExpNfa = null;
    switch (unitRegExp.type) {
      case PARENTHESIS -> {
        unitRegExpNfa = buildNfaByOrCompositeRegExp(((ParenthesisRegExp) unitRegExp).orCompositeRegExp);
      }
      case GRAMMAR -> {
        unitRegExpNfa = buildByGrammarRegExp((GrammarRegExp) unitRegExp);
      }
      case SEQUENCE_CHARS -> {
        unitRegExpNfa = buildBySequenceCharsRegExp((SequenceCharsRegExp) unitRegExp);
      }
      case ONE_CHAR_OPTION_CHARSET -> {
        unitRegExpNfa = buildByOneCharOptionCharsetRegExp((OneCharOptionCharsetRegExp) unitRegExp);
      }
    }
    return unitRegExpNfa;
  }

  private TokenNfa buildByGrammarRegExp(GrammarRegExp grammarRegExp) {
    // reg exp grammar!=null
    Grammar grammar = grammarRegExp.grammar;
    TokenNfa rnfa = ((TokenNfable) grammar).getTokenNfa().cloneForReg2TokenNfaConverter();
    return buildByNfaAndTimes(rnfa, grammarRegExp.repMinTimes, grammarRegExp.repMaxTimes);
  }

  private TokenNfa buildBySequenceCharsRegExp(SequenceCharsRegExp sequenceCharsRegExp) {
    char[] chars = sequenceCharsRegExp.chars.toCharArray();
    if (chars.length == 0) {
      throw new AstRuntimeException(
          String.format("token grammar %s:'xxx'{min,max},'xxx' is empty", taskGrammar.name));
    }
    TokenNfa rnfa = new TokenNfa();
    TokenNfaState prevLinkedState = rnfa.start;
    for (char ch : chars) {
      TokenNfaState nextState = new TokenNfaState();
      prevLinkedState.addEdge((int) ch, nextState);
      prevLinkedState = nextState;
    }
    prevLinkedState.addEdge(epsilon, rnfa.end);
    return buildByNfaAndTimes(rnfa, sequenceCharsRegExp.repMinTimes, sequenceCharsRegExp.repMaxTimes);
  }

  private TokenNfa buildByOneCharOptionCharsetRegExp(OneCharOptionCharsetRegExp oneCharOptionCharsetRegExp) {
    char[] chars = oneCharOptionCharsetRegExp.chars;
    if (chars.length == 0) {
      throw new AstRuntimeException(
          String.format("token grammar %s:'xxx'{min,max},'xxx' is empty", taskGrammar.name));
    }
    TokenNfa rnfa = new TokenNfa();
    TokenNfaState start = rnfa.start;
    TokenNfaState end = rnfa.end;
    for (char ch : chars) {
      start.addEdge((int) ch, end);
    }
    return buildByNfaAndTimes(rnfa, oneCharOptionCharsetRegExp.repMinTimes, oneCharOptionCharsetRegExp.repMaxTimes);
  }

  /*
   * 依据右值nfa为中间内容，按照左值设置重复次数，以完成左值nfa的构造.
   */
  private TokenNfa buildByNfaAndTimes(TokenNfa rightNfa, RepeatTimes repMinTimes, RepeatTimes repMaxTimes) {
    TokenNfa beBuildedNfa = new TokenNfa();
    TokenNfaState start = beBuildedNfa.start;
    TokenNfaState end = beBuildedNfa.end;
    // 0  n(n>=1) infinity
    // 0
    if (repMinTimes.isZeroTimes()) {
      // [0,0] == epsilon
      if (repMaxTimes.isZeroTimes()) {
        // start --epsilon--> end
        start.addEdge(epsilon, end);
      } else if (repMaxTimes.isNumberTimesAndGreatThanOrEqual(1)) { // [0,n]
        // 0
        start.addEdge(epsilon, end);

        // 1 start--epsilon-->preNfa--epsilon->end
        TokenNfa preNfa = rightNfa.cloneForReg2TokenNfaConverter();
        start.addEdge(epsilon, preNfa.start);
        preNfa.end.addEdge(epsilon, end);
        // [2,n]
        for (int repTimes = 2; repTimes <= repMaxTimes.times; repTimes++) {
          TokenNfa nextNfa = rightNfa.cloneForReg2TokenNfaConverter();
          preNfa.end.addEdge(epsilon, nextNfa.start);
          nextNfa.end.addEdge(epsilon, end);

          preNfa = nextNfa;
        }
      } else { // [0,infinity] == A*
        // 0
        start.addEdge(epsilon, end);
        TokenNfa cloneNfa = rightNfa.cloneForReg2TokenNfaConverter();
        // start --epsilon--> A -->epsilon-->end
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
        TokenNfa prevNfa = rightNfa.cloneForReg2TokenNfaConverter();
        start.addEdge(epsilon, prevNfa.start);
        // [2,m]
        for (int repTimes = 2; repTimes <= repMinTimes.times; repTimes++) {
          TokenNfa nextNfa = rightNfa.cloneForReg2TokenNfaConverter();
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
        TokenNfaState middle = prevNfa.end;
        // [0,maxOfDifferenceTimes] maxOfDifferenceTimes>=1
        // 0
        middle.addEdge(epsilon, end);
        // [1-maxOfDifferenceTimes]
        // 1 middle---epsilon--->prevNfa
        prevNfa = rightNfa.cloneForReg2TokenNfaConverter();
        middle.addEdge(epsilon, prevNfa.start);
        prevNfa.end.addEdge(epsilon, end);
        // [2-maxOfDifferenceTimes]
        for (int repTimes = 2; repTimes <= maxOfDifferenceTimes; repTimes++) {
          TokenNfa nextNfa = rightNfa.cloneForReg2TokenNfaConverter();
          prevNfa.end.addEdge(epsilon, nextNfa.start);
          nextNfa.end.addEdge(epsilon, end);
          prevNfa = nextNfa;
        }
        // ------max times end------
      } else { // [m,infinity] m>=1
        // -------ma start------
        TokenNfa prevNfa = rightNfa.cloneForReg2TokenNfaConverter();
        // 1 start--epsilon-->(...A)
        start.addEdge(epsilon, prevNfa.start);
        // [2,m]
        for (int repTimes = 2; repTimes <= repMinTimes.times; repTimes++) {
          TokenNfa nextNfa = rightNfa.cloneForReg2TokenNfaConverter();
          prevNfa.end.addEdge(epsilon, nextNfa.start);
          prevNfa = nextNfa;
        }
        prevNfa.end.addEdge(epsilon, end);
        // -------ma end------
        TokenNfa infinityNfa = rightNfa.cloneForReg2TokenNfaConverter();
        prevNfa.end.addEdge(epsilon, infinityNfa.start);
        infinityNfa.end.addEdge(epsilon, end);
        // infinity
        infinityNfa.end.addEdge(epsilon, infinityNfa.start);
      }
      return beBuildedNfa;
    }

    // infinity
    // [infinity,0] error
    // [infinity,n] error
    // [infinity,infinity] error
    return null;
  }
}
