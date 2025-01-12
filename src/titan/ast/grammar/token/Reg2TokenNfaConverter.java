package titan.ast.grammar.token;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import titan.ast.AstRuntimeException;
import titan.ast.grammar.Grammar;
import titan.ast.grammar.io.GrammarCharset;
import titan.ast.grammar.regexp.RegExp;

/**
 * 构造正则的nfa，并将其设置.
 *
 * @author tian wei jun
 */
public class Reg2TokenNfaConverter {

  Map<String, Grammar> tasks;
  Integer epsilon;
  Grammar taskGrammar;
  Map<String, Grammar> completedTasks;
  Map<String, Grammar> dependentFragmentGrammars;
  NfaReg2TokenNfaConverter nfaReg2TokenNfaConverter;

  /**
   * 初始化字段:grammarCharset、epsilon、tasks、completedTasks、dependentSources、nfaReg2TokenNfaConverter.
   */
  public Reg2TokenNfaConverter() {
    epsilon = GrammarCharset.EPSILON;
    tasks = new HashMap<>();
    completedTasks = new HashMap<>();
    dependentFragmentGrammars = new HashMap<>();
    nfaReg2TokenNfaConverter = new NfaReg2TokenNfaConverter();
  }

  public void addTasks(Map<String, Grammar> grammars) {
    tasks.putAll(grammars);
  }

  public void addDependentFragmentGrammars(Map<String, Grammar> terminalFragments) {
    dependentFragmentGrammars.putAll(terminalFragments);
  }

  /** 把所有的tasks对应正则的nfa构造并设置. */
  public void convert() {
    createGrammarsNfa();
    int convertMaxTimes = tasks.size();
    int convertTimes = 0;
    while (convertTimes <= convertMaxTimes && !tasks.isEmpty()) {
      doConvert();
      ++convertTimes;
    }
    if (!tasks.isEmpty()) {
      StringBuilder cycleDepentGrammars = new StringBuilder();
      for (Map.Entry<String, Grammar> entry : tasks.entrySet()) {
        cycleDepentGrammars.append(entry.getValue().name);
        cycleDepentGrammars.append(" ");
      }
      throw new AstRuntimeException(String.format("cycleDepentGrammars:'%s'", cycleDepentGrammars));
    }
  }

  private void doConvert() {
    Iterator<Map.Entry<String, Grammar>> tasksIt = tasks.entrySet().iterator();
    while (tasksIt.hasNext()) {
      Map.Entry<String, Grammar> entry = tasksIt.next();
      taskGrammar = entry.getValue();
      if (isReadyToBuild(taskGrammar)) {
        buildNfa(taskGrammar);
        completedTasks.put(taskGrammar.name, taskGrammar);
        tasksIt.remove();
      }
    }
  }

  private boolean isReadyToBuild(Grammar grammar) {
    if (grammar.isNfaRegexpContent()) {
      return true;
    }
    if (grammar.isNormalRegexpContent()) {
      return isReadyToBuild(grammar.regExp);
    }
    return false;
  }

  private boolean isReadyToBuild(RegExp regExp) {
    boolean isReadyToBuild = true;
    switch (regExp.type) {
      case COMPOSITE:
        for (RegExp child : regExp.children) {
          isReadyToBuild = isReadyToBuild && isReadyToBuild(child);
          if (!isReadyToBuild) {
            break;
          }
        }
        break;
      case UNIT:
        isReadyToBuild = isReadyToBuild && isReadyToBuildByUnit(regExp);
        break;
      default:
    }
    return isReadyToBuild;
  }

  private boolean isReadyToBuildByUnit(RegExp unit) {
    if (unit.unitType == RegExp.RegExpUnitType.GRAMMAR) {
      String grammarName = unit.sets.getFirst().grammar.name;
      return completedTasks.containsKey(grammarName)
          || dependentFragmentGrammars.containsKey(grammarName);
    }
    return true;
  }

  private void createGrammarsNfa() {
    for (Grammar grammar : tasks.values()) {
      RegExp grammarRegExp = grammar.regExp;
      if (null == grammarRegExp.tokenNfa) {
        grammarRegExp.tokenNfa = new TokenNfa();
      }
    }
  }

  private void buildNfa(Grammar grammar) {
    if (grammar.isNormalRegexpContent()) {
      buildNfa(grammar.regExp);
      return;
    }
    if (grammar.isNfaRegexpContent()) {
      nfaReg2TokenNfaConverter.convert(grammar);
    }
  }

  /**
   * 按照后序遍历顺序构造正则节点对应的nfa.
   *
   * @param regExp 树形正则节点
   */
  private void buildNfa(RegExp regExp) {
    switch (regExp.type) {
      case COMPOSITE:
        buildCompositeNfa(regExp);
        break;
      case UNIT:
        buildUnitNfa(regExp);
        break;
      default:
    }
  }

  private void buildCompositeNfa(RegExp regExp) {
    for (RegExp child : regExp.children) {
      buildNfa(child);
    }

    // build self by relationshipOfChildren
    TokenNfa rightTokenNfa = null;
    switch (regExp.relationshipOfChildren) {
      case OR:
        rightTokenNfa = buildRightNfaByOrChildren(regExp.children);
        break;
      case AND:
        rightTokenNfa = buildRightNfaBySequenceChildren(regExp.children);
        break;
      default:
    }
    buildRegExpNfaByNfa(regExp, rightTokenNfa);
  }

  private TokenNfa buildRightNfaBySequenceChildren(LinkedList<RegExp> regExps) {
    TokenNfa rightTokenNfa = new TokenNfa();
    TokenNfa prevTokenNfa = rightTokenNfa;
    TokenNfaState prevLikedState = prevTokenNfa.start;
    for (RegExp nextRegExp : regExps) {
      if (null == nextRegExp.tokenNfa) {
        nextRegExp.tokenNfa = new TokenNfa();
      }
      TokenNfa nextRegExpNfa = nextRegExp.tokenNfa;
      prevLikedState.addEdge(epsilon, nextRegExpNfa.start);
      prevTokenNfa = nextRegExpNfa;
      prevLikedState = prevTokenNfa.end;
    }
    prevLikedState.addEdge(epsilon, rightTokenNfa.end);
    return rightTokenNfa;
  }

  private TokenNfa buildRightNfaByOrChildren(LinkedList<RegExp> regExps) {
    TokenNfa rightTokenNfa = new TokenNfa();
    TokenNfaState start = rightTokenNfa.start;
    TokenNfaState end = rightTokenNfa.end;
    for (RegExp nextRegExp : regExps) {
      if (null == nextRegExp.tokenNfa) {
        nextRegExp.tokenNfa = new TokenNfa();
      }
      TokenNfa nextRegExpNfa = nextRegExp.tokenNfa;
      start.addEdge(epsilon, nextRegExpNfa.start);
      nextRegExpNfa.end.addEdge(epsilon, end);
    }
    return rightTokenNfa;
  }

  /**
   * 基本单元由'' [] grammar构成 字符的或关系只能用[]表示.
   *
   * @param unitRegExp '' [] grammar基本正则单元
   */
  private void buildUnitNfa(RegExp unitRegExp) {
    switch (unitRegExp.unitType) {
      case EMPTY:
        buildEpsilonUnitNfa(unitRegExp);
        break;
      case SEQUENCE_CHARS:
        buildSequenceCharsUnitNfa(unitRegExp);
        break;
      case ONE_CHAR_OPTION_CHARSET:
        buildOneCharOptionCharsetUnitNfa(unitRegExp);
        break;
      case GRAMMAR:
        buildGrammarUnitNfa(unitRegExp);
        break;
      case HELPER_OR:
      case HELPER_ALIAS:
        throw new AstRuntimeException(
            String.format("%s:regExp should not be helper. ", taskGrammar.name));
      default:
    }
  }

  private void buildOneCharOptionCharsetUnitNfa(RegExp unitRegExp) {
    if (unitRegExp.isNot) {
      buildOneCharOptionCharsetUnitNfaByNot(unitRegExp);
    } else {
      buildOneCharOptionCharsetUnitNfaByNoNot(unitRegExp);
    }
  }

  private void buildOneCharOptionCharsetUnitNfaByNot(RegExp unitRegExp) {
    byte[] toEdgeChars = new byte[GrammarCharset.COUNT_OF_CHARS];
    for (int ch = 0; ch < toEdgeChars.length; ch++) {
      toEdgeChars[ch] = 1;
    }
    for (RegExp.RegExpCharSet set : unitRegExp.sets) {
      switch (set.type) {
        case ONE_CHAR_OPTION_RANGE:
          for (int ch = set.minChar; ch <= set.maxChar; ch++) {
            toEdgeChars[ch] = 0;
          }
          break;
        case ONE_CHAR_OPTION_CHARS:
          for (char ch : set.chars) {
            toEdgeChars[ch] = 0;
          }
          break;
        default:
      }
    }
    TokenNfa rnfa = new TokenNfa();
    TokenNfaState start = rnfa.start;
    TokenNfaState end = rnfa.end;
    for (int ch = 0; ch < toEdgeChars.length; ch++) {
      if (toEdgeChars[ch] == 1) {
        start.addEdge(ch, end);
      }
    }
    if (start.edges.isEmpty()) {
      throw new AstRuntimeException(
          String.format("%s:~[ab]{m,n},~[ab] is empty", taskGrammar.name));
    }
    buildRegExpNfaByNfa(unitRegExp, rnfa);
  }

  private void buildOneCharOptionCharsetUnitNfaByNoNot(RegExp unitRegExp) {
    TokenNfa rnfa = new TokenNfa();
    TokenNfaState start = rnfa.start;
    TokenNfaState end = rnfa.end;
    Iterator<RegExp.RegExpCharSet> setsIt = unitRegExp.sets.iterator();
    while (setsIt.hasNext()) {
      RegExp.RegExpCharSet set = setsIt.next();
      switch (set.type) {
        case ONE_CHAR_OPTION_RANGE:
          for (int ch = set.minChar; ch <= set.maxChar; ch++) {
            start.addEdge(ch, end);
          }
          break;
        case ONE_CHAR_OPTION_CHARS:
          for (char ch : set.chars) {
            start.addEdge((int) ch, end);
          }
          break;
        default:
      }
    }
    if (start.edges.isEmpty()) {
      throw new AstRuntimeException(String.format("%s:[ab]{m,n},[ab] is empty", taskGrammar.name));
    }
    buildRegExpNfaByNfa(unitRegExp, rnfa);
  }

  private void buildSequenceCharsUnitNfa(RegExp unitRegExp) {
    RegExp.RegExpCharSet set = unitRegExp.sets.getFirst();
    char[] chars = set.chars;
    if (chars.length <= 0) {
      throw new AstRuntimeException(
          String.format("%s:~?[ab]{m,n} , [ab] is empty.", taskGrammar.name));
    }
    TokenNfa rnfa = new TokenNfa();
    TokenNfaState prevLinkedState = rnfa.start;
    for (char ch : chars) {
      TokenNfaState nextState = new TokenNfaState();
      prevLinkedState.addEdge((int) ch, nextState);
      prevLinkedState = nextState;
    }
    prevLinkedState.addEdge(epsilon, rnfa.end);
    buildRegExpNfaByNfa(unitRegExp, rnfa);
  }

  private void buildGrammarUnitNfa(RegExp unitRegExp) {
    // reg exp grammar!=null
    Grammar grammar = unitRegExp.sets.get(0).grammar;
    TokenNfa rnfa = grammar.regExp.tokenNfa.cloneForReg2TokenNfaConverter();
    buildRegExpNfaByNfa(unitRegExp, rnfa);
  }

  /**
   * 依据右值nfa为中间内容，按照左值设置重复次数，以完成左值nfa的构造.
   *
   * @param regExp 左值nfa
   * @param rightNfa 右值nfa
   */
  private void buildRegExpNfaByNfa(RegExp regExp, TokenNfa rightNfa) {
    if (null == regExp.tokenNfa) {
      regExp.tokenNfa = new TokenNfa();
    }
    RegExp.RepTimes repMinTimes = regExp.repMinTimes;
    RegExp.RepTimes repMaxTimes = regExp.repMaxTimes;
    TokenNfa beBuildedNfa = regExp.tokenNfa;
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
      return;
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
          return;
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
      return;
    }

    // infinity
    // [infinity,0] error
    // [infinity,n] error
    // [infinity,infinity]==[1,infinity]==A+
    if (repMinTimes.isInfinityTimes() && repMaxTimes.isInfinityTimes()) {
      // 1
      TokenNfa oneNfa = rightNfa.cloneForReg2TokenNfaConverter();
      start.addEdge(epsilon, oneNfa.start);
      oneNfa.end.addEdge(epsilon, end);
      // 2
      TokenNfa infinityNfa = rightNfa.cloneForReg2TokenNfaConverter();
      oneNfa.end.addEdge(epsilon, infinityNfa.start);
      infinityNfa.end.addEdge(epsilon, end);
      // infinity[2,infinity]
      infinityNfa.end.addEdge(epsilon, infinityNfa.start);
    }
  }

  private void buildEpsilonUnitNfa(RegExp unitRegExp) {
    TokenNfa epsilonUnitNfa = unitRegExp.tokenNfa;
    if (null == epsilonUnitNfa) {
      epsilonUnitNfa = new TokenNfa();
      unitRegExp.tokenNfa = epsilonUnitNfa;
    }
    // start --epsilon--> end
    epsilonUnitNfa.start.addEdge(epsilon, epsilonUnitNfa.end);
  }
}
