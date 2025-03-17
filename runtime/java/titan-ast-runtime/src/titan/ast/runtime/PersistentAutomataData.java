package titan.ast.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * PersistentData对应在文件中的对象.
 *
 * @author tian wei jun
 */
class PersistentAutomataData {
  InputStream byteInputStream;
  String[] stringPool;
  Grammar[] grammars;
  ProductionRule[] productionRules;

  ByteBuffer intByteBuffer = ByteBuffer.allocate(4);

  PersistentAutomataData(InputStream byteInputStream) {
    intByteBuffer.order(ByteOrder.BIG_ENDIAN);
    this.byteInputStream = byteInputStream;
  }

  /**
   * 只能调用一次.
   *
   * @return
   */
  String[] getStringPoolByInputStream() throws AutomataDataIoException {
    int sizeOfStrings = readInt();
    String[] strings = new String[sizeOfStrings];
    for (int indexOfString = 0; indexOfString < sizeOfStrings; indexOfString++) {
      int countOfStringBytes = readInt();
      String str = readString(countOfStringBytes);
      strings[indexOfString] = str;
    }
    this.stringPool = strings;
    return strings;
  }

  /**
   * 只能调用一次.
   *
   * @return
   */
  Grammar[] getGrammarsByInputStream() throws AutomataDataIoException {
    int sizeOfGramamrs = readInt();
    Grammar[] grammars = new Grammar[sizeOfGramamrs];

    GrammarType[] grammarTypes = GrammarType.values();
    GrammarAction[] grammarActions = GrammarAction.values();
    LookaheadMatchingMode[] lookaheadMatchingModes = LookaheadMatchingMode.values();
    for (int indexOfGrammar = 0; indexOfGrammar < sizeOfGramamrs; indexOfGrammar++) {
      GrammarType type = grammarTypes[readInt()];
      Grammar grammar = generateGrammarByType(type, indexOfGrammar);
      grammar.name = stringPool[readInt()];
      grammar.action = grammarActions[readInt()];
      // lookaheadMatchingMode
      if (type == GrammarType.TERMINAL) {
        TerminalGrammar terminalGrammar = (TerminalGrammar) grammar;
        terminalGrammar.lookaheadMatchingMode = lookaheadMatchingModes[readInt()];
      }
      grammars[indexOfGrammar] = grammar;
    }
    this.grammars = grammars;
    return grammars;
  }

  private Grammar generateGrammarByType(GrammarType type, int indexOfGrammar) {
    Grammar grammar = null;
    switch (type) {
      case TERMINAL:
        grammar = new TerminalGrammar(indexOfGrammar);
        break;
      case NONTERMINAL:
        grammar = new NonterminalGrammar(indexOfGrammar);
        break;
      case TERMINAL_FRAGMENT:
      default:
    }
    return grammar;
  }

  /**
   * 只能调用一次.
   *
   * @return
   */
  KeyWordAutomata getKeyWordAutomataByInputStream() throws AutomataDataIoException {
    KeyWordAutomata keyWordAutomata = new KeyWordAutomata();

    keyWordAutomata.emptyOrNot = readInt();

    if (keyWordAutomata.emptyOrNot == KeyWordAutomata.EMPTY) {
      return keyWordAutomata;
    }

    keyWordAutomata.rootKeyWord = grammars[readInt()];

    int keyWordsSize = readInt();

    HashMap<String, Grammar> textTerminalMap = new HashMap<>(keyWordsSize);
    for (int indexOfKeyWords = 0; indexOfKeyWords < keyWordsSize; indexOfKeyWords++) {
      int intOfText = readInt();
      String text = stringPool[intOfText];

      int intOfTerminal = readInt();
      Grammar terminal = grammars[intOfTerminal];

      textTerminalMap.put(text, terminal);
    }
    keyWordAutomata.textTerminalMap = textTerminalMap;

    return keyWordAutomata;
  }

  /**
   * 只能调用一次.
   *
   * @return
   */
  TokenDfa getTokenDfaByInputStream() throws AutomataDataIoException {
    int sizeOfTokenDfaStates = readInt();
    TokenDfaState[] tokenDfaStates = new TokenDfaState[sizeOfTokenDfaStates];
    for (int indexOfTokenDfaState = 0;
        indexOfTokenDfaState < sizeOfTokenDfaStates;
        indexOfTokenDfaState++) {
      tokenDfaStates[indexOfTokenDfaState] = new TokenDfaState();
    }
    // countOfTokenDfaStates-(type-weight-terminal-countOfEdges-[ch,dest]{countOfEdges})
    for (int indexOfTokenDfaState = 0;
        indexOfTokenDfaState < sizeOfTokenDfaStates;
        indexOfTokenDfaState++) {
      TokenDfaState tokenDfaState = tokenDfaStates[indexOfTokenDfaState];
      tokenDfaState.type = readInt();
      tokenDfaState.weight = readInt();
      int intOfTerminal = readInt();
      if (intOfTerminal >= 0) {
        tokenDfaState.terminal = grammars[intOfTerminal];
      }
      int sizeOfEdges = readInt();
      tokenDfaState.edges = new HashMap<>(sizeOfEdges);
      for (int indexOfEdge = 0; indexOfEdge < sizeOfEdges; indexOfEdge++) {
        int ch = readInt();
        TokenDfaState chToState = tokenDfaStates[readInt()];
        tokenDfaState.edges.put(ch, chToState);
      }
    }
    TokenDfa tokenDfa = new TokenDfa();
    tokenDfa.start = tokenDfaStates[0];
    return tokenDfa;
  }

  void getProductionRulesByInputStream() throws AutomataDataIoException {
    int countOfProductionRules = readInt();
    ProductionRule[] productionRules = new ProductionRule[countOfProductionRules];
    for (int indexOfProductionRule = 0;
        indexOfProductionRule < countOfProductionRules;
        indexOfProductionRule++) {
      productionRules[indexOfProductionRule] = new ProductionRule();
    }
    this.productionRules = productionRules;

    for (ProductionRule productionRule : productionRules) {
      productionRule.grammar = grammars[readInt()];
      int indexOfAliasInStringPool = readInt();
      if (indexOfAliasInStringPool >= 0) {
        productionRule.alias = stringPool[indexOfAliasInStringPool];
      }
      productionRule.reducingDfa = getSyntaxDfaByInputStream();
    }
  }

  SyntaxDfa getSyntaxDfaByInputStream() throws AutomataDataIoException {
    int sizeOfSyntaxDfaStates = readInt();
    SyntaxDfaState[] syntaxDfaStates = new SyntaxDfaState[sizeOfSyntaxDfaStates];
    for (int indexOfSyntaxDfaState = 0;
        indexOfSyntaxDfaState < sizeOfSyntaxDfaStates;
        indexOfSyntaxDfaState++) {
      syntaxDfaStates[indexOfSyntaxDfaState] = new SyntaxDfaState(indexOfSyntaxDfaState);
    }
    // countOfSyntaxDfaStates-(type-countOfEdges-[ch,dest]{countOfEdges}-countOfProductions-productions)
    for (int indexOfSyntaxDfaState = 0;
        indexOfSyntaxDfaState < sizeOfSyntaxDfaStates;
        indexOfSyntaxDfaState++) {
      SyntaxDfaState syntaxDfaState = syntaxDfaStates[indexOfSyntaxDfaState];
      syntaxDfaState.type = readInt();
      int sizeOfEdges = readInt();
      syntaxDfaState.edges = new HashMap<>(sizeOfEdges);
      for (int indexOfEdge = 0; indexOfEdge < sizeOfEdges; indexOfEdge++) {
        Grammar ch = grammars[readInt()];
        SyntaxDfaState chToState = syntaxDfaStates[readInt()];
        syntaxDfaState.edges.put(ch, chToState);
      }
      int sizeOfProductions = readInt();
      syntaxDfaState.closingProductionRules = new ArrayList<>(sizeOfProductions);
      for (int indexOfProduction = 0; indexOfProduction < sizeOfProductions; indexOfProduction++) {
        syntaxDfaState.closingProductionRules.add(productionRules[readInt()]);
      }
    }
    SyntaxDfa syntaxDfa = new SyntaxDfa();
    syntaxDfa.start = syntaxDfaStates[0];
    return syntaxDfa;
  }

  private String readString(int countOfStringBytes) throws AutomataDataIoException {
    byte[] bytes = new byte[countOfStringBytes];
    doRead(bytes, 0, countOfStringBytes);
    return new String(bytes, StandardCharsets.ISO_8859_1);
  }

  private int readInt() throws AutomataDataIoException {
    doRead(intByteBuffer.array(), 0, intByteBuffer.capacity());
    intByteBuffer.clear();
    intByteBuffer.limit(intByteBuffer.capacity());
    return intByteBuffer.getInt();
  }

  private void doRead(byte[] bytes, int offset, int length) throws AutomataDataIoException {
    int countOfRead = 0;
    try {
      countOfRead = byteInputStream.read(bytes, offset, length);
    } catch (IOException e) {
      throw new AutomataDataIoException(e);
    }
    if (countOfRead != length) {
      throw new AutomataDataIoException(
          String.format("expect %d bytes be readed,but '%d'", length, countOfRead));
    }
  }

  AstAutomataType getAstAutomataTypeByInputStream() throws AutomataDataIoException {
    int original = readInt();
    return AstAutomataType.values()[original];
  }

  Map<Grammar, Set<Grammar>> getNonterminalFollowMapByInputStream() throws AutomataDataIoException {
    int size = readInt();
    Map<Grammar, Set<Grammar>> nonterminalFollowMap = new HashMap<>(size);
    for (int indexOfNonterminal = 0; indexOfNonterminal < size; indexOfNonterminal++) {
      Grammar nonterminal = getGrammarByInputStream();
      int sizeOfFollow = readInt();
      Set<Grammar> follow = new HashSet<>(sizeOfFollow);
      for (int indexOfFollow = 0; indexOfFollow < sizeOfFollow; indexOfFollow++) {
        follow.add(getGrammarByInputStream());
      }
      nonterminalFollowMap.put(nonterminal, follow);
    }

    return nonterminalFollowMap;
  }

  Grammar getGrammarByInputStream() throws AutomataDataIoException {
    int indexOfGrammar = readInt();
    return grammars[indexOfGrammar];
  }
}
